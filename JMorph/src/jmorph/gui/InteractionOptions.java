package jmorph.gui;

import dialogs.Dialogs;
import jmorph.JMorph;

/** Manages interaction options.
 * @author Peter
 */
public final class InteractionOptions {
    
    private JMorph controller;
    
    private boolean zoomBeforeCalibration = false; // should the zoom should be measured before the calibration during automatic measuring
    private boolean doCalibrationDistanceAuto = true; // whether or not to ask for the calibration distance when automatially measuring

    public InteractionOptions(JMorph con) {
        controller = con;
    }
    
    public boolean getDoCalibrationDistanceAuto() { return doCalibrationDistanceAuto; }
    public boolean getZoomBeforeCalibration(){ return zoomBeforeCalibration; }
    public void setDoCalibrationDistanceAuto(boolean b) { doCalibrationDistanceAuto = b; }
    public void setZoomBeforeCalibration(boolean b) { zoomBeforeCalibration = b; }

    // Asks the user for the appropriate order to perform zooming and calibration during automatic measuring.
    public void selectAllOrder(String title){
        // Display the dialog:
        String prompt = "Select which you want to perform first:";
        int response;
        if (getZoomBeforeCalibration()) {
            response = Dialogs.question(controller,prompt,title,"Zoom","Calibrate","Cancel","Zoom");
        } else {
            response = Dialogs.question(controller,prompt,title,"Zoom","Calibrate","Cancel","Calibrate");
        }
        // Check the response and set the boolean property:
        switch (response) {
            case Dialogs.YES_OPTION:
                zoomBeforeCalibration = true;
                break;
            case Dialogs.NO_OPTION:
                zoomBeforeCalibration = false;
                break;
            default:
                //return;
        }
    }

}
