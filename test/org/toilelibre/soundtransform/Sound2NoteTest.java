package org.toilelibre.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.soundtransform.objects.Note;

public class Sound2NoteTest {
	private ClassLoader	classLoader	= Thread.currentThread ().getContextClassLoader ();
	private File	    input	    = new File (classLoader.getResource ("piano_a.wav").getFile ());

	@Test
	public void run () throws UnsupportedAudioFileException, IOException {

		AudioInputStream ais = AudioFileHelper.getAudioInputStream (input);
		TransformSound ts = new TransformSound ();

		Note n = Sound2Note.convert (ts.fromInputStream (ais));

	}

}
