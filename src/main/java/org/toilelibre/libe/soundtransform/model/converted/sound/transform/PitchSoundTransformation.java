package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class PitchSoundTransformation implements SoundTransformation {

    private static final float A_HUNDRED             = 100;
    private static final float DEFAULT_PERCENT_VALUE = 20;
    private float              percent               = PitchSoundTransformation.DEFAULT_PERCENT_VALUE;

    public PitchSoundTransformation (final float percent) {
        this.percent = percent;
    }

    private Sound pitch (final Sound sound, final float percent) {
        final float total = PitchSoundTransformation.A_HUNDRED;
        if (percent == total) {
            return new Sound (sound.getSamples (), sound.getFormatInfo (), sound.getChannelNum ());
        }
        final float nbSamples = sound.getSamplesLength ();
        final float nbFiltered = Math.abs (total * nbSamples / percent);
        final float incr = nbSamples / nbFiltered;
        final long [] data = sound.getSamples ();
        final long [] ret = new long [(int) nbFiltered];
        for (float i = 0 ; i < incr * nbFiltered ; i += incr) {
            final int j = (int) (i / incr);
            if (j < ret.length) {
                ret [j] = data [(int) i];
            }
        }
        return new Sound (ret, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        return this.pitch (input, this.percent);
    }
}
