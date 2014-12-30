package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public final class ConvertFromAudioInputStreamAndApplySoundTransform extends Action {

	public Sound [] convertAndApply (final AudioInputStream ais, final SoundTransformation transform) throws IOException {
		return this.transformSound.convertAndApply (ais, transform);
	}

}
