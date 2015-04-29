package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;

public interface SoundToInputStreamService<T> extends LogAware<T> {

    public abstract InputStream toStream (Sound sound, StreamInfo streamInfo) throws SoundTransformException;

}