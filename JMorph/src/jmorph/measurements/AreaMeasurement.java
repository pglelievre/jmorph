package jmorph.measurements;

import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import geometry.MyPolygon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import paint.PaintingUtils;

/** The area and centroid of a polygon defined by several points.
 * @author Peter Lelievre
 */
public class AreaMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public AreaMeasurement() {
        super();
        initialize();
    }

    // ------------------ Properties -------------------

    @SuppressWarnings("ProtectedField")
    protected MyPolygon coordsOrig = null; /** The original user-selected coordinate points before processing. */

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public AreaMeasurement deepCopy() {

        // Create the new object:
        AreaMeasurement out = new AreaMeasurement();

        // Perform default deep copy:
        super.deepCopyTo(out);

        // Clear all the outline analysis properties:
        clearOutlineAnalysis();

        // Return the object:
        return out;

    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Sets the measurement coordinates.
     * @param coords The vector of measurement coordinates.
     */
    @Override
    public void setCoordinates(MyPoint2DVector coords) {
        // Clear all the outline analysis properties before setting the coordinates:
        clearOutlineAnalysis();
        coordinates = coords;
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
        return 1024; // should be a huge number (more than anyone would ever want to use)
    }

    /** Tells you if the object uses the secondary colour when painting itself.
     * This will be used to determine whether or not to ask the user for the
     * secondary painting colour in some GUI's.
     * @return This method always returns true for an Area object.
     */
    @Override
    public boolean usesSecondaryColour() {
        return true;
    }

    /** Clears a measurement (clears the coordinate list, etc.). */
    @Override
    public void clear() {
        coordinates.clear();
        clearOutlineAnalysis();
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "area";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Measure Area " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on many points around the area to measure and then hit the SPACE BAR.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.RED;
    }

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    @Override
    protected Color defaultSecondaryColour() {
        return Color.ORANGE;
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

        // Make sure all calculations are preformed as required before exporting:
        runBeforePainting(false);

        // Calculate the calibrated area and centroid:
        double a = calibratedArea(factor);
        MyPoint2D p = calculateCentroid();
        p.transform(trans);

        // Write as "area (x,y)":
        return Float.toString((float)a) + " (" + Float.toString((float)p.getX()) + "," + Float.toString((float)p.getY()) + ")";
        
    }

    /** Provides a string with the measurement name and any additionally required characters.
     * The intention is that this string should be used when writing to a CSV file but commas should NOT be added.
     * @return A string containing the measurement name and any additionally required characters.
     */
    @Override
    public String nameForExportCSV() {

        // Add in the area and centroid coordinates to the output string:
        return getName() + " (area),(xc),(yc),(clockwise)";

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
            return "not measured,,,";
        }

        // Check for uncalibrated measurement:
        if (!canBeCalibrated(factor,trans)) {
            return "sample not calibrated,,,";
        }

        // Make sure all calculations are preformed as required before exporting:
        runBeforePainting(false);

        // Calculate the calibrated area and centroid:
        double a = calibratedArea(factor);
        MyPoint2D p = calculateCentroid();
        p.transform(trans);

        // Add the calculated and calibrated measurement values (area and centroid coordinates) to a string:
        return a + "," + p.getX() + "," + p.getY() + "," + !coordsOrig.isClockwise();

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

        // Create a path around the measurement coordinates:
        GeneralPath path;
        path = new GeneralPath();
        coordinates.addToPath(path);

        // Close the path if not measuring:
        if (!measuring) {
            path.closePath();
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
                // First point is drawn larger:
                PaintingUtils.paintPoint(g2,trans,coordinates.get(0),getPointWidth()+4,true);
            }
        }

    }

    // -------------------- New Private/Protected Methods -------------------

    /** Clears all the outline analysis properties. */
    protected void clearOutlineAnalysis(){
        coordsOrig = null;
    }

    /** Provides the uncalibrated area of the polygon.
     * @return The uncalibrated area of the polygon.
     */
    private double calculateArea() {
        // Calculate area of the polygon defined by the coordinates:
        if (coordsOrig==null) { fillCoordsOrig(); }
        return coordsOrig.area();
    }

    /** Provides the uncalibrated coordinates of the polygon centroid.
     * @return The uncalibrated coordinates of the polygon centroid.
     */
    private MyPoint2D calculateCentroid() {
        // Calculate area and centroid of the polygon defined by the coordinates:
        if (coordsOrig==null) { fillCoordsOrig(); }
        return coordsOrig.com();
    }

    /** Provides the calibrated area of the polygon.
     * @param factor
     * @return The calibrated area of the polygon.
     */
    protected double calibratedArea(double factor) {
        // Calculate area of the polygon defined by the coordinates:
        double a = calculateArea();
        // Areas need to be multipled by the square of the factor:
        return factor*factor*a;
    }

    /** Copies the coordinates into the coordsOrig MyPolygon object.
     * Clears and recalculates any outline analysis that has been performed.
     */
    protected void fillCoordsOrig() {

        // Make sure required information exists:
        if (!isMeasured()) { return; }

        // Clear the information that depends on this information:
        clearOutlineAnalysis();

        // Fill the coordsOrig information:
        coordsOrig = new MyPolygon();
        for ( int j=0 ; j<coordinates.size() ; j++ ) {
            // Deep copy over the points.
            MyPoint2D p = coordinates.get(j).deepCopy();
            coordsOrig.add(p);
        }

    }

}
