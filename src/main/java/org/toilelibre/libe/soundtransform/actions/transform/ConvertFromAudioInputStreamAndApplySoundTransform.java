package org.toilelibre.libe.soundtransform.actions.transform;

import javax.sound.sampled.AudioInputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public final class ConvertFromAudioInputStreamAndApplySoundTransform extends Action {

	public Sound [] convertAndApply (final AudioInputStream ais, final SoundTransformation transform) throws SoundTransformException {
		return this.transformSound.convertAndApply (ais, transform);
	}

}
