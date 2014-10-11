package org.toilelibre.libe.soundtransform.objects;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.transforms.ToStringSoundTransformation;

public class Sound {

	private long []	samples;
	private int	    nbBytesPerSample;
	private int	    sampleRate;
	private int	    channelNum;

	public long [] getSamples () {
		return samples;
	}

	public Sound (long [] samples, int nbBytesPerSample, int sampleRate, int channelNum) {
		super ();
		this.samples = samples;
		this.nbBytesPerSample = nbBytesPerSample;
		this.sampleRate = sampleRate;
		this.channelNum = channelNum;
	}

	public int getNbBytesPerSample () {
		return nbBytesPerSample;
	}

	public int getSampleRate () {
		return sampleRate;
	}

	public int getChannelNum () {
		return channelNum;
	}

	public Sound toSubSound (int beginning, int end) {
		long [] newsamples = (beginning < end ? Arrays.copyOfRange (this.samples, beginning, end) : new long [0]);
		return new Sound (newsamples, nbBytesPerSample, sampleRate, channelNum);
	}

	public String toString () {
		return new ToStringSoundTransformation (8000, 20).toString (this);
	}
}
