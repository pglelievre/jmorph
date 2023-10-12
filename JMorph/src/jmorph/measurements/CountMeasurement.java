package jmorph.measurements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import paint.PaintingUtils;

/** The average separation between two or more connected points.
 * @author Peter Lelievre
 */
public class CountMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public CountMeasurement() {
        super();
        initialize();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public CountMeasurement deepCopy() {

        // Create the new object:
        CountMeasurement out = new CountMeasurement();

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
        return 0;
    }

    /** Returns the maximum number of coordinates required by the measurement.
     * @return The maximum number of coordinates required by the measurement.
     */
    @Override
    public int maxNumberOfCoordinates() {
        return 1024; // should be a huge number (more than anyone would ever want to use)
    }
    
    /** Checks if the measurement has coordinates assigned to it.
     * The check passes if the coordinates vector is not null, even it it has zero size.
     * @return True if coordinates are assigned, false otherwise.
     */
    @Override
    public boolean hasCoordinates() {
        return ( coordinates!=null );
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "count";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Count " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on the items to count and hit the SPACE BAR when finished.";
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
        
        // Return the string:
        return Integer.toString(coordinates.size());

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

        // Return the string:
        return Integer.toString(coordinates.size());
        
    }

    /** Paints the object.
     * Draws the coordinate points as individual filled circles using the primary colour and the point width.
     * @param g2 A Graphics2D object to paint with.
     * @param trans An AffineTransform to use while painting.
     * @param scal A scaling value involved in the transform.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     */
    @Override
    public void paint(Graphics2D g2, AffineTransform trans, double scal, boolean measuring){

        // Check the count is non-zero:
        if (coordinates.size()==0) { return; }
        
        // Set the colour and line style for the measurement:
        g2.setPaint(getPrimaryColour()); // colour

        // Draw measurement coordinates as individual filled circles:
        PaintingUtils.paintPoints(g2,trans,coordinates,getPointWidth(),true);

    }

}
