package org.toilelibre.libe.soundtransform.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.LogEvent;
import org.toilelibre.libe.soundtransform.observer.LogEvent.LogLevel;

//WARN : long execution time soundtransform
public class SlowdownSoundTransformation extends AbstractFrequencySoundTransformation {

	private int	  factor;
	private Sound	sound;
	private int	  threshold;

	public SlowdownSoundTransformation (int threshold, int factor) {
		this.factor = factor;
		this.threshold = threshold;
	}

	@Override
	protected Sound initSound (Sound input) {
		long [] newdata = new long [input.getSamples ().length * factor];
		this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getFreq ());
		return this.sound;
	}

	@Override
	protected FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxfrequency) {
		int total = this.sound.getSamples ().length * factor;
		if (offset % ( (total / 100 - (total / 100) % this.threshold)) == 0) {
			this.log (new LogEvent (LogLevel.VERBOSE, "SlowdownSoundTransformation : Iteration #" + offset + "/" + sound.getSamples ().length / factor));
		}
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
		Complex [] complexArray = fs.getState ();
		for (int p = 0; p < factor - 1; p++) {
			complexArray = fastFourierTransformer.transform (complexArray, TransformType.INVERSE);

			for (int j = 0; j < maxfrequency; j++) {
				if (offset + p * maxfrequency + j < this.sound.getSamples ().length && this.sound.getSamples () [(int) (offset + p * maxfrequency + j)] == 0) {
					this.sound.getSamples () [(int) (offset + p * maxfrequency + j)] = (long) Math.floor (complexArray [j].getReal ());
				}
			}
		}
		return fs;
	}

	@Override
	protected int getOffsetFromASimpleLoop (int i, double step) {
		return (factor - 1) * i;
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return threshold;
	}

}
