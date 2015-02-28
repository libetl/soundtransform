package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.freqs.FilterFrequenciesProcessor;

public class SimpleFilterFrequenciesProcessor implements FilterFrequenciesProcessor {

    @Override
    public float [] filter (float [] freqs, float low, float high) {
        float [] result = new float [freqs.length];
        for (int i = 0 ; i < result.length ; i++) {
            if (freqs [i] < low || freqs [i] > high) {
                result [i] = freqs [i];
            }
        }
        return result;
    }

}
