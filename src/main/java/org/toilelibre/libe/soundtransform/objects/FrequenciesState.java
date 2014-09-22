package org.toilelibre.libe.soundtransform.objects;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.pda.FrequenciesHelper;

public class FrequenciesState {

	private Complex []	state;
	private int	       maxfrequency;

	public FrequenciesState (Complex [] state, int maxfrequency) {
		super ();
		this.state = state;
		this.maxfrequency = maxfrequency;
	}

	public Complex [] getState () {
		return state;
	}

	public int getMaxfrequency () {
		return maxfrequency;
	}

	public String toString () {
		return FrequenciesHelper.fsToString (this);
	}
}
