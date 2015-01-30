package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class NoOpSoundTransformation implements SoundTransformation {

    public NoOpSoundTransformation () {
    }

    private Sound noop (final Sound sound) {
        final long [] data = sound.getSamples ();

        // same array in newdata
        final long [] newdata = Arrays.copyOf (data, data.length);

        return new Sound (newdata, sound.getNbBytesPerSample (), sound.getSampleRate (), sound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        return this.noop (input);
    }
}
