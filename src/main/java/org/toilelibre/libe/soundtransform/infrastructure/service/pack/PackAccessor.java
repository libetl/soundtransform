package org.toilelibre.libe.soundtransform.infrastructure.service.pack;

import org.toilelibre.libe.soundtransform.infrastructure.service.freqs.FreqsAccessor;
import org.toilelibre.libe.soundtransform.model.library.note.Pack2StringHelper;
import org.toilelibre.libe.soundtransform.model.library.pack.PackConfigParser;

public abstract class PackAccessor extends FreqsAccessor {

    protected Pack2StringHelper providePack2StringHelper() {
        return new GsonPack2StringHelper();
    }

    protected PackConfigParser providePackConfigParser() {
        return new GsonPackConfigParser();
    }
}
