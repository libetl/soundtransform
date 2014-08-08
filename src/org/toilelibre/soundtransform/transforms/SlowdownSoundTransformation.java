package org.toilelibre.soundtransform.transforms;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.soundtransform.objects.FrequenciesState;
import org.toilelibre.soundtransform.objects.Sound;

//WARN : long time execution soundtransform
public class SlowdownSoundTransformation extends
        AbstractFrequencySoundTransformation {

	private int times;
	private Sound sound;
	private int threshold;

	public SlowdownSoundTransformation (int threshold) {
		this.times = 2;
		this.threshold = threshold;
    }
	
	@Override
	protected Sound initSound (Sound input) {
        double [] newdata = new double [input.getSamples ().length * times];
        this.sound = new Sound (newdata, input.getNbBytesPerFrame (), input.getFreq());
        return this.sound;
	}

	@Override
	protected FrequenciesState transformFrequencies (FrequenciesState fs,
	        int offset, int powOf2NearestLength, int length, double maxfrequency) {
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer ( DftNormalization.STANDARD);
		Complex [] complexArray = fs.getState ();
		for (int p = 0 ; p < times - 1 ; p++){
			complexArray = fastFourierTransformer.transform (complexArray, TransformType.INVERSE);
			
			for (int j = 0 ; j < maxfrequency ; j++){
				if (offset + p * maxfrequency + j < this.sound.getSamples ().length &&
						this.sound.getSamples () [(int)(offset + p * maxfrequency + j)] == 0){
			      this.sound.getSamples () [(int)(offset + p * maxfrequency + j)] = Math.floor(complexArray [j].getReal ());
				}
			}
		}
		return fs;
	}

	@Override
	protected int getOffsetFromASimpleLoop (int i, double step) {
		return (int)((times - 1) * i);
	}

	@Override
    protected double getLowThreshold (double defaultValue) {
	    return threshold;
    }

}
