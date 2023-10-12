package jmorph.measurements;

import geometry.MyPoint2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/** An angular measurement specified by three points.
 * The three points are connected 1-2-3 and the angle is defined at 2.
 * @author Peter Lelievre
 */
public class AngleMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public AngleMeasurement() {
        super();
        initialize();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public AngleMeasurement deepCopy() {

        // Create the new object:
        AngleMeasurement out = new AngleMeasurement();

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
        return "angle";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Measure Angle " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on three points to define the angle.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.GREEN;
    }

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    @Override
    protected Color defaultSecondaryColour() {
        return Color.GREEN;
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

        // Calculate the measurement:
        double ang = calculateAngle();

        // The angle should be supplied in degrees:
        return Float.toString((float)Math.toDegrees(ang)) + " (degrees)"; // degrees

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

        // Calculate the measurement:
        double ang = calculateAngle();

        // Convert the calculated and calibrated measurement to a string:
        return Double.toString(ang);

    }

    /** Provides a string with the measurement name and any additionally required characters.
     * The intention is that this string should be used when writing to a CSV file but commas should NOT be added.
     * @return A string containing the measurement name and any additionally required characters.
     */
    @Override
    public String nameForExportCSV() {
        return getName() + " (radians)";
    }

    // -------------------- New Private/Protected Methods -------------------

    /** Provides the uncalibrated angle in radians.
     * @return The uncalibrated angle in radians.
     */
    private double calculateAngle() {

        // Calculate angle between three points:
        MyPoint2D p0 = coordinates.get(0);
        MyPoint2D p1 = coordinates.get(1);
        MyPoint2D p2 = coordinates.get(2);
        return MyPoint2D.angleBetweenThreePoints(p0,p1,p2); // kept in radians, so have to convert later

    }
    
}
