package org.toilelibre.libe.soundtransform.model.inputstream.convert;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface ConvertProcessor {
    <T> InputStream convertToWavStream (final ConverterLauncher<T> launcher, final InputStream inputStream, final String fileName) throws SoundTransformException;
}
