package jmorph.measurements;

import geometry.MyPoint2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/** An linear length measurement specified between two points.
 * @author Peter Lelievre
 */
public class LengthMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public LengthMeasurement() {
        super();
        initialize();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public LengthMeasurement deepCopy() {

        // Create the new object:
        LengthMeasurement out = new LengthMeasurement();

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
        return 2;
    }

    /** Returns the maximum number of coordinates required by the measurement.
     * @return The maximum number of coordinates required by the measurement.
     */
    @Override
    public int maxNumberOfCoordinates() {
        return 2;
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "length";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Measure Length " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on two points to define the length.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.BLUE;
    }

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    @Override
    protected Color defaultSecondaryColour() {
        return Color.BLUE;
    }

    /** Checks if a measurement can be calibrated using the supplied calibration information.
     * @param factor The calibration factor.
     * @param trans
     * @return True if the measurement can be calibrated, false otherwise.
     */
    protected boolean canBeCalibrated(double factor, AffineTransform trans) {
        return ( factor!=NULL_CALIBRATION_DISTANCE ); // needs factor only
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

        // Calculate the calibrated length:
        double len = calibratedLength(factor);

        // Convert to a string:
        return Float.toString((float)len);

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

        // Calculate the calibrated length:
        double len = calibratedLength(factor);

        // Convert to a string:
        return Double.toString(len);

    }

    // -------------------- New Private/Protected Methods -------------------

    /** Provides the uncalibrated length.
     * @return The uncalibrated length.
     */
    protected double calculateLength() {
        // Calculate distance between two points:
        MyPoint2D p0 = coordinates.get(0);
        MyPoint2D p1 = coordinates.get(1);
        return MyPoint2D.distanceBetweenPoints(p0,p1);
    }

    /** Provides the calibrated length.
     * @param factor A calibration factor.
     * @return The calibrated length.
     */
    private double calibratedLength(double factor) {
        // Calculate the length:
        double len = calculateLength();
        // Length needs to be multipled by the factor:
        return factor*len;
    }
    
}
