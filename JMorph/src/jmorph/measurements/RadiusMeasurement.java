package jmorph.measurements;

import geometry.Circle;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import paint.PaintingUtils;

/** The radius of a circle defined by three points.
 * The three points must not be co-linear.
 * @author Peter Lelievre
 */
public class RadiusMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public RadiusMeasurement() {
        super();
        initialize();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public RadiusMeasurement deepCopy() {

        // Create the new object:
        RadiusMeasurement out = new RadiusMeasurement();

        // Perform default deep copy:
        super.deepCopyTo(out);

        // Return the object:
        return out;

    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Checks that the supplied coordinates are appropriate for the measurement.
     * @param coords Vector of MyPoint2D coordinate points.
     * @return True if the coordinates are okay for the measurement.
     */
    @Override
    public boolean checkCoordinates(MyPoint2DVector coords) {

        // Check the number of coordinates:
        if ( coords.size()<minNumberOfCoordinates() || coords.size()>maxNumberOfCoordinates() ) { return false; }
        
        // The points must not be collinear:
        MyPoint2D p0 = coords.get(0);
        MyPoint2D p1 = coords.get(1);
        MyPoint2D p2 = coords.get(2);
        Circle circ = new Circle(p0,p1,p2);
        MyPoint2D p = circ.getCentre();
        return ( p != null );

    }

    /** Returns the minimum number of coordinates required by the measurement.
     * @return The minimum number of coordinates required by the measurement.
     */
    @Override
    public int minNumberOfCoordinates() {
        return 3;
    }

    /** Returns the maximum number of coordinates required by the measurement.
     * @return The maximum number of coordinates required by the measurement.
     */
    @Override
    public int maxNumberOfCoordinates() {
        return 3;
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "radius";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Measure Radius " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on three non-colinear points to define the circle.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.YELLOW;
    }

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    @Override
    protected Color defaultSecondaryColour() {
        return Color.YELLOW;
    }

    /** Checks if a measurement can be calibrated using the supplied calibration information.
     * @param factor The calibration factor.
     * @param trans
     * @return True if the measurement can be calibrated, false otherwise.
     */
    protected boolean canBeCalibrated(double factor, AffineTransform trans) {
        return ( factor!=NULL_CALIBRATION_DISTANCE && trans!=null ); // needs both factor and origin
    }

    /** Provides a string that holds the value of the calculated and calibrated measurement.
     * The intention is that this string should be used for display purposes.
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @param longDisplay Set to true to display more thorough information.
     * @return A string representation of the calculated and calibrated measurement.
     */
    @Override
    public String calculateStringForDisplay(double factor, AffineTransform trans, boolean longDisplay) {

        // Check for unmeasured measurement:
        if (!isMeasured()) {
            return "not measured";
        }

        // Check for uncalibrated measurement:
        if (!canBeCalibrated(factor,trans)) {
            return "sample not calibrated";
        }

        // Calculate the calibrated radius:
        double r = calibratedRadius(factor);
        
        // Calculate the calibrated coordinate location:
        MyPoint2D p = calibratedCentre(trans);

        // Convert the calculated and calibrated measurement to a string:
        //return Float.toString((float)r);
        return r + " " + p.toStringParentheses();

    }

    /** Provides a string that holds the value of the calculated and calibrated measurement.
     * The intention is that this string should be used when writing to a CSV file
     * (some extra commas should be added where required but NOT at the start or end).
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @return A string representation of the calculated and calibrated measurement.
     */
    @Override
    public String calculateStringForExportCSV(double factor, AffineTransform trans) {

        // Check for unmeasured measurement:
        if (!isMeasured()) {
            return "not measured";
        }

        // Check for uncalibrated measurement:
        if (!canBeCalibrated(factor,trans)) {
            return "sample not calibrated";
        }

        // Calculate the calibrated radius:
        double r = calibratedRadius(factor);

        // Convert to a string:
        return Double.toString(r);

    }

    /** Paints the object.
     * @param g2 A Graphics2D object to paint with.
     * @param trans An AffineTransform to use while painting.
     * @param scal A scaling value involved in the transform.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     */
    @Override
    public void paint(Graphics2D g2, AffineTransform trans, double scal, boolean measuring){

        // Set the colour and line style for the measurement:
        g2.setPaint(getPrimaryColour()); // colour

        // Only paint some items if fully measured:
        if (isMeasured()) {

            // Calculate the circle radius and position:
            double radius = calculateRadius();
            MyPoint2D p = calculateCentre();

            // Draw a circle with that radius and centre position:
            PaintingUtils.paintPoint(g2,trans,p, (int)(2.0*scal*radius) ,false);

            // Draw a point at the circle centre:
            PaintingUtils.paintPoint(g2,trans,p,getPointWidth(),true);

        }

        // Draw measurement coordinates as individual filled circles:
        PaintingUtils.paintPoints(g2,trans,coordinates,getPointWidth(),true);

    }

    // -------------------- New Private/Protected Methods -------------------

    /** Provides the uncalibrated radius.
     * @return The uncalibrated radius.
     */
    private double calculateRadius() {

        // Calculate the radius defined by the coordinates:
        MyPoint2D p0 = coordinates.get(0);
        MyPoint2D p1 = coordinates.get(1);
        MyPoint2D p2 = coordinates.get(2);
        Circle circ = new Circle(p0,p1,p2);
        return circ.getRadius();

    }

    /** Provides the uncalibrated circumcentre.
     * @return The uncalibrated circumcentre.
     */
    private MyPoint2D calculateCentre() {

        // Calculate the centre of the circle defined by the coordinates:
        // http://en.wikipedia.org/wiki/Circumcircle#Cartesian_coordinates
        MyPoint2D p0 = coordinates.get(0);
        MyPoint2D p1 = coordinates.get(1);
        MyPoint2D p2 = coordinates.get(2);
        Circle circ = new Circle(p0,p1,p2);
        return circ.getCentre();

    }

    /** Provides the calibrated radius.
     * @return The calibrated radius.
     */
    private double calibratedRadius(double factor) {

        // Calculate the radius:
        double r = calculateRadius();

        // Lengths need to be multipled by the factor:
        return factor*r;

    }

    /** Provides the calibrated centroid location.
     * @return The calibrated radius.
     */
    private MyPoint2D calibratedCentre(AffineTransform trans) {

        // Calculate the centre:
        MyPoint2D p = calculateCentre();

        // Transform the centre point:
        p.transform(trans);
        return p;

    }

}
