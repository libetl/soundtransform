package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.pda.FrequenciesHelper;

public class PeakFindSoundTransformation extends NoOpFrequencySoundTransformation {

	private double	threshold;
	private int []	loudestfreqs;
	private int	   index;

	public PeakFindSoundTransformation () {
		this.threshold = 100;
	}

	public PeakFindSoundTransformation (double threshold) {
		this.threshold = threshold;
	}

	@Override
	public Sound initSound (Sound input) {
		this.loudestfreqs = new int [(int) (input.getSamples ().length / threshold) + 1];
		this.index = 0;
		return super.initSound (input);
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return this.threshold;
	}

	public int [] getLoudestFreqs () {
		return loudestfreqs;
	}

	@Override
	public FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length) {

		int f0 = FrequenciesHelper.f0(fs, 5);
		int fk = FrequenciesHelper.loudestMultiple (fs, f0, 50, 900);
		this.loudestfreqs [index] = fk;
		this.index++;

		return fs;
	}
}
