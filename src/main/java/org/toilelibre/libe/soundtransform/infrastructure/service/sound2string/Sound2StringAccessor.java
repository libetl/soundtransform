package org.toilelibre.libe.soundtransform.infrastructure.service.sound2string;

import org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.Sound2NoteAccessor;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound2StringHelper;

public abstract class Sound2StringAccessor extends Sound2NoteAccessor {

    protected Sound2StringHelper provideSound2StringHelper() {
        return new GraphSound2StringHelper();
    }
}
