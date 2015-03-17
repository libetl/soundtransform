package org.toilelibre.libe.soundtransform.model.freqs;

public interface FilterFrequenciesProcessor {

    float[] filter(float[] freqs, float low, float high);

}
