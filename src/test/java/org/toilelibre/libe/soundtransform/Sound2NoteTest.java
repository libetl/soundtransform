package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;
import org.toilelibre.libe.soundtransform.model.library.pack.Pack;

public class Sound2NoteTest {

	@Test
	public void run () throws UnsupportedAudioFileException, IOException {
		@SuppressWarnings ("serial")
		Map<String, Integer> frequenciesPerSound = new HashMap<String, Integer> () {
			{
				this.put ("Piano1-C.wav", 260);// OK
				this.put ("Piano2-D.wav", 293);// OK
				this.put ("Piano3-E.wav", 332);// OK
				this.put ("Piano4-F.wav", 344);// OK
				this.put ("Piano5-G.wav", 387);// OK
				this.put ("Piano6-A.wav", 451);// OK
				this.put ("Piano7-B.wav", 499);// OK
				this.put ("Piano8-C.wav", 524);// OK
			}
		};
		System.out.println ("Loading Packs");
		Pack pack = Library.getInstance ().defaultPack;
		for (String instrument : pack.keySet ()) {
			for (Integer noteKey : pack.get (instrument).keySet ()) {
				Note n = pack.get (instrument).get (noteKey);
				if (frequenciesPerSound.get (n.getName ()) != null) {
					org.junit.Assert.assertEquals (frequenciesPerSound.get (n.getName ()).intValue (), n.getFrequency ());
					System.out.println ("f0 (" + n.getName () + ") = " + n.getFrequency ());
				} else {
					System.out.println ("Did not find " + n.getName ());
				}
			}
		}
	}

	@Test
	public void shouldNotBeTwiceTheF0ValuePiano1C () throws UnsupportedAudioFileException, IOException {
		ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
		URL fileURL = classLoader.getResource ("notes/Piano1-C.wav");
		File input = new File (fileURL.getFile ());

		AudioInputStream ais = new ConvertAudioFileService ().callConverter (input);
		TransformSoundService ts = new TransformSoundService ();

		Note n = Sound2NoteService.convert ("Piano1-C.wav", ts.fromInputStream (ais));
		System.out.println ("c' 1-line octave : " + n.getFrequency () + "Hz, should be around 261Hz");
		org.junit.Assert.assertTrue (n.getFrequency () > 261 - 10 && n.getFrequency () < 261 + 10);
	}

	@Test
	public void shouldNotBeTwiceTheF0ValuePiano4F () throws UnsupportedAudioFileException, IOException {
		ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
		URL fileURL = classLoader.getResource ("notes/Piano4-F.wav");
		File input = new File (fileURL.getFile ());

		AudioInputStream ais = new ConvertAudioFileService ().callConverter (input);
		TransformSoundService ts = new TransformSoundService ();

		Note n = Sound2NoteService.convert ("Piano4-F.wav", ts.fromInputStream (ais));
		System.out.println ("f' 4 : " + n.getFrequency () + "Hz, should be around 349Hz");
		org.junit.Assert.assertTrue (n.getFrequency () > 349 - 10 && n.getFrequency () < 349 + 10);
	}

	@Test
	public void shouldBeTwiceTheF0ValuePiano4F () throws UnsupportedAudioFileException, IOException {
		ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
		URL fileURL = classLoader.getResource ("notes/Piano3-E.wav");
		File input = new File (fileURL.getFile ());

		AudioInputStream ais = new ConvertAudioFileService ().callConverter (input);
		TransformSoundService ts = new TransformSoundService ();

		Sound [] f4 = ts.fromInputStream (ais);
		PitchSoundTransformation pitcher = new PitchSoundTransformation (200);
		Sound f51 = pitcher.transform (f4 [0]);
		Sound f52 = pitcher.transform (f4 [1]);
		
		Note n = Sound2NoteService.convert ("Piano4-E.wav", new Sound [] {f51, f52});
		System.out.println ("e' 4 : " + n.getFrequency () + "Hz, should be around 664Hz");
		org.junit.Assert.assertTrue (n.getFrequency () > 664 - 10 && n.getFrequency () < 664 + 10);
	}

	@Test
	public void shouldRecognizeAPure440Note () {
		int length = 10000;

		int samplerate = 44100;
		long [] signal = new long [length];
		for (int j = 0; j < length; j++) {
			signal [j] = (long) (Math.sin (j * 440 * 2 * Math.PI / samplerate) * 32768.0);
		}
		Sound s = new Sound (signal, 2, samplerate, 1);
		Note n = Sound2NoteService.convert ("Sample A4 (440 Hz) Sound", new Sound [] { s });

		System.out.println ("Sample A4 (440Hz) Sound, but frequency found was " + n.getFrequency () + "Hz");
		org.junit.Assert.assertTrue (n.getFrequency () > 440 - 10 && n.getFrequency () < 440 + 10);
		System.out.println ("...acceptable");
	}

	@Test
	public void shouldRecognizeSimpleNotes () {
		int length = 2000;
		int [] notes = new int [] { 261, 293, 329, 349, 392, 440, 493 };
		String [] notesTitle = new String [] { "C4", "D4", "E4", "F4", "G4", "A4", "B4" };

		for (int i = 0; i < notes.length; i++) {
			int samplerate = 11025;
			long [] signal = new long [length];
			for (int j = 0; j < length; j++) {
				signal [j] = (long) (Math.sin (j * notes [i] * 2 * Math.PI / samplerate) * 32768.0);
			}
			Sound s = new Sound (signal, 2, samplerate, 1);
			Note n = Sound2NoteService.convert ("Sample " + notesTitle [i] + "(" + notes [i] + "Hz) Sound", new Sound [] { s });

			System.out.println ("Sample " + notesTitle [i] + "(" + notes [i] + "Hz) Sound, but frequency found was " + n.getFrequency () + "Hz");
			org.junit.Assert.assertTrue (n.getFrequency () > notes [i] - 10 && n.getFrequency () < notes [i] + 10);
			System.out.println ("...acceptable");
		}
	}

}
