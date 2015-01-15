package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class ReverseSoundTransformation implements SoundTransformation {

    private static Sound reverse (final Sound sound) {
        final long [] data = sound.getSamples ();
        final long [] newdata = new long [sound.getSamples ().length];
        // this is the raw audio data -- no header

        for (int i = 0 ; i < data.length ; i++) {
            newdata [i] = data [data.length - i - 1];
        }
        // normalized result in newdata
        return new Sound (newdata, sound.getNbBytesPerSample (), sound.getSampleRate (), sound.getChannelNum ());
    }

    public ReverseSoundTransformation () {
    }

    @Override
    public Sound transform (final Sound input) {
        return ReverseSoundTransformation.reverse (input);
    }
}
