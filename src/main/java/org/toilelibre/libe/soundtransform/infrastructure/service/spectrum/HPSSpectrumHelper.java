package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;

final class HPSSpectrumHelper implements SpectrumHelper<Complex []> {

    private static final int TWICE = 2;
    private static final int HALF  = 2;

    public HPSSpectrumHelper () {

    }

    @Override
    public float freqFromSampleRate (final float freq, final int sqr2length, final float sampleRate) {
        return (int) (freq * HPSSpectrumHelper.TWICE * 1.0 * sampleRate / sqr2length);
    }

    @Override
    public int getLengthOfSpectrum (final Spectrum<Complex []> fs) {
        return fs.getState ().length;
    }

    @Override
    public int getMaxIndex (final Spectrum<Complex []> fs, final int low, final int high) {
        double max = 0;
        int maxIndex = 0;
        final int reallow = low == 0 ? 1 : low;
        final int realhigh = Math.min (high, fs.getState ().length);
        for (int i = reallow ; i < realhigh ; i++) {
            if (max < fs.getState () [i].abs ()) {
                max = fs.getState () [i].abs ();
                maxIndex = i;
            }
        }
        return maxIndex;
    }


    @Override
    public int getMaxIndex (final double [] array, final int low, final int high) {
        double max = 0;
        int maxIndex = 0;
        final int reallow = low == 0 ? 1 : low;
        final int realhigh = Math.min (high, array.length);
        for (int i = reallow ; i < realhigh ; i++) {
            if (max < array [i]) {
                max = array [i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    @Override
    public double getMaxValue (final Spectrum<Complex []> fs, final int low, final int high) {
        return fs.getState () [this.getMaxIndex (fs, low, high)].abs ();
    }

    @Override
    public int getFirstPeak (final Spectrum<Complex []> fs, final int low, final int high, final double thresholdValue) {
        double max = 0;
        int maxIndex = 0;
        final int reallow = low == 0 ? 1 : low;
        final int realhigh = Math.min (high, fs.getState ().length);
        int i = reallow;
        while (fs.getState () [i].abs () < thresholdValue) {
            i++;
        }
        while (i < realhigh && fs.getState () [i].abs () >= thresholdValue) {
            if (max < fs.getState () [i].abs ()) {
                max = fs.getState () [i].abs ();
                maxIndex = i;
            }
            i++;
        }
        return maxIndex;
    }

    @Override
    public Spectrum<Complex []> productOfMultiples (final Spectrum<Complex []> fs, final int factor, final float partOfTheSpectrumToRead) {
        final int max = (int) (fs.getState ().length * partOfTheSpectrumToRead / factor);
        final Complex [] result = new Complex [max];
        for (int i = 0 ; i < max ; i++) {
            double val = fs.getState () [i].abs ();
            for (int j = 1 ; j < factor ; j++) {
                if (i * factor < fs.getSampleRate () / HPSSpectrumHelper.HALF && i * factor < fs.getState ().length) {
                    val *= fs.getState () [i * factor].abs ();
                }
            }
            result [i] = new Complex (val);
        }
        return new Spectrum<Complex []> (result, new FormatInfo (fs.getSampleSize (), fs.getSampleRate () / factor));
    }
    


    @Override
    public double [] productOfMultiples (final double [] [] fs, final float sampleRate, final int factor, final float partOfTheSpectrumToRead) {
        final int max = (int) (fs [0].length * partOfTheSpectrumToRead / factor);
        final double [] result = new double [max];
        for (int i = 0 ; i < max ; i++) {
            double val = Math.sqrt (fs [0] [i] * fs [0] [i] + fs [1] [i] * fs [1] [i]);
            for (int j = 1 ; j < factor ; j++) {
                if (i * factor < sampleRate / HPSSpectrumHelper.HALF && i * factor < fs [0].length) {
                    val *= Math.sqrt (fs [0] [i * factor] * fs[0] [i * factor] + fs [1] [i * factor] * fs [1] [i * factor]);
                }
            }
            result [i] = val;
        }
        return result;
    }
}
