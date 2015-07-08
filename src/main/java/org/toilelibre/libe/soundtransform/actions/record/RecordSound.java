package org.toilelibre.libe.soundtransform.actions.record;

import java.io.InputStream;
import java.util.List;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class RecordSound extends Action {

    public InputStream recordRawInputStream (final StreamInfo streamInfo, final Object stop) throws SoundTransformException {
        return this.recordSound.recordRawInputStream (streamInfo, stop);
    }

    public InputStream recordLimitedTimeRawInputStream (final StreamInfo streamInfo) throws SoundTransformException {
        return this.recordSound.recordLimitedTimeRawInputStream (streamInfo);
    }
    
    public <T> List<T> recordAndProcess (final StreamInfo streamInfo, final Object stop, final FluentClientOperation operation, final Class<T> returnType) throws SoundTransformException {
        return this.recordSound.<T>recordAndProcess (streamInfo, stop, operation, returnType);
    }
}
