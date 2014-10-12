package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.NoOpFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;

public class CepstrumSoundTransformation extends NoOpFrequencySoundTransformation {

	private double	   threshold;
	private int []	   loudestfreqs;
	private int	       index;
	private int	       length;
	private static int	shortSoundLength	= 9000;

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
		this.length = input.getSamples ().length;
		if (this.length < CepstrumSoundTransformation.shortSoundLength) {
			this.loudestfreqs = new int [1];
		} else {
			this.loudestfreqs = new int [(int) (input.getSamples ().length / threshold) + 1];
		}
		return super.initSound (input);
	}

	@Override
	public double getLowThreshold (double defaultValue) {
		if (this.length < CepstrumSoundTransformation.shortSoundLength) {
			return this.length;
		}
		return this.threshold;
	}

	@Override
	public int getWindowLength (double freqmax) {
		if (this.length < CepstrumSoundTransformation.shortSoundLength) {
			return (int) Math.pow (2, Math.ceil (Math.log (this.length) / Math.log (2)));
		}
		return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
	}

	public int [] getLoudestFreqs () {
		return loudestfreqs;
	}

	@Override
	public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length) {

		Spectrum fscep = SpectrumHelper.spectrumToCepstrum (fs);

		this.loudestfreqs [index] = SpectrumHelper.getMaxIndex (fscep, 0, fs.getSampleRate ());
		this.index++;

		return fscep;
	}
}
