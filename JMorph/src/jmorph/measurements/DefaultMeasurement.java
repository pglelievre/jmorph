package jmorph.measurements;

import geometry.MyPoint2DVector;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import paint.Paintable;
import paint.PaintingUtils;

/** A morphometric measurement taken on a sample image.
 * This is a default measurement that implements many of the methods in the Measurement interface
 * and which can be extended for other measurement classes.
 * After construction you should call the setName method to set the name.
 * This class does not implement the following methods in the Measurement interface class:
 * - minNumberOfCoordinates
 * - maxNumberOfCoordinates
 * - typeString
 * - defaultPrimaryColour
 * - defaultSecondaryColour
 * @author Peter Lelievre
 */
public abstract class DefaultMeasurement implements Measurement {

    // ------------------ Properties -------------------

    // Object properties:
    @SuppressWarnings("ProtectedField")
    protected String name = "UnNamed"; /** The name of the measurement. */
    @SuppressWarnings("ProtectedField")
    protected MyPoint2DVector coordinates = null; /** The coordinate points used for the measurement. */
    @SuppressWarnings("ProtectedField")
    protected boolean marked = true; /** Allows other classes to determine how to deal with this measurement. */
    private Color colour1 = Color.BLACK; /** The primary painting colour. */
    private Color colour2 = Color.BLACK; /** The secondary painting colour. */
    private int lineWidth = Paintable.DEFAULT_LINE_WIDTH; /** The line width for painting. */
    private int pointWidth = Paintable.DEFAULT_POINT_WIDTH; /** The point width for painting. */
    
    // ------------------ Constructor -------------------

    /** Constructor.
     * For a DefaultMeasurement object, the coordinates vector is initialized to an empty vector
     * and the primary and secondary colours are set to the defaults specified by the methods
     * defaultPrimaryColour and defaultSecondaryColour.
     */
    public DefaultMeasurement() {
        //MyPoint2DVector test = new MyPoint2DVector();
        coordinates = new MyPoint2DVector();
        initialize();
    }
    
    /** This method MUST be called by any subclass constructors, after the call to super()! */
    protected final void initialize() {
        // Set the colours to the defaults:
        setPrimaryColour( defaultPrimaryColour() );
        setSecondaryColour( defaultSecondaryColour() );
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @param m
     */
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    protected void deepCopyTo(DefaultMeasurement m) {

        // Copy over primitives:
        m.setName(this.getName());
        m.marked = this.marked;
        m.lineWidth = this.lineWidth;
        m.pointWidth = this.pointWidth;

        // Copy over objects:
        Color col1 = new Color( this.getPrimaryColour().getRGB() );
        Color col2 = new Color( this.getSecondaryColour().getRGB() );
        m.setPrimaryColour(col1);
        m.setSecondaryColour(col2);
        MyPoint2DVector coords = this.getCoordinates().deepCopy();
        m.setCoordinates(coords);

    }

    // -------------------- Getters -------------------

    /** Returns the name of the measurement.
     * @return The name of the measurement.
     */
    @Override
    public String getName() { return name; }

    /** Returns the measurement coordinates.
     * @return A vector of measurement coordinates.
     */
    @Override
    public MyPoint2DVector getCoordinates() { return coordinates; }

    /** Returns the marked status of the measurement.
     * @return True if the measurement is marked (for some purpose), false otherwise.
     */
    @Override
    public boolean getMarked() { return marked; }

    /** Provides a primary painting colour.
     * @return A primary painting colour.
     */
    @Override
    public Color getPrimaryColour() { return colour1; }

    /** Provides a secondary painting colour.
     * @return A secondary painting colour.
     */
    @Override
    public Color getSecondaryColour() { return colour2; }

    /** Provides the line width for painting.
     * @return The line width for painting.
     */
    @Override
    public int getLineWidth() { return lineWidth; }

    /** Provides the point width for painting.
     * @return The point width for painting.
     */
    @Override
    public int getPointWidth() { return pointWidth; }

    // -------------------- Setters -------------------

    /** Sets the name of the measurement.
     * @param n The name for the measurement.
     */
    @Override
    public void setName(String n) { name = n; }

    /** Sets the measurement coordinates.
     * @param coords The vector of measurement coordinates.
     */
    @Override
    public void setCoordinates(MyPoint2DVector coords) { coordinates = coords; }

    /** Sets the marked status of the measurement.
     * @param m The marked status for the measurement.
     */
    @Override
    public void setMarked(Boolean m) { marked = m; }

    /** Sets the primary painting colour.
     * @param c The primary painting colour.
     */
    @Override
    public final void setPrimaryColour(Color c) { colour1 = c; }

    /** Sets the secondary painting colour.
     * @param c The secondary painting colour.
     */
    @Override
    public final void setSecondaryColour(Color c) { colour2 = c; }

    /** Sets the line width for painting.
     */
    @Override
    public void setLineWidth(int w) { lineWidth = w; }

    /** Sets the point width for painting.
     */
    @Override
    public void setPointWidth(int w) { pointWidth = w; }

    // -------------------- Implemented Methods (Paintable) -------------------

    /** Tells you if the object uses the secondary colour when painting itself.
     * This will be used to determine whether or not to ask the user for the
     * secondary painting colour in some GUI's.
     * @return This method always returns false for a DefaultMeasurement object.
     */
    @Override
    public boolean usesSecondaryColour() {
        return false;
    }

    /** This method should perform any calculations that are required prior to painting.
     * This method does nothing for a DefaultMeasurement object.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     * @return Always returns true.
     */
    @Override
    public boolean runBeforePainting(boolean measuring) { return true; } // do nothing by default

    /** Paints the object.
     * For a DefaultMeasurement object, this method draws the coordinate points as
     * individual filled circles using the primary colour and the point width and connects them.
     * @param g2 A Graphics2D object to paint with.
     * @param trans An AffineTransform to use while painting.
     * @param scal A scaling value involved in the transform.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     */
    @Override
    public void paint(Graphics2D g2, AffineTransform trans, double scal, boolean measuring){

        // Set the colour and line style for the measurement:
        g2.setPaint(getPrimaryColour()); // colour

        // We will draw a path (whether fully measured or not):
        GeneralPath path = new GeneralPath();

        // Add the measurement coordinates to the path:
        coordinates.addToPath(path);

        // Transform from sample to panel coordinates:
        path.transform(trans);

        // Draw the path:
        g2.draw(path);

        // Draw measurement coordinates as individual filled circles:
        PaintingUtils.paintPoints(g2,trans,coordinates,getPointWidth(),true);

    }

    // -------------------- Implemented Methods (Measurement) -------------------

    /** Checks that the supplied coordinates are appropriate for the measurement.
     * For a DefaultMeasurement object, the check passes provided the number of
     * coordinates is between the minimum and maximum required numbers.
     * @param coords Vector of MyPoint2D coordinate points.
     * @return True if the coordinates are okay for the measurement.
     */
    @Override
    public boolean checkCoordinates(MyPoint2DVector coords) {
        // Check the number of coordinates:
        return ( coords.size()>=minNumberOfCoordinates() && coords.size()<=maxNumberOfCoordinates() );
    }

    /** Returns the minimum number of coordinates required by the measurement.
     * @return The minimum number of coordinates required by the measurement.
     */
    @Override
    public abstract int minNumberOfCoordinates();

    /** Returns the maximum number of coordinates required by the measurement.
     * @return The maximum number of coordinates required by the measurement.
     */
    @Override
    public abstract int maxNumberOfCoordinates();

    /** Checks if the measurement has coordinates assigned to it.
     * For a DefaultMeasurement object, the check passes if the coordinates vector
     * is not null and has non-zero size.
     * @return True if coordinates are assigned, false otherwise.
     */
    @Override
    public boolean hasCoordinates() {
        if (coordinates==null) { return false; }
        return ( coordinates.size() != 0 );
    }

    /** Checks if the measurement has been measured (points specified) correctly.
     * For a DefaultMeasurement object, the check passes if the measurement has coordinates
     * and those coordinates are appropriate for the measurement.
     * @return True if measured correctly, false otherwise.
     */
    @Override
    public boolean isMeasured() {
        // Check there are appropriate coordinates defined:
        if (hasCoordinates()) {
            return checkCoordinates(this.coordinates);
        } else {
            return false;
        }
    }

    /** Clears the measurement.
     * For a DefaultMeasurement object, the coordinates vector is cleared.
     */
    @Override
    public void clear() {
        coordinates.clear();
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public abstract String typeString();

    /** Provides a string with the measurement name and any additionally required characters.
     * The intention is that this string should be used when writing to a CSV file but commas should NOT be added.
     * For a DefaultMeasurement object, the string returned contains only the measurement name.
     * @return A string containing the measurement name and any additionally required characters.
     */
    @Override
    public String nameForExportCSV() {
        return getName();
    }

    // -------------------- Abstract Methods -------------------

    /** Specifies a default primary painting colour.
     * @return The default primary painting colour.
     */
    protected abstract Color defaultPrimaryColour();

    /** Specifies a default secondary painting colour.
     * @return The default secondary painting colour.
     */
    protected abstract Color defaultSecondaryColour();

}
