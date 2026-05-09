package org.toilelibre.libe.soundtransform.infrastructure.service.math;

import java.io.Serializable;

/**
 * Immutable complex number implementation
 * Replaces org.apache.commons.math3.complex.Complex
 */
public final class ComplexNumber implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final double real;
    private final double imaginary;
    
    public ComplexNumber(final double real, final double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }
    
    public ComplexNumber(final double real) {
        this(real, 0.0);
    }
    
    public static ComplexNumber valueOf(final double real, final double imaginary) {
        return new ComplexNumber(real, imaginary);
    }
    
    public static ComplexNumber valueOf(final double real) {
        return new ComplexNumber(real, 0.0);
    }
    
    public double getReal() {
        return this.real;
    }
    
    public double getImaginary() {
        return this.imaginary;
    }
    
    public double abs() {
        return Math.hypot(this.real, this.imaginary);
    }
    
    public double getArgument() {
        return Math.atan2(this.imaginary, this.real);
    }
    
    public ComplexNumber add(final ComplexNumber other) {
        return new ComplexNumber(this.real + other.real, this.imaginary + other.imaginary);
    }
    
    public ComplexNumber subtract(final ComplexNumber other) {
        return new ComplexNumber(this.real - other.real, this.imaginary - other.imaginary);
    }
    
    public ComplexNumber multiply(final ComplexNumber other) {
        return new ComplexNumber(
            this.real * other.real - this.imaginary * other.imaginary,
            this.real * other.imaginary + this.imaginary * other.real
        );
    }
    
    public ComplexNumber multiply(final double scalar) {
        return new ComplexNumber(this.real * scalar, this.imaginary * scalar);
    }
    
    public ComplexNumber divide(final ComplexNumber other) {
        final double denominator = other.real * other.real + other.imaginary * other.imaginary;
        return new ComplexNumber(
            (this.real * other.real + this.imaginary * other.imaginary) / denominator,
            (this.imaginary * other.real - this.real * other.imaginary) / denominator
        );
    }
    
    public ComplexNumber conjugate() {
        return new ComplexNumber(this.real, -this.imaginary);
    }
    
    public ComplexNumber negate() {
        return new ComplexNumber(-this.real, -this.imaginary);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ComplexNumber)) {
            return false;
        }
        final ComplexNumber other = (ComplexNumber) obj;
        return Double.doubleToLongBits(this.real) == Double.doubleToLongBits(other.real)
            && Double.doubleToLongBits(this.imaginary) == Double.doubleToLongBits(other.imaginary);
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
