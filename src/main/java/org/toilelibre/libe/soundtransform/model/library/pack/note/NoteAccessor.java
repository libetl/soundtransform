package org.toilelibre.libe.soundtransform.model.library.pack.note;

import org.toilelibre.libe.soundtransform.model.library.pack.PackAccessor;

public abstract class NoteAccessor extends PackAccessor {

    public NoteAccessor () {
        super ();
        this.usedImpls.put (SoundToNoteService.class, DefaultSoundToNoteService.class);
    }
}
