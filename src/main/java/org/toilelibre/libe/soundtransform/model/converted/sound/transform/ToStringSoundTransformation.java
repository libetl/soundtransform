package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class ToStringSoundTransformation implements SoundTransformation {

    private static final double NB_BYTE_VALUES = 1 << Byte.SIZE;
    private final int           length;
    private final int           height;
    private StringBuilder       sb             = new StringBuilder ();

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
        final double compression = input.getSamplesLength () * 1.0 / this.length;
        this.sb = new StringBuilder ();

        final float lastSample = input.getSamplesLength ();
        final long maxMagn = (long) Math.pow (ToStringSoundTransformation.NB_BYTE_VALUES, input.getNbBytesPerSample ()) / 2;
        final int step = (int) lastSample / this.length;
        final int [] valuesOnPlot = new int [this.length];
        int maxPlotValue = 0;
        double minValuePlotted = -1;
        for (int i = 0 ; i < valuesOnPlot.length ; i++) {
            double maxValue = 0;
            for (int j = 0 ; j < step ; j++) {
                final int x = i * step + j;
                if (x < input.getSamplesLength () && maxValue < input.getSampleAt (x)) {
                    maxValue = input.getSampleAt (x);
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
                this.sb.append ("^ ").append (Long.valueOf (maxMagn)).append (" (magnitude)\n");
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
        this.sb.append ("> ").append (Integer.valueOf ((int) (this.length * compression / input.getSampleRate ()))).append ("s (time)\n");

        return input;
    }
}
