package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.freqs.ReplaceFrequenciesProcessor;

final class SimpleReplaceFrequenciesProcessor implements ReplaceFrequenciesProcessor {

    @Override
    public float [] insertPart (final float [] freqs, final float [] subFreqs, final int start) {
        final float [] result = new float [Math.max (start, freqs.length) + subFreqs.length];
        System.arraycopy (freqs, 0, result, 0, Math.min (start, freqs.length));
        System.arraycopy (subFreqs, 0, result, start, subFreqs.length);
        if (freqs.length - start > 0) {
            System.arraycopy (freqs, start, result, start + subFreqs.length, freqs.length - start);
        }
        return result;
    }

    @Override
    public float [] replacePart (final float [] freqs, final float [] subFreqs, final int start) {
        final float [] result = new float [Math.max (freqs.length, start + subFreqs.length)];
        System.arraycopy (freqs, 0, result, 0, freqs.length);
        System.arraycopy (subFreqs, 0, result, start, subFreqs.length);
        return result;
    }

}
