package org.toilelibre.soundtransform.objects;

import java.util.Arrays;

public class Sound {

	private long []	samples;
	private int	    nbBytesPerSample;
	private int	    freq;

	public long [] getSamples () {
		return samples;
	}

	public Sound (long [] samples, int nbBytesPerSample, int freq) {
		super ();
		this.samples = samples;
		this.nbBytesPerSample = nbBytesPerSample;
		this.freq = freq;
	}

	public int getNbBytesPerSample () {
		return nbBytesPerSample;
	}

	public int getFreq () {
		return freq;
	}
	
	public Sound toSubSound (int beginning, int end){
	    long [] newsamples = Arrays.copyOfRange (
	            this.samples, beginning, end);
	    return new Sound (newsamples, nbBytesPerSample, freq);
	}	

    public Sound concat (Sound... otherSounds){
        int newlength = this.samples.length;
        for (int i = 0 ; i < otherSounds.length ; i++){
            newlength += otherSounds [i].getSamples().length;
        }
        long [] newsamples = new long [newlength];
        System.arraycopy(this.samples, 0, newsamples, 0, this.samples.length);
        int newindex = this.samples.length;
        for (int i = 0 ; i < otherSounds.length ; i++){
            System.arraycopy(otherSounds [i].samples, 0, newsamples, newindex, otherSounds [i].samples.length);
            newindex += otherSounds [i].samples.length;
        }
        return new Sound (newsamples, nbBytesPerSample, freq);
    }

}
