package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.Collections;
import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;

/**
 * Mixes several sounds into a new sound The sound channels will be re-sampled
 * (up sampled or down sampled) to match the first sound format info. The sounds
 * samples will be summed. Therefore, if the first sound is the opposite of the
 * second one (sample1 [i] = -sample2 [i]), the sum will be 0. (there will be
 * nothing to hear)
 *
 */
public class MixSoundTransform implements SoundTransform<Channel, Channel> {

    private final SoundAppender  soundAppender;
    private final List<Sound> otherSounds;

    /**
     * Default constructor
     * 
     * @param otherSounds1
     *            sounds to mix with the first one (passed in the transform) the
     *            transform expects to receive all the channels of each sound,
     *            even if it will not use them all for the mix. (the channelNum
     *            of the first sound will be used to match the other sounds
     *            channels before the mix operation takes place)
     */
    public MixSoundTransform (final List<Sound> otherSounds1) {
        this.soundAppender = $.select (SoundAppender.class);
        this.otherSounds = otherSounds1;
    }

    public MixSoundTransform (final Sound otherSound) {
        this.soundAppender = $.select (SoundAppender.class);
        this.otherSounds = Collections.singletonList (otherSound);
    }
    
    private Channel mix (final Channel firstSound, final Channel... sounds) {
        int maxlength = 0;
        final Channel [] ajustedSounds = new Channel [sounds.length + 1];
        ajustedSounds [0] = firstSound;
        for (int i = 1 ; i < sounds.length + 1 ; i++) {
            ajustedSounds [i] = this.soundAppender.changeNbBytesPerSample (this.soundAppender.resizeToSampleRate (sounds [i - 1], firstSound.getSampleRate ()), firstSound.getSampleSize ());
        }

        for (final Channel sound : ajustedSounds) {
            maxlength = Math.max (maxlength, sound.getSamplesLength ());
        }

        final long [] newdata = new long [maxlength];

        // find the max:
        double max = 0;
        for (int i = 0 ; i < maxlength ; i++) {
            for (final Channel sound : ajustedSounds) {
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
        return new Channel (newdata, firstSound.getFormatInfo (), firstSound.getChannelNum ());
    }

    @Override
    public Channel transform (final Channel input) {
        final Channel [] onlyOneChannelFromSounds = new Channel [this.otherSounds.size ()];
        int i = 0;
        for (final Sound sound : this.otherSounds) {
            if (sound.getNumberOfChannels() > input.getChannelNum ()) {
                onlyOneChannelFromSounds [i++] = sound.getChannels() [input.getChannelNum ()];
            }
        }
        return this.mix (input, onlyOneChannelFromSounds);
    }
}
