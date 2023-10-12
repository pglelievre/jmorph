package jmorph.splines;

import geometry.MyPoint2D;
import geometry.MyPoint2DVector;

/** Catmull–Rom spline (Kochanek–Bartels spline with continuity=0.0, bias=0.0, tension=0.0).
 * http://en.wikipedia.org/wiki/Kochanek–Bartels_spline
 * http://en.wikipedia.org/wiki/Cubic_Hermite_spline
 *
 * @author Peter
 */
public final class CatmullRomSpline extends KnotsAndTangentsSpline {

    // ------------------ Constructor -------------------

    /**
     * @param knots0 The knot points defining the outline spline.
     * @param isClosed Set to true if the outline is closed, false if open.
     */
    public CatmullRomSpline(MyPoint2DVector knots0, boolean isClosed){
        super(knots0,isClosed);
        initialize();
    }

    // -------------------- Implemented Methods -------------------

    /** Calculates the spline tangents. */
    @Override
    protected void calculate() {

        // Calculate the tangent components:
        int j1,j2;
        int n = knots.size();
        double tx,ty;
        MyPoint2D p1,p2;
        tangents = new MyPoint2DVector();
        for ( int j=0 ; j<n ; j++ ) {

            // Determine indices of neighbouring knots:
            if ( !isClosed &&  (j==0) ) {
                j1 = j;
                j2 = j + 1;
            } else if ( !isClosed && j==(n-1) ) {
                j1 = j - 1;
                j2 = j;
            } else {
                j1 = j - 1;
                j2 = j + 1;
                if (j1<0)  { j1 += n; }
                if (j2>=n) { j2 -= n; }
            }

            // Extract the knots:
            p1 = knots.get(j1);
            p2 = knots.get(j2);

            // Set the tangent:
            tx = 0.5*( p2.getX() - p1.getX() );
            ty = 0.5*( p2.getY() - p1.getY() );
            tangents.add( new MyPoint2D(tx,ty) );
        }

    }

}
