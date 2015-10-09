package org.toilelibre.libe.soundtransform.model.inputstream.fromsound;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.LogAware;

public interface SoundToInputStreamService<T> extends LogAware<T> {

    InputStream toStream (Sound sound, StreamInfo streamInfo) throws SoundTransformException;

}