package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumHelper<T> {

    float f0 (Spectrum<T> fs, int i);

    float freqFromSampleRate (float freq, int sqr2length, float sampleRate);

    int getMaxIndex (Spectrum<T> fscep, int min, int max);

    Spectrum<T> hps (Spectrum<T> fs, int factor);

}