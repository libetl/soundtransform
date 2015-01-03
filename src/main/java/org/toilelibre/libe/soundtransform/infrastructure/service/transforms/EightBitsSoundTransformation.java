package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class EightBitsSoundTransformation implements SoundTransformation {

    private int step = 1;

    public EightBitsSoundTransformation (final int step) {
        this.step = step;
    }

    @Override
    public Sound transform (final Sound input) {

        final Sound outputSound = new Sound (new long [input.getSamples ().length], input.getNbBytesPerSample (), input.getSampleRate (),
                input.getChannelNum ());
        for (int i = 0 ; i < input.getSamples ().length ; i++) {
            if (i % this.step == 0) {
                outputSound.getSamples () [i] = input.getSamples () [i];
            } else {
                outputSound.getSamples () [i] = 0;
            }
        }

        return outputSound;
    }

}
