package org.toilelibre.libe.soundtransform.model.library.pack;

import java.util.HashMap;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.PackToStringHelper;

public class Range extends HashMap<Float, Note> {

    /**
     *
     */
    private static final long serialVersionUID = 6526477231719258055L;

    public Note getNearestNote (final int frequency) {
        float nearest = Integer.MIN_VALUE;
        for (final Float i : this.keySet ()) {
            if (Math.abs (frequency - i) < Math.abs (frequency - nearest)) {
                nearest = i;
            }
        }
        return this.get (new Float (nearest));
    }

    @Override
    public String toString () {
        return $.select (PackToStringHelper.class).toString (this);
    }
}
