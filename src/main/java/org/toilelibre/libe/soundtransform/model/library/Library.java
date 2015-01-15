package org.toilelibre.libe.soundtransform.model.library;

import java.util.HashMap;
import java.util.Map;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformRuntimeException;
import org.toilelibre.libe.soundtransform.model.library.pack.AddNoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.library.pack.Range;

@SuppressWarnings ("serial")
public class Library {

    public static void addPack (final String name, final Pack p) {
        Library.packs.put (name, p);
    }

    public static Library getInstance () {
        return Library.packsList;
    }

    private static final Library           packsList = new Library ();

    private static final Map<String, Pack> packs     = new HashMap<String, Pack> ();

    public static Pack                     defaultPack;

    static {
        try {
            Library.defaultPack = new Pack () {
                /**
                 *
                 */
                private static final long serialVersionUID = -5458561559584883411L;

                {
                    this.put ("simple_piano", new Range () {
                        {
                            $.create (AddNoteService.class).addNotes (this, "Piano1-C.wav", "Piano3-E.wav", "Piano5-G.wav", "Piano7-B.wav", "Piano2-D.wav", "Piano4-F.wav", "Piano6-A.wav", "Piano8-C.wav");
                        }
                    });
                    this.put ("g-piano", new Range () {
                        {
                            $.create (AddNoteService.class).addNotes (this, "g-piano1.wav", "g-piano2.wav", "g-piano3.wav", "g-piano4.wav", "g-piano5.wav", "g-piano6.wav");
                        }
                    });
                    this.put ("chord_piano", new Range () {
                        {
                            $.create (AddNoteService.class).addNote (this, "g-piano3.wav", 329);
                            $.create (AddNoteService.class).addNote (this, "g-piano4.wav", 293);
                        }
                    });
                }
            };
        } catch (final SoundTransformException e) {
            throw new SoundTransformRuntimeException (e);
        }
    }

    private Library () {

    }

}
