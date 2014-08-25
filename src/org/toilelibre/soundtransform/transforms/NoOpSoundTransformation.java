package org.toilelibre.soundtransform.transforms;

import org.toilelibre.soundtransform.objects.Sound;

public class NoOpSoundTransformation implements SoundTransformation {

	public NoOpSoundTransformation () {
	}

	@Override
	public Sound transform (Sound input) {
		return NoOpSoundTransformation.noop (input);
	}

	private static Sound noop (Sound sound) {
		long [] data = sound.getSamples ();

		// normalized result in newdata
		long [] newdata = new long [data.length];

		for (int i = 0; i < data.length; i++) {
			newdata [i] = data [i];
		}

		return new Sound (newdata, sound.getNbBytesPerSample (), sound.getFreq ());
	}
}
