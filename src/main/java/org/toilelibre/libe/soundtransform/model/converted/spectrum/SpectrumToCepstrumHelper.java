package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumToCepstrumHelper<T> {
    Spectrum<T> spectrumToCepstrum (Spectrum<T> fs);
}