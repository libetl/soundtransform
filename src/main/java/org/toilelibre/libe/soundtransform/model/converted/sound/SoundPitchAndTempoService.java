package org.toilelibre.libe.soundtransform.model.converted.sound;

public class SoundPitchAndTempoService {

	private final SoundPitchAndTempoHelper	helper;

	public SoundPitchAndTempoService () {
		this.helper = new org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundPitchAndTempoHelper ();
	}

	public Sound callTransform (final Sound sound, final float percent, final float lengthInSeconds) {
		return this.helper.pitchAndSetLength (sound, percent, lengthInSeconds);
	}
}
