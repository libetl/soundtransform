package soundtest;

public class FilteredSound extends Sound {

	double[] time;

	public double [] getTime () {
		return time;
	}

	public void setTime (double [] time) {
		this.time = time;
	}

	public FilteredSound (double [] samples, double [] time, int nbBytesPerFrame) {
	    super (samples, nbBytesPerFrame);
	    this.time = time;
    }
	
	
}
