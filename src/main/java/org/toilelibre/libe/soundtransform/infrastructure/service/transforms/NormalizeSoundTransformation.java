package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;

public class NormalizeSoundTransformation implements SoundTransformation {

	public NormalizeSoundTransformation () {
	}

	@Override
	public Sound transform (Sound input) {
		return NormalizeSoundTransformation.normalize (input);
	}

	private static Sound normalize (Sound sound) {
		long [] data = sound.getSamples ();
		long [] newdata = new long [sound.getSamples ().length];
		// this is the raw audio data -- no header

		// find the max:
		double max = 0;
		for (int i = 0; i < data.length; i++) {
			if (Math.abs (data [i]) > max)
				max = Math.abs (data [i]);
		}

		// now find the result, with scaling:
		double maxValue = Math.pow (256, sound.getNbBytesPerSample ()) - 1;
		double ratio = maxValue / max;
		for (int i = 0; i < data.length; i++) {
			double rescaled = data [i] * ratio;
			newdata [i] = (long) Math.floor (rescaled);
		}

		// normalized result in newdata
		return new Sound (newdata, sound.getNbBytesPerSample (), sound.getSampleRate (), sound.getChannelNum ());
	}
}
