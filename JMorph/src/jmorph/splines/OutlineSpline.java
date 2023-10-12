package jmorph.splines;

import geometry.MyPoint2D;

/** A spline around an outline.
 * @author Peter Lelievre
 */
public interface OutlineSpline {

    /** Returns true if the outline is closed.
     * It may not be closed yet if it is still being defined through some user mouse-click process.
     * @return True if the outline is closed, false otherwise.
     */
    public boolean isClosed();

    /** Returns the total length of the spline curve.
     * @return The total length of the spline curve or a non-positive value if the required information does not exist.
     */
    public double splineLength();
    
    /** Interpolates the spline at some normalized arc length location along the spline.
     * @param t Normalized arc length at which to interpolate (should be on [0,1]).
     * @return The interpolated point.
     */
    public MyPoint2D interpolatePoint(double t);

    /** Interpolates the tangent at some normalized arc length location along the spline.
     * @param t Normalized arc length at which to interpolate (should be on [0,1]).
     * @return 
     */
    public MyPoint2D interpolateTangent(double t);

}
