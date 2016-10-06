package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import org.toilelibre.libe.soundtransform.infrastructure.service.Processor;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.note.Note;

@Processor
final class ConvertedSoundAppender implements SoundAppender {

    private static final int HALF           = 2;
    private static final int BYTE_NB_VALUES = 1 << 8;

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppenderI
     * #append(org.toilelibre.libe.soundtransform.model.converted.sound.Sound,
     * int, org.toilelibre.libe.soundtransform.model.converted.sound.Sound)
     */
    @Override
    public void append (final Channel origin, final int usedarraylength, final Channel... otherSounds) {
        int offset = usedarraylength;
        for (final Channel otherSound : otherSounds) {
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
    public int append (final Channel origin, final int usedarraylength, final Channel otherSound) {
        final Channel resultBeforeResize = this.changeNbBytesPerSample (otherSound, origin.getSampleSize ());
        final Channel resultBeforeCopy = this.resizeToSampleRate (resultBeforeResize, origin.getSampleRate ());
        final int lastIndex = Math.min (origin.getSamplesLength (), usedarraylength + resultBeforeCopy.getSamplesLength ());
        resultBeforeCopy.copyTo (origin, 0, usedarraylength, lastIndex - usedarraylength);
        return lastIndex;
    }

    @Override
    public Channel append (final Channel sound1, final Channel sound2) {
        final Channel sound2Ajusted = this.resizeToSampleRate (this.changeNbBytesPerSample (sound2, sound1.getSampleSize ()), sound1.getSampleRate ());
        final Channel result = new Channel (new long [sound1.getSamplesLength () + sound2.getSamplesLength ()], sound1.getFormatInfo (), sound1.getChannelNum ());

        sound1.copyTo (result);
        sound2Ajusted.copyTo (result, 0, sound1.getSamplesLength (), sound2Ajusted.getSamplesLength ());

        return result;
    }

    @Override
    public void appendNote (final Channel sound, final Note note, final double lastFreq, final int indexInSound, final int channelNum, final float lengthInSeconds) throws SoundTransformException {

        final Channel attack = note.getAttack ((int) lastFreq, channelNum, lengthInSeconds);
        final Channel decay = note.getDecay ((int) lastFreq, channelNum, lengthInSeconds);
        final Channel sustain = note.getSustain ((int) lastFreq, channelNum, lengthInSeconds);
        final Channel release = note.getRelease ((int) lastFreq, channelNum, lengthInSeconds);
        this.append (sound, indexInSound, attack, decay, sustain, release);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppender
     * #changeNbBytesPerSample(org.toilelibre.libe.soundtransform.model
     * .converted.sound.Sound, int)
     */
    @Override
    public Channel changeNbBytesPerSample (final Channel sound, final int newNbBytesPerSample) {
        final long [] newsamples = new long [sound.getSamplesLength ()];
        final long oldMax = (long) (Math.pow (ConvertedSoundAppender.BYTE_NB_VALUES, sound.getSampleSize ()) / ConvertedSoundAppender.HALF);
        final long newMax = (long) (Math.pow (ConvertedSoundAppender.BYTE_NB_VALUES, newNbBytesPerSample) / ConvertedSoundAppender.HALF);
        for (int j = 0 ; j < sound.getSamplesLength () ; j++) {
            newsamples [j] = (long) (sound.getSampleAt (j) * 1.0 * newMax / oldMax);
        }
        return new Channel (newsamples, new FormatInfo (newNbBytesPerSample, sound.getSampleRate ()), sound.getChannelNum ());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppender
     * #downsampleWithRatio(org.toilelibre.libe.soundtransform.model
     * .converted.sound.Sound, float)
     */
    @Override
    public Channel downsampleWithRatio (final Channel sound, final float ratio) {
        float appendIfGreaterThanOrEqualsRatio = 0;
        int indexResult = 0;
        final long [] result = new long [(int) Math.ceil (sound.getSamplesLength () / ratio)];
        for (int i = 0 ; i < sound.getSamplesLength () ; i++) {
            if (appendIfGreaterThanOrEqualsRatio >= ratio) {
                appendIfGreaterThanOrEqualsRatio -= ratio;
                result [indexResult++] = sound.getSampleAt (i);
            }
            appendIfGreaterThanOrEqualsRatio += 1.0;
        }
        return new Channel (result, new FormatInfo (sound.getSampleSize (), sound.getSampleRate () / ratio), sound.getChannelNum ());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.appender.
     * SoundAppender
     * #resizeToSampleRate(org.toilelibre.libe.soundtransform.model
     * .converted.sound.Sound, int)
     */
    @Override
    public Channel resizeToSampleRate (final Channel sound, final float newfreq) {
        final float ratio = (float) (newfreq * 1.0 / sound.getSampleRate ());
        if (ratio > 1) {
            return this.upsampleWithRatio (sound, ratio);
        }
        if (ratio < 1) {
            return this.downsampleWithRatio (sound, (float) (1.0 / ratio));
        }
        return sound;
    }

    private Channel upsampleWithRatio (final Channel sound, final float ratio) {
        float appendWhileLessThanOrEqualsRatio = 0;
        int indexResult = 0;
        final long [] result = new long [(int) Math.ceil (sound.getSamplesLength () * (ratio + 1))];
        for (int i = 0 ; i < sound.getSamplesLength () ; i++) {
            while (appendWhileLessThanOrEqualsRatio <= ratio) {
                result [indexResult++] = sound.getSampleAt (i);
                appendWhileLessThanOrEqualsRatio++;
            }
            appendWhileLessThanOrEqualsRatio -= ratio;
        }
        long [] outputLongArray = new long [0];
        if (indexResult > 0) {
            outputLongArray = new long [indexResult];
            System.arraycopy (result, 0, outputLongArray, 0, indexResult);
        }
        return new Channel (outputLongArray, new FormatInfo (sound.getSampleSize (), sound.getSampleRate () * ratio), sound.getChannelNum ());
    }
}
