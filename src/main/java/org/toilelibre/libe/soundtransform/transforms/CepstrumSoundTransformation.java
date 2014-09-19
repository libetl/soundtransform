package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.pda.FrequenciesHelper;

public class CepstrumSoundTransformation extends NoOpFrequencySoundTransformation {

	private double	threshold;
	private int []	loudestfreqs;
	private int	   index;

	public CepstrumSoundTransformation () {
		this.threshold = 100;
	}

	public CepstrumSoundTransformation (double threshold) {
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
		
		FrequenciesState fscep = FrequenciesHelper.spectrumToCepstrum(fs);

		this.loudestfreqs [index] = FrequenciesHelper.max(fscep);
		this.index++;

		return fscep;
	}
}
