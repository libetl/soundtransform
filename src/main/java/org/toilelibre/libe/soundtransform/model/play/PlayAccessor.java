package org.toilelibre.libe.soundtransform.model.play;

import org.toilelibre.libe.soundtransform.model.library.pack.PackAccessor;

public abstract class PlayAccessor extends PackAccessor {

    public PlayAccessor () {
        super ();
        this.usedImpls.put (PlaySoundService.class, DefaultPlaySoundService.class);
    }
}
