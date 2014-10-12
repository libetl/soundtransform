package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.apache.commons.math3.complex.Complex;

public class Spectrum {

	private Complex []	state;
	private int	       sampleRate;

	public Spectrum (Complex [] state, int sampleRate) {
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

	public String toString () {
		return SpectrumHelper.fsToString (this);
	}
}
