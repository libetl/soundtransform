package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class LinearRegressionSoundTransformation implements SoundTransformation {

    private final int step;

    public LinearRegressionSoundTransformation (final int step) {
        this.step = step;
    }

    @Override
    public Sound transform (final Sound input) {
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

        final Sound outputSound = new Sound (new long [input.getSamplesLength ()], input.getNbBytesPerSample (), input.getSampleRate (), input.getChannelNum ());
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
