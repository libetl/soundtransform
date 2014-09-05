package org.toilelibre.libe.soundtransform.transforms;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;

public class LoudestFreqsSoundTransformation extends NoOpFrequencySoundTransformation {

	private double	threshold;

	public LoudestFreqsSoundTransformation () {
		this.threshold = 100;
	}

	public LoudestFreqsSoundTransformation (double threshold) {
		this.threshold = threshold;
	}

	@Override
	public Sound initSound (Sound input) {
		return super.initSound (input);
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return this.threshold;
	}

	@Override
	public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length) {

		for (int i = 0; i < fs.getState ().length; i++) {
			fs.getState () [i] = new Complex (Math.log (fs.getState () [i].abs ()));
		}
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);

		FrequenciesState fscep = new FrequenciesState (fastFourierTransformer.transform (fs.getState (), TransformType.INVERSE), fs.getMaxfrequency ());

		return fscep;
	}
}
