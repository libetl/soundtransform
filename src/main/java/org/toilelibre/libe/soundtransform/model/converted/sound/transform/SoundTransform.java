package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface SoundTransform<T, U> {

    U transform (T input) throws SoundTransformException;
}
