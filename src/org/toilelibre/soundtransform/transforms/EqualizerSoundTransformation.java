package org.toilelibre.soundtransform.transforms;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.toilelibre.soundtransform.objects.FrequenciesState;

public class EqualizerSoundTransformation extends
		NoOpFrequencySoundTransformation {

	private double[] ranges;
	private double[] amplification;

	public EqualizerSoundTransformation(double[] ranges1,
			double[] amplification1) {
		this.ranges = ranges1;
		this.amplification = amplification1;
	}

	@Override
	public FrequenciesState transformFrequencies(FrequenciesState fs,
			int offset, int powOf2NearestLength, int length, double maxFrequency) {
		SplineInterpolator reg = new SplineInterpolator();

		PolynomialSplineFunction psf = reg.interpolate(this.ranges,
				this.amplification);
		Complex[] newAmpl = new Complex[powOf2NearestLength];
		for (double j = 0; j < length; j++) {
			double freq = j * maxFrequency / fs.getState().length;
			newAmpl[(int) j] = fs.getState()[(int) j].multiply(psf
					.value(freq / 2));
		}
		for (int j = length; j < powOf2NearestLength; j++) {
			newAmpl[j] = new Complex(0, 0);
		}
		return new FrequenciesState(newAmpl);
	}
}
