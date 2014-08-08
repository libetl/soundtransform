package org.toilelibre.soundtransform.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.soundtransform.objects.FrequenciesState;
import org.toilelibre.soundtransform.objects.Sound;

public abstract class AbstractFrequencySoundTransformation implements SoundTransformation {
	

	public AbstractFrequencySoundTransformation () {
    }

	protected abstract Sound initSound (Sound input);
	
	protected abstract FrequenciesState transformFrequencies (FrequenciesState fs, int offset, int powOf2NearestLength, int length, double maxfrequency);

	protected abstract int getOffsetFromASimpleLoop(int i, double step);
	
	protected abstract double getLowThreshold (double defaultValue);
	
	public Sound transform (Sound sound) {
	    Sound output = this.initSound(sound);
		double freqmax = sound.getFreq();
		double threshold = this.getLowThreshold (freqmax);
		int maxlength = (int)Math.pow (2, Math.ceil (Math.log (freqmax) / Math.log (2)));
		double [] data = sound.getSamples ();
		double [] newdata = output.getSamples();
		double [] transformeddata = new double [maxlength];
		
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer ( DftNormalization.STANDARD);
		for (int i = 0 ; i < data.length ; i+= threshold){
			int length = Math.min (maxlength, data.length - i);
	   		System.arraycopy (data, i, transformeddata, 0, length);			
			Complex [] complexArray = fastFourierTransformer.transform (transformeddata, TransformType.FORWARD);
			FrequenciesState fs = new FrequenciesState (complexArray);
			FrequenciesState result = this.transformFrequencies(fs, i, maxlength, length, freqmax);
			complexArray = fastFourierTransformer.transform (result.getState(), TransformType.INVERSE);
			
			int k = this.getOffsetFromASimpleLoop (i, freqmax);

			for (int j = 0 ; j < freqmax ; j++){
				if (i + j + k < newdata.length && newdata [i + j + k] == 0){
			          newdata [i + j + k] = Math.floor(complexArray [j].getReal ());
				}
			}
		}
		// normalized result in newdata
		return output;
	}

}
