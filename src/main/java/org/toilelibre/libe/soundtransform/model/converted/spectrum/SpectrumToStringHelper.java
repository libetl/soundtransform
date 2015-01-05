package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumToStringHelper {

	public abstract String fsToString (Spectrum fs);

	public abstract String fsToString (Spectrum fs, int low, int high, int compression, int height);

}