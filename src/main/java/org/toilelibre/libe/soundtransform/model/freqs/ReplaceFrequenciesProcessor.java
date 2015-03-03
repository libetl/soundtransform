package org.toilelibre.libe.soundtransform.model.freqs;

public interface ReplaceFrequenciesProcessor {

    float [] replacePart (float [] freqs, float [] subFreqs, int start);

}
