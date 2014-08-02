package org.toilelibre.soundtransform;

public class FilteredSound extends Sound {

	int division;

	public FilteredSound (double [] samples,int division, int nbBytesPerFrame, int freq) {
	    super (samples, nbBytesPerFrame, freq);
	    this.division = division;
    }
	
	
}
