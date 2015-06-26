package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;

final class NaiveSpectrumToCepstrumHelper implements SpectrumToCepstrumHelper<Complex []> {

    private static final double A_CONSTANT_TO_REDUCE_OCTAVE_ERRORS = 10.0;

    @Override
    public Spectrum<Complex []> spectrumToCepstrum (final Spectrum<Complex []> spectrum) {
        final double [] logSpectrumReals = new double [spectrum.getState ().length];
        
        for (int i = 0 ; i < logSpectrumReals.length ; i++) {
            logSpectrumReals [i] = Math.log (1 + NaiveSpectrumToCepstrumHelper.A_CONSTANT_TO_REDUCE_OCTAVE_ERRORS * spectrum.getState () [i].abs ());
        }
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);

        return new Spectrum<Complex []> (fastFourierTransformer.transform (logSpectrumReals, TransformType.INVERSE), spectrum.getFormatInfo ());
    }
}
