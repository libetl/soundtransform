package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.infrastructure.service.sound2string.Sound2StringAccessor;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum2CepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToStringHelper;

public abstract class SpectrumAccessor extends Sound2StringAccessor {

    protected SpectrumToStringHelper<Complex[]> provideSpectrumToStringHelper() {
        return new GraphSpectrumToStringHelper(this.provideSpectrumHelper());
    }

    protected SpectrumHelper<Complex[]> provideSpectrumHelper() {
        return new HPSSpectrumHelper();
    }

    protected Spectrum2CepstrumHelper<Complex[]> provideSpectrum2CepstrumHelper() {
        return new NaiveSpectrum2CepstrumHelper();
    }
}
