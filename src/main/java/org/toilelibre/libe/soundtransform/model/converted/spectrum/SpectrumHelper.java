package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumHelper {

	int f0 (Spectrum fs, int i);

	int getMaxIndex (Spectrum fscep, int i, int sampleRate);

}