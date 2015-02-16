package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;

public class MixSoundTransformation implements SoundTransformation {

    private SoundAppender soundAppender;
    private Sound [] otherSounds;
    
    public MixSoundTransformation (final Sound... otherSounds1) {
        this.soundAppender = $.select (SoundAppender.class);
        this.otherSounds = otherSounds1;
    }

    private Sound mix (final Sound firstSound, final Sound... sounds) {
        int maxlength = 0;
        Sound [] ajustedSounds = new Sound [sounds.length + 1];
        ajustedSounds [0] = sounds [0];
        for (int i = 1 ; i < sounds.length ; i++){
            ajustedSounds [i] = 
                    this.soundAppender.changeNbBytesPerSample (
                            this.soundAppender.resizeToSampleRate (sounds [i - 1], firstSound.getSampleRate ()), firstSound.getNbBytesPerSample ());
        }

        for (Sound sound : ajustedSounds){
            maxlength = Math.max (maxlength, sound.getSamples ().length);
        }
        
        final long [] newdata = new long [maxlength];

        // find the max:
        double max = 0;
        for (int i = 0 ; i < maxlength ; i++) {
            long element = 0;
            for (Sound sound : ajustedSounds){
                if (sound.getSamples ().length > i){
                  element += Math.abs (sound.getSamples () [i]);
                  newdata [i] = sound.getSamples () [i];
                }
            }
            max = Math.max (element, max);
        }

        // now find the result, with scaling:
        final double maxValue = Math.pow (256, sounds [0].getNbBytesPerSample ()) - 1;
        final double ratio = maxValue / max;
        for (int i = 0 ; i < maxlength ; i++) {
            newdata [i] *= ratio;
        }

        // normalized result in newdata
        return new Sound (newdata, firstSound.getNbBytesPerSample (), firstSound.getSampleRate (), firstSound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        return this.mix (input, this.otherSounds);
    }
}
