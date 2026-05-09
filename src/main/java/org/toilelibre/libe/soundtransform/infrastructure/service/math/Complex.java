package org.toilelibre.libe.soundtransform.infrastructure.service.math;

import java.io.Serializable;

/**
 * Pure Java implementation of a complex number without external dependencies
 */
public final class Complex implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final double EPSILON = 1e-10;

    private final double real;
    private final double imaginary;

    /**
     * Create a complex number with real and imaginary parts
     */
    public Complex (final double real, final double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    /**
     * Get the real part
     */
    public double getReal () {
        return this.real;
    }

    /**
     * Get the imaginary part
     */
    public double getImaginary () {
        return this.imaginary;
    }

    /**
     * Calculate the absolute value (magnitude)
     */
    public double abs () {
        return Math.hypot (this.real, this.imaginary);
    }

    /**
     * Calculate the squared magnitude (more efficient than abs for comparisons)
     */
    public double absSquared () {
        return this.real * this.real + this.imaginary * this.imaginary;
    }

    /**
     * Get the argument (phase angle) in radians
     */
    public double getArgument () {
        return Math.atan2 (this.imaginary, this.real);
    }

    /**
     * Add another complex number
     */
    public Complex add (final Complex other) {
        return new Complex (this.real + other.real, this.imaginary + other.imaginary);
    }

    /**
     * Subtract another complex number
     */
    public Complex subtract (final Complex other) {
        return new Complex (this.real - other.real, this.imaginary - other.imaginary);
    }

    /**
     * Multiply by another complex number
     */
    public Complex multiply (final Complex other) {
        final double newReal = this.real * other.real - this.imaginary * other.imaginary;
        final double newImag = this.real * other.imaginary + this.imaginary * other.real;
        return new Complex (newReal, newImag);
    }

    /**
     * Multiply by a scalar
     */
    public Complex multiply (final double scalar) {
        return new Complex (this.real * scalar, this.imaginary * scalar);
    }

    /**
     * Divide by another complex number
     */
    public Complex divide (final Complex other) {
        final double denominator = other.absSquared ();
        final double newReal = (this.real * other.real + this.imaginary * other.imaginary) / denominator;
        final double newImag = (this.imaginary * other.real - this.real * other.imaginary) / denominator;
        return new Complex (newReal, newImag);
    }

    /**
     * Get the complex conjugate
     */
    public Complex conjugate () {
        return new Complex (this.real, -this.imaginary);
    }

    @Override
    public String toString () {
        if (Math.abs (this.imaginary) < Complex.EPSILON) {
            return String.format ("%.6f", this.real);
        }
        if (Math.abs (this.real) < Complex.EPSILON) {
            return String.format ("%.6fi", this.imaginary);
        }
        final String sign = this.imaginary >= 0 ? "+" : "";
        return String.format ("%.6f%s%.6fi", this.real, sign, this.imaginary);
    }

    @Override
    public boolean equals (final Object obj) {
        if (!(obj instanceof Complex)) {
            return false;
        }
        final Complex other = (Complex) obj;
        return Math.abs (this.real - other.real) < Complex.EPSILON && Math.abs (this.imaginary - other.imaginary) < Complex.EPSILON;
    }

    @Override
    public int hashCode () {
        return Double.hashCode (this.real) ^ Double.hashCode (this.imaginary);
    }
}
