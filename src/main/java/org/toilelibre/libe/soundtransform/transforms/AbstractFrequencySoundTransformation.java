package org.toilelibre.libe.soundtransform.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.observer.LogAware;
import org.toilelibre.libe.soundtransform.observer.LogEvent;
import org.toilelibre.libe.soundtransform.observer.TransformObserver;

public abstract class AbstractFrequencySoundTransformation implements SoundTransformation, LogAware {

	private TransformObserver []	observers;

	public AbstractFrequencySoundTransformation () {
	}

	protected abstract Sound initSound (Sound input);

	protected abstract FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length);

	protected abstract int getOffsetFromASimpleLoop (int i, double step);

	protected abstract double getLowThreshold (double defaultValue);

	protected int getWindowLength (double freqmax) {
		return (int) Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
	}

	@Override
	public void setObservers (TransformObserver [] observers1) {
		this.observers = observers1;
	}

	@Override
	public void log (LogEvent logEvent) {
		if (this.observers == null) {
			return;
		}
		for (TransformObserver transformObserver : this.observers) {
			transformObserver.notify (logEvent);
		}
	}

	@Override
	public Sound transform (Sound sound) {
		Sound output = this.initSound (sound);
		double freqmax = sound.getFreq ();
		double threshold = this.getLowThreshold (freqmax);
		int maxlength = this.getWindowLength (freqmax);
		long [] data = sound.getSamples ();
		long [] newdata = output.getSamples ();
		double [] transformeddata = new double [maxlength];
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);
		for (int i = 0; i < data.length; i += threshold) {
			int length = Math.min (maxlength, data.length - i);
			for (int j = i; j < i + length; j++) {
				transformeddata [j - i] = data [j];
			}
			Complex [] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);
			FrequenciesState fs = new FrequenciesState (complexArray, (int) freqmax);
			FrequenciesState result = this.transformFrequencies (fs, i, maxlength, length);
			if (result == null) {
				continue;
			}
			complexArray = fastFourierTransformer.transform (result.getState (), TransformType.INVERSE);
			int k = this.getOffsetFromASimpleLoop (i, freqmax);
			for (int j = 0; j < freqmax; j++) {
				if (i + j + k < newdata.length && newdata [i + j + k] == 0) {
					newdata [i + j + k] = (long) Math.floor (complexArray [j].getReal ());
				}
			}
		}
		// normalized result in newdata
		return output;
	}

}
