package org.toilelibre.libe.soundtransform.model.library.pack;

import org.toilelibre.libe.soundtransform.model.inputstream.readsound.ReadSoundAccessor;

public abstract class PackAccessor extends ReadSoundAccessor {

    public PackAccessor () {
        super ();
        this.usedImpls.put (AddNoteService.class, DefaultAddNoteService.class);
        this.usedImpls.put (ImportPackService.class, DefaultImportPackService.class);
    }
}
