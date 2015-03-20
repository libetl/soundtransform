package org.toilelibre.libe.soundtransform.infrastructure.service.record.android;

import org.toilelibre.libe.soundtransform.infrastructure.service.play.android.AndroidPlayAccessor;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;

public abstract class AndroidRecordAccessor extends AndroidPlayAccessor {

    @Override
    protected RecordSoundProcessor provideRecordSoundProcessor () {
        return new AndroidRecordSoundProcessor ();
    }
}
