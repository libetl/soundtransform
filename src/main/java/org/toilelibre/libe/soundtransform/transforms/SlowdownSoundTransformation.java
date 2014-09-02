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

	private float	factor;
	private Sound	sound;
	private int	  threshold;
	private float	writeIfGreaterEqThan1;
	private int	  additionalFrames;

	public SlowdownSoundTransformation (int threshold, float factor) {
		this.factor = factor;
		this.threshold = threshold;
		this.writeIfGreaterEqThan1 = 0;
		this.additionalFrames = 0;
	}

	@Override
	protected Sound initSound (Sound input) {
		long [] newdata = new long [(int) (input.getSamples ().length * factor)];
		this.sound = new Sound (newdata, input.getNbBytesPerSample (), input.getFreq (), input.getChannelNum ());
		return this.sound;
	}

	@Override
	protected FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxfrequency) {
		int total = (int) (this.sound.getSamples ().length * factor);
		if (total / 100 != 0 &&
				(total / 100 - (total / 100) % this.threshold) != 0 &&
				offset % ( (total / 100 - (total / 100) % this.threshold)) == 0) {
			this.log (new LogEvent (LogLevel.VERBOSE, "SlowdownSoundTransformation : Iteration #" + offset + "/" + (int) (sound.getSamples ().length / factor)));
		}
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
		Complex [] complexArray = fs.getState ();
		float remaining = (float) (factor - Math.floor (factor));
		int padding = (int) Math.floor (this.writeIfGreaterEqThan1 + remaining);
		int loops = (int) (factor + padding - 1);
		this.additionalFrames += loops;
		for (int p = 0; p < loops; p++) {
			complexArray = fastFourierTransformer.transform (complexArray, TransformType.INVERSE);

			for (int j = 0; j < maxfrequency; j++) {
				if (offset + p * maxfrequency + j < this.sound.getSamples ().length && this.sound.getSamples () [(int) (offset + p * maxfrequency + j)] == 0) {
					this.sound.getSamples () [(int) (offset + p * maxfrequency + j)] = (long) Math.floor (complexArray [j].getReal ());
				}
			}
		}
		if (padding == 1) {
			this.writeIfGreaterEqThan1 -= 1;
		} else {
			this.writeIfGreaterEqThan1 += remaining;
		}
		return fs;
	}

	@Override
	protected int getOffsetFromASimpleLoop (int i, double step) {
		return (int) (additionalFrames * threshold);
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return threshold;
	}

}
