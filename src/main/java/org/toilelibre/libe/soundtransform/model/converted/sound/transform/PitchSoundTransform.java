package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

/**
 * Removes or adds some samples in the input sound according to the passed
 * percent parameter. This will change the pitch of the sound (the frequencies
 * will be shifted)
 */
public class PitchSoundTransform implements SoundTransform<Sound, Sound> {

    private static final float A_HUNDRED             = 100;
    private static final float DEFAULT_PERCENT_VALUE = 20;
    private float              percent               = PitchSoundTransform.DEFAULT_PERCENT_VALUE;

    /**
     * Default constructor
     * 
     * @param percent
     *            if < 100, the sound will contains more samples, therefore the
     *            sound will be pitched down, and the frequencies will be
     *            lowered if = 100, nothing happens if > 100, the sound will
     *            contains less samples, therefore the sound will be pitched up,
     *            and the frequencies will be higher
     */
    public PitchSoundTransform (final float percent) {
        this.percent = percent;
    }

    private Sound pitch (final Sound sound, final float percent) {
        final float total = PitchSoundTransform.A_HUNDRED;
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
