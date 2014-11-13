package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;

public class NaiveSpectrum2CepstrumHelper implements Spectrum2CepstrumHelper {


	public Spectrum spectrumToCepstrum (Spectrum fs) {
		for (int i = 0; i < fs.getState ().length; i++) {
			Complex c = fs.getState () [i];
			double log = Math.log (Math.pow (c.abs (), 2));
			fs.getState () [i] = new Complex (log);
		}
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);

		Spectrum fscep = new Spectrum (fastFourierTransformer.transform (fs.getState (), TransformType.FORWARD), 
				fs.getSampleRate (), fs.getNbBytes ());
		for (int i = 0; i < fscep.getState ().length; i++) {
			Complex c = fscep.getState () [i];
			double sqr = Math.pow (c.abs (), 2);
			fscep.getState () [i] = new Complex (sqr);
		}
		for (int i = 0; i < 50; i++) {
			fscep.getState () [i] = new Complex (0);
		}
		for (int i = fscep.getState ().length - 50; i < fscep.getState ().length; i++) {
			fscep.getState () [i] = new Complex (0);
		}

		return fscep;
	}
}
