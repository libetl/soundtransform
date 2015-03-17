package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class ReverseSoundTransformation implements SoundTransformation {

    public ReverseSoundTransformation() {
    }

    private Sound reverse(final Sound sound) {
        final long[] data = sound.getSamples();
        final long[] newdata = new long[sound.getSamplesLength()];
        // this is the raw audio data -- no header

        for (int i = 0; i < data.length; i++) {
            newdata[i] = data[data.length - i - 1];
        }
        // normalized result in newdata
        return new Sound(newdata, sound.getFormatInfo(), sound.getChannelNum());
    }

    @Override
    public Sound transform(final Sound input) {
        return this.reverse(input);
    }
}
