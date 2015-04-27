package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransform;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

/**
 *
 * Extracts only the frequency with the highest amplitude (not the loudest one)
 * and builds a sound with this single frequency at each step.
 *
 */
public class PurifySoundTransform extends SimpleFrequencySoundTransform<Complex []> {

    private static final double DEFAULT_STEP_VALUE = 100;
    private static final int    START_INDEX        = 100;
    private static final int    COEFFICIENT        = 100;
    private static final int    EXPONENT           = 2;

    /**
     * Default constructor
     */
    public PurifySoundTransform () {
        super ();
    }

    @Override
    public double getStep (final double defaultValue) {
        return PurifySoundTransform.DEFAULT_STEP_VALUE;
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset, final int powOf2NearestLength, final int length) {
        final Complex [] newAmpl = new Complex [powOf2NearestLength];
        int max = 0;
        double maxValue = 0;
        for (int j = 0 ; j < length ; j++) {
            final double tmp = Math.sqrt (Math.pow (fs.getState () [j].getReal (), PurifySoundTransform.EXPONENT) + Math.pow (fs.getState () [j].getImaginary (), PurifySoundTransform.EXPONENT));
            if (tmp > maxValue && j > PurifySoundTransform.START_INDEX && j < fs.getSampleRate () / PurifySoundTransform.EXPONENT) {
                max = j;
                maxValue = tmp;
            }
        }
        for (int j = 0 ; j < powOf2NearestLength ; j++) {
            newAmpl [j] = fs.getState () [j].multiply (Math.exp (-Math.pow (j - max, PurifySoundTransform.EXPONENT) / PurifySoundTransform.COEFFICIENT));
        }
        return new Spectrum<Complex []> (newAmpl, fs.getFormatInfo ());
    }

}
