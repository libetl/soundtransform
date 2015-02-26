package org.toilelibre.libe.soundtransform.model.freqs;

public class LoudestFreqsService {

    private static final float          HALF  = 0.5f;
    private static final float          TWICE = 2.0f;

    private final ChangeOctaveProcessor processor;

    public LoudestFreqsService (final ChangeOctaveProcessor processor1) {
        this.processor = processor1;
    }

    public float [] octaveDown (final float [] freqs) {
        return this.processor.multFreqs (freqs, LoudestFreqsService.HALF);
    }

    public float [] octaveUp (final float [] freqs) {
        return this.processor.multFreqs (freqs, LoudestFreqsService.TWICE);
    }

}
