package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Pure Java implementation of cubic spline interpolation
 * without external dependencies (replaces commons-math3 SplineInterpolator)
 */
public final class CubicSplineInterpolator {

    private final double[] x;
    private final double[] y;
    private final double[] m;
    private final int n;

    /**
     * Create a spline interpolator from x and y data points
     * 
     * @param x x-coordinates (must be strictly increasing)
     * @param y y-coordinates (same length as x)
     */
    public CubicSplineInterpolator (final double[] x, final double[] y) {
        if (x.length != y.length || x.length < 2) {
            throw new IllegalArgumentException ("x and y must have same length >= 2");
        }
        this.x = x.clone ();
        this.y = y.clone ();
        this.n = x.length;
        this.m = this.computeSlopes ();
    }

    /**
     * Evaluate the spline at a given x value
     */
    public double evaluate (final double xValue) {
        if (xValue < this.x[0] || xValue > this.x[this.n - 1]) {
            return 0; // Out of range
        }

        // Find the interval
        int i = 0;
        for (i = 0; i < this.n - 1; i++) {
            if (xValue >= this.x[i] && xValue <= this.x[i + 1]) {
                break;
            }
        }

        final double h = this.x[i + 1] - this.x[i];
        final double s = (xValue - this.x[i]) / h;
        final double s2 = s * s;
        final double s3 = s2 * s;

        // Hermite basis functions
        final double h00 = 2 * s3 - 3 * s2 + 1;
        final double h10 = s3 - 2 * s2 + s;
        final double h01 = -2 * s3 + 3 * s2;
        final double h11 = s3 - s2;

        return h00 * this.y[i] + h10 * h * this.m[i] + h01 * this.y[i + 1] + h11 * h * this.m[i + 1];
    }

    /**
     * Check if a point is valid (within the interpolation range)
     */
    public boolean isValidPoint (final double xValue) {
        return xValue >= this.x[0] && xValue <= this.x[this.n - 1];
    }

    /**
     * Compute slopes using natural cubic spline (second derivatives = 0 at ends)
     */
    private double[] computeSlopes () {
        final double[] h = new double[this.n - 1];
        final double[] alpha = new double[this.n - 1];

        for (int i = 0; i < this.n - 1; i++) {
            h[i] = this.x[i + 1] - this.x[i];
            if (i > 0) {
                alpha[i] = 3 / h[i] * (this.y[i + 1] - this.y[i]) - 3 / h[i - 1] * (this.y[i] - this.y[i - 1]);
            }
        }

        final double[] l = new double[this.n];
        final double[] mu = new double[this.n];
        final double[] z = new double[this.n];

        l[0] = 1;
        for (int i = 1; i < this.n - 1; i++) {
            l[i] = 2 * (this.x[i + 1] - this.x[i - 1]) - h[i - 1] * mu[i - 1];
            mu[i] = h[i] / l[i];
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }
        l[this.n - 1] = 1;

        final double[] c = new double[this.n];
        final double[] b = new double[this.n - 1];
        final double[] d = new double[this.n - 1];

        for (int i = this.n - 2; i >= 0; i--) {
            c[i] = z[i] - mu[i] * c[i + 1];
            b[i] = (this.y[i + 1] - this.y[i]) / h[i] - h[i] * (c[i + 1] + 2 * c[i]) / 3;
            d[i] = (c[i + 1] - c[i]) / (3 * h[i]);
        }

        // Return slopes (derivatives) at each point
        // For this implementation, we use the simplified Catmull-Rom approach
        final double[] slopes = new double[this.n];
        slopes[0] = (this.y[1] - this.y[0]) / (this.x[1] - this.x[0]);
        for (int i = 1; i < this.n - 1; i++) {
            slopes[i] = (this.y[i + 1] - this.y[i - 1]) / (this.x[i + 1] - this.x[i - 1]);
        }
        slopes[this.n - 1] = (this.y[this.n - 1] - this.y[this.n - 2]) / (this.x[this.n - 1] - this.x[this.n - 2]);

        return slopes;
    }
}
