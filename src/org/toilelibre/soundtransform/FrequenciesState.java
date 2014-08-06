package org.toilelibre.soundtransform;

import org.apache.commons.math3.complex.Complex;

public class FrequenciesState {

	private Complex[] state;


    public FrequenciesState(Complex[] state) {
        super();
        this.state = state;
    }
    
    public Complex[] getState() {
        return state;
    }
	
	
}
