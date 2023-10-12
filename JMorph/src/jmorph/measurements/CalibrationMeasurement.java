package jmorph.measurements;

import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/** A calibration length measurement specified by two points.
 * The two points can't be the same point.
 * @author Peter Lelievre
 */
public class CalibrationMeasurement extends LengthMeasurement {

    // ------------------- Constructor ------------------

    public CalibrationMeasurement() {
        super();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public CalibrationMeasurement deepCopy() {

        // Create the new object:
        CalibrationMeasurement out = new CalibrationMeasurement();

        // Perform default deep copy:
        super.deepCopyTo(out);

        // Return the object:
        return out;

    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Checks that the supplied coordinates are appropriate for the measurement.
     * For a Calibration measurement, the two points can't be the same point.
     * @param coords Vector of MyPoint2D coordinate points.
     * @return True if the coordinates are okay for the measurement.
     */
    @Override
    public boolean checkCoordinates(MyPoint2DVector coords) {

        // Check the number of coordinates:
        if ( coords.size()<minNumberOfCoordinates() || coords.size()>maxNumberOfCoordinates() ) { return false; }
        
        // Points must not be identical:
        MyPoint2D p0 = coords.get(0);
        MyPoint2D p1 = coords.get(1);
        return ( p0.getX() != p1.getX() || p0.getY() != p1.getY() );

    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "calibration";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Calibrate Current Sample";
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on two points to define the calibration length.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.CYAN;
    }

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    @Override
    protected Color defaultSecondaryColour() {
        return Color.CYAN;
    }

    /** Checks if a measurement can be calibrated using the supplied calibration information.
     * A calibration measurement can always be "calibrated" (the calibration does nothing).
     * @param factor The calibration factor.
     * @param trans
     * @return True if the measurement can be calibrated, false otherwise.
     */
    @Override
    protected boolean canBeCalibrated(double factor, AffineTransform trans) {
        return true; // A calibration measurement can always be "calibrated" (the calibration does nothing).
    }

    // -------------------- New Public Methods -------------------

    /** Provides the measured length.
     * @return The measured length.
     */
    public double length() {
        return calculateLength(); // the uncalibrated length of the super-class
    }

}
