package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.TransformSound;
import org.toilelibre.libe.soundtransform.objects.PacksList;
import org.toilelibre.libe.soundtransform.observer.PrintlnTransformObserver;
import org.toilelibre.libe.soundtransform.transforms.EightBitsSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.EqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.LinearRegressionSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.NoOpFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.NoOpSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.NormalizeSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.PurifySoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.ReverseSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.SpeedUpSoundTransformation;

public class WavTest {

	private ClassLoader	classLoader	= Thread.currentThread ().getContextClassLoader ();
	private File	    input	    = new File (classLoader.getResource ("before.wav").getFile ());
	// private File input = new File ("D:/Mes Soirées 80's-Spécial Discothèques/CD 1/08 Captain Sensible-Wot.mp3");
	private File	    output	    = new File (new File (classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

	@Test
	public void test8bits () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new EightBitsSoundTransformation (25));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testRemoveLowFreqs () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new EqualizerSoundTransformation (new double [] { 0, 2000, 4000, 6000, 8000, 10000, 12000, 14000, 16000,
			        18000, 22050 }, new double [] { 0, 0, 0.1, 0.3, 0.7, 1, 1, 1, 1, 1, 1 }));
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
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new LinearRegressionSoundTransformation (25));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testReverse () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new ReverseSoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testPitch () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new PitchSoundTransformation (85));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testNormalize () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new NormalizeSoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testFreqNoOp () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new NoOpFrequencySoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testNoOp () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new NoOpSoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testSlowdown () {
		// WARN : quite long
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new SlowdownSoundTransformation (200, 1.2f));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testSpeedUp () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new SpeedUpSoundTransformation (200, 1.5f));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testPurify () {
		try {
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, new PurifySoundTransformation ());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testShape () {
		try {
			System.out.println("Loading packs");
			PacksList packsList = PacksList.getInstance ();
			new TransformSound (new PrintlnTransformObserver ()).transformFile (input, output, 
					new EqualizerSoundTransformation(
							new double [] {0, 20, 50, 100, 120, 140,  160,  180,  200,  220, 1000, 2000,  5000,  11000, 22050}, 
							new double [] {1,  1,  1,   1,   1,   1,    1, 0.70, 0.30, 0.10, 0.10,  0.10,  0.10,  0.10,  0.10}),
					new ShapeSoundTransformation (packsList.defaultPack, "piano_low"));
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
