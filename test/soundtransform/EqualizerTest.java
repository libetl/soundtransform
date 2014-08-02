package soundtransform;

import java.util.Arrays;

import org.junit.Test;

import soundtest.EqualizerSoundTransformation;
import soundtest.Sound;

public class EqualizerTest {

	@Test
	public void test (){
		double [] testarray = new double [] {0, 1, 2, 3, 4, 5, 6, 7};
		Sound testsound = new Sound (testarray, 1, 4);
		EqualizerSoundTransformation est =
				new EqualizerSoundTransformation (
						new double [] {0, 4, 8}, new double []{1, 1, 1});
		Sound resultsound = est.transform(testsound);
		System.out.println(
				Arrays.toString(resultsound.getSamples()));
	}
}
