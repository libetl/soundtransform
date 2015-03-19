package org.toilelibre.libe.soundtransform.model.inputstream;

import java.io.File;
import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;

public interface AudioFileService<T> extends LogAware<T> {

    public abstract InputStream streamFromFile(File file) throws SoundTransformException;

    public abstract InputStream streamFromRawStream(InputStream is, StreamInfo streamInfo) throws SoundTransformException;

    public abstract void fileFromStream(InputStream ais2, File fDest) throws SoundTransformException;

}