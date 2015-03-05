package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class EightBitsSoundTransformation implements SoundTransformation {

    private final int step;

    public EightBitsSoundTransformation (final int step) {
        this.step = step;
    }

    @Override
    public Sound transform (final Sound input) {

        final Sound outputSound = new Sound (new long [input.getSamplesLength ()], input.getFormatInfo (), input.getChannelNum ());
        for (int i = 0 ; i < input.getSamplesLength () ; i++) {
            if ((i % this.step) == 0) {
                outputSound.setSampleAt (i, input.getSampleAt (i));
            } else {
                outputSound.setSampleAt (i, 0);
            }
        }

        return outputSound;
    }

}
