package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class Silence implements Note {

    private static final float ONE_FOURTH       = 1.0f / 4;
    private static final int   DEFAULT_NB_BYTES = 2;
    private static final int   SAMPLE_RATE      = 48000;

    private Sound generateSilence (final float lengthInSeconds) {
        final int nbSamples = (int) (Silence.SAMPLE_RATE * lengthInSeconds * 1.0);
        return new Sound (new long [nbSamples], Silence.DEFAULT_NB_BYTES, Silence.SAMPLE_RATE, 0);
    }

    @Override
    public Sound getAttack (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (Silence.ONE_FOURTH * lengthInSeconds);
    }

    @Override
    public Sound getDecay (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (Silence.ONE_FOURTH * lengthInSeconds);
    }

    @Override
    public float getFrequency () {
        return 0;
    }

    @Override
    public String getName () {
        return "SILENCE";
    }

    @Override
    public Sound getRelease (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (Silence.ONE_FOURTH * lengthInSeconds);
    }

    @Override
    public Sound getSustain (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (Silence.ONE_FOURTH * lengthInSeconds);
    }

}
