package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.freqs.ChangeOctaveProcessor;

final class SimpleChangeOctaveProcessor implements ChangeOctaveProcessor {

    @Override
    public float [] multFreqs (final float [] freqs, final float factor) {
        final float [] output = new float [freqs.length];
        for (int i = 0 ; i < freqs.length ; i++) {
            output [i] = freqs [i] * factor;
        }
        return output;
    }

}
