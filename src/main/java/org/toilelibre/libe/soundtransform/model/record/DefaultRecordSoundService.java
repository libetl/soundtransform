package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;
import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

class DefaultRecordSoundService<T extends Serializable> implements RecordSoundService<T> {

    private final RecordSoundProcessor processor;

    public DefaultRecordSoundService (final RecordSoundProcessor processor1) {
        this.processor = processor1;

    }

    @Override
    public InputStream record (Object stop) throws SoundTransformException {
        return this.processor.record (stop);
    }

}
