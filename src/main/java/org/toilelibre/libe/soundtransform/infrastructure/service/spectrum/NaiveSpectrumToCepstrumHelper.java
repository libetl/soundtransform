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
        final Spectrum<Complex []> logSpectrum = new Spectrum<Complex []> (spectrum.getState (), spectrum.getFormatInfo ());
        for (int i = 0 ; i < logSpectrum.getState ().length ; i++) {
            logSpectrum.getState () [i] = new Complex (Math.log (1 + NaiveSpectrumToCepstrumHelper.A_CONSTANT_TO_REDUCE_OCTAVE_ERRORS * spectrum.getState () [i].abs ()));
        }
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);

        final Spectrum<Complex []> fscep = new Spectrum<Complex []> (fastFourierTransformer.transform (logSpectrum.getState (), TransformType.INVERSE), logSpectrum.getFormatInfo ());
        for (int i = 0 ; i < fscep.getState ().length ; i++) {
            fscep.getState () [i] = new Complex (fscep.getState () [i].getReal (), 0);
        }
        return fscep;
    }
}
