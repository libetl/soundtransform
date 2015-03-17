package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;

class NaiveSpectrum2CepstrumHelper implements Spectrum2CepstrumHelper<Complex[]> {

    private static final double SQUARE = 2;
    private static final int LOW_FILTER = 50;
    private static final int HIGH_FILTER = 50;

    @Override
    public Spectrum<Complex[]> spectrumToCepstrum(final Spectrum<Complex[]> fs) {
        for (int i = 0; i < fs.getState().length; i++) {
            final Complex c = fs.getState()[i];
            final double log = Math.log(Math.pow(c.abs(), NaiveSpectrum2CepstrumHelper.SQUARE));
            fs.getState()[i] = new Complex(log);
        }
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);

        final Spectrum<Complex[]> fscep = new Spectrum<Complex[]>(fastFourierTransformer.transform(fs.getState(), TransformType.FORWARD), fs.getFormatInfo());
        for (int i = 0; i < fscep.getState().length; i++) {
            final Complex c = fscep.getState()[i];
            final double sqr = Math.pow(c.abs(), NaiveSpectrum2CepstrumHelper.SQUARE);
            fscep.getState()[i] = new Complex(sqr);
        }
        for (int i = 0; i < NaiveSpectrum2CepstrumHelper.LOW_FILTER; i++) {
            fscep.getState()[i] = new Complex(0);
        }
        for (int i = fscep.getState().length - NaiveSpectrum2CepstrumHelper.HIGH_FILTER; i < fscep.getState().length; i++) {
            fscep.getState()[i] = new Complex(0);
        }

        return fscep;
    }
}
