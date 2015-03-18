package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumToStringHelper<T> {

    public abstract String fsToString (Spectrum<T> fs);

    public abstract String fsToString (Spectrum<T> fs, int low, int high, int compression, int height);

}