package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.objects.PacksList;
import org.toilelibre.libe.soundtransform.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.transforms.ShapeSoundTransformation;

public class ShapeTest {

	@Test
	public void testShapeASimplePianoNoteAsAChordNote () {

		try {
			System.out.println ("Loading packs");
			PacksList packsList = PacksList.getInstance ();
			ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
			File input = new File (classLoader.getResource ("notes/Piano2-D.wav").getFile ());
			File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
			AudioInputStream outputStream = new TransformSound (new PrintlnTransformObserver ()).transformAudioStream (AudioFileHelper.getAudioInputStream (input), new ShapeSoundTransformation (
			        packsList.defaultPack, "chord_piano"));

			AudioSystem.write (outputStream, AudioFileFormat.Type.WAVE, output);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
