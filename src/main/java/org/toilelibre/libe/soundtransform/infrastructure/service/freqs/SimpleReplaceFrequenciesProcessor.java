package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.freqs.ReplaceFrequenciesProcessor;

public class SimpleReplaceFrequenciesProcessor implements ReplaceFrequenciesProcessor {

    @Override
    public float [] replacePart (float [] freqs, float [] subFreqs, int start) {
        final float [] result = new float [Math.max (freqs.length, start + subFreqs.length)];
        System.arraycopy (freqs, 0, result, 0, freqs.length);
        System.arraycopy (subFreqs, 0, result, start, subFreqs.length);
        return result;
    }

}
