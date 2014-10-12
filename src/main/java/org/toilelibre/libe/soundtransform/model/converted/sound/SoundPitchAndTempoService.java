package org.toilelibre.libe.soundtransform.model.converted.sound;

public class SoundPitchAndTempoService {

	private SoundPitchAndTempoHelper	helper;

	public SoundPitchAndTempoService () {
		this.helper = new org.toilelibre.libe.soundtransform.infrastructure.service.appender.ConvertedSoundPitchAndTempoHelper ();
	}

	public Sound callTransform (Sound sound, float percent, int length) {
		return this.helper.pitchAndSetLength (sound, percent, length);
	}
}
