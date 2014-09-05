package org.toilelibre.libe.soundtransform.objects;

import java.util.Arrays;

public class Sound {

	private long []	samples;
	private int	    nbBytesPerSample;
	private int	    freq;
	private int	    channelNum;

	public long [] getSamples () {
		return samples;
	}

	public Sound (long [] samples, int nbBytesPerSample, int freq, int channelNum) {
		super ();
		this.samples = samples;
		this.nbBytesPerSample = nbBytesPerSample;
		this.freq = freq;
		this.channelNum = channelNum;
	}

	public int getNbBytesPerSample () {
		return nbBytesPerSample;
	}

	public int getFreq () {
		return freq;
	}

	public int getChannelNum () {
		return channelNum;
	}

	public Sound toSubSound (int beginning, int end) {
		long [] newsamples = Arrays.copyOfRange (this.samples, beginning, end);
		return new Sound (newsamples, nbBytesPerSample, freq, channelNum);
	}

	public void append (int usedarraylength, Sound... otherSounds) {
		int offset = usedarraylength;
		for (int i = 0; i < otherSounds.length; i++) {
			Sound otherSound = otherSounds [i];
			for (int j = 0; j < otherSound.getSamples ().length; j++) {
				if (offset < this.getSamples ().length) {
					this.getSamples () [offset++] = otherSound.getSamples () [j];
				}
			}
		}

	}
}
