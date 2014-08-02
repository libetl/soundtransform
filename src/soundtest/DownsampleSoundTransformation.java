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
		        fs.getSamples ().length, input.getNbBytesPerFrame (), input.getFreq());
	}

	protected UnivariateFunction getFunction (Sound fs) {
		double[] time = new double [fs.getSamples ().length];
		for (int i = 0 ; i < time.length ; i++){
			time [i] = i;
		}
		return new SplineInterpolator ()
		        .interpolate (time, fs.getSamples ());
    }


	private static Sound buildSoundFromFunction (UnivariateFunction function,
	        int length, int nbBytesPerFrame, int freq) {
		double [] result = new double [length];
		for (int i = 0; i < length; i++) {
			result [i] = function.value (i);
		}
		return new Sound (result, nbBytesPerFrame, freq);
	}
}

