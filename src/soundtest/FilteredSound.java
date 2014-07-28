package soundtest;

public class FilteredSound extends Sound {

	int division;

	public FilteredSound (double [] samples,int division, int nbBytesPerFrame) {
	    super (samples, nbBytesPerFrame);
	    this.division = division;
    }
	
	
}
