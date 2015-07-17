package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;

public class Channel {

    protected final long []  samples;
    private final FormatInfo formatInfo;
    private final int        channelNum;

    public Channel (final long [] samples1, final FormatInfo fomatInfo1, final int channelNum1) {
        super ();
        this.samples = samples1;
        this.formatInfo = fomatInfo1;
        this.channelNum = channelNum1;
    }

    public int getChannelNum () {
        return this.channelNum;
    }

    public FormatInfo getFormatInfo () {
        return this.formatInfo;
    }

    public long getSampleAt (final int i) {
        return this.samples [i];
    }

    public float getSampleRate () {
        return this.formatInfo.getSampleRate ();
    }

    public String viewSamplesArray () {
        return Arrays.toString (this.samples);
    }

    public int getSampleSize () {
        return this.formatInfo.getSampleSize ();
    }

    public int getSamplesLength () {
        return this.samples.length;
    }

    public void setSampleAt (final int i, final long value) {
        this.samples [i] = value;
    }

    @Override
    public String toString () {
        return $.select (SoundToStringService.class).convert (this);
    }

    public void copyTo (final long [] samples) {
        this.copyTo (this.samples, 0, 0, this.getSamplesLength ());
    }

    public void copyTo (final Channel channel) {
        this.copyTo (channel.samples);
    }

    public void copyTo (final long [] samples, final int srcPos, final int dstPos, final int length) {
        System.arraycopy (this.samples, srcPos, samples, dstPos, length);
    }

    public void copyTo (final Channel channel, final int srcPos, final int dstPos, final int length) {
        this.copyTo (channel.samples, srcPos, dstPos, length);
    }
}
