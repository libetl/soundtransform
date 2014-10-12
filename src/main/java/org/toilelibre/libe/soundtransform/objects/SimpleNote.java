package org.toilelibre.libe.soundtransform.objects;

import org.toilelibre.libe.soundtransform.sound.SoundPitchAndTempoHelper;

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
		int lengthOfSubsound = subsound [0].getSamples ().length;
		int lengthOfSound = (this.attack [0].getSamples ().length + this.decay [0].getSamples ().length + this.sustain [0].getSamples ().length + this.release [0].getSamples ().length);
		return lengthOfSubsound * 1.0f / lengthOfSound;
	}

	private float getPercent (int frequency2) {
		return (float) (frequency2 * 100.0 / this.frequency);
    }

	private Sound get (Sound [] adsr, int channelnum) {
		if (adsr.length == 0){
			return new Sound (new long [0], 0, 0, 0);
		}
		if (adsr.length <= channelnum){
			return adsr [adsr.length - 1];
		}
	    return adsr [channelnum];
    }
	
	@Override
	public Sound getAttack (int frequency, int channelnum, int length) {
		return SoundPitchAndTempoHelper.pitchAndSetLength (this.get (this.attack, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.attack) * length));
	}

	@Override
	public Sound getDecay (int frequency, int channelnum, int length) {
		return SoundPitchAndTempoHelper.pitchAndSetLength (this.get (this.decay, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.decay) * length));
	}

	@Override
	public Sound getSustain (int frequency, int channelnum, int length) {
		return SoundPitchAndTempoHelper.pitchAndSetLength (this.get (this.sustain, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.sustain) * length));
	}

	@Override
	public Sound getRelease (int frequency, int channelnum, int length) {
		return SoundPitchAndTempoHelper.pitchAndSetLength (this.get (this.release, channelnum), this.getPercent (frequency), (int) (this.getRatio (this.release) * length));
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
