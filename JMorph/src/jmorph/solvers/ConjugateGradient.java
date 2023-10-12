package jmorph.solvers;

import jmorph.maths.MathsUtils;

/** Conjugate gradient solver for full symmetric systems with Jacobi preconditioner.
 * http://en.wikipedia.org/wiki/Conjugate_gradient_method
 *
 * Could be extended to allow different preconditioners
 * (which is the reason why I haven't called the solve method directly from the constructors).
 *
 * @author Peter Lelievre
 */
public class ConjugateGradient {

    // ------------------ Properties -------------------

    // Defaults:
    private final int MAX_ITERATIONS=500;
    private final double CG_TOLERANCE=1.0E-6;

    private int n=0; // length of arrays
    private double[][] A=null; // matrix
    private double[] b=null; // right hand side
    private int maxIterations = MAX_ITERATIONS;
    private double tolerance = CG_TOLERANCE; // tolerance on the relative residual

    // ------------------ Constructors -------------------

    /** Constructor with default maximum iterations of 500 and relative residual tolerance 1.eE-6.
     * @param n The number of unknowns in the solution vector x.
     * @param A The left-hand-side matrix in the system Ax=b.
     * @param b The right-hand-side vector in the system Ax=b.
     */
    public ConjugateGradient(int n, double[][] A , double[] b){
        this.n = n;
        this.A = A;
        this.b = b;
    }

    /** Constructor with parameters for maximum iterations and relative residual tolerance.
     * @param n The number of unknowns in the solution vector x.
     * @param A The left-hand-side matrix in the system Ax=b.
     * @param b The right-hand-side vector in the system Ax=b.
     * @param maxit The maximum iterations for the CG solver.
     * @param tol The tolerance on the relative residual for the CG solution.
     */
    public ConjugateGradient(int n, double[][] A , double[] b, int maxit, double tol){
        this.n = n;
        this.A = A;
        this.b = b;
        maxIterations = maxit;
        tolerance = tol;
    }

    // ------------------ Public Methods -------------------

    //* Solves the system with the CG algorithm. */
    public double[] solve() {

        // Initialize solution to an all zero array:
        double[] x = new double[n];
        java.util.Arrays.fill(x,0.0);

        // Check for trivial solution:
        double bnrm = MathsUtils.dotProduct(n,b,b);
        if (bnrm==0.0) { return x; }

        // Generate Jacobi preconditioner:
        double[] con = new double[n];
        java.util.Arrays.fill(con,0.0);
        for ( int j=0;j<n;j++ ) {
            if ( A[j][j]!=0.0 ){
                con[j] = 1.0 / A[j][j];
            }
        }

        // Initialize:
        double[] z = new double[n];
        double[] p = new double[n];
        double[] q = new double[n];
        double[] r = new double[n];
        java.util.Arrays.fill(p,0.0);
        java.util.Arrays.fill(r,0.0); // A*x
        for ( int j=0;j<n;j++ ) {
           r[j] = b[j] - r[j]; // r = b - A*x
        }
        double rhop=1.0, rho, beta, pap, alpha, rnrm, rcg;

        // Iterate:
        for ( int k=0;k<Math.min(maxIterations,n);k++ ) {

            for ( int j=0;j<n;j++ ) {
                z[j] = con[j] * r[j]; // z = M*r (M=preconditioner)
            }

            rho = MathsUtils.dotProduct(n,r,z);
            if (rhop==0.0) { return null; } // will cause division by zero

            beta = rho / rhop;

            for ( int j=0;j<n;j++ ) {
                p[j] = z[j] + beta*p[j];
            }

            java.util.Arrays.fill(q,0.0);
            for ( int j1=0;j1<n;j1++ ) {
                for ( int j2=0;j2<n;j2++ ) {
                    q[j1] += A[j1][j2]*p[j2]; // q = A*p
                }
            }

            pap = MathsUtils.dotProduct(n,p,q);
            if (pap==0.0) { return null; } // will cause division by zero
            if (pap<=0.0) { return null; } // A not positive definite

            alpha = rho / pap;
            for ( int j=0;j<n;j++ ) {
                x[j] += alpha*p[j];
                r[j] -= alpha*q[j];
            }

            rnrm = MathsUtils.dotProduct(n,r,r);
            rhop = rho;
            rcg = Math.sqrt(rnrm/bnrm);

            //Check for convergence of relative residual:
            if ( rcg<=tolerance ) {
/*                java.util.Arrays.fill(r,0.0);
                for ( int j1=0;j1<n;j1++ ) {
                    for ( int j2=0;j2<n;j2++ ) {
                        r[j1] = r[j1] + A[j1][j2]*x[j2]; // r = A*x
                    }
                    r[j1] = r[j1] - b[j1]; // r = A*x-b
                } */
                break;
            }

        }

        return x;

    }

}
