package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.android;

import org.toilelibre.libe.soundtransform.infrastructure.service.record.android.AndroidRecordAccessor;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

public abstract class AndroidContextLoaderAccessor extends AndroidRecordAccessor {

    @Override
    protected ContextLoader provideContextLoader () {
        return new AndroidContextLoader ();
    }
}
