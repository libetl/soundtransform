package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.apache.commons.math3.complex.Complex;

public class Spectrum {

	private Complex []	state;
	private int	       sampleRate;
	private int        nbBytes;

	public Spectrum (Complex [] state, int sampleRate, int nbBytes) {
		super ();
		this.state = state;
		this.sampleRate = sampleRate;
	}

	public Complex [] getState () {
		return state;
	}

	public int getSampleRate () {
		return sampleRate;
	}
	
	public int getNbBytes () {
		return nbBytes;
	}

	public void setNbBytes (int nbBytes) {
		this.nbBytes = nbBytes;
	}

	public String toString () {
		return new Spectrum2StringService ().convert (this);
	}
}
