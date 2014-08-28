package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.LogEvent;
import org.toilelibre.libe.soundtransform.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SpeedUpSoundTransformation extends AbstractFrequencySoundTransformation {

	private float	  factor;
	private Sound	sound;
	private int	  threshold;
	private float writeIfGreaterEqThanFactor;

	public SpeedUpSoundTransformation (int threshold, float factor) {
		this.factor = factor;
		this.threshold = threshold;
		this.writeIfGreaterEqThanFactor = 0;
	}

	@Override
	protected Sound initSound (Sound input) {
		long [] newdata = new long [(int)(input.getSamples ().length / factor)];
		this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getFreq ());
		return this.sound;
	}

	@Override
	protected FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxfrequency) {
		int total = (int)(this.sound.getSamples ().length / factor);
		if (offset % ( (total / 100 - (total / 100) % this.threshold)) == 0) {
			this.log (new LogEvent (LogLevel.VERBOSE, "SpeedUpSoundTransformation : Iteration #" + offset + "/" + (int)(sound.getSamples ().length * factor)));
		}
		if (this.writeIfGreaterEqThanFactor >= factor){
			this.writeIfGreaterEqThanFactor -= factor;
			return fs;
		}else{
			this.writeIfGreaterEqThanFactor++;
			return null;
		}
	}

	@Override
	protected int getOffsetFromASimpleLoop (int i, double step) {
		return (int)(-i * (factor - 1) / factor);
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return threshold;
	}

}
