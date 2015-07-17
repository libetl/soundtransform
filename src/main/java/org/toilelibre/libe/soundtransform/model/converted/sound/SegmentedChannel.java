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
    private static final String THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED = null;
    private List<Sound> channelParts;

    public SegmentedChannel (FormatInfo formatInfo, final List<Sound> sounds) {
        super (null, formatInfo, 0);
        this.channelParts = sounds;
    }

    @Override
    public long getSampleAt (int index) {
        int channelPartIndex = this.getChannelPartIndex (index);
        int samplesIndex = this.getSamplesIndex (index);

        return this.channelParts.get (channelPartIndex).getChannels () [0].getSampleAt (samplesIndex);
    }

    @Override
    public String viewSamplesArray () {
        return SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED;
    }

    @Override
    public String toString () {
        return SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED;
    }
    
    private int getChannelPartIndex (int index){
        int realIndex = index;
        int channelPartIndex = 0;
        while (channelPartIndex < this.channelParts.size () &&
                realIndex > this.channelParts.get (channelPartIndex).getSamplesLength ()) {
            realIndex -= this.channelParts.get (channelPartIndex).getSamplesLength ();
            channelPartIndex++;
        }
        if (channelPartIndex == this.channelParts.size ()) {
            throw new SoundTransformRuntimeException (new SoundTransformException (SegmentedChannelErrorCode.ARRAY_INDEX_OUT_OF_BOUNDS, new ArrayIndexOutOfBoundsException (), index));
        }
        return channelPartIndex;
    }

    private int getSamplesIndex (int index){
        int realIndex = index;
        int channelPartIndex = 0;
        while (channelPartIndex < this.channelParts.size () &&
                realIndex > this.channelParts.get (channelPartIndex).getSamplesLength ()) {
            realIndex -= this.channelParts.get (channelPartIndex).getSamplesLength ();
            channelPartIndex++;
        }
        return realIndex;
    }

    @Override
    public int getSamplesLength () {
        int segmentIndex = 0;
        int numberOfSamples = 0;
        while (segmentIndex < this.channelParts.size ()) {
            numberOfSamples += this.channelParts.get (segmentIndex).getSamplesLength ();
            segmentIndex++;
        }
        return numberOfSamples;
    }

    @Override
    public void setSampleAt (int index, long value) {
        int channelPartIndex = this.getChannelPartIndex (index);
        int samplesIndex = this.getSamplesIndex (index);
        
        this.channelParts.get (channelPartIndex).getChannels () [0].setSampleAt (samplesIndex, value);
    }

    @Override
    public void copyTo (long [] samples) {
        this.copyTo (samples, 0, 0, this.getSamplesLength ());
    }

    @Override
    public void copyTo (Channel channel) {
        this.copyTo (channel.samples, 0, 0, this.getSamplesLength ());
    }

    @Override
    public void copyTo (long [] samples, int srcPos, int dstPos, int length) {
        int dstPos1 = dstPos;
        int channelPartIndex = this.getChannelPartIndex (srcPos);
        int samplesIndex = this.getSamplesIndex (srcPos);
        for (int i = channelPartIndex ; i < this.channelParts.size () ; i++) {
            this.channelParts.get (i).getChannels () [0].copyTo (samples, (i == channelPartIndex ? samplesIndex : 0), dstPos1, this.channelParts.get (i).getChannels () [0].getSamplesLength ());
            dstPos1 += this.channelParts.get (i).getChannels () [0].getSamplesLength ();
        }
    }

    @Override
    public void copyTo (Channel channel, int srcPos, int dstPos, int length) {
        this.copyTo (channel.samples, srcPos, dstPos, length);
    }

}
