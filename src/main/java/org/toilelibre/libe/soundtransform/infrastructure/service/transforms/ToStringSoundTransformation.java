package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class ToStringSoundTransformation implements SoundTransformation {

    private final int     length;
    private final int     height;
    private StringBuilder sb = new StringBuilder ();

    public ToStringSoundTransformation (final int length, final int height) {
        this.length = length;
        this.height = height;
    }

    public String toString (final Sound sound) {
        this.transform (sound);
        return this.sb.toString ();
    }

    @Override
    public Sound transform (final Sound input) {
        final double compression = input.getSamples ().length / this.length;
        this.sb = new StringBuilder ();

        final float lastSample = input.getSamples ().length;
        final long maxMagn = (long) Math.pow (256, input.getNbBytesPerSample ()) / 2;
        final int step = (int) lastSample / this.length;
        final int [] valuesOnPlot = new int [this.length];
        int maxPlotValue = 0;
        double minValuePlotted = -1;
        for (int i = 0 ; i < valuesOnPlot.length ; i++) {
            double maxValue = 0;
            for (int j = 0 ; j < step ; j++) {
                final int x = i * step + j;
                if (x < input.getSamples ().length && maxValue < input.getSamples () [x]) {
                    maxValue = input.getSamples () [x];
                }
            }
            if (minValuePlotted == -1 || minValuePlotted > maxValue) {
                minValuePlotted = maxValue;
            }
            valuesOnPlot [i] = (int) (maxValue * this.height / maxMagn);
            if (maxPlotValue < valuesOnPlot [i] && i > 0) {
                maxPlotValue = valuesOnPlot [i];
            }
        }
        for (int i = 0 ; i < valuesOnPlot.length ; i++) {
            valuesOnPlot [i] -= minValuePlotted * this.height / maxMagn;
        }
        for (int j = this.height ; j >= 0 ; j--) {
            if (j == this.height) {
                this.sb.append ("^ " + maxMagn + " (magnitude)\n");
                continue;
            } else {
                this.sb.append ("|");
            }
            for (int i = 0 ; i < this.length ; i++) {
                if (valuesOnPlot [i] == j) {
                    this.sb.append ("_");
                } else if (valuesOnPlot [i] > j) {
                    this.sb.append ("#");
                } else {
                    this.sb.append (" ");
                }
            }
            this.sb.append ("\n");
        }
        this.sb.append ("L");
        for (int i = 0 ; i < this.length ; i++) {
            this.sb.append ("-");
        }
        this.sb.append ("> " + (int) (this.length * compression / input.getSampleRate ()) + "s (time)\n");

        return input;
    }
}
