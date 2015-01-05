package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;

public class SoundToStringTest {

	@Test
	public void testFsToString () throws SoundTransformException {
		final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
		final File input = new File (classLoader.getResource ("before.wav").getFile ());

		final InputStream ais = new ConvertAudioFileService ().callConverter (input);
		final Sound s = new TransformSoundService ().fromInputStream (ais) [0];
		new SimpleFrequencySoundTransformation () {

			@Override
			public Spectrum transformFrequencies (final Spectrum fs) {
				System.out.println (fs);
				return super.transformFrequencies (fs);
			}

		}.transform (s);

	}

	@Test
	public void testToString () throws SoundTransformException {
		final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
		final File input = new File (classLoader.getResource ("before.wav").getFile ());

		final InputStream ais = new ConvertAudioFileService ().callConverter (input);
		System.out.println (new TransformSoundService ().fromInputStream (ais) [0]);

	}
}
