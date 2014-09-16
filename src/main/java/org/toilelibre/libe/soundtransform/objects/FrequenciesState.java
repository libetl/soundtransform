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
	    return Integer.parseInt(toString.substring(toString.lastIndexOf('-') + 2, toString.lastIndexOf('H'))) - 10;
	}

	public int max (){
	    String toString = this.toString(50, 900);
	    toString = toString.substring(0, toString.lastIndexOf('\n'));
	    return Integer.parseInt(
	    		toString.substring(toString.lastIndexOf('-') + 2, toString.lastIndexOf('H')));
	}
	
	public String toString () {
	    return this.toString(0, (int)maxfrequency / 2);
	}
	
	public String toString (int low, int high) {
		float lastFrequency = (float)high;
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
			    if (peakValue * 2 < state [i * step + j + low].abs ()){
			        peakValue = state [i * step + j + low].abs ();
			        peakIndex = i * step + j + low;
			    }
			    if (maxValue < state [i * step + j + low].abs ()){
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
		for (int i = low ; i < high ; i++){
			if (max < this.state [i].abs ()){
				max = (int) Math.ceil (this.state [i].abs ());
				maxIndex = i;
			}
		}
	    return maxIndex;
    }

}
