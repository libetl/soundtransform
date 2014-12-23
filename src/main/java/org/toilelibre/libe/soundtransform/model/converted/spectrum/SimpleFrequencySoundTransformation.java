package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

/**
 * Simple proxy to avoid useless parameters in the overriden method
 * @author lionel
 *
 */
public class SimpleFrequencySoundTransformation extends AbstractFrequencySoundTransformation {

	public SimpleFrequencySoundTransformation () {
	}

	@Override
	public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length, boolean soundDetected) {
		return this.transformFrequencies (fs, offset, powOf2NearestLength, length);
	}
	
	public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length){
		return this.transformFrequencies (fs, offset, powOf2NearestLength);
	}

	public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength){
		return this.transformFrequencies (fs, offset);
	}

	public Spectrum transformFrequencies (Spectrum fs, int offset){
		return this.transformFrequencies (fs);
	}
	
	public Spectrum transformFrequencies (Spectrum fs){
		return fs;
	}

	@Override
	public Sound initSound (Sound input) {
		long [] newdata = new long [input.getSamples ().length];
		return new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
	}

	@Override
	public int getOffsetFromASimpleLoop (int i, double step) {
		return 0;
	}

	@Override
	public double getLowThreshold (double defaultValue) {
		return defaultValue;
	}
}
