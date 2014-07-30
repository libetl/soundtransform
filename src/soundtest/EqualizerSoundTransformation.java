package soundtest;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class EqualizerSoundTransformation implements SoundTransformation {
	
	private int [] ranges;
	private int [] amplification;

	public EqualizerSoundTransformation (int [] ranges1, int [] amplification1) {
		this.ranges = ranges1;
		this.amplification = amplification1;
    }


	@Override
	public Sound transform (Sound input) {
		return EqualizerSoundTransformation.equalize (input, this.ranges, this.amplification);
	}

	private static Sound equalize (Sound sound, int [] r, int [] a) {
		double [] data = sound.getSamples ();
		double [] newdata = new double [
		              (int)Math.pow (2, Math.ceil (Math.log (
		            		  sound.getSamples ().length) / Math.log (2)))];
   		System.arraycopy (data, 0, newdata, 0, data.length);
		
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer ( DftNormalization.STANDARD);
		Complex[] complexArray = fastFourierTransformer.transform (newdata, TransformType.FORWARD);
		for (int i = 0 ; i < complexArray.length ; i++){
			double module = Math.sqrt (
					Math.pow (complexArray [i].getReal (), 2) +
					Math.pow (complexArray [i].getImaginary (), 2));
			double phase = complexArray [i].getArgument ();
		}
		// normalized result in newdata
		return new Sound (newdata, sound.getNbBytesPerFrame ());
	}
}
