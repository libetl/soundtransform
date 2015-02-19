package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SubSoundExtractSoundTransformation implements SoundTransformation {

    private final int start;
    private final int end;

    public SubSoundExtractSoundTransformation (int start1, int end1) {
        this.start = start1;
        this.end = end1;
    }

    @Override
    public Sound transform (final Sound input) throws SoundTransformException {
        final Sound result = new Sound (new long [this.end - this.start], input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());

        for (int i = this.start ; i < this.end ; i++) {
            result.getSamples () [i - this.start] = input.getSamples () [i];
        }
        return result;
    }

}
