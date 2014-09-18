package org.toilelibre.libe.soundtransform.objects;

import org.apache.commons.math3.complex.Complex;

public class FrequenciesState {

	private Complex []	state;
	private int	       maxfrequency;

	public FrequenciesState (Complex [] state, int maxfrequency) {
		super ();
		this.state = state;
		this.maxfrequency = maxfrequency;
	}

	public Complex [] getState () {
		return state;
	}

	public int getMaxfrequency () {
		return maxfrequency;
	}

	public int f0 (int hpcfactor) {
		return this.f0 (this.hpc (hpcfactor), hpcfactor);
	}

	private int f0 (double [] hpc, int hpcfactor) {
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

	public double [] hpc (int factor) {
		double [] result = new double [maxfrequency / (2 * factor)];
		for (int i = 1; i < maxfrequency / (2 * factor); i++) {
			result [i] = state [i].abs ();
			for (int j = 1; j < factor; j++) {
				if (i * factor < maxfrequency / 2) {
					result [i] *= state [i * factor].abs ();
				}
			}
		}
		return result;
	}

	public int peak () {
		String toString = this.toString (50, 900);
		return Integer.parseInt (toString.substring (toString.lastIndexOf ('-') + 2, toString.lastIndexOf ('H'))) - 10;
	}

	public int max () {
		String toString = this.toString (50, 900);
		toString = toString.substring (0, toString.lastIndexOf ('\n'));
		return Integer.parseInt (toString.substring (toString.lastIndexOf ('-') + 2, toString.lastIndexOf ('H')));
	}

	public String toString () {
		return this.toString (0, (int) maxfrequency / 2);
	}

	public String toString (int low, int high) {
		float lastFrequency = (float) high;
		int length = (int) lastFrequency / 20;
		int height = 15;
		int maxIndex = this.getMaxIndex (low, high);
		int maxMagn = (int) this.state [maxIndex].abs ();
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
				if (peakValue * 2 < state [i * step + j + low].abs ()) {
					peakValue = state [i * step + j + low].abs ();
					peakIndex = i * step + j + low;
				}
				if (maxValue < state [i * step + j + low].abs ()) {
					maxValue = state [i * step + j + low].abs ();
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

	private int getMaxIndex (int low, int high) {
		int max = 0;
		int maxIndex = 0;
		for (int i = low; i < high; i++) {
			if (max < this.state [i].abs ()) {
				max = (int) Math.ceil (this.state [i].abs ());
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public int pgcd (int hpcfactor) {
		double [] hpc = this.hpc (hpcfactor);
		int f0 = this.f0 (hpc, hpcfactor);
		if (f0 == 0) {
			return 0;
		}
		int pgcd = f0;
		int i = f0;
		while (i < hpc.length) {
			pgcd = this.pgcd (pgcd, i);
			i += f0;
		}
		return pgcd;
	}

	private int pgcd (int a, int b) { // début de pgcd ()
		if (a < b) // on veut le premier argument plus grand
			return (pgcd (b, a));
		else if (b == 0) // condition d'arrêt
			return (a);
		else
			// on poursuit l'algorithme d'Euclide
			return (pgcd (b, a % b));

	}

	public int loudestMultiple (int f0, int low, int high) {
		if (f0 == 0) {
			return 0;
		}
		int loudest = f0;
		double loudestValue = this.state [f0].abs ();
		int i = Math.max (f0, low);
		while (i < high) {
			if (this.state [i].abs () > loudestValue) {
				loudest = i;
				loudestValue = this.state [i].abs ();
			}
			i += f0;
		}
		return loudest;
	}

}
