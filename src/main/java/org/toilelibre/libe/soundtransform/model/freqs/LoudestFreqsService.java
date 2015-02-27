package org.toilelibre.libe.soundtransform.model.freqs;

public class LoudestFreqsService {

    private static final float          HALF  = 0.5f;
    private static final float          TWICE = 2.0f;

    private final ChangeOctaveProcessor changeOctaveProcessor;
    private final AdjustFrequenciesProcessor adjustFrequenciesProcessor;

    public LoudestFreqsService (final ChangeOctaveProcessor changeOctaveProcessor1,
            final AdjustFrequenciesProcessor adjustFrequenciesProcessor1) {
        this.changeOctaveProcessor = changeOctaveProcessor1;
        this.adjustFrequenciesProcessor = adjustFrequenciesProcessor1;
    }

    public float [] octaveDown (final float [] freqs) {
        return this.changeOctaveProcessor.multFreqs (freqs, LoudestFreqsService.HALF);
    }

    public float [] octaveUp (final float [] freqs) {
        return this.changeOctaveProcessor.multFreqs (freqs, LoudestFreqsService.TWICE);
    }

    public float [] adjust (float [] freqs) {
        return this.adjustFrequenciesProcessor.adjust (freqs);
    }

}
