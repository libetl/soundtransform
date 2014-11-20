package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;

public class HPSSpectrumHelper implements SpectrumHelper {

	public static int freqFromSampleRate (int freq, int sqr2length, int sampleRate) {
		return (int) (freq * 2.0 * sampleRate / sqr2length);
	}

	/**
	 * Find the f0 (fundamental frequency) using the Harmonic Product Spectrum
	 * 
	 * @param fs
	 *            spectrum at a specific time
	 * @param hpsfactor
	 *            number of times to multiply the frequencies together
	 * @return a fundamental frequency (in Hz)
	 */
	public int f0 (Spectrum fs, int hpsfactor) {
		return HPSSpectrumHelper.freqFromSampleRate (this.getMaxIndex (HPSSpectrumHelper.hps (fs, hpsfactor), 0, fs.getState ().length / hpsfactor), fs.getState ().length * 2 / hpsfactor,
		        fs.getSampleRate ());
	}

	private static Spectrum hps (Spectrum fs, int factor) {
		int max = fs.getState ().length / factor;
		Complex [] result = new Complex [max];
		for (int i = 0; i < max; i++) {
			double val = fs.getState () [i].abs ();
			for (int j = 1; j < factor; j++) {
				if (i * factor < fs.getSampleRate () / 2 && i * factor < fs.getState ().length) {
					val *= fs.getState () [i * factor].abs ();
				}
			}
			result [i] = new Complex (val);
		}
		return new Spectrum (result, fs.getSampleRate () / factor, fs.getNbBytes ());
	}

	
	public int getMaxIndex (Spectrum fs, int low, int high) {
		double max = 0;
		int maxIndex = 0;
		int reallow = low == 0 ? 1 : low;
		int realhigh = Math.min (high, fs.getState ().length);
		for (int i = reallow; i < realhigh; i++) {
			if (max < fs.getState () [i].abs () &&
					fs.getState () [i].abs () > Math.pow (256, fs.getNbBytes ()) + 1) {
				max = fs.getState () [i].abs ();
				maxIndex = i;
			}
		}
		return maxIndex;
	}
}
