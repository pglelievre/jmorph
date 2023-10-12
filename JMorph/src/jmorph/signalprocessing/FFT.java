package jmorph.signalprocessing;

import jmorph.maths.Complex;

/** Contains static methods for computing the FFT and inverse FFT of a length N complex sequence.
 * Author: Peter Lelievre.
 */
public class FFT {

    /** Calculates the FFT of x.
     * @param x Length must be power of 2.
     * @return  */
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) { return new Complex[] { x[0] }; }

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    /** Calculates the inverse FFT of x.
     * @param x Length must be power of 2.
     * @return  */
    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }

        return y;

    }

    /** Calculates the circular convolution of x and y.
     * @param x
     * @param y
     * @return  */
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) { throw new RuntimeException("Dimensions don't agree"); }

        int N = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[N];
        for (int i = 0; i < N; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }

    /** Calculates the linear convolution of x and y.
     * @param x
     * @param y
     * @return  */
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        Complex[] a = new Complex[2*x.length];
        System.arraycopy(x, 0, a, 0, x.length);
        //for (int i = 0;        i <   x.length; i++) { a[i] = x[i]; }
        for (int i = x.length; i < 2*x.length; i++) { a[i] = ZERO; }

        Complex[] b = new Complex[2*y.length];
        System.arraycopy(y, 0, b, 0, y.length);
        //for (int i = 0;        i <   y.length; i++) { b[i] = y[i]; }
        for (int i = y.length; i < 2*y.length; i++) { b[i] = ZERO; }

        return cconvolve(a, b);
    }
    
}
