package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.javax;

import org.toilelibre.libe.soundtransform.infrastructure.service.play.javax.JavaxPlayAccessor;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

public abstract class JavaxContextLoaderAccessor extends JavaxPlayAccessor {

    @Override
    protected ContextLoader provideContextLoader () {
        return new ErrorContextLoader ();
    }
}
