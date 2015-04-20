package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;

/**
 * Mixes several sounds into a new sound
 * The sound channels will be re-sampled (up sampled or down sampled) to match the first sound format info.
 * The sounds samples will be summed. 
 * Therefore, if the first sound is the opposite of the second one (sample1 [i] = -sample2 [i]), the sum will be 0. (there will be nothing to hear)
 *
 */
public class MixSoundTransformation implements SoundTransformation {

    private final SoundAppender  soundAppender;
    private final List<Sound []> otherSounds;

    /**
     * Default constructor
     * @param otherSounds1 sounds to mix with the first one (passed in the transform) 
     *                     the transform expects to receive all the channels of each sound, 
     *                     even if it will not use them all for the mix. 
     *                     (the channelNum of the first sound will be used to match the other sounds channels
     *                     before the mix operation takes place)
     */
    public MixSoundTransformation (final List<Sound []> otherSounds1) {
        this.soundAppender = $.select (SoundAppender.class);
        this.otherSounds = otherSounds1;
    }

    private Sound mix (final Sound firstSound, final Sound... sounds) {
        int maxlength = 0;
        final Sound [] ajustedSounds = new Sound [sounds.length + 1];
        ajustedSounds [0] = firstSound;
        for (int i = 1 ; i < sounds.length + 1 ; i++) {
            ajustedSounds [i] = this.soundAppender.changeNbBytesPerSample (this.soundAppender.resizeToSampleRate (sounds [i - 1], firstSound.getSampleRate ()), firstSound.getSampleSize ());
        }

        for (final Sound sound : ajustedSounds) {
            maxlength = Math.max (maxlength, sound.getSamplesLength ());
        }

        final long [] newdata = new long [maxlength];

        // find the max:
        double max = 0;
        for (int i = 0 ; i < maxlength ; i++) {
            for (final Sound sound : ajustedSounds) {
                if (sound.getSamplesLength () > i) {
                    newdata [i] += sound.getSampleAt (i);
                }
            }
            max = Math.max (newdata [i], max);
        }

        // now find the result, with scaling:
        final double maxValue = Math.pow (256, sounds [0].getSampleSize ()) - 1;
        final double ratio = maxValue / (max * ajustedSounds.length);
        for (int i = 0 ; i < maxlength ; i++) {
            newdata [i] *= ratio;
        }

        // normalized result in newdata
        return new Sound (newdata, firstSound.getFormatInfo (), firstSound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        final Sound [] onlyOneChannelFromSounds = new Sound [this.otherSounds.size ()];
        int i = 0;
        for (final Sound [] sounds : this.otherSounds) {
            if (sounds.length > input.getChannelNum ()) {
                onlyOneChannelFromSounds [i++] = sounds [input.getChannelNum ()];
            }
        }
        return this.mix (input, onlyOneChannelFromSounds);
    }
}
