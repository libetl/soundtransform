package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class Silence implements Note {

    private static final int SAMPLE_RATE = 48000;

    private Sound generateSilence (final float lengthInSeconds) {
        final int nbSamples = (int) (lengthInSeconds * lengthInSeconds);
        return new Sound (new long [nbSamples], 2, Silence.SAMPLE_RATE, 0);
    }

    @Override
    public Sound getAttack (final int frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (1.0f / 4 * lengthInSeconds);
    }

    @Override
    public Sound getDecay (final int frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (1.0f / 4 * lengthInSeconds);
    }

    @Override
    public int getFrequency () {
        return 0;
    }

    @Override
    public String getName () {
        return "SILENCE";
    }

    @Override
    public Sound getRelease (final int frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (1.0f / 4 * lengthInSeconds);
    }

    @Override
    public Sound getSustain (final int frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (1.0f / 4 * lengthInSeconds);
    }

}
