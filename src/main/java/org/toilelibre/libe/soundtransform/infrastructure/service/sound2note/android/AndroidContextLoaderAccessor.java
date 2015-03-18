package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.android;

import org.toilelibre.libe.soundtransform.infrastructure.service.play.android.AndroidPlayAccessor;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

public abstract class AndroidContextLoaderAccessor extends AndroidPlayAccessor {

    @Override
    protected ContextLoader provideContextLoader () {
        return new AndroidContextLoader ();
    }
}
