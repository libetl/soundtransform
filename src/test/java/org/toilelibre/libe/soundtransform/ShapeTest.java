package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.format.AudioFileHelper;
import org.toilelibre.libe.soundtransform.objects.PacksList;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.pda.Sound2Note;
import org.toilelibre.libe.soundtransform.sound.SoundAppender;
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

			int frequency = Sound2Note.convert ("output chord_note", new TransformSound (new PrintlnTransformObserver ()).fromInputStream (AudioFileHelper.getAudioInputStream (output)))
			        .getFrequency ();
			System.out.println ("Output chord note should be around 293Hz, but is " + frequency + "Hz");
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testShapeASimplePianoNoteAsAChordNoteSameFrequency () {

		try {
			System.out.println ("Loading packs");
			PacksList packsList = PacksList.getInstance ();
			ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
			File input = new File (classLoader.getResource ("notes/Piano3-E.wav").getFile ());
			File output = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");
			AudioInputStream outputStream = new TransformSound (new PrintlnTransformObserver ()).transformAudioStream (AudioFileHelper.getAudioInputStream (input), new ShapeSoundTransformation (
			        packsList.defaultPack, "chord_piano"));

			AudioSystem.write (outputStream, AudioFileFormat.Type.WAVE, output);

			int frequency = Sound2Note.convert ("output chord_note", new TransformSound (new PrintlnTransformObserver ()).fromInputStream (AudioFileHelper.getAudioInputStream (output)))
			        .getFrequency ();
			System.out.println ("Output chord note should be around 332Hz, but is " + frequency + "Hz");
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testAppendSoundsWithDifferentNbBytes () throws IOException, UnsupportedAudioFileException {
		ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
		File input1 = new File (classLoader.getResource ("notes/Piano2-D.wav").getFile ());
		File input2 = new File (classLoader.getResource ("notes/g-piano3.wav").getFile ());
		Sound [] s1 = new TransformSound ().fromInputStream (AudioFileHelper.getAudioInputStream (input1));
		Sound [] s2 = new TransformSound ().fromInputStream (AudioFileHelper.getAudioInputStream (input2));
		SoundAppender.append (s2 [0], 1000, s1 [0]);
	}
}
