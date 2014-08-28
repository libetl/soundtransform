package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.transforms.SlowdownSoundTransformation;

public class SlowdownTest {

	@Test
	public void test () {
		long [] testarray = new long [] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		Sound testsound = new Sound (testarray, 1, testarray.length);
		SlowdownSoundTransformation est = new SlowdownSoundTransformation (2, 2);
		Sound resultsound = est.transform (testsound);
		System.out.println (Arrays.toString (resultsound.getSamples ()));
	}
}
