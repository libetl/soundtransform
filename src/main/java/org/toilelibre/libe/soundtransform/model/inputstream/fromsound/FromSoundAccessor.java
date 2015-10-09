package org.toilelibre.libe.soundtransform.model.inputstream.fromsound;

import org.toilelibre.libe.soundtransform.model.inputstream.format.FormatAccessor;

public abstract class FromSoundAccessor extends FormatAccessor {

    public FromSoundAccessor () {
        super ();
        this.usedImpls.put (SoundToInputStreamService.class, DefaultSoundToInputStreamService.class);
    }
}
