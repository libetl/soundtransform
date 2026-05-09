package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Represents a piecewise polynomial spline function
 */
public final class PolynomialSplineFunction {

    private final double[] x;      // knot points
    private final double[] y;      // y values at knots
    private final double[] b;      // coefficients for x term
    private final double[] c;      // coefficients for x^2 term
    private final double[] d;      // coefficients for x^3 term

    public PolynomialSplineFunction (final double[] x, final double[] y, final double[] b, final double[] c, final double[] d) {
        this.x = x.clone ();
        this.y = y.clone ();
        this.b = b.clone ();
        this.c = c.clone ();
        this.d = d.clone ();
    }

    /**
     * Evaluate the spline function at point t
     * For interval [x[i], x[i+1]]:
     * S(t) = y[i] + b[i]*(t-x[i]) + c[i]*(t-x[i])^2 + d[i]*(t-x[i])^3
     */
    public double value (final double t) {
        if (t < this.x[0] || t > this.x[this.x.length - 1]) {
            throw new IllegalArgumentException ("Value " + t + " is outside the valid range [" + this.x[0] + ", " + this.x[this.x.length - 1] + "]");
        }

        int i = this.findInterval (t);
        final double dx = t - this.x[i];
        return this.y[i] + this.b[i] * dx + this.c[i] * dx * dx + this.d[i] * dx * dx * dx;
    }

    /**
     * Check if a point is valid for this function
     */
    public boolean isValidPoint (final double t) {
        return t >= this.x[0] && t <= this.x[this.x.length - 1];
    }

    /**
     * Find the interval index for a given x value
     */
    private int findInterval (final double t) {
        int left = 0;
        int right = this.x.length - 1;

        while (left < right) {
            final int mid = (left + right) / 2;
            if (this.x[mid] < t) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return Math.max (0, Math.min (left - 1, this.x.length - 2));
    }
}
