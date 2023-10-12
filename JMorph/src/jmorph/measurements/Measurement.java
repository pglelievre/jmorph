package jmorph.measurements;

import geometry.MyPoint2DVector;
import java.awt.geom.AffineTransform;
import paint.Paintable;

/** Interface for a morphometric measurement taken on a sample image.
 * If you want to define a new measurement to insert into JMorph then this is the interface to use.
 * You may want to extend the DefaultMeasurement class which implements some of the methods.
 * @author Peter Lelievre
 */
public interface Measurement extends Paintable {

    // ------------------- Properties ------------------

    public static final double NULL_CALIBRATION_DISTANCE = -1.0; /** A negative value used to mark a lack of calibration. */

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new Measurement object copied from this one.
     */
    public Measurement deepCopy();
    
    // ------------------- Clearing ------------------

    /** Clears a measurement (e.g. measurement coordinates, etc.)
     * to initialize it before measuring via user interaction. */
    public void clear();

    // -------------------- Getters -------------------

    /** Returns the name of the measurement.
     * The name is a user-defined quantity that is different from the type of measurement.
     * For example, the user may define two measurements named "length" and "width"
     * which are both of the "linear" measurement type.
     * @return The name of the measurement.
     */
    public String getName();

    /** Returns the measurement coordinates.
     * @return A vector of measurement coordinates.
     */
    public MyPoint2DVector getCoordinates();

    /** Returns the marked status of the measurement.
     * Measurements are marked or unmarked by the user depending on whether or not they want to perform them.
     * @return True if the measurement is marked (for some purpose), false otherwise.
     */
    public boolean getMarked();

    // -------------------- Setters -------------------

    /** Sets the name of the measurement.
     * The name is a user-defined quantity that is different from the type of measurement.
     * For example, the user may define two measurements named "length" and "width"
     * which are both of the "linear" measurement type.
     * @param n The name for the measurement.
     */
    public void setName(String n);

    /** Sets the measurement coordinates.
     * @param coords The vector of measurement coordinates.
     */
    public void setCoordinates(MyPoint2DVector coords);

    /** Sets the marked status of the measurement.
     * Measurements are marked or unmarked by the user depending on whether or not they want to perform them.
     * @param m The marked status for the measurement.
     */
    public void setMarked(Boolean m);

    // -------------------- Methods that define the number of coordinates associated with the measurement -------------------

    /** Returns the minimum number of coordinates required by the measurement.
     * @return The minimum number of coordinates required by the measurement.
     */
    public int minNumberOfCoordinates();

    /** Returns the maximum number of coordinates required by the measurement.
     * @return The maximum number of coordinates required by the measurement.
     */
    public int maxNumberOfCoordinates();

    // -------------------- Methods that check the coordinates for the measurement -------------------

    /** Checks that the supplied coordinates are appropriate for the measurement.
     * For example, if a measurement requires two points with different x coordinates
     * then you would use this method to check that there are only two points
     * and that the x coordinates of those points are different.
     * @param coords Vector of MyPoint2D coordinate points.
     * @return True if the coordinates are okay for the measurement.
     */
    public boolean checkCoordinates(MyPoint2DVector coords);

    /** Checks if the measurement has any coordinates assigned to it
     * (but not necessarily all required coordinates).
     * @return True if some coordinates have been assigned, false otherwise.
     */
    public boolean hasCoordinates();

    /** Checks if the measurement has been measured (the points have been specified) correctly.
     * For example, this may require checking that enough coordinates have been supplied
     * and that those supplied are appropriate.
     * @return True if measured correctly, false otherwise.
     */
    public boolean isMeasured();

    // -------------------- Methods that provide strings for displaying or exporting -------------------

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    public String typeString();

    /** Provides a string that holds the value of the calculated and calibrated measurement.
     * The intention is that this string should be used for display purposes.
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @param longDisplay Set to true to display more thorough information.
     * @return A string representation of the calculated and calibrated measurement.
     * If the measurement can't be calibrated using the provided factor and transform
     * then the result should be something like "sample not calibrated".
     * If the measurement has not been measured correctly then the result should be something like "not measured".
     */
    public String calculateStringForDisplay(double factor, AffineTransform trans, boolean longDisplay);

    /** Provides a string that holds the value of the calculated and calibrated measurement.
     * The intention is that this string should be used when writing to a CSV file
     * (some extra commas should be added where required but NOT at the start or end).
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @return A string representation of the calculated and calibrated measurement.
     * If the measurement can't be calibrated using the provided factor and transform
     * then the result should be something like "sample not calibrated".
     * If the measurement has not been measured correctly then the result should be something like "not measured".
     */
    public String calculateStringForExportCSV(double factor, AffineTransform trans);

    /** Provides a string with the measurement name and any additionally required characters.
     * The intention is that this string should be used when writing to a CSV file but commas should NOT be added.
     * @return A string containing the measurement name and any additionally required characters.
     */
    public String nameForExportCSV();

    // -------------------- Methods that define title and prompt for dialogs -------------------

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    public String instructionTitle();

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    public String instructionPrompt();

}
