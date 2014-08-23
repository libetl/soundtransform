package org.toilelibre.soundtransform.objects;

public class Sound {

	private long[] samples;
	private int nbBytesPerSample;
	private int freq;

	public long[] getSamples() {
		return samples;
	}

	public Sound(long[] samples, int nbBytesPerSample, int freq) {
		super();
		this.samples = samples;
		this.nbBytesPerSample = nbBytesPerSample;
		this.freq = freq;
	}

	public int getNbBytesPerSample() {
		return nbBytesPerSample;
	}

	public int getFreq() {
		return freq;
	}

}
