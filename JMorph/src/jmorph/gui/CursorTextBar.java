package jmorph.gui;

import jmorph.JMorph;
import geometry.MyPoint2D;
import gui.TextBar;
import java.awt.geom.AffineTransform;
import jmorph.Sample;

/** The TextBar object that provides information about the cursor location.
 * @author Peter
 */
public final class CursorTextBar extends TextBar {
    private static final long serialVersionUID = 1L;
    
    private JMorph controller;
    
    public CursorTextBar(JMorph con) { controller = con; }
    
    /** Updates the cursor bar by writing the current cursor position.
     * @param p
     * @param isOrigin */
    public void updateCursor(MyPoint2D p, boolean isOrigin) {

        // Clear the cursor bar:
        setText(" ");

        // Check for no samples loaded:
        if (!controller.hasSamples()) {return;}
        
        // Check for no point:
        if (p==null) { return; }

        // Transform the current point unless we are measuring the origin:
        MyPoint2D p0;
        String s;
        if (isOrigin) {
            s = "Uncalibrated (x,y) = ";
            p0 = p;
        } else {
            // Check if the current sample has been calibrated and has origin specified:
            Sample sample = controller.getCurrentSample();
            if ( sample.isCalibrated() && sample.hasOrigin()) {
                // Transform a copy of the input point:
                p0 = p.deepCopy();
                AffineTransform trans = sample.calibrationTransform();
                p0.transform(trans);
                s = "Calibrated (x,y) = ";
            } else {
                // Shift a copy of the input point by the origin if it exists:
                if (sample.hasOrigin()) {
                    // Get the origin point for the current sample:
                    p0 = sample.getOriginPoint();
                    p0 = new MyPoint2D( p.getX()-p0.getX() , p.getY()-p0.getY() );
                } else {
                    p0 = p;
                }
                // Either way, it's uncalibrated:
                s = "Uncalibrated (x,y) = ";
            }
        }

        // Add the location coordinates to the input string:
        s += " = " + p0.toStringParentheses();
        
        // Display the text in the cursor bar:
        setText(s);
        
    }
    
}
