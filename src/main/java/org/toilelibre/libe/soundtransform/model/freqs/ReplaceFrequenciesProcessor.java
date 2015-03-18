package org.toilelibre.libe.soundtransform.model.freqs;

public interface ReplaceFrequenciesProcessor {

    float [] insertPart (float [] freqs, float [] subFreqs, int start);

    float [] replacePart (float [] freqs, float [] subFreqs, int start);

}
