package jmorph.measurements;

import geometry.MyPoint2D;
import geometry.MyPolygon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import jmorph.splines.CatmullRomSpline;
import jmorph.splines.CirclePreservingSpline;
import jmorph.splines.OutlineSpline;
import paint.PaintingUtils;

/** The area and centroid of a spline curve passing through several coordinate points.
 * @author Peter Lelievre
 */
public class SplineMeasurement extends AreaMeasurement {

    // ------------------ Properties -------------------

    public static final int INTERP_POWER = 8; /** The number of interpolated outline points is 2^INTERP_POWER */

    public static final boolean USE_CIRCLE_SPLINE_DEFAULT = true; /** Whether or not to use a circle-preserving spline by default. */

    @SuppressWarnings("ProtectedField")
    protected OutlineSpline outlineSpline = null; /** The outline spline through the coordinate points. */
    @SuppressWarnings("ProtectedField")
    protected boolean useCircleSpline = USE_CIRCLE_SPLINE_DEFAULT; /** Use circle-preserving spline (true) or KB spline (false)? */
    @SuppressWarnings("ProtectedField")
    protected MyPolygon coordsInterp = null; /** The spline interpolated coordinate points. */
    @SuppressWarnings("ProtectedField")
    protected boolean isClosed = true; /** Set to false to alter the behavior of the spline (should only be false when being measured). */

    // ------------------- Constructor ------------------

    public SplineMeasurement() {
        super();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public SplineMeasurement deepCopy() {

        // Create the new object:
        SplineMeasurement out = new SplineMeasurement();

        // Perform default deep copy:
        super.deepCopyTo(out);

        // Clear all the outline analysis properties:
        clearOutlineAnalysis();

        // Return the object:
        return out;

    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Clears all the outline analysis properties. */
    @Override
    public void clearOutlineAnalysis(){
        coordsOrig = null;
        clearSpline();
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "spline";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Measure Spline-Interpolated Area " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on many points around the area to measure and then hit the SPACE BAR.";
    }

    /** This method should perform any calculations that are required prior to painting.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     * @return Always returns true.
     */
    @Override
    public boolean runBeforePainting(boolean measuring) {
        // Make sure all required outline analysis steps have been performed:
        isClosed = !measuring;
        if (coordsInterp==null) { fillInterp(); }
        return true;
    }

    /** Paints the object.
     * @param g2 A Graphics2D object to paint with.
     * @param trans An AffineTransform to use while painting.
     * @param scal A scaling value involved in the transform.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     */
    @Override
    public void paint(Graphics2D g2, AffineTransform trans, double scal, boolean measuring){

        // Only paint some items if fully measured:
        if (isMeasured()) {

            // Calculate the centroid position:
            MyPoint2D p = calculateCentroid();

            // Draw a point at the polygon centroid
            g2.setPaint(getPrimaryColour());
            PaintingUtils.paintPoint(g2,trans,p,getPointWidth(),true);

        }

        // Always draw interpolated outline if it exists, whether measuring or not:
        GeneralPath path = new GeneralPath();
        if (coordsInterp==null) {
            // Draw an un-closed path around the measurement coordinates:
            coordinates.addToPath(path);
        } else {
            // Draw a closed path around the interpolated points:
            addInterpToPath(path);
        }

        // Transform the path from sample to panel coordinates:
        path.transform(trans);

        // Draw the path:
        g2.setPaint(getPrimaryColour());
        g2.draw(path);

        // Draw measurement coordinates as individual filled circles:
        if (coordinates!=null) {
            g2.setPaint(Color.BLACK);
            PaintingUtils.paintPoints(g2,trans,coordinates,getPointWidth(),true);
            if (!measuring) {
                int n = coordinates.size()-1;
                PaintingUtils.paintPoint(g2,trans,coordinates.get(0),2*getPointWidth(),true); // first point
                PaintingUtils.paintPoint(g2,trans,coordinates.get(n),2*getPointWidth(),false); // last point
            }
        }

    }

    // -------------------- New Pubic Methods -------------------

    /** Sets the useCircleSpline property.
     * @param use The value to use for the useCircleSpline property.
     */
    public void setUseCircleSpline(boolean use){

        // Set the choice:
        useCircleSpline = use;

        // Clear the spline information and anything that relies on it:
        clearSpline();

    }
    
    // -------------------- New Private/Protected Methods -------------------

    /** Clears all the outline spline properties and anything that depends on them. */
    protected void clearSpline(){
        outlineSpline = null;
        clearInterp();
    }

    /** Clears all the outline interpolation properties and anything that depends on them. */
    protected void clearInterp(){
        coordsInterp = null;
    }

    /** Calculates the outline spline. */
    protected void fillSpline() {

         // Make sure required information exists:
        if (coordsOrig==null) { fillCoordsOrig(); }
        if (coordsOrig==null) { return; }

        // Clear the information that depends on this information:
        clearSpline();

        // Calculate the spline:
        if (useCircleSpline) {
            outlineSpline = new CirclePreservingSpline(coordsOrig,isClosed);
            // Don't need to deep copy here because the outline spline is recalculated
            // each time the coordsOrig object changes (in the setCoordinates method
            // of the Area super class).
        } else {
            outlineSpline = new CatmullRomSpline(coordsOrig,isClosed);
        }

    }

    /** Interpolates the outline at a number of equally spaced points. */
    protected void fillInterp(){

        // Make sure required information exists:
        if (outlineSpline==null) { fillSpline(); }
        if (outlineSpline==null) { return; }

        // Clear the information that depends on this information:
        clearInterp();

        // Fill the interpolation arrays:
        int n = (int)Math.pow(2,INTERP_POWER);
        double outlineStep = 1.0 / n;
        coordsInterp = new MyPolygon();
        double t;
        MyPoint2D p;
        for ( int j=0 ; j<n ; j++ ) {
            // Interpolate at the next point along the outline curve:
            t = j * outlineStep;
            p = outlineSpline.interpolatePoint(t);
            if (p==null) { break; } // will occur if the outline is not closed
            coordsInterp.add(p);
        }

    }

    /** Adds the interpolated coordinates in the vector to a general path object for plotting.
     * The first point is added using path.moveTo() and the subsequent points are added using path.lineTo().
     * The path is closed if the Spline object is closed (determined using the isClosed method).
     * @param path The path to add the interpolated points to.
     */
    protected void addInterpToPath(GeneralPath path) {

        if (coordsInterp==null) { return; } // nothing to add

        // Add the first point:
        MyPoint2D p = coordsInterp.get(0);
        path.moveTo((float)p.getX(),(float)p.getY());

        // Loop over the other points:
        for ( int j=1 ; j<coordsInterp.size() ; j++ ) {
            p = coordsInterp.get(j);
            path.lineTo((float)p.getX(),(float)p.getY());
        }

        // Close the path:
        if (outlineSpline.isClosed()) {
            path.closePath();
        }

    }

    /** Provides the uncalibrated area of the outline.
     * If the spline interpolation has not been calculated then the area returned
     * is that of the polygon defined by the original coordinates.
     * @return The uncalibrated area.
     */
    private double calculateArea() {
        // Calculate area of the polygon defined by the coordinates:
        if (coordsInterp==null) { fillInterp(); }
        if (coordsInterp==null) {
            if (coordsOrig==null) { fillCoordsOrig(); }
            // Here we are using the original coordinates:
            return coordsOrig.area();
        } else {
            // Here we are using the spline interpolated coordinates:
            return coordsInterp.area();
        }
    }

    /** Provides the uncalibrated coordinates of the outline centroid.
     * If the spline interpolation has not been calculated then the centroid returned
     * is that for the polygon defined by the original coordinates.
     * @return The uncalibrated coordinates of the outline centroid.
     */
    protected MyPoint2D calculateCentroid() {
        // Calculate area and centroid of the polygon defined by the coordinates:
        if (coordsInterp==null) { fillInterp(); }
        if (coordsInterp==null) {
            if (coordsOrig==null) { fillCoordsOrig(); }
            // Here we are using the original coordinates:
            return coordsOrig.com();
        } else {
            // Here we are using the spline interpolated coordinates:
            return coordsInterp.com();
        }
    }

}
