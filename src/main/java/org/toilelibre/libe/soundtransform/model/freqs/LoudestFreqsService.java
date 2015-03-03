package org.toilelibre.libe.soundtransform.model.freqs;

public class LoudestFreqsService {

    private static final float               HALF  = 0.5f;
    private static final float               TWICE = 2.0f;

    private final ChangeOctaveProcessor       changeOctaveProcessor;
    private final AdjustFrequenciesProcessor  adjustFrequenciesProcessor;
    private final FilterFrequenciesProcessor  filterFrequenciesProcessor;
    private final ReplaceFrequenciesProcessor replaceFrequenciesProcessor;

    public LoudestFreqsService (final ChangeOctaveProcessor changeOctaveProcessor1, final AdjustFrequenciesProcessor adjustFrequenciesProcessor1, final FilterFrequenciesProcessor filterFrequenciesProcessor1, final ReplaceFrequenciesProcessor replaceFrequenciesProcessor1) {
        this.changeOctaveProcessor = changeOctaveProcessor1;
        this.adjustFrequenciesProcessor = adjustFrequenciesProcessor1;
        this.filterFrequenciesProcessor = filterFrequenciesProcessor1;
        this.replaceFrequenciesProcessor = replaceFrequenciesProcessor1;
    }

    public float [] adjust (final float [] freqs) {
        return this.adjustFrequenciesProcessor.adjust (freqs);
    }

    public float [] filterRange (final float [] freqs, final float low, final float high) {
        return this.filterFrequenciesProcessor.filter (freqs, low, high);
    }

    public float [] octaveDown (final float [] freqs) {
        return this.changeOctaveProcessor.multFreqs (freqs, LoudestFreqsService.HALF);
    }

    public float [] octaveUp (final float [] freqs) {
        return this.changeOctaveProcessor.multFreqs (freqs, LoudestFreqsService.TWICE);
    }

    public float [] replacePart (float [] freqs, float [] subFreqs, int start) {
        return this.replaceFrequenciesProcessor.replacePart (freqs, subFreqs, start);
    }

}
