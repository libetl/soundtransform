package org.toilelibre.libe.soundtransform.model.record;

import org.toilelibre.libe.soundtransform.model.play.PlayAccessor;

public abstract class RecordAccessor extends PlayAccessor {

    public RecordAccessor () {
        super ();
        this.usedImpls.put (RecordSoundService.class, DefaultRecordSoundService.class);
    }
}
