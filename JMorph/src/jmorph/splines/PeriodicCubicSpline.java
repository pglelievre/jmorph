package jmorph.splines;

import jmorph.solvers.ConjugateGradient;

/** Cubic spline through a periodic 1D function.
 * http://mathworld.wolfram.com/CubicSpline.html
 * @author Peter Lelievre
 */
public class PeriodicCubicSpline {

    // ------------------ Properties -------------------

    private int n=0; // length of arrays
    private PeriodicSegments x=null; // holds the x values and x range information
    private double[] y=null; // y values
    private double[] derivs=null; // cubic spline derivatives for y versus x

    // ------------------ Constructor -------------------

    /** The isDefined method should be used after this constructor to check the construction.
     * @param n The number of points on the cubic spline.
     * @param x1 The lower bound on the x values.
     * @param x2 The upper bound on the x values.
     * @param x The x values.
     * @param y The y values (y is a 1D function of x).
     */
    public PeriodicCubicSpline(int n, double x1, double x2, double[] x, double[] y){

        this.x = new PeriodicSegments(x,x1,x2);

        // Check for error:
        if (!this.x.isDefined()) {
            this.x = null;
            return;
        }

        this.n = n;
        this.y = y;

        // Calculate the spline derivatives:
        calculate();

    }

    // -------------------- Public Methods -------------------

    /** Checks if the object has been defined correctly.
     * It many not have bent if the array provided to the constructor was not
     * monotonically increasing or the bound information was bad.
     * @return True if the object has been defined correctly, false otherwise.
     */
    public boolean isDefined() {
        return (x!=null);
    }

    /** Interpolates the cubic spline at some x location.
     * @param xp The x location at which to interpolate.
     * @return The y(xp) spline value.
     */
    public double interpolate(double xp){

        // Make sure that the spline has been calculated:
        if (derivs==null) { calculate(); }

        // Find the segment on which to interpolate:
        PeriodicSegments.FindSegmentInfo info = x.findSegment(xp);

        // Interpolate the value:
        int k1 = info.index1;
        int k2 = info.index2;
        CubicPair cp = new CubicPair(y[k1],y[k2],derivs[k1],derivs[k2]);
        return cp.interpolate(info.location);

    }

    // -------------------- Private Methods -------------------

    /** Calculates the spline derivates. */
    private void calculate() {

        // Generate the system to solve for the cubic spline second deriviates:
        double [][] mat = new double[n][n];
        double [] rhs = new double[n];
        int j1;
        int j2;
        for ( int j=0;j<n;j++ ) {
            j1 = j - 1;
            j2 = j + 1;
            if (j1<0)  { j1 += n; }
            if (j2>=n) { j2 -= n; }
            mat[j][j1] = 1.0;
            mat[j][j]  = 4.0;
            mat[j][j2] = 1.0;
            rhs[j] = 3.0*( y[j2] - y[j1] );
        }

        // Solve the system:
        ConjugateGradient cgs = new ConjugateGradient(n,mat,rhs); // uses default maximum iterations and tolerance
        derivs = cgs.solve();

    }

}
