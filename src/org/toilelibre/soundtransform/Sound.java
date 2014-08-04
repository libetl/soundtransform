package org.toilelibre.soundtransform;

public class Sound {

	private double[] samples;
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

	public int getNbBytesPerFrame () {
		return nbBytesPerFrame;
	}

	public int getFreq() {
		return freq;
	}
	
}
