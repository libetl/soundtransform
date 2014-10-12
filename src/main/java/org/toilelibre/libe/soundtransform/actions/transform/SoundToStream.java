package org.toilelibre.libe.soundtransform.actions.transform;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public final class SoundToStream extends Action {

	public AudioInputStream toStream (Sound [] channels, AudioFormat audioFormat) {
		return this.transformSound.toStream (channels, audioFormat);
	}
}
