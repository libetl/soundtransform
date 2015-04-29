package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;

/**
 * Leaves only one sample out of [step] ones, the others are set to 0. The
 * effect is to produce a sound that sounds like a video game console. (a good
 * step value for a CD format is 25)
 */
public class EightBitsSoundTransform implements SoundTransform<Channel, Channel> {

    private final int step;

    /**
     * Default constructor
     * 
     * @param step
     *            iteration step value
     */
    public EightBitsSoundTransform (final int step) {
        this.step = step;
    }

    @Override
    public Channel transform (final Channel input) {

        final Channel outputSound = new Channel (new long [input.getSamplesLength ()], input.getFormatInfo (), input.getChannelNum ());
        for (int i = 0 ; i < input.getSamplesLength () ; i++) {
            if (i % this.step == 0) {
                outputSound.setSampleAt (i, input.getSampleAt (i));
            } else {
                outputSound.setSampleAt (i, 0);
            }
        }

        return outputSound;
    }

}
