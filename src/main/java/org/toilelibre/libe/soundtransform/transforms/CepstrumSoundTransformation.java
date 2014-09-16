package org.toilelibre.libe.soundtransform.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;

public class CepstrumSoundTransformation extends NoOpFrequencySoundTransformation {

	private double	threshold;
	private int []	loudestfreqs;
	private int	   index;

	public CepstrumSoundTransformation () {
		this.threshold = 100;
	}

	public CepstrumSoundTransformation (double threshold) {
		this.threshold = threshold;
	}

	@Override
	public Sound initSound (Sound input) {
		this.loudestfreqs = new int [(int) (input.getSamples ().length / threshold) + 1];
		this.index = 0;
		return super.initSound (input);
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return this.threshold;
	}

	public int [] getLoudestFreqs () {
		return loudestfreqs;
	}

	private int computeLoudestFreq (FrequenciesState fs) {
		double max = 0;
		double freq = 0;
		for (int j = 50; j < 900; j++) {
			double val = fs.getState () [j].abs ();
			freq = (max < val ? j : freq);
			max = (max < val ? val : max);
		}
		return (int)freq;
	}

	@Override
	public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length) {

		for (int i = 0; i < fs.getState ().length; i++) {
			Complex c = fs.getState () [i];
			double abs = c.abs ();
			double abs2 = Math.pow (abs, 2);
			double log = Math.log (abs2);
			fs.getState () [i] = new Complex (log);
		}
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);

		FrequenciesState fscep = new FrequenciesState (fastFourierTransformer.transform (fs.getState (), TransformType.INVERSE), fs.getMaxfrequency ());

        this.loudestfreqs [index] = this.computeLoudestFreq (fscep);;
		this.index++;

		return fscep;
	}
}
