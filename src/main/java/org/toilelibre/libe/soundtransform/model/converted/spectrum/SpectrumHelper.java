package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.apache.commons.math3.complex.Complex;

public interface SpectrumHelper<T> {

    int f0 (Spectrum<T> fs, int i);

    int getMaxIndex (Spectrum<T> fscep, int i, int sampleRate);

    int freqFromSampleRate (int freq, int sqr2length, int sampleRate);

    Spectrum<Complex []> hps (Spectrum<Complex []> fs, int factor);

}