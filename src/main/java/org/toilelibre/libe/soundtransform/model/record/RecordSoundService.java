package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;
import java.util.List;

import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.observer.LogAware;

public interface RecordSoundService<T> extends LogAware<T> {

    public abstract InputStream recordRawInputStream (StreamInfo streamInfo, Object stop) throws SoundTransformException;

    public abstract InputStream recordLimitedTimeRawInputStream (StreamInfo streamInfo) throws SoundTransformException;

    public abstract <O> List<O> recordAndProcess (StreamInfo streamInfo, Object stop, FluentClientOperation operation, Class<O> returnType) throws SoundTransformException;

}