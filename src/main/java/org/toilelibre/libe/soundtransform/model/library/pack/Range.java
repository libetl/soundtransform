package org.toilelibre.libe.soundtransform.model.library.pack;

import java.util.HashMap;

import org.toilelibre.libe.soundtransform.model.library.note.Note;

public class Range extends HashMap<Integer, Note> {

    /**
     *
     */
    private static final long serialVersionUID = 6526477231719258055L;

    public Note getNearestNote (final int frequency) {
        int nearest = Integer.MIN_VALUE;
        for (final Integer i : this.keySet ()) {
            if (Math.abs (frequency - i.intValue ()) < Math.abs (frequency - nearest)) {
                nearest = i.intValue ();
            }
        }
        return this.get (new Integer (nearest));
    }
}
