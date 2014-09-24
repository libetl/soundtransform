package org.toilelibre.libe.soundtransform.pda;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;

public class FrequenciesHelper {

	public static final int MAX_EAR_FREQUENCY = 20000;
	
	public static int f0 (FrequenciesState fs, int hpcfactor) {
		return FrequenciesHelper.max (FrequenciesHelper.hpc (fs, hpcfactor));
	}

	public static int max (FrequenciesState hpc) {
		int f0 = 0;
		double f0val = hpc.getState () [f0].getReal ();
		for (int i = 0; i < hpc.getState ().length; i++) {
			if (f0val < hpc.getState () [i].getReal ()) {
				f0val = hpc.getState () [i].getReal ();
				f0 = i;
			}
		}
		return f0 * MAX_EAR_FREQUENCY / (2 * hpc.getMaxfrequency ());
	}

	private static FrequenciesState hpc (FrequenciesState fs, int factor) {
		int max = Math.min (fs.getMaxfrequency () / (2 * factor), fs.getState ().length);
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
		int maxPlotIndex = 0;
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
				maxPlotIndex = i;
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
		sb.append ("> " + (length * compression / lastFrequency * MAX_EAR_FREQUENCY) + "Hz (freq)\n");
		for (int i = 0; i < length; i++) {
			sb.append (" ");
			if (i == maxPlotIndex){
				int foundFreq = (int)((i / lastFrequency * MAX_EAR_FREQUENCY) * compression);
				sb.append ("^" + foundFreq + "Hz");
				i += (foundFreq == 0 ? 1 : Math.log10 (foundFreq)) + 2;
			}
			
		}
		return sb.toString ();
	}

	private static int getMaxIndex (FrequenciesState fs, int low, int high) {
		int max = 0;
		int maxIndex = 0;
		int realhigh = Math.min (high, fs.getState ().length);
		for (int i = low; i < realhigh; i++) {
			if (max < fs.getState () [i].abs ()) {
				max = (int) Math.ceil (fs.getState () [i].abs ());
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static int pgcd (FrequenciesState fs, int hpcfactor) {
		FrequenciesState hpc = FrequenciesHelper.hpc (fs, hpcfactor);
		int f0 = FrequenciesHelper.max (hpc);
		if (f0 == 0) {
			return 0;
		}
		int pgcd = f0;
		int i = f0;
		while (i < hpc.getState ().length) {
			pgcd = FrequenciesHelper.pgcd (pgcd, i);
			i += f0;
		}
		return pgcd;
	}

	private static int pgcd (int a, int b) {
		if (a < b) {
			return pgcd (b, a);
		} else if (b == 0) {
			return (a);
		}
		return pgcd (b, a % b);

	}

	public static int loudestMultiple (FrequenciesState fs, int f0) {
		return FrequenciesHelper.loudestMultiple (fs, f0, 0, fs.getMaxfrequency () / 2);
	}
	
	public static int loudestMultiple (FrequenciesState fs, int f0, int low, int high) {
		if (f0 == 0) {
			return 0;
		}
		int loudest = f0;
		double loudestValue = fs.getState () [f0].abs ();
		int i = Math.max (f0, low);
		int realhigh = Math.min (high, fs.getState ().length);
		while (i < realhigh) {
			if (fs.getState () [i].abs () > loudestValue) {
				loudest = i;
				loudestValue = fs.getState () [i].abs ();
			}
			i += f0;
		}
		return loudest;
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
