package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public interface SpectrumHelper<T> {

    float freqFromSampleRate (float freq, int sqr2length, float sampleRate);

    int getMaxIndex (Spectrum<T> fs, int min, int max);
    
    double getMaxValue (Spectrum<T> fs, int min, int max);

    int getFirstPeak (Spectrum<T> fs, int min, int max, double thresholdValue);
    
    Spectrum<T> productOfMultiples (Spectrum<T> fs, int factor);

    int getLengthOfSpectrum (Spectrum<T> fs);

}