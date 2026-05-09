package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Pure Java implementation of cubic spline interpolation
 * Replaces commons-math3 SplineInterpolator
 */
public final class CubicSplineInterpolator {

    /**
     * Interpolate using cubic splines
     * 
     * @param x array of x coordinates (must be sorted in ascending order)
     * @param y array of y coordinates (same length as x)
     * @return PolynomialSplineFunction for evaluation
     */
    public PolynomialSplineFunction interpolate (final double[] x, final double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException ("x and y arrays must have the same length");
        }
        if (x.length < 2) {
            throw new IllegalArgumentException ("Need at least 2 points");
        }

        final int n = x.length - 1;
        final double[] h = new double[n];
        final double[] alpha = new double[n];

        for (int i = 0; i < n; i++) {
            h[i] = x[i + 1] - x[i];
            if (h[i] <= 0) {
                throw new IllegalArgumentException ("x values must be strictly increasing");
            }
        }

        for (int i = 1; i < n; i++) {
            alpha[i] = (3.0 / h[i]) * (y[i + 1] - y[i]) - (3.0 / h[i - 1]) * (y[i] - y[i - 1]);
        }

        final double[] l = new double[n + 1];
        final double[] mu = new double[n];
        final double[] z = new double[n + 1];

        l[0] = 1;
        mu[0] = 0;
        z[0] = 0;

        for (int i = 1; i < n; i++) {
            l[i] = 2 * (x[i + 1] - x[i - 1]) - h[i - 1] * mu[i - 1];
            mu[i] = h[i] / l[i];
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }

        l[n] = 1;
        z[n] = 0;

        final double[] c = new double[n + 1];
        final double[] b = new double[n];
        final double[] d = new double[n];

        c[n] = 0;
        for (int j = n - 1; j >= 0; j--) {
            c[j] = z[j] - mu[j] * c[j + 1];
            b[j] = (y[j + 1] - y[j]) / h[j] - h[j] * (c[j + 1] + 2 * c[j]) / 3;
            d[j] = (c[j + 1] - c[j]) / (3 * h[j]);
        }

        return new PolynomialSplineFunction (x, y, b, c, d);
    }
}
