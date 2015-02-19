package org.toilelibre.libe.soundtransform.model.freqs;

public class LoudestFreqsService {

    private final ChangeOctaveProcessor processor;

    public LoudestFreqsService (ChangeOctaveProcessor processor1) {
        this.processor = processor1;
    }

    public float [] octaveDown (float [] freqs) {
        return this.processor.multFreqs (freqs, 0.5f);
    }

    public float [] octaveUp (float [] freqs) {
        return this.processor.multFreqs (freqs, 2.0f);
    }

}
