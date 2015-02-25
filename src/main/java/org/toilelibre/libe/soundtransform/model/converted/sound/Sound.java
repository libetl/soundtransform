package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
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

    private final long [] samples;
    private final int     nbBytesPerSample;
    private final int     sampleRate;
    private final int     channelNum;

    public Sound (final long [] samples, final int nbBytesPerSample, final int sampleRate, final int channelNum) {
        super ();
        this.samples = samples;
        this.nbBytesPerSample = nbBytesPerSample;
        this.sampleRate = sampleRate;
        this.channelNum = channelNum;
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

    public int getNbBytesPerSample () {
        return this.nbBytesPerSample;
    }

    public int getSampleRate () {
        return this.sampleRate;
    }

    public long getSampleAt (final int i) {
        return this.samples [i];
    }

    public void setSampleAt (final int i, final long value) {
        this.samples [i] = value;
    }
    
    public long [] getSamples () {
        return this.samples;
    }

    public int getSamplesLength (){
        return this.samples.length;
    }

    @Override
    public String toString () {
        return $.create (SoundToStringService.class).convert (this);
    }
}
