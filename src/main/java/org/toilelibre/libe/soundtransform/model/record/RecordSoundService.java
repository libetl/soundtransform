package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public interface RecordSoundService<T extends Serializable> {

    public abstract InputStream recordRawInputStream (StreamInfo streamInfo, Object stop) throws SoundTransformException;

    public abstract InputStream recordLimitedTimeRawInputStream (StreamInfo streamInfo) throws SoundTransformException;

}