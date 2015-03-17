package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.android;

import org.toilelibre.libe.soundtransform.infrastructure.service.play.android.AndroidPlayAccessor;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;

public abstract class AndroidContextLoaderAccessor extends AndroidPlayAccessor {

    protected ContextLoader provideContextLoader() {
        return new AndroidContextLoader();
    }
}
