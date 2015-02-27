package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.freqs.AdjustFrequenciesProcessor;

public class AdjustFrequenciesToPianoProcessor implements AdjustFrequenciesProcessor {

    @Override
    public float [] adjust (float [] freqs) {
        final float [] output = new float [freqs.length];
        for (int i = 0 ; i < freqs.length ; i++) {
            output [i] = PianoFrequency.getNearestNote (freqs [i]).getFrequency ();
        }
        return output;
    }

}
