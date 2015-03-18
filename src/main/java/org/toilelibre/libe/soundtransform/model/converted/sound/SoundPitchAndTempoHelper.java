package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface SoundPitchAndTempoHelper {

    Sound pitchAndSetLength (Sound sound, float percent, float lengthInSeconds) throws SoundTransformException;
}
