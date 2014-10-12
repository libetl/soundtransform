package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.LinearRegressionSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.NormalizeSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.PurifySoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SpeedUpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.NoOpFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;

public class WavTest {

	private ClassLoader	classLoader	= Thread.currentThread ().getContextClassLoader ();
	private File	    input	    = new File (classLoader.getResource ("before.wav").getFile ());
	// private File input = new File ("D:/Mes Soirées 80's-Spécial Discothèques/CD 1/08 Captain Sensible-Wot.mp3");
	private File	    output	    = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

	@Test
	public void test8bits () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new EightBitsSoundTransformation (25));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testRemoveLowFreqs () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new EqualizerSoundTransformation (new double [] { 0, 2000, 4000, 6000, 8000, 10000, 12000, 14000,
			        16000, 18000, 22050 }, new double [] { 0, 0, 0.1, 0.3, 0.7, 1, 1, 1, 1, 1, 1 }));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testLinearReg () {
		// will remove the high freqs and smooth the signal
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new LinearRegressionSoundTransformation (25));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testReverse () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new ReverseSoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testPitch () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new PitchSoundTransformation (100));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testNormalize () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new NormalizeSoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testFreqNoOp () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new NoOpFrequencySoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testNoOp () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new NoOpSoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	//@Test
	public void testSlowdown () {
		// WARN : quite long
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new SlowdownSoundTransformation (200, 1.2f));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	//@Test
	public void testSpeedUp () {
		// WARN : quite long
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new SpeedUpSoundTransformation (200, 1.5f));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	//@Test
	public void testPurify () {
		// WARN : quite long
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new PurifySoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	//@Test
	public void testShape () {
		// WARN : quite long
		try {
			System.out.println ("Loading packs");
			Library packsList = Library.getInstance ();

			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (input, output, new EqualizerSoundTransformation (new double [] { 0, 20, 50, 100, 120, 140, 160, 180, 200, 220,
			        1000, 2000, 5000, 11000, 22050 }, new double [] { 0, 0.5, 0.75, 1, 1, 1, 1, 0.70, 0.30, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00 }), new NormalizeSoundTransformation (),
			        new ShapeSoundTransformation (packsList.defaultPack, "simple_piano"));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
