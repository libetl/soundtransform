package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;

public class SoundToStringTest {

	@Test
	public void testToString () {
		ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
		File input = new File (classLoader.getResource ("before.wav").getFile ());
		try {
			AudioInputStream ais = new ConvertAudioFileService ().callConverter (input);
			System.out.println (new TransformSoundService ().fromInputStream (ais) [0]);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
