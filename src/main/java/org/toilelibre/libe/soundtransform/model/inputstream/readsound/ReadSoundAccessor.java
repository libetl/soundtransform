package org.toilelibre.libe.soundtransform.model.inputstream.readsound;

import org.toilelibre.libe.soundtransform.model.inputstream.fromsound.FromSoundAccessor;

public abstract class ReadSoundAccessor extends FromSoundAccessor {

    public ReadSoundAccessor () {
        super ();
        this.usedImpls.put (InputStreamToSoundService.class, DefaultInputStreamToSoundService.class);
    }
}
