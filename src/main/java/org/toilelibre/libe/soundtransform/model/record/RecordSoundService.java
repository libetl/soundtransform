package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface RecordSoundService<T extends Serializable> {

    public abstract InputStream record(Object stop) throws SoundTransformException;

}