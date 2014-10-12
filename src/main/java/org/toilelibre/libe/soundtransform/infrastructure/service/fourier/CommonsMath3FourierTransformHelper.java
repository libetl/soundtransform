package org.toilelibre.libe.soundtransform.infrastructure.service.fourier;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.AbstractFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class CommonsMath3FourierTransformHelper implements FourierTransformHelper {
	
	public Sound transform (AbstractFrequencySoundTransformation st, Sound sound){
		Sound output = st.initSound (sound);
		double freqmax = sound.getSampleRate ();
		double threshold = st.getLowThreshold (freqmax);
		int maxlength = st.getWindowLength (freqmax);
		long [] data = sound.getSamples ();
		long [] newdata = output.getSamples ();
		double [] transformeddata = new double [maxlength];
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
		for (int i = 0; i < data.length; i += threshold) {
			int length = Math.min (maxlength, data.length - i);
			for (int j = i; j < i + length; j++) {
				transformeddata [j - i] = data [j];
			}
			Complex [] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);
			Spectrum fs = new Spectrum (complexArray, (int) freqmax);
			Spectrum result = st.transformFrequencies (fs, i, maxlength, length);
			if (result == null) {
				continue;
			}
			complexArray = fastFourierTransformer.transform (result.getState (), TransformType.INVERSE);
			int k = st.getOffsetFromASimpleLoop (i, freqmax);
			for (int j = 0; j < freqmax; j++) {
				if (i + j + k < newdata.length && newdata [i + j + k] == 0) {
					newdata [i + j + k] = (long) Math.floor (complexArray [j].getReal ());
				}
			}
		}
		return output;
	}
}
