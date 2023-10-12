package jmorph.measurements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import paint.PaintingUtils;

/** An single coordinate point specifying the origin of a coordinate system.
 * @author Peter Lelievre
 */
public class OriginMeasurement extends LandmarkMeasurement {

    // ------------------- Constructor ------------------

    public OriginMeasurement() {
        super();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public OriginMeasurement deepCopy() {

        // Create the new object:
        OriginMeasurement out = new OriginMeasurement();

        // Perform default deep copy:
        super.deepCopyTo(out);

        // Return the object:
        return out;

    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "origin";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Select Calibration Origin";
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on the origin used for landmarks and outline points.";
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
     * An origin measurement can always be "calibrated" (the calibration does nothing)
     * so this method always returns true.
     * @param factor The calibration factor.
     * @return True if the measurement can be calibrated, false otherwise.
     */
    @Override
    protected boolean canBeCalibrated(double factor, AffineTransform trans) {
        return true; // An origin measurement can always be "calibrated" (the calibration does nothing).
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

        // Draw origin as individual unfilled circles:
        PaintingUtils.paintPoints(g2,trans,coordinates,2*getPointWidth(),false);

    }

}
