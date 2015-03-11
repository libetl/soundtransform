package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public abstract class FormulaNote implements Note {

    private static final int   BYTE_NB_VALUES              = 1 << Byte.SIZE;
    private static final int   HALF                        = 2;
    private static final int   DEFAULT_SAMPLE_RATE         = 48000;
    private static final int   DEFAULT_NB_BYTES_PER_SAMPLE = 2;
    private static final float ONE_TENTH                   = 1.0f / 10;
    private static final float ONE_HALF                    = 1.0f / 2;
    private static final float ONE_FIFTH                   = 1.0f / 2;
    private static final float ATTACK_START_AMPLITUDE      = 0;
    private static final float ATTACK_END_AMPLITUDE        = 1;
    private static final float DECAY_START_AMPLITUDE       = 1;
    private static final float DECAY_END_AMPLITUDE         = 0.8f;
    private static final float SUSTAIN_START_AMPLITUDE     = 0.8f;
    private static final float SUSTAIN_END_AMPLITUDE       = 0.8f;
    private static final float RELEASE_START_AMPLITUDE     = 0.8f;
    private static final float RELEASE_END_AMPLITUDE       = 0;

    private final FormatInfo   formatInfo;
    private final int          maxVal;

    public FormulaNote () {
        this (new FormatInfo (FormulaNote.DEFAULT_NB_BYTES_PER_SAMPLE, FormulaNote.DEFAULT_SAMPLE_RATE));
    }

    public FormulaNote (final FormatInfo formatInfo1) {
        this.formatInfo = formatInfo1;
        this.maxVal = (int) Math.pow (FormulaNote.BYTE_NB_VALUES, this.formatInfo.getSampleSize ()) / FormulaNote.HALF;

    }

    protected abstract float applyFormula (int j, float frequency, float sampleRate2);

    private long [] generateLongArray (final float frequency, final int nbSamples, final float sampleRate, final int maxVal, final float startAmplitude, final float endAmplitude) {
        final long [] signal = new long [nbSamples];
        for (int j = 0 ; j < nbSamples ; j++) {
            final float coeff = (float) ((j * endAmplitude + (nbSamples - j) * startAmplitude) / (1.0 * nbSamples));
            signal [j] = (long) (this.applyFormula (j, frequency, sampleRate) * maxVal * coeff);
        }

        return signal;
    }

    private Sound generatePureNote (final float frequency, final float lengthInSeconds, final int channelnum, final float startAmplitude, final float endAmplitude) {
        final int nbSamples = (int) (this.formatInfo.getSampleRate () * lengthInSeconds * 1.0);
        return new Sound (this.generateLongArray (frequency, nbSamples, this.formatInfo.getSampleRate (), this.maxVal, startAmplitude, endAmplitude), this.formatInfo, channelnum);
    }

    @Override
    public Sound getAttack (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, FormulaNote.ONE_TENTH * lengthInSeconds, channelnum, FormulaNote.ATTACK_START_AMPLITUDE, FormulaNote.ATTACK_END_AMPLITUDE);
    }

    @Override
    public Sound getDecay (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, FormulaNote.ONE_FIFTH * lengthInSeconds, channelnum, FormulaNote.DECAY_START_AMPLITUDE, FormulaNote.DECAY_END_AMPLITUDE);
    }

    @Override
    public float getFrequency () {
        return -1; // Unknown
    }

    @Override
    public abstract String getName ();

    @Override
    public Sound getRelease (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, FormulaNote.ONE_FIFTH * lengthInSeconds, channelnum, FormulaNote.RELEASE_START_AMPLITUDE, FormulaNote.RELEASE_END_AMPLITUDE);
    }

    @Override
    public Sound getSustain (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, FormulaNote.ONE_HALF * lengthInSeconds, channelnum, FormulaNote.SUSTAIN_START_AMPLITUDE, FormulaNote.SUSTAIN_END_AMPLITUDE);
    }

}
