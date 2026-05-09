package org.toilelibre.libe.soundtransform.infrastructure.service.fourier;

import java.io.Serializable;

/**
 * Immutable complex number representation to replace org.apache.commons.math3.complex.Complex
 */
public final class Complex implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double real;
    private final double imaginary;

    private static final double EPSILON = 1e-10;

    public Complex(final double real) {
        this(real, 0);
    }

    public Complex(final double real, final double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal() {
        return this.real;
    }

    public double getImaginary() {
        return this.imaginary;
    }

    public double abs() {
        if (Double.isInfinite(this.real) || Double.isInfinite(this.imaginary)) {
            return Double.POSITIVE_INFINITY;
        }
        if (Double.isNaN(this.real) || Double.isNaN(this.imaginary)) {
            return Double.NaN;
        }
        if (this.real == 0) {
            return Math.abs(this.imaginary);
        }
        if (this.imaginary == 0) {
            return Math.abs(this.real);
        }
        final double a = Math.abs(this.real);
        final double b = Math.abs(this.imaginary);
        if (a < b) {
            return b * Math.sqrt(1 + (a / b) * (a / b));
        }
        return a * Math.sqrt(1 + (b / a) * (b / a));
    }

    public Complex add(final Complex other) {
        return new Complex(this.real + other.real, this.imaginary + other.imaginary);
    }

    public Complex subtract(final Complex other) {
        return new Complex(this.real - other.real, this.imaginary - other.imaginary);
    }

    public Complex multiply(final Complex other) {
        return new Complex(
            this.real * other.real - this.imaginary * other.imaginary,
            this.real * other.imaginary + this.imaginary * other.real
        );
    }

    public Complex multiply(final double factor) {
        return new Complex(this.real * factor, this.imaginary * factor);
    }

    public Complex divide(final double divisor) {
        return new Complex(this.real / divisor, this.imaginary / divisor);
    }

    public Complex negate() {
        return new Complex(-this.real, -this.imaginary);
    }

    public Complex conjugate() {
        return new Complex(this.real, -this.imaginary);
    }

    public double getArgument() {
        return Math.atan2(this.imaginary, this.real);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Complex)) {
            return false;
        }
        final Complex c = (Complex) other;
        return Math.abs(this.real - c.real) < Complex.EPSILON &&
               Math.abs(this.imaginary - c.imaginary) < Complex.EPSILON;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(this.real) + Double.hashCode(this.imaginary);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", this.real, this.imaginary);
    }
}
