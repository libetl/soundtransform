package org.toilelibre.libe.soundtransform.model.library;

import java.util.HashMap;
import java.util.Map;

import org.toilelibre.libe.soundtransform.model.library.pack.AddNoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;
import org.toilelibre.libe.soundtransform.model.library.pack.Range;

public class Library {

	private static Library	packsList	= new Library ();

	public static Library getInstance () {
		return Library.packsList;
	}

	private Library () {

	}

	private Map<String, Pack>	packs	= new HashMap<String, Pack> ();

	public static void addPack (String name, Pack p) {
		Library.getInstance ().packs.put (name, p);
	}

	@SuppressWarnings ("serial")
	public Pack	defaultPack	= new Pack () {
		                        {
			                        this.put ("simple_piano", new Range () {
				                        {
					                        AddNoteService.addNotes (this, "Piano1-C.wav", "Piano3-E.wav", "Piano5-G.wav", "Piano7-B.wav", "Piano2-D.wav", "Piano4-F.wav", "Piano6-A.wav",
					                                "Piano8-C.wav");
				                        }
			                        });
			                        this.put ("g-piano", new Range () {
				                        {
					                        AddNoteService.addNotes (this, "g-piano1.wav", "g-piano2.wav", "g-piano3.wav", "g-piano4.wav", "g-piano5.wav", "g-piano6.wav");
				                        }
			                        });
			                        /*
									 * this.put ("piano_low", new Range () { {
									 * PacksList.this.addNote (this,
									 * "piano_low.wav"); } }); this.put
									 * ("piano_soft", new Range () { {
									 * PacksList.this.addNote (this,
									 * "piano_a.wav"); } });
									 */
			                        this.put ("chord_piano", new Range () {
				                        {
					                        AddNoteService.addNote (this, "g-piano3.wav", 329);
					                        AddNoteService.addNote (this, "g-piano4.wav", 293);
				                        }
			                        });
		                        }
	                        };

}
