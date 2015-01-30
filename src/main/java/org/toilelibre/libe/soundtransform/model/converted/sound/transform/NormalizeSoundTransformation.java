package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class NormalizeSoundTransformation implements SoundTransformation {

    public NormalizeSoundTransformation () {
    }

    private Sound normalize (final Sound sound) {
        final long [] data = sound.getSamples ();
        final long [] newdata = new long [sound.getSamples ().length];
        // this is the raw audio data -- no header

        // find the max:
        double max = 0;
        for (final long element : data) {
            if (Math.abs (element) > max) {
                max = Math.abs (element);
            }
        }

        // now find the result, with scaling:
        final double maxValue = Math.pow (256, sound.getNbBytesPerSample ()) - 1;
        final double ratio = maxValue / max;
        for (int i = 0 ; i < data.length ; i++) {
            final double rescaled = data [i] * ratio;
            newdata [i] = (long) Math.floor (rescaled);
        }

        // normalized result in newdata
        return new Sound (newdata, sound.getNbBytesPerSample (), sound.getSampleRate (), sound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        return this.normalize (input);
    }
}
