package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToStringHelper;

public class GraphSpectrumToStringHelper implements SpectrumToStringHelper {


    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.SpectrumToStringH#fsToString(org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum)
     */
    @Override
    public String fsToString (final Spectrum fs) {
        return this.fsToString (fs, 0, fs.getSampleRate () / 2, 20, 20);
    }

    /* (non-Javadoc)
     * @see org.toilelibre.libe.soundtransform.infrastructure.service.spectrum.SpectrumToStringH#fsToString(org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum, int, int, int, int)
     */
    @Override
    public String fsToString (final Spectrum fs, final int low, final int high, final int compression, final int height) {
        final StringBuffer sb = new StringBuffer ();
        final float lastFrequency = fs.getState ().length < high ? fs.getState ().length : (float) high;
        final int length = (int) lastFrequency / compression;
        final int maxIndex = new HPSSpectrumHelper ().getMaxIndex (fs, low, high);
        final long maxMagn = (int) (20.0 * Math.log10 (fs.getState () [maxIndex].abs ()));
        final int step = (int) lastFrequency / length;
        final int [] valuesOnPlot = new int [length];
        int maxPlotValue = 0;
        double minValuePlotted = -1;
        for (int i = 0; i < valuesOnPlot.length; i++) {
            double maxValue = 0;
            for (int j = 0; j < step; j++) {
                final int x = i * step + j + low;
                if (x < fs.getState ().length && maxValue < fs.getState () [x].abs ()) {
                    maxValue = 20.0 * Math.log10 (fs.getState () [x].abs ());
                }
            }
            if (minValuePlotted == -1 || minValuePlotted > maxValue) {
                minValuePlotted = maxValue;
            }
            valuesOnPlot [i] = (int) (maxValue * height / maxMagn);
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
        sb.append ("> " + HPSSpectrumHelper.freqFromSampleRate (length * compression, (int) lastFrequency * 2, (int) lastFrequency * 2) + "Hz (freq)\n");
        for (int i = 0; i < length; i++) {
            sb.append (" ");
            if (i == maxIndex / compression) {
                final int foundFreq = HPSSpectrumHelper.freqFromSampleRate (maxIndex, (int) lastFrequency * 2, (int) lastFrequency * 2);
                sb.append ("^" + foundFreq + "Hz");
                i += (foundFreq == 0 ? 1 : Math.log10 (foundFreq)) + 2;
            }

        }
        return sb.toString ();
    }

}
