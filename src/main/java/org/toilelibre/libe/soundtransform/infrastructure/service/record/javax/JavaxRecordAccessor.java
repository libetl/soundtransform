package org.toilelibre.libe.soundtransform.infrastructure.service.record.javax;

import org.toilelibre.libe.soundtransform.infrastructure.service.play.javax.JavaxPlayAccessor;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

public abstract class JavaxRecordAccessor extends JavaxPlayAccessor {

    @Override
    protected RecordSoundProcessor provideRecordSoundProcessor () {
        return new LineListenerRecordSoundProcessor ();
    }
}
