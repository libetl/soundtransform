package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

public class SegmentedChannel extends Channel {

    static class IndexData {
        int globalIndex;
        int arrayIndex;
        int arrayPosition;

        public IndexData (final int globalIndex, final int arrayIndex, final int arrayPosition) {
            super ();
            this.globalIndex = globalIndex;
            this.arrayIndex = arrayIndex;
            this.arrayPosition = arrayPosition;
        }

        public IndexData (final IndexData otherIndex) {
            this (otherIndex.globalIndex, otherIndex.arrayIndex, otherIndex.arrayPosition);
        }

    }

    enum SegmentedChannelErrorCode implements ErrorCode {
        ARRAY_INDEX_OUT_OF_BOUNDS ("Index out of the samples array (%1d)");

        private final String messageFormat;

        SegmentedChannelErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    public static final String THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED = "This channel is segmented and cannot be displayed";
    private final List<Sound>  channelParts;
    private int                recentlyViewedSamplesArrayNumber;
    private int                recentlyViewedSamplesLength;
    private IndexData          recentlyViewedIndex                               = new IndexData (0, 0, 0);

    public SegmentedChannel (final FormatInfo formatInfo, final List<Sound> sounds) {
        super (null, formatInfo, 0);
        this.channelParts = sounds;
    }

    private void updateRecentlyViewedChannelLength () {

        int segmentIndex = this.recentlyViewedSamplesArrayNumber;
        int numberOfSamples = this.recentlyViewedSamplesLength;
        while (segmentIndex < this.channelParts.size ()) {
            numberOfSamples += this.channelParts.get (segmentIndex).getSamplesLength ();
            segmentIndex++;
        }
        this.recentlyViewedSamplesLength = numberOfSamples;
        this.recentlyViewedSamplesArrayNumber = segmentIndex;
    }

    @Override
    public synchronized long getSampleAt (final int index) {
        this.updateRecentlyViewedIndex (index);

        return this.channelParts.get (this.recentlyViewedIndex.arrayIndex).getChannels () [0].getSampleAt (this.recentlyViewedIndex.arrayPosition);
    }

    @Override
    public String viewSamplesArray () {
        return SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED;
    }

    @Override
    public String toString () {
        return SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED;
    }

    @Override
    public int getSamplesLength () {
        this.updateRecentlyViewedChannelLength ();
        return this.recentlyViewedSamplesLength;
    }

    @Override
    public synchronized void setSampleAt (final int index, final long value) {
        this.updateRecentlyViewedIndex (index);

        this.channelParts.get (this.recentlyViewedIndex.arrayIndex).getChannels () [0].setSampleAt (this.recentlyViewedIndex.arrayPosition, value);
    }

    @Override
    public synchronized void copyTo (final long [] samples) {
        this.copyTo (samples, 0, 0, this.getSamplesLength ());
    }

    @Override
    public synchronized void copyTo (final Channel channel) {
        this.copyTo (channel.samples, 0, 0, this.getSamplesLength ());
    }

    @Override
    public synchronized void copyTo (final long [] samples, final int srcPos, final int dstPos, final int length) {
        int dstPos1 = dstPos;
        int remainingLength = length;
        this.updateRecentlyViewedIndex (srcPos);
        int i = this.recentlyViewedIndex.arrayIndex;
        while (i < this.channelParts.size () && remainingLength > 0) {
            this.channelParts.get (i).getChannels () [0].copyTo (samples, i == this.recentlyViewedIndex.arrayIndex ? this.recentlyViewedIndex.arrayPosition : 0, dstPos1, Math.min (this.channelParts.get (i).getChannels () [0].getSamplesLength (), remainingLength));
            dstPos1 += this.channelParts.get (i).getChannels () [0].getSamplesLength ();
            remainingLength -= this.channelParts.get (i).getChannels () [0].getSamplesLength ();
            i++;
        }
    }

    @Override
    public synchronized void copyTo (final Channel channel, final int srcPos, final int dstPos, final int length) {
        this.copyTo (channel.samples, srcPos, dstPos, length);
    }

    private void updateRecentlyViewedIndex (final int wantedIndex) {

        final IndexData tmpIndex = new IndexData (this.recentlyViewedIndex);

        while (tmpIndex.globalIndex != wantedIndex) {
            final int newPosition = tmpIndex.arrayPosition + wantedIndex - tmpIndex.globalIndex;

            if (newPosition < 0) {
                this.goToPreviousSampleArray (tmpIndex);
            } else if (newPosition >= this.channelParts.get (tmpIndex.arrayIndex).getSamplesLength ()) {
                this.goToNextSampleArray (tmpIndex);
            } else {
                this.setFoundIndexAndPosition (tmpIndex, wantedIndex, newPosition);
            }
        }

        this.recentlyViewedIndex = tmpIndex;
    }

    private void goToNextSampleArray (final IndexData index) {
        index.arrayIndex++;
        if (index.arrayIndex >= this.channelParts.size ()) {
            throw new SoundTransformRuntimeException (new SoundTransformException (SegmentedChannelErrorCode.ARRAY_INDEX_OUT_OF_BOUNDS, new ArrayIndexOutOfBoundsException (), index.arrayIndex));
        }
        index.globalIndex += this.channelParts.get (index.arrayIndex - 1).getSamplesLength () - index.arrayPosition;
        index.arrayPosition = 0;
    }

    private void goToPreviousSampleArray (final IndexData index) {
        index.arrayIndex--;
        if (index.arrayIndex < 0) {
            throw new SoundTransformRuntimeException (new SoundTransformException (SegmentedChannelErrorCode.ARRAY_INDEX_OUT_OF_BOUNDS, new ArrayIndexOutOfBoundsException (), index.arrayIndex));
        }
        index.globalIndex -= index.arrayPosition + 1;
        index.arrayPosition = this.channelParts.get (index.arrayIndex).getSamplesLength () - 1;
    }

    private void setFoundIndexAndPosition (final IndexData index, final int wantedIndex, final int newPosition) {
        index.arrayPosition = newPosition;
        index.globalIndex = wantedIndex;
    }

}
