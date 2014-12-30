package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;

public class ConvertedSoundAppender implements SoundAppender {

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppenderI
     * #append(org.toilelibre.libe.soundtransform.model.converted.sound.Sound,
     * int, org.toilelibre.libe.soundtransform.model.converted.sound.Sound)
     */
    @Override
    public void append (final Sound origin, final int usedarraylength, final Sound... otherSounds) {
        int offset = usedarraylength;
        for (final Sound otherSound : otherSounds) {
            offset = this.append (origin, offset, otherSound);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppenderI
     * #append(org.toilelibre.libe.soundtransform.model.converted.sound.Sound,
     * int, org.toilelibre.libe.soundtransform.model.converted.sound.Sound)
     */
    @Override
    public int append (final Sound origin, final int usedarraylength, final Sound otherSound) {
        final Sound resultBeforeResize = this.changeNbBytesPerSample (otherSound, origin.getNbBytesPerSample ());
        final Sound resultBeforeCopy = this.resizeToSampleRate (resultBeforeResize, origin.getSampleRate ());
        final int lastIndex = Math.min (origin.getSamples ().length, usedarraylength + resultBeforeCopy.getSamples ().length);
        System.arraycopy (resultBeforeCopy.getSamples (), 0, origin.getSamples (), usedarraylength, lastIndex - usedarraylength);
        return lastIndex;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppenderI
     * #changeNbBytesPerSample(org.toilelibre.libe.soundtransform.model
     * .converted.sound.Sound, int)
     */
    @Override
    public Sound changeNbBytesPerSample (final Sound sound, final int newNbBytesPerSample) {
        final long [] newsamples = new long [sound.getSamples ().length];
        final long oldMax = (long) (Math.pow (256, sound.getNbBytesPerSample ()) / 2);
        final long newMax = (long) (Math.pow (256, newNbBytesPerSample) / 2);
        for (int j = 0; j < sound.getSamples ().length; j++) {
            newsamples [j] = (long) (sound.getSamples () [j] * 1.0 * newMax / oldMax);
        }
        return new Sound (newsamples, newNbBytesPerSample, sound.getSampleRate (), sound.getChannelNum ());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppenderI
     * #downsampleWithRatio(org.toilelibre.libe.soundtransform.model
     * .converted.sound.Sound, float)
     */
    @Override
    public Sound downsampleWithRatio (final Sound sound, final float ratio) {
        float appendIfGreaterThanOrEqualsRatio = 0;
        int indexResult = 0;
        final long [] result = new long [(int) Math.ceil (sound.getSamples ().length / ratio)];
        for (int i = 0; i < sound.getSamples ().length; i++) {
            if (appendIfGreaterThanOrEqualsRatio >= ratio) {
                appendIfGreaterThanOrEqualsRatio -= ratio;
                result [indexResult++] = sound.getSamples () [i];
            } else {
                appendIfGreaterThanOrEqualsRatio += 1.0;
            }
        }
        return new Sound (result, sound.getNbBytesPerSample (), (int) (sound.getSampleRate () / ratio), sound.getChannelNum ());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppenderI
     * #resizeToSampleRate(org.toilelibre.libe.soundtransform.model
     * .converted.sound.Sound, int)
     */
    @Override
    public Sound resizeToSampleRate (final Sound sound, final int newfreq) {
        final float ratio = (float) (newfreq * 1.0 / sound.getSampleRate ());
        if (ratio > 1) {
            return this.upsampleWithRatio (sound, ratio);
        }
        return this.downsampleWithRatio (sound, (float) (1.0 / ratio));
    }

    private Sound upsampleWithRatio (final Sound sound, final float ratio) {
        float appendWhileLessThanOrEqualsRatio = 0;
        int indexResult = 0;
        final long [] result = new long [(int) Math.ceil (sound.getSamples ().length * (ratio + 1))];
        for (int i = 0; i < sound.getSamples ().length; i++) {
            while (appendWhileLessThanOrEqualsRatio <= ratio) {
                result [indexResult++] = sound.getSamples () [i];
                appendWhileLessThanOrEqualsRatio++;
            }
            appendWhileLessThanOrEqualsRatio -= ratio;
        }
        return new Sound ( indexResult == 0 ? new long [0] : Arrays.copyOfRange (result, 0, indexResult - 1), sound.getNbBytesPerSample (), (int) (sound.getSampleRate () * ratio),
                sound.getChannelNum ());
    }
}
