package org.toilelibre.libe.soundtransform.model.play;

import org.toilelibre.libe.soundtransform.model.library.pack.note.NoteAccessor;

public abstract class PlayAccessor extends NoteAccessor {

    public PlayAccessor () {
        super ();
        this.usedImpls.put (PlayObjectService.class, DefaultPlayObjectService.class);
    }
}
