package org.toilelibre.libe.soundtransform.sound;

import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.transforms.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.transforms.SpeedUpSoundTransformation;

public class SoundPitchAndTempoHelper {

	public static Sound pitchAndSetLength (Sound sound, float percent, int length) {

		Sound result = sound;

		PitchSoundTransformation pitcher = new PitchSoundTransformation (percent);
		if (percent < 98 || percent > 102) {
			result = pitcher.transform (result);
		}
		double factor = sound.getSamples ().length == 0 ? 0 : length * 1.0 / result.getSamples ().length;
		if (factor == 0) {
			return result;
		} else if (factor < 0.98 || factor > 1.02) {
			if (factor < 0.98) {
				SpeedUpSoundTransformation speedup = new SpeedUpSoundTransformation (100, (float) (1 / factor));
				result = speedup.transform (result);

			} else if (factor > 1.02) {
				SlowdownSoundTransformation slowdown = new SlowdownSoundTransformation (100, (float) factor);
				result = slowdown.transform (result);
			}
		}
		return result;
	}
}
