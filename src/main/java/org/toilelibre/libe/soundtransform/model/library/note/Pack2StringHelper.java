package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.library.pack.Range;

public interface Pack2StringHelper {

    String toString (Range range);

    String toString (Note note);

    String toString (Pack pack);
}
