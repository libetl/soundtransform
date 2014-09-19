package org.toilelibre.libe.soundtransform.objects;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.toilelibre.libe.soundtransform.AudioFileHelper;
import org.toilelibre.libe.soundtransform.TransformSound;
import org.toilelibre.libe.soundtransform.pda.Sound2Note;

public class PacksList {

	private static PacksList	packsList	= new PacksList ();

	public static PacksList getInstance () {
		return PacksList.packsList;
	}

	private PacksList () {

	}

	private ClassLoader	classLoader	= Thread.currentThread ().getContextClassLoader ();
	TransformSound	    ts	        = new TransformSound ();
	@SuppressWarnings ("serial")
	public Pack	        defaultPack	= new Pack () {
		                                {
			                                this.put ("simple_piano", new Range () {
				                                {
					                                PacksList.this.addNotes (this, "Piano1-C.wav", "Piano3-E.wav", "Piano5-G.wav", "Piano7-B.wav", "Piano2-D.wav", "Piano4-F.wav", "Piano6-A.wav",
					                                        "Piano8-C.wav");
				                                }
			                                });
			                                this.put ("g-piano", new Range () {
				                                {
					                                PacksList.this.addNotes (this, "g-piano1.wav", "g-piano2.wav", "g-piano3.wav", "g-piano4.wav", "g-piano5.wav", "g-piano6.wav");
				                                }
			                                });
			                                this.put ("piano_low", new Range () {
				                                {
					                                PacksList.this.addNote (this, "piano_low.wav");
				                                }
			                                });
			                                this.put ("piano_soft", new Range () {
				                                {
					                                PacksList.this.addNote (this, "piano_a.wav");
				                                }
			                                });
			                                this.put ("a-piano", new Range () {
				                                {
					                                PacksList.this.addNotes (this, "a-piano3.wav", "a-piano4.wav");
				                                }
			                                });
		                                }
	                                };

	private void addNotes (Range range, String... fileNames) {
		for (String fileName : fileNames) {
			this.addNote (range, fileName);
		}
	}

	private void addNote (Range range, String fileName) {
		try {
			java.net.URL completeURL = classLoader.getResource ("notes/" + fileName);
			if (completeURL == null) {
				System.err.println (fileName + " not found");
				return;
			}
			String completeFileName = completeURL.getFile ();
			File file = new File (completeFileName);
			Note n = Sound2Note.convert (ts.fromInputStream (AudioFileHelper.getAudioInputStream (file)));
			range.put (n.getFrequency (), n);
		} catch (UnsupportedAudioFileException e) {
		} catch (IllegalArgumentException e) {
			System.err.println (fileName + " could not be parsed as an ADSR note");
		} catch (IOException e) {
		}

	}
}
