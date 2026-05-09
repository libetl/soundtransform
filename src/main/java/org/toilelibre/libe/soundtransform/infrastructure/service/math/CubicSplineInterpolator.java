package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Pure Java implementation of natural cubic spline interpolation
 * without external dependencies
 */
public final class CubicSplineInterpolator {

    private final double [] x;
    private final double [] y;
    private final double [] a;
    private final double [] b;
    private final double [] c;
    private final double [] d;

    /**
     * Create a cubic spline interpolator from data points
     *
     * @param xValues
     *            x coordinates (must be sorted in ascending order)
     * @param yValues
     *            y coordinates
     */
    public CubicSplineInterpolator (final double [] xValues, final double [] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException ("X and Y arrays must have the same length");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException ("At least 2 points are required");
        }

        this.x = xValues.clone ();
        this.y = yValues.clone ();

        final int n = xValues.length - 1;
        this.a = new double [n];
        this.b = new double [n];
        this.c = new double [n];
        this.d = new double [n];

        calculateCoefficients ();
    }

    /**
     * Interpolate the value at a given x coordinate
     */
    public double evaluate (final double xValue) {
        if (xValue < this.x [0] || xValue > this.x [this.x.length - 1]) {
            return 0; // Return 0 for out-of-bounds (matches commons-math behavior)
        }

        // Find the interval
        int i = 0;
        for (int j = 1 ; j < this.x.length ; j++) {
            if (xValue < this.x [j]) {
                i = j - 1;
                break;
            }
            if (j == this.x.length - 1) {
                i = j - 1;
            }
        }

        final double h = xValue - this.x [i];
        return this.a [i] + this.b [i] * h + this.c [i] * h * h + this.d [i] * h * h * h;
    }

    /**
     * Check if a point is valid for interpolation
     */
    public boolean isValidPoint (final double xValue) {
        return xValue >= this.x [0] && xValue <= this.x [this.x.length - 1];
    }

    /**
     * Calculate the spline coefficients using natural cubic spline method
     */
    private void calculateCoefficients () {
        final int n = this.x.length - 1;

        // Calculate differences
        final double [] h = new double [n];
        final double [] alpha = new double [n];

        for (int i = 0 ; i < n ; i++) {
            h [i] = this.x [i + 1] - this.x [i];
            if (i > 0) {
                alpha [i] = 3.0 / h [i] * (this.y [i + 1] - this.y [i]) - 3.0 / h [i - 1] * (this.y [i] - this.y [i - 1]);
            }
        }

        // Solve tridiagonal system for c coefficients
        final double [] l = new double [n + 1];
        final double [] mu = new double [n];
        final double [] z = new double [n + 1];

        l [0] = 1;
        mu [0] = 0;
        z [0] = 0;

        for (int i = 1 ; i < n ; i++) {
            l [i] = 2 * (this.x [i + 1] - this.x [i - 1]) - h [i - 1] * mu [i - 1];
            mu [i] = h [i] / l [i];
            z [i] = (alpha [i] - h [i - 1] * z [i - 1]) / l [i];
        }

        l [n] = 1;
        z [n] = 0;

        final double [] cCoeff = new double [n + 1];
        for (int j = n - 1 ; j >= 0 ; j--) {
            cCoeff [j] = z [j] - mu [j] * cCoeff [j + 1];
        }

        // Calculate final coefficients
        for (int i = 0 ; i < n ; i++) {
            this.a [i] = this.y [i];
            this.b [i] = (this.y [i + 1] - this.y [i]) / h [i] - h [i] * (cCoeff [i + 1] + 2 * cCoeff [i]) / 3.0;
            this.c [i] = cCoeff [i];
            this.d [i] = (cCoeff [i + 1] - cCoeff [i]) / (3.0 * h [i]);
        }
    }
}
