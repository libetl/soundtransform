package org.toilelibre.libe.soundtransform.model.converted.spectrum;

final class DefaultSpectrumToStringService<T> implements SpectrumToStringService<T> {

    private final SpectrumToStringHelper<T> spectrumHelper;

    public DefaultSpectrumToStringService (final SpectrumToStringHelper<T> helper1) {
        this.spectrumHelper = helper1;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.model.converted.spectrum.
     * Spectrum2StringHelper
     * #convert(org.toilelibre.libe.soundtransform.model.converted
     * .spectrum.Spectrum)
     */
    @Override
    public String convert (final Spectrum<T> spectrum) {
        return this.spectrumHelper.fsToString (spectrum);
    }
}
