package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface FrequencyHelper {
    float findFrequency (Channel [] sound) throws SoundTransformException;
}
