package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.AudioFileHelper;
import org.toilelibre.libe.soundtransform.Sound2Note;
import org.toilelibre.libe.soundtransform.TransformSound;
import org.toilelibre.libe.soundtransform.objects.Note;

public class Sound2NoteTest {
    
	
	@Test
	public void run () throws UnsupportedAudioFileException, IOException {
	    ClassLoader classLoader = Sound2NoteTest.class.getClassLoader();
	    URL fileURL = classLoader.getResource ("piano_low.wav");
	    File        input       = new File (fileURL.getFile ());

		AudioInputStream ais = AudioFileHelper.getAudioInputStream (input);
		TransformSound ts = new TransformSound ();

		Note n = Sound2Note.convert (ts.fromInputStream (ais));
		System.out.println(n);

	}

}
