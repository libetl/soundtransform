package soundtest;

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
		int freqmax = 22000;
		int maxlength = (int)Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
		double [] data = sound.getSamples ();
		double [] newdata = new double [sound.getSamples ().length];
		double [] transformeddata = new double [maxlength];
		
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer ( DftNormalization.STANDARD);
		for (int i = 0 ; i < data.length ; i++){
			int length = Math.min (maxlength, data.length - i);
	   		System.arraycopy (data, i, transformeddata, 0, length);			
			Complex[] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);
			double [] newAmpl = new double [maxlength];
			for (int j = 0 ; j < length ; j++){
			  double module = Math.sqrt (
					  Math.pow (complexArray [j].getReal (), 2) +
					  Math.pow (complexArray [j].getImaginary (), 2));
			  double phase = complexArray [j].getArgument ();
			  double freq = j / complexArray.length * freqmax;
			  System.out.println ((j * freqmax / complexArray.length) + " -> " + module);
			  newAmpl [(int)freq] = module * psf.value (j);
			}
			complexArray = fastFourierTransformer.transform (newAmpl, TransformType.INVERSE);

			newdata [i] = complexArray [0].getReal ();
		}
		// normalized result in newdata
		return new Sound (newdata, sound.getNbBytesPerFrame ());
	}
}
