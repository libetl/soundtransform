package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundTransform;

/**
 * Smoothes a sound graph. The effect is to remove the treble frequencies
 * without any time-to-frequency domain transform
 *
 */
public class LinearRegressionSoundTransform implements SoundTransform<Channel, Channel> {

    private final int step;

    /**
     * Default constructor
     * 
     * @param step1
     *            iteration step value
     */
    public LinearRegressionSoundTransform (final int step1) {
        this.step = step1;
    }

    @Override
    public Channel transform (final Channel input) {
        final SplineInterpolator reg = new SplineInterpolator ();
        final double [] x = new double [input.getSamplesLength () / this.step];
        final double [] y = new double [input.getSamplesLength () / this.step];
        for (int i = 0 ; i < input.getSamplesLength () ; i += this.step) {
            if (i / this.step < x.length) {
                x [i / this.step] = i;
                y [i / this.step] = input.getSampleAt (i);
            }
        }

        final PolynomialSplineFunction psf = reg.interpolate (x, y);

        final Channel outputSound = new Channel (new long [input.getSamplesLength ()], input.getFormatInfo (), input.getChannelNum ());
        for (int i = 0 ; i < input.getSamplesLength () ; i++) {
            if (i < x [x.length - 1]) {
                outputSound.setSampleAt (i, (long) psf.value (i));
            } else {
                outputSound.setSampleAt (i, input.getSampleAt (i));
            }
        }

        return outputSound;
    }

}
