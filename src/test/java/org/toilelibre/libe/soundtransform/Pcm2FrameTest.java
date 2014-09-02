package org.toilelibre.libe.soundtransform;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.TransformSound;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.PrintlnTransformObserver;

public class Pcm2FrameTest {

	@Test
	public void testReversibleData () throws IOException {
		RandomDataGenerator rdg = new RandomDataGenerator ();
		byte [] data = new byte [256];
		for (int i = 0; i < data.length; i++) {
			data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
		}
		System.out.println (Arrays.toString (data));
		TransformSound ts = new TransformSound (new PrintlnTransformObserver (true));
		Sound [] channels = ts.byteArrayToFrames (data, 2, data.length / 4, 2, 44100.0, false, true);

		byte [] out = ts.framesToByteArray (channels, 2, false, true);
		System.out.println (Arrays.toString (out));
	}
}
