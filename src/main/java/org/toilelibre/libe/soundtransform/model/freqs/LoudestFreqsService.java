package org.toilelibre.libe.soundtransform.model.freqs;

public class LoudestFreqsService {

    private final ChangeOctaveProcessor processor;

    public LoudestFreqsService (final ChangeOctaveProcessor processor1) {
        this.processor = processor1;
    }

    public float [] octaveDown (final float [] freqs) {
        return this.processor.multFreqs (freqs, 0.5f);
    }

    public float [] octaveUp (final float [] freqs) {
        return this.processor.multFreqs (freqs, 2.0f);
    }

}
