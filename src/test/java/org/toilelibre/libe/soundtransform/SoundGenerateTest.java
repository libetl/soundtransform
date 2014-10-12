package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.model.TransformSoundService;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class SoundGenerateTest {

	@Test
	public void generateA440HzSound () {
		int length = 4000;
		int soundfreq = 440;
		int sampleInBytes = 2;

		int samplerate = 44100;
		long [] signal = new long [length];
		for (int j = 0; j < length; j++) {
			signal [j] = (long) (Math.sin (j * soundfreq * 2 * Math.PI / samplerate) * 32768.0);
		}
		Sound s = new Sound (signal, sampleInBytes, samplerate, 1);

		AudioInputStream ais = new TransformSoundService ().toStream (new Sound [] { s }, new AudioFormat (samplerate, sampleInBytes * 8, 1, true, false));
		File fDest = new File (new File (Thread.currentThread ().getContextClassLoader ().getResource ("before.wav").getFile ()).getParent () + "/after.wav");

		try {
			AudioSystem.write (ais, AudioFileFormat.Type.WAVE, fDest);
		} catch (IOException e) {
		}
	}
}
