package org.toilelibre.libe.soundtransform.model.inputstream.convert;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.logging.LogAware;

@Service
public interface FormatConvertService<T> extends LogAware<T> {

    InputStream convertToWav (InputStream compressedInputStream, String fileName) throws SoundTransformException;

}