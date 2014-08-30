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

	public Sound concat (boolean inPlace, int offset, Sound... otherSounds) {
		int newlength = offset;
		for (int i = 0; i < otherSounds.length; i++) {
			newlength += otherSounds [i].getSamples ().length;
		}
		long [] newsamples;
		if (newlength <= this.getSamples().length && inPlace){
			newsamples = this.samples;
		}else{
			newsamples = new long [newlength];
			System.arraycopy (this.samples, 0, newsamples, 0, this.samples.length);
		}
		int newindex = offset;
		for (int i = 0; i < otherSounds.length; i++) {
			System.arraycopy (otherSounds [i].samples, 0, newsamples, newindex, otherSounds [i].samples.length);
			newindex += otherSounds [i].samples.length;
		}
		return new Sound (newsamples, nbBytesPerSample, freq, channelNum);
	}

}
