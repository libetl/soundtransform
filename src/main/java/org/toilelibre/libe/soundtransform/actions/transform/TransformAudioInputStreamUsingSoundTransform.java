package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;

public final class TransformAudioInputStreamUsingSoundTransform extends Action {

	public AudioInputStream transformAudioStream (final AudioInputStream ais, final SoundTransformation... sts) throws IOException {
		return this.transformSound.transformAudioStream (ais, sts);
	}

}
