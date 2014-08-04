package org.toilelibre.soundtransform;

public class Sound {

	private double[] samples;
	private byte [] raw;
	private int nbBytesPerFrame;
	private int freq;
	
	public double [] getSamples () {
		return samples;
	}

	public Sound (double [] samples, int nbBytesPerFrame, int freq) {
		super ();
	    this.samples = samples;
	    this.nbBytesPerFrame = nbBytesPerFrame;
	    this.freq = freq;
    }

	public Sound (double [] samples, byte [] raw, int nbBytesPerFrame, int freq) {
	    this (samples, nbBytesPerFrame, freq);
	    this.samples = samples;
	    this.nbBytesPerFrame = nbBytesPerFrame;
	    this.freq = freq;
	    this.raw = raw;
    }

	public int getNbBytesPerFrame () {
		return nbBytesPerFrame;
	}

	public int getFreq() {
		return freq;
	}

	public byte [] getRaw () {
		return raw;
	}
	
}
