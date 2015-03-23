package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

final class DefaultSoundPitchAndTempoService implements SoundPitchAndTempoService {

    private final SoundPitchAndTempoHelper helper;

    public DefaultSoundPitchAndTempoService (final SoundPitchAndTempoHelper helper1) {
        this.helper = helper1;
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoService#callTransform(org.toilelibre.libe.soundtransform.model.converted.sound.Sound, float, float)
     */
    @Override
    public Sound callTransform (final Sound sound, final float percent, final float lengthInSeconds) throws SoundTransformException {
        return this.helper.pitchAndSetLength (sound, percent, lengthInSeconds);
    }
}
