package org.toilelibre.soundtransform;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;

public class EqualizerSoundTransformation extends AbstractFrequencySoundTransformation {
	
	private double [] ranges;
	private double [] amplification;

	public EqualizerSoundTransformation (double [] ranges1, double [] amplification1) {
		this.ranges = ranges1;
		this.amplification = amplification1;
    }

    @Override
    public FrequenciesState transformFrequencies(FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxFrequency) {
        SplineInterpolator reg = new SplineInterpolator();

        PolynomialSplineFunction psf = reg.interpolate (this.ranges, this.amplification);
        Complex [] newAmpl = new Complex [powOf2NearestLength];
        for (double j = 0 ; j < length ; j++){
          double freq = j * maxFrequency / fs.getState().length;
          newAmpl [(int)j] = fs.getState() [(int)j].multiply(psf.value (freq / 2));
        }
        for (int j = length ; j < powOf2NearestLength ; j++){
            newAmpl [j] = new Complex (0, 0);
        }
        return new FrequenciesState (newAmpl);
    }

    @Override
    public Sound initSound(Sound input) {
        double [] newdata = new double [input.getSamples ().length];
        return new Sound (newdata, input.getNbBytesPerFrame (), input.getFreq());
    }

    @Override
    protected int getOffsetFromASimpleLoop(int i, double step) {
        return 0;
    }

	@Override
    protected double getLowThreshold (double defaultValue) {
	    return defaultValue;
    }
}
