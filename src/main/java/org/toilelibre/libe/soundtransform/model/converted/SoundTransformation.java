package org.toilelibre.libe.soundtransform.model.converted;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface SoundTransformation {

    Sound transform(Sound input) throws SoundTransformException;
}
