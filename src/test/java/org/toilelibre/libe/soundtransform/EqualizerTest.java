package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.EqualizerSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class EqualizerTest {

	@Test
	public void test () {
		long [] testarray = new long [] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		Sound testsound = new Sound (testarray, 1, testarray.length, 1);
		EqualizerSoundTransformation est = new EqualizerSoundTransformation (new double [] { 0, 4, 8 }, new double [] { 1, 1, 1 });
		Sound resultsound = est.transform (testsound);
		System.out.println (Arrays.toString (resultsound.getSamples ()));
	}
}
