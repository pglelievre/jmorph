package jmorph.measurements;

import java.awt.Color;
import java.awt.geom.AffineTransform;

/** The average separation between two or more connected points.
 * @author Peter Lelievre
 */
public class SeparationMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public SeparationMeasurement() {
        super();
        initialize();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public SeparationMeasurement deepCopy() {

        // Create the new object:
        SeparationMeasurement out = new SeparationMeasurement();

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
        return 1024; // should be a huge number (more than anyone would ever want to use)
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "separation";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Measure Separation " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click along the separated items and then hit the SPACE BAR.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.WHITE;
    }

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    @Override
    protected Color defaultSecondaryColour() {
        return Color.WHITE;
    }

    /** Checks if a measurement can be calibrated using the supplied calibration information.
     * @param factor The calibration factor.
     * @param trans
     * @return True if the measurement can be calibrated, false otherwise.
     */
    protected boolean canBeCalibrated(double factor, AffineTransform trans) {
        return ( factor!=NULL_CALIBRATION_DISTANCE );
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

        // Calculate the calibrated separation, total length and number of points:
        double sep = calibratedSeparation(factor);
        int n = coordinates.size();
        double len = sep * n;
        
        // Return the string:
        String s = System.lineSeparator() + "   " + Float.toString((float)sep) + " (separation)";
        s = s + System.lineSeparator() + "   " + Float.toString((float)len) + " (total length)";
        s = s + System.lineSeparator() + "   " + Integer.toString(n) + " (number of points)";
        return s;

    }

    /** Provides a string with the measurement name and any additionally required characters.
     * The intention is that this string should be used when writing to a CSV file but commas should NOT be added.
     * @return A string containing the measurement name and any additionally required characters.
     */
    @Override
    public String nameForExportCSV() {
        return getName() + " (separation),(total length),(number of points)";
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
            return "not measured,,";
        }

        // Check for uncalibrated measurement:
        if (!canBeCalibrated(factor,trans)) {
            return "sample not calibrated,,";
        }

        // Calculate the calibrated separation, total length and number of points:
        double sep = calibratedSeparation(factor);
        int n = coordinates.size();
        double len = sep * n;
        
        // Return the string:
        return Double.toString(sep) + "," + Double.toString(len) + "," + Integer.toString(n);
        
    }

    // -------------------- New Private/Protected Methods -------------------

//    /** Provides the uncalibrated average separation.
//     * @return The uncalibrated average separation.
//     */
//    private double calculateSeparation() {
//        // Calculate the average separation of the coordinates:
//        return coordinates.averageSeparation();
//    }

    /** Provides the calibrated average separation.
     * @return The calibrated average separation.
     */
    private double calibratedSeparation(double factor) {
        // Calculate the average separation of the coordinates:
        double sep = coordinates.averageSeparation();
        // Lengths need to be multipled by the factor:
        return factor*sep;

    }

}
