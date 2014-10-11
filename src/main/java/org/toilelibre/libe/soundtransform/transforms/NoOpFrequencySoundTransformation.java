package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.objects.Spectrum;
import org.toilelibre.libe.soundtransform.objects.Sound;

public class NoOpFrequencySoundTransformation extends AbstractFrequencySoundTransformation {

	public NoOpFrequencySoundTransformation () {
	}

	@Override
	public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length) {
		return fs;
	}

	@Override
	public Sound initSound (Sound input) {
		long [] newdata = new long [input.getSamples ().length];
		return new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
	}

	@Override
	protected int getOffsetFromASimpleLoop (int i, double step) {
		return 0;
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return defaultValue;
	}
}
