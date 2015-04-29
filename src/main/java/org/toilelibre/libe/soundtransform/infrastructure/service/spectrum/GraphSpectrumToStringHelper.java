package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToStringHelper;

final class GraphSpectrumToStringHelper implements SpectrumToStringHelper<Complex []> {

    private static final int                 TWICE                        = 2;
    private static final int                 TWO                          = 2;
    private static final int                 HALF                         = 2;
    private static final int                 COMPRESSION                  = 20;
    private static final int                 HEIGHT                       = 20;
    private static final float               DECIBELS_FORMULA_COEFFICIENT = 20.0f;
    private final SpectrumHelper<Complex []> spectrumHelper;

    public GraphSpectrumToStringHelper (final SpectrumHelper<Complex []> spectrumHelper1) {
        this.spectrumHelper = spectrumHelper1;
    }

    private void diplayFooter (final StringBuilder sb, final int length, final SpectrumHelper<Complex []> spectrumHelper, final int compression, final float lastFrequency) {
        sb.append ("L");
        for (int i = 0 ; i < length ; i++) {
            sb.append ("-");
        }
        sb.append ("> ").append (spectrumHelper.freqFromSampleRate (length * compression, (int) lastFrequency * GraphSpectrumToStringHelper.TWICE, (int) lastFrequency * GraphSpectrumToStringHelper.TWICE)).append ("Hz (freq)\n");
    }

    private void displayRow (final StringBuilder sb, final int j, final int [] valuesOnPlot, final double maxMagn, final int length, final int height) {
        if (j == height) {
            sb.append ("^ ").append (Double.valueOf (maxMagn)).append (" (magnitude)\n");
            return;
        } else {
            sb.append ("|");
        }
        for (int i = 0 ; i < length ; i++) {
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

    private void displayLoudestFrequency (final StringBuilder sb, final int length, final SpectrumHelper<Complex []> spectrumHelper, final int maxIndex, final int compression, final float lastFrequency) {
        int i = 0;
        while (i < length) {
            sb.append (" ");
            if (i == maxIndex / compression) {
                final float foundFreq = spectrumHelper.freqFromSampleRate (maxIndex, (int) lastFrequency * GraphSpectrumToStringHelper.TWICE, (int) lastFrequency * GraphSpectrumToStringHelper.TWICE);
                sb.append ("^").append (Float.valueOf (foundFreq)).append ("Hz");
                i += (foundFreq == 0 ? 1 : Math.log10 (foundFreq)) + GraphSpectrumToStringHelper.TWO;
            }
            i++;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.
     * SpectrumToStringH
     * #fsToString(org.toilelibre.libe.soundtransform.model.converted
     * .spectrum.Spectrum)
     */
    @Override
    public String fsToString (final Spectrum<Complex []> fs) {
        return this.fsToString (fs, 0, (int) fs.getSampleRate () / GraphSpectrumToStringHelper.HALF, GraphSpectrumToStringHelper.COMPRESSION, GraphSpectrumToStringHelper.HEIGHT);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.
     * SpectrumToStringH
     * #fsToString(org.toilelibre.libe.soundtransform.model.converted
     * .spectrum.Spectrum, int, int, int, int)
     */
    @Override
    public String fsToString (final Spectrum<Complex []> fs, final int low, final int high, final int compression, final int height) {
        final StringBuilder sb = new StringBuilder ();
        final float lastFrequency = fs.getState ().length < high ? fs.getState ().length : (float) high;
        final int length = (int) lastFrequency / compression;
        final int maxIndex = this.spectrumHelper.getMaxIndex (fs, low, high);
        final double maxValue = fs.getState () [maxIndex].abs ();
        final double maxMagn = GraphSpectrumToStringHelper.DECIBELS_FORMULA_COEFFICIENT * Math.log10 (maxValue);
        final int step = (int) lastFrequency / length;
        final int [] valuesOnPlot = this.prepareValuesOnPlot (fs, step, this.getMaxValueOrMaxMagn (maxValue, maxMagn), length, low, height);
        for (int j = height ; j >= 0 ; j--) {
            this.displayRow (sb, j, valuesOnPlot, this.getMaxValueOrMaxMagn (maxValue, maxMagn), length, height);
        }

        this.diplayFooter (sb, length, this.spectrumHelper, compression, lastFrequency);
        this.displayLoudestFrequency (sb, length, this.spectrumHelper, maxIndex, compression, lastFrequency);
        return sb.toString ();
    }

    private double getMaxValueOrMaxMagn (final double maxValue, final double maxMagn) {
        return maxMagn < 0 ? maxValue : maxMagn;
    }

    private int [] prepareValuesOnPlot (final Spectrum<Complex []> fs, final int step, final double max, final int length, final int low, final int height) {
        final int [] valuesOnPlot = new int [length];
        int maxPlotValue = 0;
        double minValuePlotted = -1;
        for (int i = 0 ; i < valuesOnPlot.length ; i++) {
            double maxStepValue = 0;
            double maxStepMagn = 0;
            for (int j = 0 ; j < step ; j++) {
                final int x = i * step + j + low;
                if (x < fs.getState ().length && maxStepMagn < fs.getState () [x].abs ()) {
                    maxStepValue = fs.getState () [x].abs ();
                    maxStepMagn = GraphSpectrumToStringHelper.DECIBELS_FORMULA_COEFFICIENT * Math.log10 (fs.getState () [x].abs ());
                }
            }
            if (minValuePlotted == -1 || minValuePlotted > maxStepMagn) {
                minValuePlotted = this.getMaxValueOrMaxMagn (maxStepValue, maxStepMagn);
            }
            valuesOnPlot [i] = (int) (minValuePlotted * height / maxStepMagn);
            if (maxPlotValue < valuesOnPlot [i] && i > 0) {
                maxPlotValue = valuesOnPlot [i];
            }
        }
        for (int i = 0 ; i < valuesOnPlot.length ; i++) {
            valuesOnPlot [i] -= minValuePlotted * height / max;
        }

        return valuesOnPlot;
    }

}
