package soundtest;

public class Sound {

	double[] samples;
	int nbBytesPerFrame;
	
	public double [] getSamples () {
		return samples;
	}

	public void setSamples (double [] samples) {
		this.samples = samples;
	}

	public Sound (double [] samples, int nbBytesPerFrame) {
	    super ();
	    this.samples = samples;
	    this.nbBytesPerFrame = nbBytesPerFrame;
    }

	public int getNbBytesPerFrame () {
		return nbBytesPerFrame;
	}

	public void setNbBytesPerFrame (int nbBytesPerFrame) {
		this.nbBytesPerFrame = nbBytesPerFrame;
	}
	
	
	
}
