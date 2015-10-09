package org.toilelibre.libe.soundtransform.infrastructure.service.pack;

import org.toilelibre.libe.soundtransform.infrastructure.service.freqs.FreqsAccessor;
import org.toilelibre.libe.soundtransform.model.library.pack.PackConfigParser;
import org.toilelibre.libe.soundtransform.model.library.pack.PackToStringHelper;

public abstract class PackAccessor extends FreqsAccessor {

    protected PackToStringHelper providePack2StringHelper () {
        return new GsonPackToStringHelper ();
    }

    protected PackConfigParser providePackConfigParser () {
        return new GsonPackConfigParser ();
    }
}
