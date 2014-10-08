package org.toilelibre.libe.soundtransform.objects;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.transforms.ToStringSoundTransformation;

public class Sound {

	private long []	samples;
	private int	    nbBytesPerSample;
	private int	    freq;
	private int	    channelNum;

	public long [] getSamples () {
		return samples;
	}

	public Sound (long [] samples, int nbBytesPerSample, int freq, int channelNum) {
		super ();
		this.samples = samples;
		this.nbBytesPerSample = nbBytesPerSample;
		this.freq = freq;
		this.channelNum = channelNum;
	}

	public int getNbBytesPerSample () {
		return nbBytesPerSample;
	}

	public int getFreq () {
		return freq;
	}

	public int getChannelNum () {
		return channelNum;
	}

	public Sound toSubSound (int beginning, int end) {
		long [] newsamples = (beginning < end ? Arrays.copyOfRange (this.samples, beginning, end) : new long [0]);
		return new Sound (newsamples, nbBytesPerSample, freq, channelNum);
	}

	public void append (int usedarraylength, Sound... otherSounds) {
		int offset = usedarraylength;
		for (int i = 0; i < otherSounds.length; i++) {
			Sound otherSound = otherSounds [i];
			for (int j = 0; j < otherSound.getSamples ().length; j++) {
				int iter = Math.max (1, otherSound.nbBytesPerSample - this.nbBytesPerSample + 1);
				int pow = Math.max (0, this.nbBytesPerSample - otherSound.nbBytesPerSample);
				long multiple = (pow > 0 ? (long) Math.pow (256, pow) / 2 : 1);
				long sampleValue = otherSound.getSamples () [j];
				for (int k = iter - 1; k >= 0 ; k--){
				  long divide = (long) Math.pow (256, k);
				  if (offset < this.getSamples ().length) {
					  long newvalue = (long) (sampleValue * multiple / divide);
					  this.getSamples () [offset++] = newvalue;
					  sampleValue %= divide;
				  }
				}
			}
		}
	}

	public String toString () {
		return new ToStringSoundTransformation (8000, 20).toString (this);
	}
}
