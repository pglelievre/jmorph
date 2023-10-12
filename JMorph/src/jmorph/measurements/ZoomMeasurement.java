package jmorph.measurements;

import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import paint.PaintingUtils;

/** A zoom box defined by two points at opposite corners of the box.
 * The two points can't have the same x or y values.
 * @author Peter Lelievre
 */
public class ZoomMeasurement extends DefaultMeasurement {

    // ------------------- Constructor ------------------

    public ZoomMeasurement() {
        super();
        initialize();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public ZoomMeasurement deepCopy() {

        // Create the new object:
        ZoomMeasurement out = new ZoomMeasurement();

        // Perform default deep copy:
        super.deepCopyTo(out);

        // Return the object:
        return out;

    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Checks that the supplied coordinates are appropriate for the measurement.
     * For a Zoom measurement, the two points can't have the same x or y values.
     * @param coords Vector of MyPoint2D coordinate points.
     * @return True if the coordinates are okay for the measurement.
     */
    @Override
    public boolean checkCoordinates(MyPoint2DVector coords) {

        // Check the number of coordinates:
        if ( coords.size()<minNumberOfCoordinates() || coords.size()>maxNumberOfCoordinates() ) { return false; }

        // Points must not have same x or same y:
        MyPoint2D p0 = coords.get(0);
        MyPoint2D p1 = coords.get(1);
        return ( p0.getX() != p1.getX() && p0.getY() != p1.getY() );

    }

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
        return "zoom";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Zoom Current Sample";
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on two points to define opposite corners of the zoom box.";
    }

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    @Override
    protected Color defaultPrimaryColour() {
        return Color.BLACK;
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
     * This method should not be used (there are no calculated measurements for a zoom measurement).
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @param longDisplay Set to true to display more thorough information.
     * @return A string representation of the calculated and calibrated measurement.
     */
    @Override
    public String calculateStringForDisplay(double factor, AffineTransform trans, boolean longDisplay) {
        return null;
    }

    /** Provides a string that holds the value of the calculated and calibrated measurement.
     * The intention is that this string should be used when writing to a CSV file
     * (some extra commas should be added where required but NOT at the start or end).
     * This method should not be used (there are no calculated measurements for a zoom measurement).
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @return A string representation of the calculated and calibrated measurement.
     */
    @Override
    public String calculateStringForExportCSV(double factor, AffineTransform trans) {
        return null;
    }

    /** Paints the object.
     * @param g2 A Graphics2D object to paint with.
     * @param trans An AffineTransform to use while painting.
     * @param scal A scaling value involved in the transform.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     */
    @Override
    public void paint(Graphics2D g2, AffineTransform trans, double scal, boolean measuring){

        MyPoint2D p;

        // Set the colour and line style for the measurement:
        g2.setPaint(getPrimaryColour()); // colour
        
        // Only paint some items if fully measured:
        if (isMeasured()) {

            // Calculate the minimum (x,y) coordinate:
            p = new MyPoint2D(coordinates.minX(),coordinates.minY());

            // Transform coordinates:
            p.transform(trans);

            // Calculate the size of the zoom box:
            double zoomWidth  = scal*coordinates.rangeX();
            double zoomHeight = scal*coordinates.rangeY();

            // Draw a rectangle representing the zoom box:
            g2.drawRect(
                    (int)p.getX(),
                    (int)p.getY(),
                    (int)zoomWidth,
                    (int)zoomHeight);

            // Draw again (white on black):
            g2.setPaint(getSecondaryColour()); // colour
            Stroke stroke = g2.getStroke();
            BasicStroke basic = (BasicStroke)stroke;
            float width = basic.getLineWidth();
            Stroke dashed = new BasicStroke( width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2.setStroke(dashed);
            g2.drawRect(
                    (int)p.getX(),
                    (int)p.getY(),
                    (int)zoomWidth,
                    (int)zoomHeight);
            g2.setStroke(stroke);
            
        }

        // Draw measurement coordinates as individual filled circles:
        PaintingUtils.paintPoints(g2,trans,coordinates,getPointWidth(),true);

    }
    
}