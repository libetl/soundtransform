package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.freqs.FilterFrequenciesProcessor;

final class SimpleFilterFrequenciesProcessor implements FilterFrequenciesProcessor {

    @Override
    public float [] filter (final float [] freqs, final float low, final float high) throws SoundTransformException {
        if (low >= high) {
            throw new SoundTransformException (FilterFrequenciesProcessorErrorCode.INVALID_RANGE, new IndexOutOfBoundsException (), low, high);
        }
        final float [] result = new float [freqs.length];
        for (int i = 0 ; i < result.length ; i++) {
            if (freqs [i] < low || freqs [i] > high) {
                result [i] = freqs [i];
            }
        }
        return result;
    }

}
