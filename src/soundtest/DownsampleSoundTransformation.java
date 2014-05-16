package soundtest;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;

public class DownsampleSoundTransformation implements SoundTransformation {
	
	public DownsampleSoundTransformation () {
    }


	@Override
	public Sound transform (Sound input) {
		Sound fs = input;
		UnivariateFunction function = this.getFunction (fs);
		return DownsampleSoundTransformation.buildSoundFromFunction (function,
		        (int) fs.getTime () [fs.getTime ().length - 1], input.getNbBytesPerFrame ());
	}

	protected UnivariateFunction getFunction (Sound fs) {
		return new SplineInterpolator ()
		        .interpolate (fs.getTime (), fs.getSamples ());
    }


	private static Sound buildSoundFromFunction (UnivariateFunction function,
	        int length, int nbBytesPerFrame) {
		double [] result = new double [length];
		for (int i = 0; i < length; i++) {
			result [i] = function.value (i);
		}
		return new Sound (result, nbBytesPerFrame);
	}
}

