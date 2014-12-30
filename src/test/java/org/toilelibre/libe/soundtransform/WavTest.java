package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.observer.PrintlnTransformObserver;
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
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.library.Library;

public class WavTest {

	private final ClassLoader	classLoader	= Thread.currentThread ().getContextClassLoader ();
	private final File	    input	    = new File (this.classLoader.getResource ("before.wav").getFile ());
	// private File input = new File ("D:/Mes Soirées 80's-Spécial Discothèques/CD 1/08 Captain Sensible-Wot.mp3");
	private final File	    output	    = new File (new File (this.classLoader.getResource ("before.wav").getFile ()).getParent () + "/after.wav");

	@Test
	public void test8bits () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new EightBitsSoundTransformation (25));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testFreqNoOp () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new SimpleFrequencySoundTransformation ());
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testLinearReg () {
		// will remove the high freqs and smooth the signal
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new LinearRegressionSoundTransformation (25));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testNoOp () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new NoOpSoundTransformation ());
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testNormalize () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new NormalizeSoundTransformation ());
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testPitch () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new PitchSoundTransformation (100));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	//@Test
	public void testPurify () {
		// WARN : quite long
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new PurifySoundTransformation ());
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testRemoveLowFreqs () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new EqualizerSoundTransformation (new double [] { 0, 2000, 4000, 6000, 8000, 10000, 12000, 14000,
			        16000, 18000, 22050 }, new double [] { 0, 0, 0.1, 0.3, 0.7, 1, 1, 1, 1, 1, 1 }));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testReverse () {
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new ReverseSoundTransformation ());
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	@Test
	public void testShape () {
		// WARN : quite long
		try {
			System.out.println ("Loading packs");
			final Library packsList = Library.getInstance ();

			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new ShapeSoundTransformation (packsList.defaultPack, "simple_piano"));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	//@Test
	public void testSlowdown () {
		// WARN : quite long
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new SlowdownSoundTransformation (200, 1.2f));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}

	//@Test
	public void testSpeedUp () {
		// WARN : quite long
		try {
			new TransformSoundService (new PrintlnTransformObserver ()).transformFile (this.input, this.output, new SpeedUpSoundTransformation (200, 1.5f));
		} catch (final UnsupportedAudioFileException e) {
			e.printStackTrace ();
		} catch (final IOException e) {
			e.printStackTrace ();
		}
	}
}
