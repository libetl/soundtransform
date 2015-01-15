package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class EqualizerSoundTransformation extends
        SimpleFrequencySoundTransformation {

    private final double [] ranges;
    private final double [] amplification;

    public EqualizerSoundTransformation (FourierTransformHelper helper1,
            final double [] ranges1, final double [] amplification1) {
        super (helper1);
        this.ranges = ranges1;
        this.amplification = amplification1;
    }

    @Override
    public Spectrum transformFrequencies (final Spectrum fs, final int offset,
            final int powOf2NearestLength, final int length) {
        final SplineInterpolator reg = new SplineInterpolator ();

        final PolynomialSplineFunction psf = reg.interpolate (this.ranges,
                this.amplification);
        final Complex [] newAmpl = new Complex [powOf2NearestLength];
        for (double j = 0 ; j < length ; j++) {
            final double freq = j * fs.getSampleRate () / fs.getState ().length;
            newAmpl [(int) j] = fs.getState () [(int) j].multiply (psf
                    .value (freq / 2));
        }
        for (int j = length ; j < powOf2NearestLength ; j++) {
            newAmpl [j] = new Complex (0, 0);
        }
        return new Spectrum (newAmpl, fs.getSampleRate (), fs.getNbBytes ());
    }
}
