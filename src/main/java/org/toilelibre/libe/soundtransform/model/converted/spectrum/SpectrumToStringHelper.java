package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumToStringHelper<T> {

    String fsToString (Spectrum<T> fs);

    String fsToString (Spectrum<T> fs, int low, int high, int compression, int height);

}