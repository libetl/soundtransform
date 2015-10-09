package org.toilelibre.libe.soundtransform.model.library.pack.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FrequencyHelper {
    float findFrequency (Sound sound) throws SoundTransformException;
}
