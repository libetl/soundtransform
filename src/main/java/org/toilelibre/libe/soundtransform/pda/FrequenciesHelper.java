package org.toilelibre.libe.soundtransform.pda;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;

public class FrequenciesHelper {

	public static final int MAX_EAR_FREQUENCY = 44100;

	public static int freqFromSampleRate (int freq, int sampleRate){
	    return (int)(freq * MAX_EAR_FREQUENCY / (sampleRate * 2.0));
	}

	public static int toSampleRate (int freq, int sampleRate){
	    return (int)(freq * (sampleRate * 2.0) / MAX_EAR_FREQUENCY);
	}

	public static int f0 (FrequenciesState fs, int hpsfactor) {
		return FrequenciesHelper.freqFromSampleRate(
		        FrequenciesHelper.getMaxIndex (
		                FrequenciesHelper.hps (fs, hpsfactor), 0, fs.getState().length / hpsfactor), 
		                fs.getState().length * 2 / hpsfactor);
	}


	private static FrequenciesState hps (FrequenciesState fs, int factor) {
		int max = fs.getState ().length / factor;
		Complex [] result = new Complex [max];
		for (int i = 0; i < max; i++) {
			double val = fs.getState () [i].abs ();
			for (int j = 1; j < factor; j++) {
				if (i * factor < fs.getMaxfrequency () / 2 &&
						i * factor < fs.getState ().length) {
					val *= fs.getState () [i * factor].abs ();
				}
			}
			result [i] = new Complex (val);
		}
		return new FrequenciesState (result, fs.getMaxfrequency () / factor);
	}

	public static String fsToString (FrequenciesState fs) {
		return FrequenciesHelper.fsToString (fs, 0, (int) fs.getMaxfrequency () / 2, 20, 20);
	}

	public static String fsToString (FrequenciesState fs, int low, int high, int compression, int height) {
		StringBuffer sb = new StringBuffer ();
		float lastFrequency = (fs.getState ().length < high ? fs.getState ().length : (float) high);
		int length = (int) lastFrequency / compression;
		int maxIndex = FrequenciesHelper.getMaxIndex (fs, low, high);
		long maxMagn = (int)(20.0 * Math.log10 (fs.getState () [maxIndex].abs ()));
		int step = (int) lastFrequency / length;
		int [] valuesOnPlot = new int [length];
		int maxPlotValue = 0;
		double minValuePlotted = -1;
		for (int i = 0; i < valuesOnPlot.length; i++) {
			double maxValue = 0;
			for (int j = 0; j < step; j++) {
				int x = i * step + j + low;
				if (x < fs.getState ().length && maxValue < fs.getState () [x].abs ()) {
					maxValue = 20.0 * Math.log10 (fs.getState () [x].abs ());
				}
			}
			if (minValuePlotted == -1 || minValuePlotted > maxValue) {
				minValuePlotted = maxValue;
			}
			valuesOnPlot [i] = (int) (maxValue * height / (maxMagn));
			if (maxPlotValue < valuesOnPlot [i] && i > 0) {
				maxPlotValue = valuesOnPlot [i];
			}
		}
		for (int i = 0; i < valuesOnPlot.length; i++) {
			valuesOnPlot [i] -= minValuePlotted * height / maxMagn;
		}
		for (int j = height; j >= 0; j--) {
			if (j == height) {
				sb.append ("^ " + maxMagn + " (magnitude)\n");
				continue;
			} else {
				sb.append ("|");
			}
			for (int i = 0; i < length; i++) {
				if (valuesOnPlot [i] == j) {
					sb.append ("_");
				} else if (valuesOnPlot [i] > j) {
					sb.append ("#");
				} else {
					sb.append (" ");
				}
			}
			sb.append ("\n");
		}
		sb.append ("L");
		for (int i = 0; i < length; i++) {
			sb.append ("-");
		}
		sb.append ("> " + FrequenciesHelper.freqFromSampleRate(length * compression, (int)lastFrequency * 2) + "Hz (freq)\n");
		for (int i = 0; i < length; i++) {
			sb.append (" ");
			if (i == maxIndex / compression){
				int foundFreq = FrequenciesHelper.freqFromSampleRate(maxIndex, (int)lastFrequency * 2);
				sb.append ("^" + foundFreq + "Hz");
				i += (foundFreq == 0 ? 1 : Math.log10 (foundFreq)) + 2;
			}
			
		}
		return sb.toString ();
	}

	public static int getMaxIndex (FrequenciesState fs, int low, int high) {
		double max = 0;
		int maxIndex = 0;
		int realhigh = Math.min (high, fs.getState ().length);
		for (int i = low; i < realhigh; i++) {
			if (max < fs.getState () [i].abs ()) {
			    max = fs.getState () [i].abs ();
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static FrequenciesState spectrumToCepstrum (FrequenciesState fs) {
		for (int i = 0; i < fs.getState ().length; i++) {
			Complex c = fs.getState () [i];
			double log = Math.log (Math.pow (c.abs (), 2));
			fs.getState () [i] = new Complex (log);
		}
		FastFourierTransformer fastFourierTransformer = new FastFourierTransformer (DftNormalization.STANDARD);

		FrequenciesState fscep = new FrequenciesState (fastFourierTransformer.transform (fs.getState (), TransformType.FORWARD), fs.getMaxfrequency ());
		for (int i = 0; i < fscep.getState ().length; i++) {
			Complex c = fscep.getState () [i];
			double sqr = Math.pow (c.abs (), 2);
			fscep.getState () [i] = new Complex (sqr);
		}
		for (int i = 0; i < 50; i++) {
			fscep.getState ()[i] = new Complex (0);
		}
		for (int i = fscep.getState ().length - 50; i < fscep.getState ().length; i++) {
			fscep.getState ()[i] = new Complex (0);
		}
		

		return fscep;
	}
}
