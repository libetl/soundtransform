package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

/**
 * Simple proxy to avoid useless parameters in the overriden method
 *
 * @author lionel
 *
 */
public class SimpleFrequencySoundTransformation extends AbstractFrequencySoundTransformation {

	public SimpleFrequencySoundTransformation () {
	}

	@Override
	public double getLowThreshold (final double defaultValue) {
		return defaultValue;
	}

	@Override
	public int getOffsetFromASimpleLoop (final int i, final double step) {
		return 0;
	}

	@Override
	public Sound initSound (final Sound input) {
		final long [] newdata = new long [input.getSamples ().length];
		return new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
	}

	public Spectrum transformFrequencies (final Spectrum fs) {
		return fs;
	}

	public Spectrum transformFrequencies (final Spectrum fs, final int offset) {
		return this.transformFrequencies (fs);
	}

	public Spectrum transformFrequencies (final Spectrum fs, final int offset, final int powOf2NearestLength) {
		return this.transformFrequencies (fs, offset);
	}

	public Spectrum transformFrequencies (final Spectrum fs, final int offset, final int powOf2NearestLength, final int length) {
		return this.transformFrequencies (fs, offset, powOf2NearestLength);
	}

	@Override
	public Spectrum transformFrequencies (final Spectrum fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevel) {
		return this.transformFrequencies (fs, offset, powOf2NearestLength, length);
	}
}
