package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

public class SegmentedChannel extends Channel {

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
    private int recentlyViewedIndex = 0;
    private int recentlyViewedSamplesArrayIndex = 0;
    private int recentlyViewedSamplesArrayPosition = 0;
    private int recentlyViewedSamplesLength = 0;
    private int recentlyViewedSamplesArrayNumber = 0;

    public SegmentedChannel (final FormatInfo formatInfo, final List<Sound> sounds) {
        super (null, formatInfo, 0);
        this.channelParts = sounds;
    }
    
    private void updateRecentlyViewedChannelLength () {

        int segmentIndex = recentlyViewedSamplesArrayNumber;
        int numberOfSamples = recentlyViewedSamplesLength;
        while (segmentIndex < this.channelParts.size ()) {
            numberOfSamples += this.channelParts.get (segmentIndex).getSamplesLength ();
            segmentIndex++;
        }
        this.recentlyViewedSamplesLength = numberOfSamples;
        this.recentlyViewedSamplesArrayNumber = segmentIndex;
    }

    @Override
    public long getSampleAt (final int index) {
        this.updateRecentlyViewedIndex (index);

        return this.channelParts.get (this.recentlyViewedSamplesArrayIndex).getChannels () [0].getSampleAt (this.recentlyViewedSamplesArrayPosition);
    }

    @Override
    public String viewSamplesArray () {
        return SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED;
    }

    @Override
    public String toString () {
        return SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED;
    }


    private void updateRecentlyViewedIndex (int wantedIndex) {
        
        int currentGlobalIndex = this.recentlyViewedIndex;
        int currentIndex = this.recentlyViewedSamplesArrayIndex;
        int currentPosition = this.recentlyViewedSamplesArrayPosition;
        
        while (currentGlobalIndex != wantedIndex) {
            int delta = (int) Math.signum (wantedIndex - currentGlobalIndex);
            currentGlobalIndex += delta;
            currentPosition += delta;
            
            if (currentPosition < 0) {
                currentIndex--;
                if (currentIndex < 0) {
                    throw new SoundTransformRuntimeException (new SoundTransformException (SegmentedChannelErrorCode.ARRAY_INDEX_OUT_OF_BOUNDS, new ArrayIndexOutOfBoundsException (), currentIndex));
                }
                currentPosition = this.channelParts.get (currentIndex).getSamplesLength () - 1;
            }

            if (currentPosition >= this.channelParts.get (currentIndex).getSamplesLength ()) {
                currentIndex++;
                if (currentIndex >= this.channelParts.size ()) {
                    throw new SoundTransformRuntimeException (new SoundTransformException (SegmentedChannelErrorCode.ARRAY_INDEX_OUT_OF_BOUNDS, new ArrayIndexOutOfBoundsException (), currentIndex));
                }
                currentPosition = 0;
            }        }
        
        this.recentlyViewedIndex = wantedIndex;
        this.recentlyViewedSamplesArrayIndex = currentIndex;
        this.recentlyViewedSamplesArrayPosition = currentPosition;
    }
    

    @Override
    public int getSamplesLength () {
        this.updateRecentlyViewedChannelLength ();
        return this.recentlyViewedSamplesLength;
    }

    @Override
    public synchronized void setSampleAt (final int index, final long value) {
        this.updateRecentlyViewedIndex (index);

        this.channelParts.get (this.recentlyViewedSamplesArrayIndex).getChannels () [0].setSampleAt (this.recentlyViewedSamplesArrayPosition, value);
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
        int i = this.recentlyViewedSamplesArrayIndex;
        while (i < this.channelParts.size () && remainingLength > 0) {
            this.channelParts.get (i).getChannels () [0].copyTo (samples, i == this.recentlyViewedSamplesArrayIndex ? this.recentlyViewedSamplesArrayPosition : 0, dstPos1, Math.min (this.channelParts.get (i).getChannels () [0].getSamplesLength (), remainingLength));
            dstPos1 += this.channelParts.get (i).getChannels () [0].getSamplesLength ();
            remainingLength -= this.channelParts.get (i).getChannels () [0].getSamplesLength ();
            i++;
        }
    }

    @Override
    public synchronized void copyTo (final Channel channel, final int srcPos, final int dstPos, final int length) {
        this.copyTo (channel.samples, srcPos, dstPos, length);
    }

}
