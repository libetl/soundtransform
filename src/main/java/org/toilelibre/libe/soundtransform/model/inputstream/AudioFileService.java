package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;

public interface AudioFileService<T> extends LogAware<T> {

    InputStream streamFromFile (File file) throws SoundTransformException;

    InputStream streamFromRawStream (InputStream is, StreamInfo streamInfo) throws SoundTransformException;

    void fileFromStream (InputStream ais2, File fDest) throws SoundTransformException;

    InputStream streamFromInputStream (InputStream is) throws SoundTransformException;

}