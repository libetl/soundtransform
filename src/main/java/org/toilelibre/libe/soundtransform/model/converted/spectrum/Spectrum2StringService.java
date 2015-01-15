package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public class Spectrum2StringService<T> {

    private final SpectrumToStringHelper<T> spectrumHelper;

    public Spectrum2StringService (SpectrumToStringHelper<T> helper1) {
        this.spectrumHelper = helper1;
    }

    public String convert (final Spectrum<T> spectrum) {
        return this.spectrumHelper.fsToString (spectrum);
    }
}
