package org.toilelibre.libe.soundtransform.model.converted.sound;

import java.util.Arrays;

public class Sound {

	private final long []	samples;
	private final int	    nbBytesPerSample;
	private final int	    sampleRate;
	private final int	    channelNum;

	public Sound (final long [] samples, final int nbBytesPerSample, final int sampleRate, final int channelNum) {
		super ();
		this.samples = samples;
		this.nbBytesPerSample = nbBytesPerSample;
		this.sampleRate = sampleRate;
		this.channelNum = channelNum;
	}

	public int getChannelNum () {
		return this.channelNum;
	}

	public int getNbBytesPerSample () {
		return this.nbBytesPerSample;
	}

	public int getSampleRate () {
		return this.sampleRate;
	}

	public long [] getSamples () {
		return this.samples;
	}

	@Override
    public String toString () {
		return new SoundToStringService ().convert (this);
	}

	public Sound toSubSound (final int beginning, final int end) {
		final long [] newsamples = beginning < end ? Arrays.copyOfRange (this.samples, beginning, end) : new long [0];
		return new Sound (newsamples, this.nbBytesPerSample, this.sampleRate, this.channelNum);
	}
}
