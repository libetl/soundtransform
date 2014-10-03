package org.toilelibre.libe.soundtransform.transforms;

import org.toilelibre.libe.soundtransform.objects.Sound;

public class ToStringSoundTransformation implements SoundTransformation {

    private int length;
    private int height;
    private StringBuffer sb = new StringBuffer();

    public ToStringSoundTransformation(int length, int height) {
        this.length = length;
        this.height = height;
    }

    @Override
    public Sound transform(Sound input) {
        double compression = input.getSamples().length / this.length;
        this.sb = new StringBuffer();

        float lastSample = input.getSamples().length;
        long maxMagn = 128;
        int step = (int) lastSample / this.length;
        int[] valuesOnPlot = new int[this.length];
        int maxPlotValue = 0;
        double minValuePlotted = -1;
        for (int i = 0; i < valuesOnPlot.length; i++) {
            double maxValue = 0;
            for (int j = 0; j < step; j++) {
                int x = i * step + j;
                if (x < input.getSamples().length
                        && maxValue < input.getSamples()[x]) {
                    maxValue = 20.0 * Math.log10(input.getSamples()[x]);
                }
            }
            if (minValuePlotted == -1 || minValuePlotted > maxValue) {
                minValuePlotted = maxValue;
            }
            valuesOnPlot[i] = (int) (maxValue * height / (maxMagn));
            if (maxPlotValue < valuesOnPlot[i] && i > 0) {
                maxPlotValue = valuesOnPlot[i];
            }
        }
        for (int i = 0; i < valuesOnPlot.length; i++) {
            valuesOnPlot[i] -= minValuePlotted * height / maxMagn;
        }
        for (int j = height; j >= 0; j--) {
            if (j == height) {
                sb.append("^ " + maxMagn + " (magnitude)\n");
                continue;
            } else {
                sb.append("|");
            }
            for (int i = 0; i < this.length; i++) {
                if (valuesOnPlot[i] == j) {
                    sb.append("_");
                } else if (valuesOnPlot[i] > j) {
                    sb.append("#");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        sb.append("L");
        for (int i = 0; i < this.length; i++) {
            sb.append("-");
        }
        sb.append("> " + (int)(this.length * compression / input.getFreq()) + "s (time)\n");

        return input;
    }

    public String toString (Sound sound){
        this.transform(sound);
        return this.sb.toString();
    }
}
