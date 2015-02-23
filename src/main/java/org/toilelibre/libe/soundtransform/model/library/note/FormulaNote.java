package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public abstract class FormulaNote implements Note {

    private static final int DEFAULT_SAMPLE_RATE         = 48000;
    private static final int DEFAULT_NB_BYTES_PER_SAMPLE = 2;
    private final int        sampleRate;
    private final int        nbBytesPerSample;
    private final int        maxVal;

    public FormulaNote () {
        this (FormulaNote.DEFAULT_SAMPLE_RATE, FormulaNote.DEFAULT_NB_BYTES_PER_SAMPLE);
    }

    public FormulaNote (final int sampleRate1, final int nbBytesPerSamples1) {
        this.sampleRate = sampleRate1;
        this.nbBytesPerSample = nbBytesPerSamples1;
        this.maxVal = (int) Math.pow (256, this.nbBytesPerSample) / 2;

    }

    protected abstract float applyFormula (int j, float frequency, float sampleRate2);

    private long [] generateLongArray (final float frequency, final int nbSamples, final float sampleRate, final int maxVal, final float startAmplitude, final float endAmplitude) {
        final long [] signal = new long [nbSamples];
        for (int j = 0 ; j < nbSamples ; j++) {
            final float coeff = (float) (((j * endAmplitude) + ((nbSamples - j) * startAmplitude)) / (1.0 * nbSamples));
            signal [j] = (long) (this.applyFormula (j, frequency, sampleRate) * maxVal * coeff);
        }

        return signal;
    }

    private Sound generatePureNote (final float frequency, final float lengthInSeconds, final int channelnum, final float startAmplitude, final float endAmplitude) {
        final int nbSamples = (int) (this.sampleRate * lengthInSeconds * 1.0);
        return new Sound (this.generateLongArray (frequency, nbSamples, this.sampleRate, this.maxVal, startAmplitude, endAmplitude), this.nbBytesPerSample, this.sampleRate, channelnum);
    }

    @Override
    public Sound getAttack (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, (1.0f / 10) * lengthInSeconds, channelnum, 0, 1);
    }

    @Override
    public Sound getDecay (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, (1.0f / 5) * lengthInSeconds, channelnum, 1, 0.8f);
    }

    @Override
    public float getFrequency () {
        return -1; // Unknown
    }

    @Override
    public abstract String getName ();

    @Override
    public Sound getRelease (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, (1.0f / 5) * lengthInSeconds, channelnum, 0.8f, 0);
    }

    @Override
    public Sound getSustain (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, (1.0f / 2) * lengthInSeconds, channelnum, 0.8f, 0.8f);
    }

}
