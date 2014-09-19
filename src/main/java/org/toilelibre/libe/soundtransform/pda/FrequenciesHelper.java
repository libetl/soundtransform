package org.toilelibre.libe.soundtransform.pda;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.toilelibre.libe.soundtransform.objects.FrequenciesState;

public class FrequenciesHelper {

	public static int f0 (FrequenciesState fs, int hpcfactor) {
		return FrequenciesHelper.f0 (FrequenciesHelper.hpc (fs, hpcfactor), hpcfactor);
	}

	private static int f0 (double [] hpc, int hpcfactor) {
		int f0 = 0;
		double f0val = hpc [f0];
		for (int i = 0; i < hpc.length; i++) {
			if (f0val < hpc [i]) {
				f0val = hpc [i];
				f0 = i;
			}
		}
		return f0;
	}

	private static double [] hpc (FrequenciesState fs, int factor) {
		double [] result = new double [fs.getMaxfrequency () / (2 * factor)];
		for (int i = 1; i < fs.getMaxfrequency () / (2 * factor); i++) {
			result [i] = fs.getState () [i].abs ();
			for (int j = 1; j < factor; j++) {
				if (i * factor < fs.getMaxfrequency () / 2) {
					result [i] *= fs.getState () [i * factor].abs ();
				}
			}
		}
		return result;
	}

	public static int peak (FrequenciesState fs) {
		String toString = FrequenciesHelper.fsToString (fs, 50, 900);
		return Integer.parseInt (toString.substring (toString.lastIndexOf ('-') + 2, toString.lastIndexOf ('H'))) - 10;
	}

	public static int max (FrequenciesState fs) {
		String toString = FrequenciesHelper.fsToString (fs, 50, 900);
		toString = toString.substring (0, toString.lastIndexOf ('\n'));
		return Integer.parseInt (toString.substring (toString.lastIndexOf ('-') + 2, toString.lastIndexOf ('H')));
	}

	public static String fsToString (FrequenciesState fs) {
		return FrequenciesHelper.fsToString (fs, 0, (int) fs.getMaxfrequency () / 2);
	}

	public static String fsToString (FrequenciesState fs, int low, int high) {
		float lastFrequency = (float) high;
		int length = (int) lastFrequency / 20;
		int height = 15;
		int maxIndex = FrequenciesHelper.getMaxIndex (fs, low, high);
		int maxMagn = (int) fs.getState () [maxIndex].abs ();
		StringBuffer sb = new StringBuffer ();
		int step = (int) lastFrequency / length;
		int [] valuesOnPlot = new int [length];
		int maxPlotIndex = 0;
		int maxPlotValue = 0;
		double peakIndex = 0;
		double peakValue = 0;
		for (int i = 0; i < valuesOnPlot.length; i++) {
			double maxValue = 0;
			for (int j = 0; j < step; j++) {
				if (peakValue * 2 < fs.getState () [i * step + j + low].abs ()) {
					peakValue = fs.getState () [i * step + j + low].abs ();
					peakIndex = i * step + j + low;
				}
				if (maxValue < fs.getState () [i * step + j + low].abs ()) {
					maxValue = fs.getState () [i * step + j + low].abs ();
				}
			}
			valuesOnPlot [i] = (int) (maxValue * height / (maxMagn));
			if (maxPlotValue < valuesOnPlot [i]) {
				maxPlotValue = valuesOnPlot [i];
				maxPlotIndex = i;
			}
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
		sb.append ("> " + lastFrequency + "Hz (freq)");
		sb.append ("\nMax is in the range " + (int) (maxPlotIndex * 1.0 / length * lastFrequency + low) + "Hz - " + (int) ( (maxPlotIndex + 1.0) / length * lastFrequency + low) + "Hz");
		sb.append ("\nFirst peak is in the range " + (int) (peakIndex - 10) + "Hz - " + (int) (peakIndex + 10) + "Hz");
		return sb.toString ();
	}

	private static int getMaxIndex (FrequenciesState fs, int low, int high) {
		int max = 0;
		int maxIndex = 0;
		for (int i = low; i < high; i++) {
			if (max < fs.getState () [i].abs ()) {
				max = (int) Math.ceil (fs.getState () [i].abs ());
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static int pgcd (FrequenciesState fs, int hpcfactor) {
		double [] hpc = FrequenciesHelper.hpc (fs, hpcfactor);
		int f0 = FrequenciesHelper.f0 (hpc, hpcfactor);
		if (f0 == 0) {
			return 0;
		}
		int pgcd = f0;
		int i = f0;
		while (i < hpc.length) {
			pgcd = FrequenciesHelper.pgcd (pgcd, i);
			i += f0;
		}
		return pgcd;
	}

	private static int pgcd (int a, int b) { // début de pgcd ()
		if (a < b) // on veut le premier argument plus grand
			return (pgcd (b, a));
		else if (b == 0) // condition d'arrêt
			return (a);
		else
			// on poursuit l'algorithme d'Euclide
			return (pgcd (b, a % b));

	}

	public static int loudestMultiple (FrequenciesState fs, int f0, int low, int high) {
		if (f0 == 0) {
			return 0;
		}
		int loudest = f0;
		double loudestValue = fs.getState () [f0].abs ();
		int i = Math.max (f0, low);
		while (i < high) {
			if (fs.getState () [i].abs () > loudestValue) {
				loudest = i;
				loudestValue = fs.getState () [i].abs ();
			}
			i += f0;
		}
		return loudest;
	}
	
	public static FrequenciesState spectrumToCepstrum (FrequenciesState fs){
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
		
		return fscep;
	}
}
