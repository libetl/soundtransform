package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.infrastructure.service.sound2string.SoundToStringAccessor;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToStringHelper;

public abstract class SpectrumAccessor extends SoundToStringAccessor {

    protected SpectrumToStringHelper<Complex []> provideSpectrumToStringHelper () {
        return new GraphSpectrumToStringHelper (this.provideSpectrumHelper ());
    }

    protected SpectrumHelper<Complex []> provideSpectrumHelper () {
        return new HPSSpectrumHelper ();
    }

    protected SpectrumToCepstrumHelper<Complex []> provideSpectrum2CepstrumHelper () {
        return new NaiveSpectrumToCepstrumHelper ();
    }
}
