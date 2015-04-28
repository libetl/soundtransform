package org.toilelibre.libe.soundtransform.model.converted.sound;

final class DefaultSoundToStringService implements SoundToStringService {

    private final SoundToStringHelper helper;

    public DefaultSoundToStringService (final SoundToStringHelper helper1) {
        this.helper = helper1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.toilelibre.libe.soundtransform.model.converted.sound.Sound2StringService
     * #convert(org.toilelibre.libe.soundtransform.model.converted.sound.Sound)
     */
    @Override
    public String convert (final Sound input) {
        return this.helper.process (input);
    }
}
