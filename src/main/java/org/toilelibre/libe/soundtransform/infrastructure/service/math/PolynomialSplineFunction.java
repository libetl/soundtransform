package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Represents a piecewise polynomial spline function
 */
public final class PolynomialSplineFunction {

    private final double[] xValues;
    private final double[] yValues;
    private final double[] b;
    private final double[] c;
    private final double[] d;

    /**
     * Create a spline function with coefficients
     */
    public PolynomialSplineFunction (final double[] xValues, final double[] yValues, final double[] b, final double[] c, final double[] d) {
        this.xValues = xValues;
        this.yValues = yValues;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * Evaluate the spline at a given x value
     */
    public double value (final double x) {
        // Find the interval
        int i = 0;
        for (i = 0; i < this.xValues.length - 1; i++) {
            if (x < this.xValues[i + 1]) {
                break;
            }
        }

        final double dx = x - this.xValues[i];
        return this.yValues[i] + this.b[i] * dx + this.c[i] * dx * dx + this.d[i] * dx * dx * dx;
    }

    /**
     * Check if x is within the valid range for interpolation
     */
    public boolean isValidPoint (final double x) {
        return x >= this.xValues[0] && x <= this.xValues[this.xValues.length - 1];
    }

    /**
     * Get the first x value
     */
    public double getLeftBoundary () {
        return this.xValues[0];
    }

    /**
     * Get the last x value
     */
    public double getRightBoundary () {
        return this.xValues[this.xValues.length - 1];
    }
}
