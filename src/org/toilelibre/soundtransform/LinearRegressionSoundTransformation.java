package org.toilelibre.soundtransform;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class LinearRegressionSoundTransformation implements SoundTransformation {

	private int step = 1;
	
	public LinearRegressionSoundTransformation (int step) {
		this.step = step;
    }


	@Override
    public Sound transform (Sound input) {
		SplineInterpolator reg = new SplineInterpolator();
	    double [] x = new double [input.getSamples ().length / step];
	    double [] y = new double [input.getSamples ().length / step];
		for (int i = 0 ; i < input.getSamples ().length ; i+= step){
			if (i / step < x.length){
			  x [i / step] = i;
			  y [i / step] = input.getSamples () [i];
			}
		}
		
		PolynomialSplineFunction psf = reg.interpolate (x, y);
		
		Sound outputSound = new Sound (new double [input.getSamples ().length],
				input.getNbBytesPerFrame (), input.getFreq());
		for (int i = 0 ; i < input.getSamples ().length ; i+= step){
			if (i / step < x.length){
			  outputSound.getSamples () [i] = psf.value (i);
			}else{
			  outputSound.getSamples () [i] = input.getSamples () [i];
			}
		}
		
		return outputSound;
    }


}
