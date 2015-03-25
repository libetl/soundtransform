package org.toilelibre.libe.soundtransform.model.library.pack;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface ContextLoader {

    InputStream read (Object context, int id) throws SoundTransformException;

    InputStream read (Object context, Class<?> rClass, String idName) throws SoundTransformException;
}
