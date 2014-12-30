package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;

public class SlowdownSoundTest {

	@Test
	public void testSlowdown () {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
        final File input  = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
	    final File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new SlowdownSoundTransformation (200, 2.5f));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}
}
