package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamAccessor;

public abstract class NoteAccessor extends InputStreamAccessor {

    public NoteAccessor (){
        super ();
        this.usedImpls.put(SoundToNoteService.class, DefaultSoundToNoteService.class);
    }
}
