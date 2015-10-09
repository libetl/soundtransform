package org.toilelibre.libe.soundtransform.model.library.pack.note;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

public class Silence implements Note {

    private static final float ONE_FOURTH       = 1.0f / 4;
    private static final int   DEFAULT_NB_BYTES = 2;
    private static final int   SAMPLE_RATE      = 48000;

    private Channel generateSilence (final float lengthInSeconds) {
        final int nbSamples = (int) (Silence.SAMPLE_RATE * lengthInSeconds * 1.0);
        return new Channel (new long [nbSamples], new FormatInfo (Silence.DEFAULT_NB_BYTES, Silence.SAMPLE_RATE), 0);
    }

    @Override
    public Channel getAttack (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (Silence.ONE_FOURTH * lengthInSeconds);
    }

    @Override
    public Channel getDecay (final float frequency, final int channelnum, final float lengthInSeconds) {
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
    public Channel getRelease (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (Silence.ONE_FOURTH * lengthInSeconds);
    }

    @Override
    public Channel getSustain (final float frequency, final int channelnum, final float lengthInSeconds) {
        return this.generateSilence (Silence.ONE_FOURTH * lengthInSeconds);
    }

}
