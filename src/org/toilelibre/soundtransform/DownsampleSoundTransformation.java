package org.toilelibre.soundtransform;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;

public class DownsampleSoundTransformation implements SoundTransformation {
	
	private int times;


	public DownsampleSoundTransformation (int times) {
		this.times = times;
    }


	@Override
	public Sound transform (Sound input) {
		Sound fs = input;
		UnivariateFunction function = this.getFunction (fs);
		return DownsampleSoundTransformation.buildSoundFromFunction (function, this.times,
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


	private static Sound buildSoundFromFunction (UnivariateFunction function, int times,
	        int length, int nbBytesPerFrame, int freq) {
		double [] result = new double [length * times];
		for (int i = 0; i < length * times; i++) {
			result [i] = function.value (i / times);
		}
		return new Sound (result, nbBytesPerFrame, freq);
	}
}

