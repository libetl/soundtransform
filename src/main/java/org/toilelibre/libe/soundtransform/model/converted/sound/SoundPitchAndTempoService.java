package org.toilelibre.libe.soundtransform.model.converted.sound;

public class SoundPitchAndTempoService {

    private final SoundPitchAndTempoHelper helper;

    public SoundPitchAndTempoService (SoundPitchAndTempoHelper helper1) {
        this.helper = helper1;
    }

    public Sound callTransform (final Sound sound, final float percent, final float lengthInSeconds) {
        return this.helper.pitchAndSetLength (sound, percent, lengthInSeconds);
    }
}
