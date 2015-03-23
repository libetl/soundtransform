package org.toilelibre.libe.soundtransform.model.library.pack;

import org.toilelibre.libe.soundtransform.model.library.note.NoteAccessor;

public abstract class PackAccessor extends NoteAccessor {

    public PackAccessor () {
        super ();
        this.usedImpls.put (AddNoteService.class, DefaultAddNoteService.class);
        this.usedImpls.put (ImportPackService.class, DefaultImportPackService.class);
    }
}
