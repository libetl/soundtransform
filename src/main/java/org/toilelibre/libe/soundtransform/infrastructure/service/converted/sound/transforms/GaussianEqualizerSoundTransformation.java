package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class GaussianEqualizerSoundTransformation extends SimpleFrequencySoundTransformation<Complex []> {

    private static final int DELTA_X  = 3500;
    private static final int EXPONENT = 2;
    private static final int DIVISION = 1000;
    private static final int HALF     = 2;

    public GaussianEqualizerSoundTransformation () {
        super ();
    }

    private Complex function (final double x) {
        return new Complex (1 - Math.exp (-Math.pow (x - GaussianEqualizerSoundTransformation.DELTA_X, GaussianEqualizerSoundTransformation.EXPONENT) / GaussianEqualizerSoundTransformation.DIVISION) / GaussianEqualizerSoundTransformation.HALF);
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs, final int offset, final int powOf2NearestLength, final int length) {
        final Complex [] newAmpl = new Complex [powOf2NearestLength];
        for (double j = 0 ; j < length ; j++) {
            final double freq = j * fs.getSampleRate () / fs.getState ().length;
            newAmpl [(int) j] = fs.getState () [(int) j].multiply (this.function (freq));
        }
        for (int j = length ; j < powOf2NearestLength ; j++) {
            newAmpl [j] = new Complex (0, 0);
        }
        return new Spectrum<Complex []> (newAmpl, new FormatInfo (fs.getSampleSize (), fs.getSampleRate ()));
    }
}
