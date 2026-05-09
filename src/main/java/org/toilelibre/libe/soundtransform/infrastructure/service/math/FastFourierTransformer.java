package org.toilelibre.libe.soundtransform.infrastructure.service.math;

/**
 * Pure Java implementation of Fast Fourier Transform using Cooley-Tukey algorithm
 * Replaces commons-math3 FastFourierTransformer without external dependencies
 */
public final class FastFourierTransformer {

    /**
     * Transform type enumeration
     */
    public enum TransformType {
        FORWARD, INVERSE
    }

    /**
     * DFT normalization enumeration
     */
    public enum DftNormalization {
        STANDARD, UNITARY
    }

    private final DftNormalization normalization;

    /**
     * Constructor with normalization parameter
     */
    public FastFourierTransformer (final DftNormalization normalization) {
        this.normalization = normalization;
    }

    /**
     * Transform a real-valued input array to complex array using FFT
     * Input array must have length that is a power of 2
     */
    public Complex [] transform (final double [] input, final TransformType type) {
        final int n = input.length;
        this.ensurePowerOfTwo (n);

        final Complex [] complexInput = new Complex [n];
        for (int i = 0; i < n; i++) {
            complexInput [i] = new Complex (input [i], 0);
        }

        return this.transformInternalComplex (complexInput, type == TransformType.INVERSE);
    }

    /**
     * Transform a complex array using FFT
     */
    public Complex [] transform (final Complex [] input, final TransformType type) {
        final int n = input.length;
        this.ensurePowerOfTwo (n);

        final Complex [] data = new Complex [n];
        System.arraycopy (input, 0, data, 0, n);

        return this.transformInternalComplex (data, type == TransformType.INVERSE);
    }

    /**
     * Transform in-place using double[][] format [real, imaginary]
     */
    public static void transformInPlace (final double [][] data, final DftNormalization normalization, final TransformType type) {
        final int n = data [0].length;
        if (n != data [1].length) {
            throw new IllegalArgumentException ("Real and imaginary arrays must have same length");
        }

        final FastFourierTransformer transformer = new FastFourierTransformer (normalization);
        final Complex [] complexData = new Complex [n];
        for (int i = 0; i < n; i++) {
            complexData [i] = new Complex (data [0] [i], data [1] [i]);
        }

        final Complex [] result = transformer.transformInternalComplex (complexData, type == TransformType.INVERSE);

        for (int i = 0; i < n; i++) {
            data [0] [i] = result [i].getReal ();
            data [1] [i] = result [i].getImaginary ();
        }
    }

    /**
     * Internal FFT implementation using Cooley-Tukey algorithm
     */
    private Complex [] transformInternalComplex (final Complex [] data, final boolean inverse) {
        final int n = data.length;

        if (n == 1) {
            return data;
        }

        // Bit reversal permutation
        this.bitReversalPermutation (data);

        // FFT computation
        for (int s = 1; s <= Math.log (n) / Math.log (2); s++) {
            final int m = 1 << s; // 2^s
            final double angle = (inverse ? 1 : -1) * 2 * Math.PI / m;
            final Complex wm = new Complex (Math.cos (angle), Math.sin (angle));

            for (int k = 0; k < n; k += m) {
                Complex w = new Complex (1, 0);

                for (int j = 0; j < m / 2; j++) {
                    final Complex t = w.multiply (data [k + j + m / 2]);
                    final Complex u = data [k + j];

                    data [k + j] = u.add (t);
                    data [k + j + m / 2] = u.subtract (t);

                    w = w.multiply (wm);
                }
            }
        }

        // Apply normalization
        if (this.normalization == DftNormalization.STANDARD && inverse) {
            final double factor = 1.0 / n;
            for (int i = 0; i < n; i++) {
                data [i] = data [i].multiply (factor);
            }
        } else if (this.normalization == DftNormalization.UNITARY) {
            final double factor = 1.0 / Math.sqrt (n);
            for (int i = 0; i < n; i++) {
                data [i] = data [i].multiply (factor);
            }
        }

        return data;
    }

    /**
     * Perform bit reversal permutation on the array
     */
    private void bitReversalPermutation (final Complex [] data) {
        final int n = data.length;
        final int bits = (int) (Math.log (n) / Math.log (2));

        for (int i = 0; i < n; i++) {
            final int j = this.reverseBits (i, bits);
            if (i < j) {
                final Complex temp = data [i];
                data [i] = data [j];
                data [j] = temp;
            }
        }
    }

    /**
     * Reverse the bits of a number
     */
    private int reverseBits (final int x, final int bits) {
        int result = 0;
        for (int i = 0; i < bits; i++) {
            result = (result << 1) | (x & 1);
            x >>= 1;
        }
        return result;
    }

    /**
     * Ensure that the input length is a power of 2
     */
    private void ensurePowerOfTwo (final int n) {
        if (n == 0 || (n & (n - 1)) != 0) {
            throw new IllegalArgumentException ("Input length must be a power of 2, got: " + n);
        }
    }
}
