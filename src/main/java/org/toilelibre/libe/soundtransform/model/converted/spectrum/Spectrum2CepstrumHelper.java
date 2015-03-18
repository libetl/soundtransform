package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface Spectrum2CepstrumHelper<T> {
    public Spectrum<T> spectrumToCepstrum (Spectrum<T> fs);
}