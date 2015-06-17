/*package org.toilelibre.libe.soundtransform.infrastructure.service.converted.sound.transforms;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.AbstractWindowSoundTransform;

public final class DolphChebyshevWindowSoundTransform extends AbstractWindowSoundTransform {

    protected double applyFunction (int iteration, int length) {
        return this.w0 (iteration, length, this.beta (length)).abs () * (iteration - (length - 1) / 2);
    }

    private Complex w0 (int iteration, int length, double beta) {
        return this.sumOf0Phases (iteration, length, beta).multiply (1.0 / length);
    }

    private Complex sumOf0Phases (int iteration, int length, double beta) {
        Complex sum = new Complex (0, 0);
        double coshOnBeta = Math.cosh (length / Math.cosh (beta));
        for (int k = 0 ; k < length - 1 ; k++) {
            sum.add (new Complex (this.zeroPhase (k, length, beta, coshOnBeta)).multiply (this.exp (0, 2.0 * Math.PI * k * iteration / length)));
        }
        return sum;
    }

    private Complex exp (double re, double im) {
        return new Complex (Math.exp (re) * Math.cos (im), Math.exp (re) * Math.sin (im));
    }

    private double zeroPhase (int k, int length, double beta, double coshOnBeta) {
        return Math.cos (length * 1.0 / Math.cos (beta * Math.cos (Math.PI * k / length))) / coshOnBeta;
    }

    private double beta (int length) {
        return Math.cosh (1.0 / (length * Math.cosh (Math.pow (10, this.alpha ()))));
    }

    private double alpha () {
        return -20;
    }

}
*/