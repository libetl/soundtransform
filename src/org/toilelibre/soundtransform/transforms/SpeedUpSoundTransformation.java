package org.toilelibre.soundtransform.transforms;

import org.toilelibre.soundtransform.objects.FrequenciesState;
import org.toilelibre.soundtransform.objects.Sound;
import org.toilelibre.soundtransform.observer.LogEvent;
import org.toilelibre.soundtransform.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SpeedUpSoundTransformation extends AbstractFrequencySoundTransformation {

	private int	  factor;
	private Sound	sound;
	private int	  threshold;

	public SpeedUpSoundTransformation (int threshold, int factor) {
		this.factor = factor;
		this.threshold = threshold;
	}

	@Override
	protected Sound initSound (Sound input) {
		long [] newdata = new long [input.getSamples ().length / factor];
		this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getFreq ());
		return this.sound;
	}

	@Override
	protected FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxfrequency) {
		int total = this.sound.getSamples ().length / factor;
		if (offset % ( (total / 100 - (total / 100) % this.threshold)) == 0) {
			this.log (new LogEvent (LogLevel.VERBOSE, "SpeedUpSoundTransformation : Iteration #" + offset + "/" + sound.getSamples ().length * factor));
		}
		if (offset % (threshold * factor) == 0){
			return fs;
		}
		return null;
	}

	@Override
	protected int getOffsetFromASimpleLoop (int i, double step) {
		return - i * (factor - 1) / factor;
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return threshold;
	}

}
