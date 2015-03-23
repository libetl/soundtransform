package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public interface InputStreamToByteArrayHelper {

    byte [] convertToByteArray (InputStream is) throws SoundTransformException;

}
