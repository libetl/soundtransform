package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.objects.Sound;

public class PitchSoundTransformation implements SoundTransformation {

	private int	percent	= 20;

	public PitchSoundTransformation (int percent) {
		this.percent = percent;
	}

	@Override
	public Sound transform (Sound input) {
		return PitchSoundTransformation.pitch (input, this.percent);
	}

	private static Sound pitch (Sound sound, float percent) {
		float total = 100;
		if (percent == total) {
			return new Sound (sound.getSamples (), sound.getNbBytesPerSample (), sound.getSampleRate (), sound.getChannelNum ());
		}
		float nbSamples = sound.getSamples ().length;
		float nbFiltered = Math.abs (percent / total * nbSamples);
		float incr = nbSamples / nbFiltered;
		long [] data = sound.getSamples ();
		long [] ret = new long [(int) (nbFiltered)];
		for (float i = 0; i < incr * nbFiltered; i += incr) {
			int j = (int) (i / incr);
			if (j < ret.length) {
				ret [j] = data [(int) i];
			}
		}
		return new Sound (ret, sound.getNbBytesPerSample (), sound.getSampleRate (), sound.getChannelNum ());
	}
}
