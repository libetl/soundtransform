package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class SlowdownTest {

	@Test
	public void test () {
		final long [] testarray = new long [] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		final Sound testsound = new Sound (testarray, 1, testarray.length, 1);
		final SlowdownSoundTransformation est = new SlowdownSoundTransformation (2, 2);
		final Sound resultsound = est.transform (testsound);
		System.out.println (Arrays.toString (resultsound.getSamples ()));
	}
}
