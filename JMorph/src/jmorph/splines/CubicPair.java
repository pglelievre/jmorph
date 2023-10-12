package jmorph.splines;

/** A single section of a cubic spline.
 * @author Peter Lelievre
 */
public class CubicPair {

    // ------------------ Properties -------------------

    private double y1,y2,d1,d2; // function values and spline derivatives

    // ------------------ Constructor -------------------

    /**
     * @param y1 Function value at the first point in the cubic spline section.
     * @param y2 Function value at the second point in the cubic spline section.
     * @param d1 Derivative value at the first point in the cubic spline section.
     * @param d2 Derivative value at the second point in the cubic spline section.
     */
    public CubicPair(double y1, double y2, double d1, double d2){
        this.y1 = y1;
        this.y2 = y2;
        this.d1 = d1;
        this.d2 = d2;
    }

    // -------------------- Public Methods -------------------

    /** Interpolates the cubic spline at some normalized location (arc length) along the segment.
     * @param t The normalized location (arc length) along the segment (should be on [0,1]).
     * @return 
     */
    public double interpolate(double t){

        // Interpolate the value:
        double dy = y2 - y1;
        double a = y1;
        double b = d1;
        double c =  3.0*dy - 2.0*d1 - d2;
        double d = -2.0*dy +     d1 + d2;
        //double f = a + b*t + c*Math.pow(t,2.0) + d*Math.pow(t,3.0);
        double f = a + t*(b + t*(c + t*d));
        return f;

    }

}
