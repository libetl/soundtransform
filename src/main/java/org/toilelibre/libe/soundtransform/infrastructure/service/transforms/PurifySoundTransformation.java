package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class PurifySoundTransformation extends SimpleFrequencySoundTransformation {

	public PurifySoundTransformation () {
	}

	@Override
	public double getLowThreshold (final double defaultValue) {
		return 100;
	}

	@Override
	public int getWindowLength (final double freqmax) {
		return super.getWindowLength (freqmax);
	}

	@Override
	public Spectrum transformFrequencies (final Spectrum fs, final int offset, final int powOf2NearestLength, final int length) {
		final Complex [] newAmpl = new Complex [powOf2NearestLength];
		int max = 0;
		double maxValue = 0;
		for (int j = 0; j < length; j++) {
			final double tmp = Math.sqrt (Math.pow (fs.getState () [j].getReal (), 2) + Math.pow (fs.getState () [j].getImaginary (), 2));
			if (tmp > maxValue && j > 100 && j < fs.getSampleRate () / 2) {
				max = j;
				maxValue = tmp;
			}
		}
		for (int j = 0; j < powOf2NearestLength; j++) {
			newAmpl [j] = fs.getState () [j].multiply (Math.exp (-Math.pow (j - max, 2) / 100));
		}
		return new Spectrum (newAmpl, fs.getSampleRate (), fs.getNbBytes ());
	}

}
