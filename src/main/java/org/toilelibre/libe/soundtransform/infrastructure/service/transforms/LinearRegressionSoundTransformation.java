package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public class LinearRegressionSoundTransformation implements SoundTransformation {

    private int step = 1;

    public LinearRegressionSoundTransformation (final int step) {
        this.step = step;
    }

    @Override
    public Sound transform (final Sound input) {
        final SplineInterpolator reg = new SplineInterpolator ();
        final double [] x = new double [input.getSamples ().length / this.step];
        final double [] y = new double [input.getSamples ().length / this.step];
        for (int i = 0 ; i < input.getSamples ().length ; i += this.step) {
            if (i / this.step < x.length) {
                x [i / this.step] = i;
                y [i / this.step] = input.getSamples () [i];
            }
        }

        final PolynomialSplineFunction psf = reg.interpolate (x, y);

        final Sound outputSound = new Sound (new long [input.getSamples ().length], input.getNbBytesPerSample (), input.getSampleRate (),
                input.getChannelNum ());
        for (int i = 0 ; i < input.getSamples ().length ; i++) {
            if (i < x [x.length - 1]) {
                outputSound.getSamples () [i] = (long) psf.value (i);
            } else {
                outputSound.getSamples () [i] = input.getSamples () [i];
            }
        }

        return outputSound;
    }

}
