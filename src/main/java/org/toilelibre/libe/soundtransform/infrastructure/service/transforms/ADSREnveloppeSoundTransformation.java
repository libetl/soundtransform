package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;

public class ADSREnveloppeSoundTransformation extends SimpleFrequencySoundTransformation<Complex []> {
    private int       arraylength = 0;
    private double    threshold;
    private double [] magnitude;

    public ADSREnveloppeSoundTransformation (double threshold1) {
        super ();
        this.threshold = threshold1;
    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        return this.threshold;
    }

    public double [] getMagnitude () {
        return magnitude;
    }

    @Override
    public Sound initSound (final Sound input) {
        this.arraylength = 0;
        this.magnitude = new double [(int) (input.getSamples ().length / this.threshold + 1)];
        return super.initSound (input);
    }

    @Override
    public Spectrum<Complex []> transformFrequencies (final Spectrum<Complex []> fs) {
        this.magnitude [this.arraylength++] = this.computeMagnitude (fs);
        return super.transformFrequencies (fs);
    }

    public int computeMagnitude (final Spectrum<Complex []> fs) {
        double sum = 0;
        for (int i = 0 ; i < fs.getState ().length ; i++) {
            sum += fs.getState () [i].abs ();
        }
        return (int) (sum / fs.getState ().length);
    }
}