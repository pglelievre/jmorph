package jmorph.splines;

/** Joined line segments defined by a monotonically increasing 1D array of x locations that define a periodic 1D function.
 * @author Peter Lelievre.
 */
@SuppressWarnings("PublicInnerClass")
public class PeriodicSegments {

    // ------------------ Properties -------------------

    private double[] x = null; // the x locations
    private double x1, x2; // the bounds of the periodic function

    // ------------------ Constructor -------------------

    /** The isDefined method should be used after this constructor to check the construction.
     * @param x Monotonically increasing x locations defining a periodic 1D function.
     * @param x1 The lower bound on x for the periodic 1D function.
     * @param x2 The upper bound on x for the periodic 1D function.
     */
    public PeriodicSegments(double[] x, double x1, double x2) {

        // Check that the x values are monotonically increasing:
        if (!isMonotonicIncreasing(x)) { return; }

        // Check the bounds:
        if (x1>=x2) { return; }

        // Check that all the x values are within the bounds:
        for ( int i=0 ; i<x.length ; i++ ) {
            if ( !checkBetweenBounds(x[i],x1,x2) ) { return; }
        }

        this.x1 = x1;
        this.x2 = x2;
        this.x = x;

    }

    // -------------------- Static Classes -------------------

    /** The class definition for the object returned by the findSegment method. */
    @SuppressWarnings("PublicField")
    public static class FindSegmentInfo {
        public int index1 = -1; /** An index of a point in a PeriodicSegments object. */
        public int index2 = -1; /** Another index of a point in a PeriodicSegments object. */
        public double location = -1; /** A location (normalized length) along a segment in a PeriodicSegments object. */
        public FindSegmentInfo() {};
    }

    // -------------------- Static Methods -------------------

    /** Checks if the values in a supplied double array are monotonically increasing (equal is not okay).
     * @param x The double array to check.
     * @return True if monotonic, false otherwise.
     */
    public static boolean isMonotonicIncreasing(double[] x) {
        for ( int i=1 ; i<x.length ; i++ ) {
            if ( x[i-1] >= x[i] ) { return false; }
        }
        return true;
    }

    /** Takes a list of theta values around an outline, finds where those values cross-over and makes them monotonic.
     * @param theta The list of theta values around the outline.
     * @return The index just after the cross-over or: -1 if failed to make theta monotonic; -2 if there are multiple cross-overs.
     */
    public static int fixCrossOver(double[] theta) {

        int n = theta.length;
        int it = 0;

       // Expect theta values to increase and have at most a single jump decrease:
       for ( int j=1 ; j<n ; j++ ) {
            // Check for a jump decrease:
            if ( theta[j-1] > theta[j] ) {
                // Check for multiple theta decreases:
                if (it!=0) {
                    return -2;
                }
                it = j;
            }
        }

        // Make the values monotonic:
        for ( int j=it ; j<n ; j++ ) {
            theta[j] += 2.0*Math.PI;
        }

        // Check for monotonicity:
        if (!isMonotonicIncreasing(theta)) { return -1; }

        // Return the index:
        return it;

    }

    // -------------------- Public Methods -------------------

    /** Returns true if the x values have been defined.
     * They will not have been defined if the array provided to the constructor was not monotonically increasing.
     * @return True if the x values have been defined, false otherwise.
     */
    public boolean isDefined() {
        return (x!=null);
    }

    /** Determines which segment is at a particular x location.
     * @param xp0 The x location.
     * @return The .index1 field holds the index of the segment and the left straddling x point; the .index2 field holds the index of the right straddling x point; null if a problem occurs (e.g. if xp is out of range or x not monotonically increasing).
     */
    public FindSegmentInfo findSegment(double xp0) {

        // Make sure the x locations have been set:
        if (x==null) { return null; }

        // Make sure the x locations are monotonic:
        if (!isMonotonicIncreasing(x)) { return null; }

        // xp should be on [x1,x2]
        double xp = xp0;
        if (!checkBetweenBounds(xp,x1,x2)) {
            double dx = x2 - x1;
            xp = x1 + ( (xp-x1) % dx ); // modular division
            if (xp<x1) { xp += dx; }
        }

        // Find which x values straddle the input point:
        int n = x.length;
        int k=0;
        for ( int i=1 ; i<n ; i++ ) {
            if ( x[i-1]<=xp && xp<x[i] ) {
                k = i;
                break;
            }
        }

        // If the above didn't work then assume that the location is between the last and first points:
        //if (k==0) { k = 0; }

        // Set the indices of the straddle points:
        int k1 = k - 1;
        int k2 = k;
        if (k1<0) { k1 += n; }

        // Determine the distance along the segment:
        double t, dx;
        if (k==0) {
            // Have to be careful if the location is between the last and first points:
            if ( xp <= x[0] ) {
                t = (xp-x1) + (x2-x[n-1]); // distance to x1 plus distance of last point to x2
            } else {
                t = xp - x[n-1]; // distance to last point
            }
            dx = (x[0]-x1) + (x2-x[n-1]); // distance from x1 to first point plus distance from last point to x2
        } else {
            t = xp - x[k1];
            dx = x[k2] - x[k1];
        }
        if (dx==0.0) { // avoid division by zero
            t = 0.0;
        } else {
            t /= dx; // t is a parameter on [0,1]
        }

        // Return the appropriate information:
        FindSegmentInfo out = new FindSegmentInfo();
        out.index1 = k1;
        out.index2 = k2;
        out.location = t;
        return out;

    }

    // -------------------- Private Methods -------------------

    /** Checks whether an x location is within two bounds.
     * @param xp The x location.
     * @param x1 The lower bound.
     * @param x2 The upper bound.
     * @return True if x is on [x1,x2], false otherwise.
     */
    private boolean checkBetweenBounds(double xp, double x1, double x2) {
        return ( xp>=x1 && xp<=x2 );
    }

}
