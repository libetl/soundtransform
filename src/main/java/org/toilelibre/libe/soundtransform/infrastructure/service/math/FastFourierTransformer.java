package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Pure Java implementation of Fast Fourier Transform (Cooley-Tukey algorithm)
 * without external dependencies
 */
public final class FastFourierTransformer {

    private static final int MIN_POWER_OF_TWO = 1;

    /**
     * Perform forward FFT on real-valued input
     * 
     * @param input array of real values (length must be a power of 2)
     * @return array of complex values
     */
    public static Complex[] transform (final double[] input) {
        return FastFourierTransformer.fft (input, false);
    }

    /**
     * Perform inverse FFT
     * 
     * @param input array of complex values (length must be a power of 2)
     * @return array of complex values
     */
    public static Complex[] inverseTransform (final Complex[] input) {
        return FastFourierTransformer.ifft (input);
    }

    /**
     * Perform in-place FFT on 2D array [real[], imag[]]
     * 
     * @param data 2D array where data[0] = real parts, data[1] = imaginary parts
     * @param forward true for forward, false for inverse
     */
    public static void transformInPlace (final double[][] data, final boolean forward) {
        if (data.length != 2 || data[0].length != data[1].length) {
            throw new IllegalArgumentException ("Input must be [real[], imag[]] with equal lengths");
        }

        final int n = data[0].length;
        final Complex[] input = new Complex[n];
        for (int i = 0; i < n; i++) {
            input[i] = new Complex (data[0][i], data[1][i]);
        }

        final Complex[] output = forward ? FastFourierTransformer.fft (input, false) : FastFourierTransformer.ifft (input);

        for (int i = 0; i < n; i++) {
            data[0][i] = output[i].getReal ();
            data[1][i] = output[i].getImaginary ();
        }
    }

    /**
     * Internal FFT implementation using Cooley-Tukey algorithm
     */
    private static Complex[] fft (final double[] input, final boolean inverse) {
        final int n = input.length;
        if (!FastFourierTransformer.isPowerOfTwo (n)) {
            throw new IllegalArgumentException ("Input length must be a power of 2, got: " + n);
        }

        final Complex[] complex = new Complex[n];
        for (int i = 0; i < n; i++) {
            complex[i] = new Complex (input[i], 0);
        }

        return FastFourierTransformer.fft (complex, inverse);
    }

    /**
     * Internal FFT implementation for complex input
     */
    private static Complex[] fft (final Complex[] input, final boolean inverse) {
        final int n = input.length;

        if (n == 1) {
            return input;
        }

        if (!FastFourierTransformer.isPowerOfTwo (n)) {
            throw new IllegalArgumentException ("Input length must be a power of 2");
        }

        // Divide
        final Complex[] even = new Complex[n / 2];
        final Complex[] odd = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            even[i] = input[2 * i];
            odd[i] = input[2 * i + 1];
        }

        // Conquer
        final Complex[] evenFFT = FastFourierTransformer.fft (even, inverse);
        final Complex[] oddFFT = FastFourierTransformer.fft (odd, inverse);

        // Combine
        final Complex[] output = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            final double angle = (inverse ? 2 : -2) * Math.PI * k / n;
            final Complex t = new Complex (Math.cos (angle), Math.sin (angle)).multiply (oddFFT[k]);
            output[k] = evenFFT[k].add (t);
            output[k + n / 2] = evenFFT[k].subtract (t);
        }

        return output;
    }

    /**
     * Inverse FFT
     */
    private static Complex[] ifft (final Complex[] input) {
        final int n = input.length;

        // Conjugate input
        final Complex[] conjugate = new Complex[n];
        for (int i = 0; i < n; i++) {
            conjugate[i] = input[i].conjugate ();
        }

        // Forward FFT on conjugate
        final Complex[] result = FastFourierTransformer.fft (conjugate, true);

        // Conjugate and scale output
        final Complex[] output = new Complex[n];
        for (int i = 0; i < n; i++) {
            output[i] = result[i].conjugate ().multiply (1.0 / n);
        }

        return output;
    }

    /**
     * Check if a number is a power of 2
     */
    private static boolean isPowerOfTwo (final int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * Get next power of 2 that is >= n
     */
    public static int nextPowerOfTwo (final int n) {
        if (n <= FastFourierTransformer.MIN_POWER_OF_TWO) {
            return FastFourierTransformer.MIN_POWER_OF_TWO;
        }
        int power = FastFourierTransformer.MIN_POWER_OF_TWO;
        while (power < n) {
            power *= 2;
        }
        return power;
    }
}
