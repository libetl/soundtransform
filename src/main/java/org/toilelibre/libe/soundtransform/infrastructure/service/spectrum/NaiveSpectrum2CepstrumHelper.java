package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;

final class NaiveSpectrum2CepstrumHelper implements SpectrumToCepstrumHelper<Complex[]> {

    @Override
    public Spectrum<Complex[]> spectrumToCepstrum(final Spectrum<Complex[]> spectrum) {
        Spectrum<Complex[]> logSpectrum = new Spectrum<Complex[]>(spectrum.getState().clone(), spectrum.getFormatInfo());
        for (int i = 0; i < logSpectrum.getState().length; i++) {
            logSpectrum.getState()[i] = new Complex(Math.log(spectrum.getState() [i].abs()));
        }
        final FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);

        final Spectrum<Complex[]> fscep = new Spectrum<Complex[]>(fastFourierTransformer.transform(logSpectrum.getState().clone(), TransformType.INVERSE), logSpectrum.getFormatInfo());
        for (int i = 0; i < fscep.getState().length; i++) {
            fscep.getState()[i] = new Complex (Math.abs(fscep.getState() [i].getReal()), 0);
        }
        fscep.getState()[0] = new Complex (0, 0);
        fscep.getState()[1] = new Complex (0, 0);
        fscep.getState()[2] = new Complex (0, 0);
        fscep.getState()[fscep.getState().length - 1] = new Complex (0, 0);
        fscep.getState()[fscep.getState().length - 2] = new Complex (0, 0);
        fscep.getState()[fscep.getState().length - 3] = new Complex (0, 0);
        return fscep;
    }
}
