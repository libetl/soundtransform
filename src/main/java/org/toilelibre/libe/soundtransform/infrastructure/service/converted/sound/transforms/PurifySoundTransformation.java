package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class PurifySoundTransformation extends SimpleFrequencySoundTransformation<Complex []> {

    private static final double DEFAULT_STEP_VALUE = 100;
    private static final int    START_INDEX        = 100;
    private static final int    COEFFICIENT        = 100;
    private static final int    EXPONENT           = 2;

    public PurifySoundTransformation () {
        super ();
    }

    @Override
    public double getStep (final double defaultValue) {
        return PurifySoundTransformation.DEFAULT_STEP_VALUE;
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset, final int powOf2NearestLength, final int length) {
        final Complex [] newAmpl = new Complex [powOf2NearestLength];
        int max = 0;
        double maxValue = 0;
        for (int j = 0 ; j < length ; j++) {
            final double tmp = Math.sqrt (Math.pow (fs.getState () [j].getReal (), PurifySoundTransformation.EXPONENT) + Math.pow (fs.getState () [j].getImaginary (), PurifySoundTransformation.EXPONENT));
            if (tmp > maxValue && j > PurifySoundTransformation.START_INDEX && j < fs.getSampleRate () / PurifySoundTransformation.EXPONENT) {
                max = j;
                maxValue = tmp;
            }
        }
        for (int j = 0 ; j < powOf2NearestLength ; j++) {
            newAmpl [j] = fs.getState () [j].multiply (Math.exp (-Math.pow (j - max, PurifySoundTransformation.EXPONENT) / PurifySoundTransformation.COEFFICIENT));
        }
        return new Spectrum<Complex []> (newAmpl, fs.getFormatInfo ());
    }

}
