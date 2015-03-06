package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.model.freqs.CompressFrequenciesProcessor;

public class SimpleCompressFrequenciesProcessor implements CompressFrequenciesProcessor {

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.freqs.
     * CompressFrequenciesProcessor#compress(float[], float)
     */
    @Override
    public float [] compress (final float [] array, final float factor) {
        final float factorInv = 1.0f / factor;
        final float [] result = new float [(int) Math.ceil (array.length * factorInv)];
        float writeWhileLessThan1 = 1;
        int resultIndex = 0;
        for (final float element : array) {
            writeWhileLessThan1--;
            while (writeWhileLessThan1 < 1) {
                result [resultIndex++] = element;
                writeWhileLessThan1 += factor;
            }
        }
        return result;
    }

}
