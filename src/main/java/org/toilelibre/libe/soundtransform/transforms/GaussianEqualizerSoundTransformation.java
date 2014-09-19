package org.toilelibre.libe.soundtransform.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;

public class GaussianEqualizerSoundTransformation extends NoOpFrequencySoundTransformation {


	public GaussianEqualizerSoundTransformation () {
	}

	@Override
	public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length) {
		Complex [] newAmpl = new Complex [powOf2NearestLength];
		for (double j = 0; j < length; j++) {
			double freq = j * fs.getMaxfrequency () / fs.getState ().length;
			newAmpl [(int) j] = fs.getState () [(int) j].multiply (this.function (freq));
		}
		for (int j = length; j < powOf2NearestLength; j++) {
			newAmpl [j] = new Complex (0, 0);
		}
		return new FrequenciesState (newAmpl, fs.getMaxfrequency ());
	}

	private Complex function(double x) {
		return new Complex (1 - Math.exp((-Math.pow(x - 3500, 2)/1000))/2);
	}
}
