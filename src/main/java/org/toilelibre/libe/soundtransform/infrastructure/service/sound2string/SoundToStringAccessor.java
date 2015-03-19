package org.toilelibre.libe.soundtransform.infrastructure.service.sound2string;

import org.toilelibre.libe.soundtransform.infrastructure.service.sound2note.SoundToNoteAccessor;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundToStringHelper;

public abstract class SoundToStringAccessor extends SoundToNoteAccessor {

    protected SoundToStringHelper provideSound2StringHelper () {
        return new GraphSoundToStringHelper ();
    }
}
