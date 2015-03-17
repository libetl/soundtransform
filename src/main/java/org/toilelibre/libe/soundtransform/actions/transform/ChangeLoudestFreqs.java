package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;

public final class ChangeLoudestFreqs extends Action {

    public float[] adjust(final float[] freqs) {
        return this.loudestFreqsService.adjust(freqs);
    }

    public float[] compress(final float[] freqs, final float factor) {
        return this.loudestFreqsService.compress(freqs, factor);
    }

    public float[] filterRange(final float[] freqs, final float low, final float high) {
        return this.loudestFreqsService.filterRange(freqs, low, high);
    }

    public float[] insertPart(final float[] freqs, final float[] subFreqs, final int start) {
        return this.loudestFreqsService.insertPart(freqs, subFreqs, start);
    }

    public float[] octaveDown(final float[] freqs) {
        return this.loudestFreqsService.octaveDown(freqs);
    }

    public float[] octaveUp(final float[] freqs) {
        return this.loudestFreqsService.octaveUp(freqs);
    }

    public float[] replacePart(final float[] freqs, final float[] subFreqs, final int start) {
        return this.loudestFreqsService.replacePart(freqs, subFreqs, start);
    }
}
