package jmorph.splines;

import dialogs.Dialogs;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;

/** Closed or open outline spline defined by tangents at knot points.
 * @author Peter Lelievre
 */
public abstract class KnotsAndTangentsSpline implements OutlineSpline {

    // ------------------ Properties -------------------

    @SuppressWarnings("ProtectedField")
    protected MyPoint2DVector knots=null; // the knot points
    @SuppressWarnings("ProtectedField")
    protected MyPoint2DVector tangents=null; // the spline tangents
    private JoinedSegments segments=null; // contains information about the length of each spline segment
    @SuppressWarnings("ProtectedField")
    protected boolean isClosed = true;

    // ------------------ Constructor -------------------

    /**
     * @param knots0 The knot points defining the outline spline.
     * @param isClosed Set to true if the outline is closed, false if open.
     */
    public KnotsAndTangentsSpline(MyPoint2DVector knots0, boolean isClosed){
        knots = knots0;
        this.isClosed = isClosed;
    }
    
    /** This method MUST be called by any subclass constructors, after the call to super()! */
    protected final void initialize() {
        calculate(); // calculates the tangents
        calculateSegmentLengths(); // requires the tangents
    }
    

    // -------------------- Implemented Methods -------------------

    /** Returns true if the outline is closed.
     * The value returned depends on whether the spline was specified as open or closed during construction.
     * @return True if the outline is closed, false otherwise.
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    /** Returns the total length of the spline curve.
     * @return The total length of the spline curve or a non-positive value if the required information does not exist.
     */
    @Override
    public double splineLength() {
        if (segments==null) { calculateSegmentLengths(); }
        if (segments==null) { return 0.0; }
        return segments.getTotalLength();
    }

    /** Interpolates the spline at some normalized arc length location along the spline.
     * @param t Normalized arc length at which to interpolate (should be on [0,1]).
     * @return The interpolated point.
     */
    @Override
    public MyPoint2D interpolatePoint(double t){

        // Make sure that the spline has been calculated:
        if (tangents==null) { calculate(); }
        if (tangents==null) { return null; }
        if (segments==null) { calculateSegmentLengths(); }
        if (segments==null) { return null; }

        // t should be on [0,1]
        if ( t<0.0 || t>1.0 ) {
            Dialogs.codeError(null,"t out of range in KnotsAndTangents.interpolatePoint");
            return null;
        }

        // Find segment we need to use:
        JoinedSegments.FindSegmentInfo info = segments.findSegment(t);

        // If the above didn't work then an error should be thrown:
        if (info==null) {
            Dialogs.codeError(null,"unable to find segment in KnotsAndTangents.interpolatePoint");
            return null;
        }

        // Interpolate the point:
        return interpolatePointOnSegment( info.index , info.location );

    }

    /** Interpolates the tangent at some normalized arc length location along the spline.
     * @param t Normalized arc length at which to interpolate (should be on [0,1]).
     * @return 
     */
    @Override
    public MyPoint2D interpolateTangent(double t){

        // Make sure that the spline has been calculated:
        if (tangents==null) { calculate(); }
        if (tangents==null) { return null; }
        if (segments==null) { calculateSegmentLengths(); }
        if (segments==null) { return null; }

        // t should be on [0,1]
        if ( t<0.0 || t>1.0 ) {
            Dialogs.codeError(null,"t out of range in KnotsAndTangents.interpolateTangent");
            return null;
        }

        // Find segment we need to use:
        JoinedSegments.FindSegmentInfo info = segments.findSegment(t);

        // If the above didn't work then an error should be thrown:
        if (info==null) {
            Dialogs.codeError(null,"unable to find segment in KnotsAndTangents.interpolateTangent");
            return null;
        }

        // Interpolate the tangent:
        return interpolateTangentOnSegment( info.index , info.location );

    }

    // -------------------- Abstract Methods -------------------

    /** Calculates the spline tangents. */
    protected abstract void calculate();

    // -------------------- Private Methods -------------------

    /** Interpolates the spline at some normalized location on some segment.
     * @param j The index of the segment to interpolate on.
     * @param t The normalized location along the segment at which to interpolate (should be on [0,1]).
     * @return The interpolated point.
     */
    private MyPoint2D interpolatePointOnSegment(int j, double t){

        // Make sure that the spline has been calculated:
        if (tangents==null) { calculate(); }
        if (tangents==null) { return null; }

        // j should be on [0,n-1], t should be on [0,1]
        int n = knots.size();
        if ( j<0 || j>=n ) {
            Dialogs.codeError(null,"j out of range in KnotsAndTangents.interpolatePointOnSegment");
            return null;
        }
        if ( t<0.0 || t>1.0 ) {
            Dialogs.codeError(null,"t out of range in KnotsAndTangents.interpolatePointOnSegment");
            return null;
        }

        // Determine the indices to use:
        int j1 = j;
        int j2 = j + 1;
        if (j2>=n) { j2 -= n; }

        // If the index specifies between the last and first point and it is not closed then return null:
        if ( !isClosed && j2==0 ) {
            return null;
        }

        // Create TangentPair object:
        TangentPair tpair = new TangentPair(knots.get(j1),knots.get(j2),tangents.get(j1),tangents.get(j2));

        // Calculate the point:
        return tpair.interpolatePoint(t);

    }

    /** Interpolates the tangent at some normalized location on some segment.
     * @param j The index of the segment to interpolate on.
     * @param t The normalized location along the segment at which to interpolate (should be on [0,1]).
     * @return The tangent at the interpolation point.
     */
    private MyPoint2D interpolateTangentOnSegment(int j, double t){

        // Make sure that the spline has been calculated:
        if (tangents==null) { calculate(); }
        if (tangents==null) { return null; }

        // j should be on [1,n], t should be on [0,1]
        int n = knots.size();
        if ( j<0 || j>=n ) {
            Dialogs.codeError(null,"j out of range in KnotsAndTangents.interpolateTangentOnSegment");
            return null;
        }
        if ( t<0.0 || t>1.0 ) {
            Dialogs.codeError(null,"t out of range in KnotsAndTangents.interpolateTangentOnSegment");
            return null;
        }

        // Determine the indices to use:
        int j1 = j;
        int j2 = j + 1;
        if (j2>=n) { j2 -= n; }

        // If the index specifies between the last and first point and it is not closed then return null:
        if ( !isClosed && j2==0 ) {
            return null;
        }

        // Create TangentPair object:
        TangentPair tpair = new TangentPair(knots.get(j1),knots.get(j2),tangents.get(j1),tangents.get(j2));

        // Calculate the point:
        return tpair.interpolateTangent(t);

    }

    /** Calculates the spline tangents and segment lengths. */
    protected void calculateSegmentLengths() {

        // Check that the tangents have been calculated:
        if (tangents==null) { calculate(); }
        if (tangents==null) { return; }

        // Calculate the segment lengths:
        int n = knots.size();
        if (!isClosed) { n -= 1; }
        double[] ds = new double[n];
        for ( int j=0 ; j<n ; j++ ) {

            // Determine the indices to use:
            int j1 = j;
            int j2 = j + 1;
            if (j2>=n) { j2 -= n; }

            // Create TangentPair object:
            TangentPair tpair = new TangentPair(knots.get(j1),knots.get(j2),tangents.get(j1),tangents.get(j2));

            // Get the length:
            ds[j] = tpair.getLength();

        }

        // Create the JoinedSegments object:
        segments = new JoinedSegments(ds);

    }

}
