package org.toilelibre.libe.soundtransform.model.library.note;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoService;

public class SimpleNote implements Note {

	private Sound []	 attack;
	private Sound []	 decay;
	private Sound []	 sustain;
	private Sound []	 release;
	private int	         frequency;
	private final String	fileName;

	public SimpleNote (String fileName, Sound [] channels, int frequency, int attack, int decay, int sustain, int release) {
		this.frequency = frequency;
		this.attack = new Sound [channels.length];
		this.decay = new Sound [channels.length];
		this.sustain = new Sound [channels.length];
		this.release = new Sound [channels.length];
		this.fileName = fileName;
		for (int i = 0; i < channels.length; i++) {
			this.attack [i] = channels [i].toSubSound (attack, decay);
			this.decay [i] = channels [i].toSubSound (decay, sustain);
			this.sustain [i] = channels [i].toSubSound (sustain, release);
			this.release [i] = channels [i].toSubSound (release, channels [i].getSamples ().length - 1);
		}
	}

	private float getRatio (Sound [] subsound) {
		float lengthOfSubsound = 1.0f * subsound [0].getSamples ().length / subsound [0].getSampleRate();
		float lengthOfSound = 1.0f * (this.attack [0].getSamples ().length / this.attack [0].getSampleRate() + 
		        this.decay [0].getSamples ().length / this.decay [0].getSampleRate() + 
		        this.sustain [0].getSamples ().length / this.sustain [0].getSampleRate() + 
		        this.release [0].getSamples ().length / this.release [0].getSampleRate());
		return lengthOfSubsound * 1.0f / lengthOfSound;
	}

	private float getPercent (int frequency2) {
		return (float) (frequency2 * 100.0 / this.frequency);
	}

	private Sound get (Sound [] adsr, int channelnum) {
		if (adsr.length == 0) {
			return new Sound (new long [0], 0, 0, 0);
		}
		if (adsr.length <= channelnum) {
			return adsr [adsr.length - 1];
		}
		return adsr [channelnum];
	}

	@Override
	public Sound getAttack (int frequency, int channelnum, float length) {
		return new SoundPitchAndTempoService ().callTransform (this.get (this.attack, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.attack) * length));
	}

	@Override
	public Sound getDecay (int frequency, int channelnum, float length) {
		return new SoundPitchAndTempoService ().callTransform (this.get (this.decay, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.decay) * length));
	}

	@Override
	public Sound getSustain (int frequency, int channelnum, float length) {
		return new SoundPitchAndTempoService ().callTransform (this.get (this.sustain, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.sustain) * length));
	}

	@Override
	public Sound getRelease (int frequency, int channelnum, float length) {
		return new SoundPitchAndTempoService ().callTransform (this.get (this.release, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.release) * length));
	}

	@Override
	public int getFrequency () {
		return this.frequency;
	}

	@Override
	public String getName () {
		return this.fileName;
	}

}
