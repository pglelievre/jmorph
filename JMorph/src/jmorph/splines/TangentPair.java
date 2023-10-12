package jmorph.splines;

import dialogs.Dialogs;
import geometry.MyPoint2D;

/** A single section of a spline interpolated between tangents at knot points.
 * @author Peter Lelievre
 */
public class TangentPair {

    // ------------------ Properties -------------------

    private final int N_INTEGRATION_SEGMENTS = 1024; // number of segments for numerical integration

    private MyPoint2D knot1=null; // the first knot point
    private MyPoint2D knot2=null; // the second knot point
    private MyPoint2D tangent1=null; // the first spline tangent
    private MyPoint2D tangent2=null; // the second spline tangent
    private double length=0; // length of the spline segment
    private boolean hasBeenCalculated = false; // whether or not the segment length has been calculated through numerical integration

    // ------------------ Constructor -------------------

    /**
     * @param k1 The first knot point.
     * @param k2 The second knot point.
     * @param t1 The spline tangent at the first knot point.
     * @param t2 The spline tangent at the second knot point.
     */
    public TangentPair(MyPoint2D k1, MyPoint2D k2, MyPoint2D t1, MyPoint2D t2){
        knot1 = k1;
        knot2 = k2;
        tangent1 = t1;
        tangent2 = t2;
    }

    // -------------------- Getters -------------------

    /** Returns the length of the segment.
     * @return The length of the segment.
     */
    public double getLength() {
        if (!hasBeenCalculated) { calculateSegmentLength(); }
        return length;
    }

    // -------------------- Public Methods -------------------

    /** Interpolates the spline at some normalized location between the knots.
     * @param t A normalized location along the spline segment (should be on [0,1]).
     * @return The interpolated point.
     */
    public MyPoint2D interpolatePoint(double t){

        // Make sure that the spline has been specified:
        if ( knot1==null || knot2==null || tangent1==null || tangent2==null ) { return null; }

        // t should be on [0,1]
        if ( t<0.0 || t>1.0 ) {
            Dialogs.codeError(null,"t=" + t + " out of range in TangentPair.interpolatePoint");
            return null;
        }

        // Calculate the point:
        double t2 = Math.pow(t,2.0);
        double t3 = Math.pow(t,3.0);
        double a1 =  2.0*t3 - 3.0*t2 + 1.0;
        double b1 =      t3 - 2.0*t2 + t;
        double a2 = -2.0*t3 + 3.0*t2;
        double b2 =      t3 -     t2;
        double px = a1*knot1.getX() + b1*tangent1.getX() + a2*knot2.getX() + b2*tangent2.getX();
        double py = a1*knot1.getY() + b1*tangent1.getY() + a2*knot2.getY() + b2*tangent2.getY();
        return new MyPoint2D(px,py);

    }

    /** Interpolates the tangent at some normalized location between the knots.
     * @param t A normalized location along the spline segment (should be on [0,1]).
     * @return The interpolated tangent.
     */
    public MyPoint2D interpolateTangent(double t){

        // Make sure that the spline has been specified:
        if ( knot1==null || knot2==null || tangent1==null || tangent2==null ) { return null; }

        // t should be on [0,1]
        if ( t<0.0 || t>1.0 ) {
            Dialogs.codeError(null,"t out of range in TangentPair.interpolateTangent");
            return null;
        }

        // Calculate the point:
        double t2 = Math.pow(t,2.0);
        double a1 =  6.0*t2 - 6.0*t;
        double b1 =  3.0*t2 - 4.0*t + 1.0;
        double a2 = -6.0*t2 + 6.0*t;
        double b2 =  3.0*t2 - 2.0*t;
        double px = a1*knot1.getX() + b1*tangent1.getX() + a2*knot2.getX() + b2*tangent2.getX();
        double py = a1*knot1.getY() + b1*tangent1.getY() + a2*knot2.getY() + b2*tangent2.getY();
        return new MyPoint2D(px,py);

    }

    // -------------------- Private Methods -------------------

    /** Calculates the segment length by numerical integration. */
    private void calculateSegmentLength() {

        // Check that the spline has been specified:
        if ( knot1==null || knot2==null || tangent1==null || tangent2==null ) { return; }

        // Calculate the segment length by numerical integration:
        length = 0.0;
        MyPoint2D p1,p2;
        double dt = 1.0 / N_INTEGRATION_SEGMENTS;
        double t; // = 0.0;
        p1 = interpolatePoint(0.0);
        for ( int k=1 ; k<=N_INTEGRATION_SEGMENTS ; k++ ) {
            t = Math.min( 1.0 , k*dt ); // to ensure that machine precision problems don't cause error with t>1.0
            p2 = interpolatePoint(t);
            length += MyPoint2D.distanceBetweenPoints(p1,p2);
            p1 = p2;
        }

        // Mark it as having been calculated:
        hasBeenCalculated = true;

    }
    
}
