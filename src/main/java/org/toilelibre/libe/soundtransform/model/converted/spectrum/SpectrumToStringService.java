package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumToStringService<T> {

    public abstract String convert (Spectrum<T> spectrum);

}