package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.library.note.Note;

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

    @Override
    public void appendNote (final Sound sound, final Note note, final double lastFreq, final int indexInSound, final int channelNum, final float lengthInSeconds) {

        if (lengthInSeconds < 0.6) {
            final Sound sustain = note.getSustain ((int) lastFreq, channelNum, lengthInSeconds * 2);
            this.append (sound, indexInSound, sustain);
        } else {
            final Sound attack = note.getAttack ((int) lastFreq, channelNum, lengthInSeconds);
            final Sound decay = note.getDecay ((int) lastFreq, channelNum, lengthInSeconds);
            final Sound sustain = note.getSustain ((int) lastFreq, channelNum, lengthInSeconds);
            final Sound release = note.getRelease ((int) lastFreq, channelNum, lengthInSeconds);
            this.append (sound, indexInSound, attack, decay, sustain, release);
        }
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
        for (int j = 0 ; j < sound.getSamples ().length ; j++) {
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
        for (int i = 0 ; i < sound.getSamples ().length ; i++) {
            if (appendIfGreaterThanOrEqualsRatio >= ratio) {
                appendIfGreaterThanOrEqualsRatio -= ratio;
                result [indexResult++] = sound.getSamples () [i];
            }
            appendIfGreaterThanOrEqualsRatio += 1.0;
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
        for (int i = 0 ; i < sound.getSamples ().length ; i++) {
            while (appendWhileLessThanOrEqualsRatio <= ratio) {
                result [indexResult++] = sound.getSamples () [i];
                appendWhileLessThanOrEqualsRatio++;
            }
            appendWhileLessThanOrEqualsRatio -= ratio;
        }
        long [] outputLongArray = new long [0];
        if (indexResult > 0) {
            outputLongArray = new long [indexResult];
            System.arraycopy (result, 0, outputLongArray, 0, indexResult);
        }
        return new Sound (outputLongArray, sound.getNbBytesPerSample (), (int) (sound.getSampleRate () * ratio), sound.getChannelNum ());
    }
}
