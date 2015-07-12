package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public interface RunnableWithInputStream extends Runnable {

    <T> T runWithInputStreamAndGetResult (InputStream inputStream, StreamInfo streamInfo, Class<T> returnType) throws SoundTransformException;

}
