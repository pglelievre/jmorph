package jmorph.splines;

import geometry.Circle;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;

/** Spline that preserves circle-like characteristics.
 *
 * @author Peter
 */
public final class CirclePreservingSpline extends KnotsAndTangentsSpline {
    
    // ------------------ Constructor -------------------

    /**
     * @param knots0 The knot points defining the outline spline.
     * @param isClosed Set to true if the outline is closed, false if open.
     */
    public CirclePreservingSpline(MyPoint2DVector knots0, boolean isClosed){
        super(knots0,isClosed);
        initialize();
    }

    // -------------------- Implemented Methods -------------------

    /** Calculates the spline tangents. */
    @Override
    protected void calculate() {

        // Calculate the tangent components:
        int n = knots.size();
        int j1,j2;
        tangents = new MyPoint2DVector();
        MyPoint2D pj,p0,p1,p2;
        double p0x,p0y,p1x,p1y,p2x,p2y,a,b,c,d,xc,yc,dx,dy,dlen,tx,ty,tlen,dotprod;
        Circle circ;
        MyPoint2D cc;
        for ( int j=0 ; j<n ; j++ ) {

            // Determine indices of neighbouring knots:
            int j0; // the middle knot
            if ( !isClosed &&  (j==0) ) {
                j0 = j + 1;
            } else if ( !isClosed && j==(n-1) ) {
                j0 = j - 1;
            } else {
                j0 = j;
            }
            j1 = j0 - 1;
            j2 = j0 + 1;
            if (j1<0)  { j1 += n; }
            if (j2>=n) { j2 -= n; }

            // Fit a circular arc through the three points:
            pj = knots.get(j);
            p0 = knots.get(j0);
            p1 = knots.get(j1);
            p2 = knots.get(j2);
            circ = new Circle(p0,p1,p2);
            cc = circ.getCentre();

            // Determine indices of neighbouring knots as per CatmullRom:
            /*
            if ( !isClosed &&  (j==0) ) {
                j1 = j;
                j2 = j + 1;
            } else if ( !isClosed && j==(n-1) ) {
                j1 = j - 1;
                j2 = j;
            } else {
                j1 = j - 1;
                j2 = j + 1;
                if (j1<0)  { j1 = j1 + n; }
                if (j2>=n) { j2 = j2 - n; }
            }
            */
            
            // Calculate and set the tangent:
            if (cc==null) {
                // (the 3 points are colinear)
                tx = 0.5*( p2.getX() - p1.getX() );
                ty = 0.5*( p2.getY() - p1.getY() );
            } else {
                xc = cc.getX();
                yc = cc.getY();
                // Calculate tangent at current knot point as per CatmulRom (for scaling purposes):
                dx = 0.5*( p2.getX() - p1.getX() );
                dy = 0.5*( p2.getY() - p1.getY() );
                dlen = Math.sqrt(dx*dx + dy*dy);
                // Calculate tangent at current knot point:
                // http://en.wikipedia.org/wiki/Circle#Tangent_lines
                ty = pj.getX() - xc;
                tx = pj.getY() - yc;
                tlen = Math.sqrt(tx*tx + ty*ty);
                ty = -ty;
                // Need to make sure the tangent points in the correct direction:
                dotprod = (tx*dx + ty*dy)/(tlen*dlen);
                if ( dotprod < 0.0 ) {
                    tx = -tx;
                    ty = -ty;
                }
                // Normalize to be comparable to the KBSpline:
                tx = tx/tlen*dlen;
                ty = ty/tlen*dlen;
            }
            tangents.add( new MyPoint2D(tx,ty) );
            
        }

    }

}
