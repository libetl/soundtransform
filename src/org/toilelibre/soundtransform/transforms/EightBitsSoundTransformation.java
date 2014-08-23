package org.toilelibre.soundtransform.transforms;

import org.toilelibre.soundtransform.objects.Sound;

public class EightBitsSoundTransformation implements SoundTransformation {

	private int step = 1;

	public EightBitsSoundTransformation(int step) {
		this.step = step;
	}

	@Override
	public Sound transform(Sound input) {

		Sound outputSound = new Sound(new long[input.getSamples().length],
				input.getNbBytesPerSample(), input.getFreq());
		for (int i = 0; i < input.getSamples().length; i++) {
			if (i % step == 0) {
				outputSound.getSamples()[i] = input.getSamples()[i];
			} else {
				outputSound.getSamples()[i] = 0;
			}
		}

		return outputSound;
	}

}
