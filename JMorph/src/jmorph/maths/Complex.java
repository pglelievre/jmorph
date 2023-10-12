package jmorph.maths;

/** Data type for complex numbers.
 * @author Peter Lelievre
 */
public final class Complex {
    
    // Immutable parameters (once constructed cannot be changed).
    protected final double real;   // the real part
    protected final double imag;   // the imaginary part

    public Complex(double r, double i) {
        real = r;
        imag = i;
    }

    public double abs() {
        return Math.hypot(real,imag);
    }

    public double phase() {
        return Math.atan2(imag,real); // [-pi,pi]
    }

    public Complex plus(Complex c) {
        double r = this.real + c.real;
        double i = this.imag + c.imag;
        return new Complex(r,i);
    }

    public Complex minus(Complex c) {
        double r = this.real - c.real;
        double i = this.imag - c.imag;
        return new Complex(r,i);
    }

    public Complex times(Complex c) {
        double r = this.real * c.real - this.imag * c.imag;
        double i = this.real * c.imag + this.imag * c.real;
        return new Complex(r,i);
    }

    public Complex times(double alpha) {
        double r = alpha * real;
        double i = alpha * imag;
        return new Complex(r,i);
    }

    public Complex conjugate() {
        return new Complex(real,-imag);
    }

    public Complex reciprocal() {
        double scale = real*real + imag*imag;
        double r =  real / scale;
        double i = -imag / scale;
        return new Complex(r,i);
    }

    public double real() { return real; }
    public double imag() { return imag; }

    public Complex divide(Complex b) {
        return this.times(b.reciprocal());
    }

    public Complex pow(double p) {
        double r = Math.pow(this.abs(),p) * Math.cos(p*this.phase());
        double i = Math.pow(this.abs(),p) * Math.sin(p*this.phase());
        return new Complex(r,i);
    }
    
}
