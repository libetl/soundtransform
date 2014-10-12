package org.toilelibre.libe.soundtransform.model.converted.sound;

public class SoundToStringService {

	private Sound2StringHelper	helper;

	public SoundToStringService () {
		this.helper = new org.toilelibre.libe.soundtransform.infrastructure.service.sound2string.GraphSound2StringHelper ();
	}

	public String convert (Sound input) {
		return this.helper.process (input);
	}
}
