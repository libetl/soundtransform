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

	public int peak (){
	    String toString = this.toString(50, 900);
	    return Integer.parseInt(toString.substring(toString.lastIndexOf('-') + 2, toString.lastIndexOf('H')));
	}
	
	public String toString () {
	    return this.toString(0, (int)maxfrequency / 2);
	}
	
	public String toString (int min, int max) {
		float lastFrequency = (float)max;
		int length = (int) lastFrequency / 20;
		int height = 15;
		int maxMagn = this.getMaxValue ();
		StringBuffer sb = new StringBuffer ();
		int step = (int) lastFrequency / length;
		int [] valuesOnPlot = new int [length];
		int maxIndex = 0;
		int maxValue = 0;
		for (int i = 0; i < valuesOnPlot.length; i++) {
			double peak = 0;
			for (int j = 0; j < step; j++) {
			    if (peak < state [i * step + j + min].abs ()){
			        peak = state [i * step + j + min].abs ();
			    }
			}
			valuesOnPlot [i] = (int) (peak * height / (maxMagn));
			if (maxValue < valuesOnPlot [i]) {
				maxValue = valuesOnPlot [i];
				maxIndex = i;
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
		sb.append ("\nMax is in the range " + (int) (maxIndex * 1.0 / length * lastFrequency + min) + "Hz - " + (int) ( (maxIndex + 1.0) / length * lastFrequency + min) + "Hz");
		return sb.toString ();
	}

	private int getMaxValue () {
		int max = 0;
		for (int i = 0 ; i < this.state.length ; i++){
			if (max < this.state [i].abs ()){
				max = (int) Math.ceil (this.state [i].abs ());
			}
		}
	    return max;
    }

}
