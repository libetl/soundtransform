package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

/**
 * Removes or adds some samples in the input sound according to the passed
 * percent parameter. This will change the pitch of the sound (the frequencies
 * will be shifted)
 */
public class PitchSoundTransform implements SoundTransform<Channel, Channel> {

    private static final float A_HUNDRED             = 100;
    private final  float       percent;

    /**
     * Default constructor
     *
     * @param percent1
     *            if &lt; 100, the sound will contains more samples, therefore
     *            the sound will be pitched down, and the frequencies will be
     *            lowered if = 100, nothing happens if &gt; 100, the sound will
     *            contains less samples, therefore the sound will be pitched up,
     *            and the frequencies will be higher
     */
    public PitchSoundTransform (final float percent1) {
        this.percent = percent1;
    }

    private Channel pitch (final Channel sound, final float percent1) {
        final float total = PitchSoundTransform.A_HUNDRED;
        if (percent1 == total) {
            return sound;
        }
        final float nbSamples = sound.getSamplesLength ();
        final float nbFiltered = Math.abs (total * nbSamples / percent1);
        final float incr = nbSamples / nbFiltered;

        final long [] ret = new long [(int) nbFiltered];
        for (double i = 0 ; i < incr * nbFiltered ; i += incr) {
            final int j = (int) (i / incr);
            if (j < ret.length) {
                ret [j] = sound.getSampleAt ((int) i);
            }
        }
        return new Channel (ret, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Channel transform (final Channel input) {
        return this.pitch (input, this.percent);
    }
}
