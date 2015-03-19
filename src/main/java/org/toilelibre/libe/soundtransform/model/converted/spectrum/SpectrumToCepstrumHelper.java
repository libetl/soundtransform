package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumToCepstrumHelper<T> {
    public Spectrum<T> spectrumToCepstrum (Spectrum<T> fs);
}