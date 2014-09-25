package org.toilelibre.libe.soundtransform.transforms;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.pda.FrequenciesHelper;

public class PeakFindSoundTransformation extends NoOpFrequencySoundTransformation {

	private double	   threshold;
	private int []	   loudestfreqs;
	private int	       index;
	private int	       length;
	private static int	shortSoundLength	= 9000;

	public PeakFindSoundTransformation () {
		this.threshold = 100;
	}

	public PeakFindSoundTransformation (double threshold) {
		this.threshold = threshold;
	}

	@Override
	public Sound initSound (Sound input) {
		this.index = 0;
		this.length = input.getSamples ().length;
		if (this.length < PeakFindSoundTransformation.shortSoundLength) {
			this.loudestfreqs = new int [1];
		} else {
			this.loudestfreqs = new int [(int) (input.getSamples ().length / threshold) + 1];
		}
		return super.initSound (input);
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		if (this.length < PeakFindSoundTransformation.shortSoundLength) {
			return this.length;
		}		
		return this.threshold;
	}

	@Override
	protected int getWindowLength (double freqmax) {
		if (this.length < PeakFindSoundTransformation.shortSoundLength) {
			return (int) Math.pow (2, Math.ceil (Math.log (this.length) / Math.log (2)));
		}
		return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
	}

	public int [] getLoudestFreqs () {
		return loudestfreqs;
	}

	@Override
	public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length) {

        int[] peaks = new int [10];
	    for (int i = 1 ; i <= 10 ; i++){
	        peaks [i - 1] = FrequenciesHelper.f0(fs, i);
	    }
        Arrays.sort(peaks);
        int f0 = this.bestCandidate (peaks);

        if (this.index < this.loudestfreqs.length){
		  this.loudestfreqs [this.index] = f0;
        }
		this.index++;

		return fs;
	}

    private int bestCandidate(int[] peaks) {
        int leftEdge = 0;
        while (leftEdge < peaks.length && peaks [leftEdge] <= 0){
            leftEdge++;
        }
        int rightEdge = leftEdge;
        while (rightEdge < peaks.length &&
                Math.abs((peaks[rightEdge] - peaks [leftEdge])  * 1.0 / peaks[rightEdge]) * 100.0 < 10){
            rightEdge++;
        }
        int sum = 0;
        for (int i = leftEdge ; i < rightEdge ; i++){
            sum += peaks [i];
        }

        return (rightEdge == leftEdge ? sum : sum / (rightEdge - leftEdge));
    }
}
