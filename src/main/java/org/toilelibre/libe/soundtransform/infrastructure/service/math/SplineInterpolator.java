package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Pure Java implementation of cubic spline interpolation
 * Replaces commons-math3 SplineInterpolator
 */
public final class SplineInterpolator {

    /**
     * Interpolate using cubic spline
     * 
     * @param xValues x coordinates (must be sorted in ascending order)
     * @param yValues y coordinates (must have same length as xValues)
     * @return PolynomialSplineFunction that can evaluate the spline at any x
     */
    public PolynomialSplineFunction interpolate (final double[] xValues, final double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException ("xValues and yValues must have the same length");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException ("Need at least 2 points to interpolate");
        }

        // Verify x values are sorted
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException ("xValues must be strictly increasing");
            }
        }

        final int n = xValues.length;

        // Calculate differences
        final double[] h = new double[n - 1];
        final double[] alpha = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            h[i] = xValues[i + 1] - xValues[i];
            alpha[i] = 3.0 * (yValues[i + 1] - yValues[i]) / h[i] - 3.0 * (yValues[i] - (i > 0 ? yValues[i - 1] : yValues[i])) / (i > 0 ? h[i - 1] : h[i]);
        }

        // Solve tridiagonal system
        final double[] l = new double[n];
        final double[] mu = new double[n - 1];
        final double[] z = new double[n];
        l[0] = 1;
        mu[0] = 0;
        z[0] = 0;

        for (int i = 1; i < n - 1; i++) {
            l[i] = 2 * (xValues[i + 1] - xValues[i - 1]) - h[i - 1] * mu[i - 1];
            mu[i] = h[i] / l[i];
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }

        l[n - 1] = 1;
        z[n - 1] = 0;

        // Back substitution
        final double[] c = new double[n];
        final double[] b = new double[n - 1];
        final double[] d = new double[n - 1];

        c[n - 1] = 0;
        for (int j = n - 2; j >= 0; j--) {
            c[j] = z[j] - mu[j] * c[j + 1];
            b[j] = (yValues[j + 1] - yValues[j]) / h[j] - h[j] * (c[j + 1] + 2 * c[j]) / 3.0;
            d[j] = (c[j + 1] - c[j]) / (3.0 * h[j]);
        }

        return new PolynomialSplineFunction (xValues, yValues, b, c, d);
    }
}
