package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.freqs.SurroundInRangeProcessor;

final class SimpleSurroundInOctaveProcessor implements SurroundInRangeProcessor {

    private static final float OCTAVE_HALF = 1.5f;
    private static final float TWICE = 2f;
    
    @Override
    public float[] surroundFreqsInRange(float[] freqs, final float low, final float high) throws SoundTransformException {
        if (low >= high){
            throw new SoundTransformException(SurroundInRangeProcessorErrorCode.INVALID_RANGE, new IndexOutOfBoundsException(), low, high);
        }
        final float [] output = new float [freqs.length];
        for (int i = 0 ; i < freqs.length ; i++) {
            output [i] = this.surroundFreqInRange (freqs [i], low, high);
        }
        return output;
    }

    private float surroundFreqInRange(float inputValue, final float low, final float high) {
        float result = inputValue;
        while (result < low) {
            result *= 2.0;
        }
        while (result > high) {
            result /= 2.0;
        }
        
        while (result < low && result * TWICE > high){
            result *= OCTAVE_HALF;
        }
        return result;
    }

}
