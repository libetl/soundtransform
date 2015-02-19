package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;

public final class ShiftOctaveLoudestFreqs extends Action {

    public float [] octaveDown (final float [] freqs) {
        return this.loudestFreqsService.octaveDown (freqs);
    }

    public float [] octaveUp (final float [] freqs) {
        return this.loudestFreqsService.octaveUp (freqs);
    }

}
