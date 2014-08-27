package org.toilelibre.soundtransform.objects;

import org.toilelibre.soundtransform.transforms.PitchSoundTransformation;
import org.toilelibre.soundtransform.transforms.SlowdownSoundTransformation;
import org.toilelibre.soundtransform.transforms.SpeedUpSoundTransformation;

public class SimpleNote implements Note {

	private Sound []	attack;
	private Sound []	decay;
	private Sound []	sustain;
	private Sound []	release;
	private int	     frequency;

	public SimpleNote (Sound [] channels, int frequency, int attack, int decay, int sustain, int release) {
		this.frequency = frequency;
		this.attack = new Sound [channels.length];
		this.decay = new Sound [channels.length];
		this.sustain = new Sound [channels.length];
		this.release = new Sound [channels.length];
		for (int i = 0; i < channels.length; i++) {
			this.attack [i] = channels [i].toSubSound (attack, decay);
			this.decay [i] = channels [i].toSubSound (decay, sustain);
			this.sustain [i] = channels [i].toSubSound (sustain, release);
			this.release [i] = channels [i].toSubSound (release, channels [i].getSamples ().length - 1);
		}
	}

	@Override
	public Sound [] getAttack (int frequency, int length) {
		return this.transformSubsound (this.attack, frequency, (int) 0.1 * length);
	}

	@Override
	public Sound [] getDecay (int frequency, int length) {
		return this.transformSubsound (this.decay, frequency, (int) 0.2 * length);
	}

	@Override
	public Sound [] getSustain (int frequency, int length) {
		return this.transformSubsound (this.sustain, frequency, (int) 0.5 * length);
	}

	@Override
	public Sound [] getRelease (int frequency, int length) {
		return this.transformSubsound (this.release, frequency, (int) 0.2 * length);
	}

	private Sound [] transformSubsound (Sound [] subSound, int frequency, int length) {

		int percent = (int) (frequency * 100.0 / this.frequency);
		Sound [] result = new Sound [subSound.length];

		PitchSoundTransformation pitcher = new PitchSoundTransformation (percent);
		if (percent < 98 || percent > 102) {
			for (int i = 0; i < result.length; i++) {
				result [i] = pitcher.transform (subSound [i]);
			}
		}
		double factor = length / subSound.length;
		if (factor < 0.98 || factor > 1.02) {
			if (factor < 0.98) {
				SpeedUpSoundTransformation speedup = new SpeedUpSoundTransformation (100, percent);
				for (int i = 0; i < result.length; i++) {
					result [i] = speedup.transform (subSound [i]);
				}

			} else if (factor > 1.02) {
				SlowdownSoundTransformation slowdown = new SlowdownSoundTransformation (100, percent);
				for (int i = 0; i < result.length; i++) {
					result [i] = slowdown.transform (subSound [i]);
				}
			}
		}
		return result;
	}

}
