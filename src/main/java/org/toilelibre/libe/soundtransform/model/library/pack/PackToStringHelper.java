package org.toilelibre.libe.soundtransform.model.library.pack;

import org.toilelibre.libe.soundtransform.model.library.pack.note.Note;

public interface PackToStringHelper {

    String toString (Range range);

    String toString (Note note);

    String toString (Pack pack);
}
