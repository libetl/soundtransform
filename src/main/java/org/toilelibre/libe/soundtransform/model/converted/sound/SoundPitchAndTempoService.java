package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SoundPitchAndTempoService {

    private final SoundPitchAndTempoHelper helper;

    public SoundPitchAndTempoService (final SoundPitchAndTempoHelper helper1) {
        this.helper = helper1;
    }

    public Sound callTransform (final Sound sound, final float percent, final float lengthInSeconds) throws SoundTransformException {
        return this.helper.pitchAndSetLength (sound, percent, lengthInSeconds);
    }
}
