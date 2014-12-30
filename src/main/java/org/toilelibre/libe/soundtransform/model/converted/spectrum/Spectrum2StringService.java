package org.toilelibre.libe.soundtransform.model.converted.spectrum;

public class Spectrum2StringService {

    private final SpectrumToStringHelper spectrumHelper;

    public Spectrum2StringService (){
        this.spectrumHelper = new org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.GraphSpectrumToStringHelper ();
    }

    public String convert (final Spectrum spectrum){
        return this.spectrumHelper.fsToString (spectrum);
    }
}
