package soundtest;

public class Sound {

	double[] samples;
	int[] audio;
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

	public int [] getAudio () {
		return audio;
	}

	public void setAudio (int [] audio) {
		this.audio = audio;
	}

	public int getNbBytesPerFrame () {
		return nbBytesPerFrame;
	}

	public void setNbBytesPerFrame (int nbBytesPerFrame) {
		this.nbBytesPerFrame = nbBytesPerFrame;
	}
	
	
	
}
