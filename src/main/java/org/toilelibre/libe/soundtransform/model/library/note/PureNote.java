package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class PureNote implements Note {

    private static final int SAMPLE_RATE = 48000;
    private static final int NB_BYTES_PER_SAMPLE = 2;
    private static final int MAX_VAL = (int) Math.pow (256, NB_BYTES_PER_SAMPLE) / 2;


    private Sound generatePureNote (final float frequency, final float lengthInSeconds, final int channelnum) {
        final int nbSamples = (int) (PureNote.SAMPLE_RATE * lengthInSeconds * 1.0);
        return new Sound (this.generateSinLongArray (frequency, nbSamples, PureNote.SAMPLE_RATE, PureNote.MAX_VAL), PureNote.NB_BYTES_PER_SAMPLE, PureNote.SAMPLE_RATE, channelnum);
    }

    private long [] generateSinLongArray (final float frequency, final int nbSamples, final float sampleRate, final int maxVal) {
        final long [] signal = new long [nbSamples];
        for (int j = 0 ; j < nbSamples ; j++) {
            signal [j] = (long) (Math.sin (j * frequency * 2 * Math.PI / sampleRate) * maxVal);
        }

        return signal;
    }

    @Override
    public Sound getAttack (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, 1.0f / 4 * lengthInSeconds, channelnum);
    }

    @Override
    public Sound getDecay (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, 1.0f / 4 * lengthInSeconds, channelnum);
    }

    @Override
    public float getFrequency () {
        return -1; //Unknown
    }

    @Override
    public String getName () {
        return "PURENOTE";
    }

    @Override
    public Sound getRelease (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, 1.0f / 4 * lengthInSeconds, channelnum);
    }

    @Override
    public Sound getSustain (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generatePureNote (frequency, 1.0f / 4 * lengthInSeconds, channelnum);
    }

}
