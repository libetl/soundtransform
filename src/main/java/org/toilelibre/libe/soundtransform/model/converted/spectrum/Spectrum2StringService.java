package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public class Spectrum2StringService {

    private final SpectrumToStringHelper spectrumHelper;

    public Spectrum2StringService (SpectrumToStringHelper helper1) {
        this.spectrumHelper = helper1;
    }

    public String convert (final Spectrum spectrum) {
        return this.spectrumHelper.fsToString (spectrum);
    }
}
