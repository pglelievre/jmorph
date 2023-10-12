package jmorph;

import geometry.MyPoint2D;
import jmorph.gui.InteractionOptions;
import jmorph.measurements.Measurement;

/** Manages user interactions with the GUI.
 * @author Peter
 */
public class InteractionManager {
    
    private final MouseInteractionManager mouseManager; // manages mouse interactions
    private final InteractionOptions interactionOptions; // interaction options
    
    public InteractionManager(JMorph con) {
        mouseManager = new MouseInteractionManager(con);
        interactionOptions = new InteractionOptions(con);
    }
    
    // Wrappers for the MouseInteractionManager class:
    public void mouseMove(MyPoint2D p) { mouseManager.mouseMove(p); }
    public void mouseClick(MyPoint2D p) { mouseManager.mouseClick(p); }
    public void keyType(char c) { mouseManager.keyType(c); }
    public boolean measuringZoomBox() { return mouseManager.measuringZoomBox(); }
    public boolean shouldPaintMeasurement(Measurement m) { return mouseManager.shouldPaintMeasurement(m); }
    public void startMeasuring(Measurement m, int i, int mode) { mouseManager.startMeasuring(m,i,mode); }
    public boolean isMeasuring() { return mouseManager.isMeasuring(); }
    
    // Wrappers for the InteractionOptions class:
    public boolean getDoCalibrationDistanceAuto() { return interactionOptions.getDoCalibrationDistanceAuto(); }
    public boolean getZoomBeforeCalibration() { return interactionOptions.getZoomBeforeCalibration(); }
    public void setDoCalibrationDistanceAuto(boolean b) { interactionOptions.setDoCalibrationDistanceAuto(b); }
    public void setZoomBeforeCalibration(boolean b) { interactionOptions.setZoomBeforeCalibration(b); }
    public void selectAllOrder(String title){ interactionOptions.selectAllOrder(title); }
    
}
