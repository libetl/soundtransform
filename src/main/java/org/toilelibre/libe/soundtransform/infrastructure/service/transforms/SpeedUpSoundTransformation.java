package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SpeedUpSoundTransformation extends SimpleFrequencySoundTransformation {

	private float	factor;
	private Sound	sound;
	private int	  threshold;
	private float	writeIfGreaterEqThanFactor;

	public SpeedUpSoundTransformation (int threshold, float factor) {
		this.factor = factor;
		this.threshold = threshold;
		this.writeIfGreaterEqThanFactor = 0;
	}

	@Override
	public Sound initSound (Sound input) {
		long [] newdata = new long [(int) (input.getSamples ().length / factor)];
		this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
		return this.sound;
	}

	@Override
	public Spectrum transformFrequencies (Spectrum fs, int offset) {
		int total = (int) (this.sound.getSamples ().length / factor);
		if (total / 100 != 0 && (total / 100 - (total / 100) % this.threshold) != 0 && offset % ( (total / 100 - (total / 100) % this.threshold)) == 0) {
			this.log (new LogEvent (LogLevel.VERBOSE, "SpeedUpSoundTransformation : Iteration #" + offset + "/" + (int) (sound.getSamples ().length * factor)));
		}
		if (this.writeIfGreaterEqThanFactor >= factor) {
			this.writeIfGreaterEqThanFactor -= factor;
			return fs;
		} else {
			this.writeIfGreaterEqThanFactor++;
			return null;
		}
	}

	@Override
	public int getOffsetFromASimpleLoop (int i, double step) {
		return (int) (-i * (factor - 1) / factor);
	}

	@Override
	public double getLowThreshold (double defaultValue) {
		return threshold;
	}

}
