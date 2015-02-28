package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;

public final class ChangeLoudestFreqs extends Action {

    public float [] adjust (final float [] freqs) {
        return this.loudestFreqsService.adjust (freqs);
    }

    public float [] filterRange (final float [] freqs, final float low, final float high) {
        return this.loudestFreqsService.filterRange (freqs, low, high);
    }

    public float [] octaveDown (final float [] freqs) {
        return this.loudestFreqsService.octaveDown (freqs);
    }

    public float [] octaveUp (final float [] freqs) {
        return this.loudestFreqsService.octaveUp (freqs);
    }

}
