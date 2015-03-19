package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;

public class Sound implements Cloneable {

    enum SoundErrorCode implements ErrorCode {
        CLONE_FAILED ("Clone operation on a sound failed");

        private String messageFormat;

        SoundErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final long []    samples;
    private final FormatInfo formatInfo;
    private final int        channelNum;

    public Sound (final long [] samples1, final FormatInfo fomatInfo1, final int channelNum1) {
        super ();
        this.samples = samples1;
        this.formatInfo = fomatInfo1;
        this.channelNum = channelNum1;
    }

    @Override
    public Sound clone () {
        try {
            return (Sound) super.clone ();
        } catch (final CloneNotSupportedException e) {
            throw new SoundTransformRuntimeException (SoundErrorCode.CLONE_FAILED, e);
        }
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
