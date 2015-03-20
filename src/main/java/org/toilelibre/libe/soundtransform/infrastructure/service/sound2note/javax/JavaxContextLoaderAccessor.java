package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.javax;

import org.toilelibre.libe.soundtransform.infrastructure.service.record.javax.JavaxRecordAccessor;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

public abstract class JavaxContextLoaderAccessor extends JavaxRecordAccessor {

    @Override
    protected ContextLoader provideContextLoader () {
        return new ErrorContextLoader ();
    }
}
