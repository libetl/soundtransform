package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;

public class SlowdownSoundTest {

	@Test
	public void testSlowdown () {
        ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        File input  = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
	    File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new SlowdownSoundTransformation (200, 2.5f));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
