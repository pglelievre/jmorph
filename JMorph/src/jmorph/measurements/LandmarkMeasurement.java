package jmorph.measurements;

import geometry.MyPoint2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import paint.PaintingUtils;

/** A single coordinate point landmark measurement.
 * @author Peter Lelievre
 */
public class LandmarkMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public LandmarkMeasurement() {
        super();
        initialize();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public LandmarkMeasurement deepCopy() {

        // Create the new object:
        LandmarkMeasurement out = new LandmarkMeasurement();

        // Perform default deep copy:
        super.deepCopyTo(out);

        // Return the object:
        return out;

    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Returns the minimum number of coordinates required by the measurement.
     * @return The minimum number of coordinates required by the measurement.
     */
    @Override
    public int minNumberOfCoordinates() {
        return 1;
    }

    /** Returns the maximum number of coordinates required by the measurement.
     * @return The maximum number of coordinates required by the measurement.
     */
    @Override
    public int maxNumberOfCoordinates() {
        return 1;
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "landmark";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Mark Landmark " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on the landmark.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.MAGENTA;
    }

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    @Override
    protected Color defaultSecondaryColour() {
        return Color.MAGENTA;
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

        // Check for uncalibrated measurement:\
        if (!canBeCalibrated(factor,trans)) {
            return "sample not calibrated";
        }

        // Deep copy the landmark point:
        MyPoint2D p = coordinates.get(0).deepCopy();

        // Apply the calibration transform to the new point object:
        p.transform(trans);

        // The landmark point should be written "x,y":
         return "(" + Float.toString((float)p.getX()) + "," + Float.toString((float)p.getY()) + ")"; // (x,y)

    }

    /** Provides a string with the measurement name and any additionally required characters.
     * The intention is that this string should be used when writing to a CSV file but commas should NOT be added.
     * For a DefaultMeasurement object, the string returned contains only the measurement name.
     * @return A string containing the measurement name and any additionally required characters.
     */
    @Override
    public String nameForExportCSV() {
        return getName() + " (x)," + getName() + " (y)";
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
            return "not measured,"; // the extra comma here is important because there are two values
        }

        // Check for uncalibrated measurement:
        if (!canBeCalibrated(factor,trans)) {
            return "sample not calibrated,"; // the extra comma here is important because there are two values
        }

        // Deep copy the landmark point:
        MyPoint2D p = coordinates.get(0).deepCopy();

        // Apply the calibration transform to the new point object:
        p.transform(trans);

        // Convert to a string:
        return p.getX() + "," + p.getY();

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

        // Draw measurement coordinates as individual filled circles:
        PaintingUtils.paintPoints(g2,trans,coordinates,getPointWidth(),true);

    }
    
}
