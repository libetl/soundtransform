package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.List;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;

public class MixSoundTransformation implements SoundTransformation {

    private final SoundAppender soundAppender;
    private final List<Sound []> otherSounds;

    public MixSoundTransformation (final List<Sound[]> otherSounds1) {
        this.soundAppender = $.select (SoundAppender.class);
        this.otherSounds = otherSounds1;
    }

    private Sound mix (final Sound firstSound, final Sound... sounds) {
        int maxlength = 0;
        final Sound [] ajustedSounds = new Sound [sounds.length + 1];
        ajustedSounds [0] = firstSound;
        for (int i = 1 ; i < sounds.length + 1; i++) {
            ajustedSounds [i] = this.soundAppender.changeNbBytesPerSample (this.soundAppender.resizeToSampleRate (sounds [i - 1], firstSound.getSampleRate ()), firstSound.getNbBytesPerSample ());
        }

        for (final Sound sound : ajustedSounds) {
            maxlength = Math.max (maxlength, sound.getSamples ().length);
        }

        final long [] newdata = new long [maxlength];

        // find the max:
        double max = 0;
        for (int i = 0 ; i < maxlength ; i++) {
            for (final Sound sound : ajustedSounds) {
                if (sound.getSamples ().length > i) {
                    newdata [i] += sound.getSamples () [i];
                }
            }
            max = Math.max (newdata [i], max);
        }

        // now find the result, with scaling:
        final double maxValue = Math.pow (256, sounds [0].getNbBytesPerSample ()) - 1;
        final double ratio = maxValue / (max * ajustedSounds.length);
        for (int i = 0 ; i < maxlength ; i++) {
            newdata [i] *= ratio;
        }

        // normalized result in newdata
        return new Sound (newdata, firstSound.getNbBytesPerSample (), firstSound.getSampleRate (), firstSound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        Sound [] onlyOneChannelSounds = new Sound [this.otherSounds.size ()];
        int i = 0;
        for (Sound [] sounds : this.otherSounds){
            if (sounds.length > input.getChannelNum ()) {
                onlyOneChannelSounds [i++] = sounds [input.getChannelNum ()];
            }
        }
        return this.mix (input, onlyOneChannelSounds);
    }
}
