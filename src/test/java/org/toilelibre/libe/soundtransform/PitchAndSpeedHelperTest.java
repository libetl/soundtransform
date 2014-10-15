package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.library.note.Note;
import org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService;

public class PitchAndSpeedHelperTest {
	
	@Test
	public void shouldBeTwiceTheF0ValuePiano3e () throws UnsupportedAudioFileException, IOException {
		ClassLoader classLoader = Sound2NoteTest.class.getClassLoader ();
		URL fileURL = classLoader.getResource ("notes/Piano3-E.wav");
		File input = new File (fileURL.getFile ());

		AudioInputStream ais = new ConvertAudioFileService ().callConverter (input);
		TransformSoundService ts = new TransformSoundService ();

		Sound [] e3 = ts.fromInputStream (ais);
		SoundPitchAndTempoHelper helper = new ConvertedSoundPitchAndTempoHelper ();
		Sound [] e4 = new Sound [2];
		e4 [0] = helper.pitchAndSetLength (e3 [0], 200, 10000);
		e4 [1] = helper.pitchAndSetLength (e3 [1], 200, 10000);
		
		AudioInputStream ais2 = ts.toStream (e4, ais.getFormat ());
		File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

		try {
			AudioSystem.write (ais2, AudioFileFormat.Type.WAVE, fDest);
		} catch (IOException e) {
		}
		Note n = Sound2NoteService.convert ("e4", e4);
		System.out.println ("e' 4 : " + n.getFrequency () + "Hz, should be around 658Hz");
		org.junit.Assert.assertTrue (n.getFrequency () > 658 - 10 && n.getFrequency () < 658 + 10);
	}
}
