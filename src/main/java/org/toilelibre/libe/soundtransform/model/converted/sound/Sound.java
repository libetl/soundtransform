package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;

public class Sound {

    private final long []    samples;
    private final FormatInfo formatInfo;
    private final int        channelNum;

    public Sound (final long [] samples1, final FormatInfo fomatInfo1, final int channelNum1) {
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

    public long [] getSamples () {
        return this.samples;
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
}
