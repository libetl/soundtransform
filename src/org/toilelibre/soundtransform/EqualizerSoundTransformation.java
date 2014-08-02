package org.toilelibre.soundtransform;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class EqualizerSoundTransformation implements SoundTransformation {
	
	private double [] ranges;
	private double [] amplification;

	public EqualizerSoundTransformation (double [] ranges1, double [] amplification1) {
		this.ranges = ranges1;
		this.amplification = amplification1;
    }


	@Override
	public Sound transform (Sound input) {

		SplineInterpolator reg = new SplineInterpolator();

		PolynomialSplineFunction psf = reg.interpolate (this.ranges, this.amplification);
		return EqualizerSoundTransformation.equalize (input, psf);
	}

	private static Sound equalize (Sound sound, PolynomialSplineFunction psf) {
		double freqmax = sound.getFreq();
		int maxlength = (int)Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
		double [] data = sound.getSamples ();
		double [] newdata = new double [sound.getSamples ().length];
		double [] transformeddata = new double [maxlength];
		
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer ( DftNormalization.STANDARD);
		for (int i = 0 ; i < data.length ; i+= freqmax){
			int length = Math.min (maxlength, data.length - i);
	   		System.arraycopy (data, i, transformeddata, 0, length);			
			Complex [] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);
			Complex [] newAmpl = new Complex [maxlength];
			for (double j = 0 ; j < length ; j++){
			  double freq = j * freqmax / complexArray.length;
			  newAmpl [(int)j] = complexArray [(int)j].multiply(psf.value (freq / 2));
			}
			for (int j = length ; j < maxlength ; j++){
				newAmpl [j] = new Complex (0, 0);
			}
			complexArray = fastFourierTransformer.transform (newAmpl, TransformType.INVERSE);
			
			for (int j = 0 ; j < freqmax ; j++){
				if (i + j < newdata.length){
			      newdata [i + j] = Math.floor(complexArray [j].getReal ());
				}
			}
		}
		// normalized result in newdata
		return new Sound (newdata, sound.getNbBytesPerFrame (), sound.getFreq());
	}
}
