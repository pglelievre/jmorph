package jmorph;

import dialogs.Dialogs;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.Cursor;
import jmorph.measurements.CalibrationMeasurement;
import jmorph.measurements.Measurement;
import jmorph.measurements.OriginMeasurement;
import jmorph.measurements.ZoomMeasurement;

/** Manages events caused by the user interacting with the mouse on the 2D viewer panel.
 * @author Peter
 */
public final class MouseInteractionManager {
    
    // Measurement modes:
    private static final int DUMMY_MODE_VALUE = -1; /** a dummy value used when the mode has not been set */
    public static final int SINGLE_MEASUREMENT_MODE = 0; /** specifies a single measurement at once */
    public static final int ALL_MEASUREMENTS_MODE = 1; /** specifies automatic measuring of all measurements for the current sample */
    public static final int ALL_SAMPLES_MODE = 2; /** specifies automatic measuring of all measurements for all samples */
    public static final int MOVE_COORDINATE_MODE = 3; /** specifies adjustment of a single coordinate point for a single measurement for the current sample */
    
    // Measurement identifiers (DO NOT CHANGE THESE VALUES!):
    public static final int ZOOM_INDEX = -3; /** specifies the Zoom measurement for a sample */
    public static final int CALIBRATION_INDEX = -2; /** specifies the Calibration measurement for a sample */
    public static final int ORIGIN_INDEX = -1; /** specifies the Origin measurement for a sample */
    
    private JMorph controller;
    private int measureMode = DUMMY_MODE_VALUE; // one of the measurement modes defined above
    private int measurementIndex = ZOOM_INDEX; // index of measurement being measured
    private Measurement measurement = null; // pointer to measurement object being measured
    private MyPoint2DVector clickPoints = null; // a vector of clicked points
    private MyPoint2D currentPoint = null; // current cursor position
    private boolean clickPointEnabled = false; // set to true when user is measuring
    private int moveCoordinateIndex = -1; // an index into the measurement coordinates that is being moved
    private MyPoint2DVector originalCoordinates = null; // the original measurement coordinates
   
    public MouseInteractionManager(JMorph con) {
        controller = con;
    }

    public boolean isMeasuring() { return clickPointEnabled; }

    /** Gets a bunch of user clicked points for use by a particular measurement.
     * @param m The measurement to which the clicked points will apply.
     * @param i The index of the measurement within its measurement list.
     * @param mode One of the static measurement mode values in this class.
     */
    public void startMeasuring(Measurement m, int i, int mode) {

        // Make sure an image exists:
        if ( !controller.currentSampleImageExists() && mode!=ALL_MEASUREMENTS_MODE && mode!=ALL_SAMPLES_MODE ) { return; }

        // Save the supplied input information to the corresponding object variables:
        measurement = m;
        measurementIndex = i;
        measureMode = mode;

        // Checking:
        switch (measurementIndex) {
            case ZOOM_INDEX:
                if ( !(measurement instanceof ZoomMeasurement) ) {
                    Dialogs.codeError(controller,"Inconsistent non-zoom measurement encountered in SampleImagePanel.startMeasuring");
                    return;
                }
                break;
            case CALIBRATION_INDEX:
                if ( !(measurement instanceof CalibrationMeasurement) ) {
                    Dialogs.codeError(controller,"Inconsistent non-calibration measurement encountered in SampleImagePanel.startMeasuring");
                    return;
                }
                break;
            case ORIGIN_INDEX:
                if ( !(measurement instanceof OriginMeasurement) ) {
                    Dialogs.codeError(controller,"Inconsistent non-origin measurement encountered in SampleImagePanel.startMeasuring");
                    return;
                }
                break;
            default:
                if ( (measurement instanceof ZoomMeasurement) ) {
                    Dialogs.codeError(controller,"Inconsistent zoom measurement encountered in SampleImagePanel.startMeasuring");
                    return;
                }
                if ( (measurement instanceof CalibrationMeasurement) ) {
                    Dialogs.codeError(controller,"Inconsistent calibration measurement encountered in SampleImagePanel.startMeasuring");
                    return;
                }
                if ( (measurement instanceof OriginMeasurement) ) {
                    Dialogs.codeError(controller,"Inconsistent origin measurement encountered in SampleImagePanel.startMeasuring");
                    return;
                }
        }

        // We may want to skip this measurement:
        int response;
        if (!controller.currentSampleImageExists()) {
            // If no image exists for the current sample then skip it:
            response = InteractionDialogs.INSTRUCTIONS_SKIP_OPTION;
        } else if ( ( measureMode==ALL_MEASUREMENTS_MODE || measureMode==ALL_SAMPLES_MODE ) && !m.getMarked() ) {
            // If automatically performing all measurements and the current measurement is not marked then skip it:
            response = InteractionDialogs.INSTRUCTIONS_SKIP_OPTION;
        } else {
            // Display the instructions:
            String title = m.instructionTitle();
            String prompt;
            if ( measureMode == MOVE_COORDINATE_MODE ) {
                prompt = "Fully click (button up, then down) on the coordinate to move," + System.lineSeparator()
                        + "then fully click where you want it moved to.";
            } else {
                prompt = m.instructionPrompt();
            }
            response = InteractionDialogs.instructions(controller,
                    ( measureMode==ALL_MEASUREMENTS_MODE || measureMode==ALL_SAMPLES_MODE ),
                    prompt,title);
        }

        // Check response:
        if (response == InteractionDialogs.INSTRUCTIONS_CANCEL_OPTION) {
            // Clean up and return;
            cleanUp();
            return;
        }
        if (response == InteractionDialogs.INSTRUCTIONS_SKIP_OPTION) {
            // Start the next measurement:
            nextMeasurement();
            return;
        }

        // Make sure the tight fit transform has been calculated:
        controller.calculateTightFitTransform();

        // Make sure that the clicked points vector and current point are cleared:
        currentPoint = null;
        clickPoints = new MyPoint2DVector();
        moveCoordinateIndex = -1;

        // Store the original measurement coordinates so we can go back if required:
        originalCoordinates = measurement.getCoordinates().deepCopy();

        // Disable the JMorph menu:
        //controller.disableClickableItems();

        // Clear the measurement and redraw the sample information, if necessary:
        if ( measureMode != MOVE_COORDINATE_MODE ) {
            measurement.clear();
            controller.drawCurrentSample(true);
        }

        // Set the cursor to something new:
        controller.setSampleImagePanelCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        // Make sure the SampleImagePanel has the focus:
        controller.requestFocusInSampleImagePanel();

        // Turn on the mouse and key monitors:
        clickPointEnabled = true;
        
        // Disable the JMorph menu and tool bars:
        controller.checkClickableItemsEnabled();

    }

    /** Stops measuring and completes the current measurement. */
    private void stopMeasuring() {

        // Turn off the mouse and key monitors:
        clickPointEnabled = false;
        
        // Enable the JMorph menu and tool bars:
        controller.checkClickableItemsEnabled();

        // Reset the cursor:
        //controller.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        controller.setSampleImagePanelCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        // Repaint to ensure last point selected is plotted before any further dialogs:
        controller.redraw();

        // Check that enough points were gathered:
        if ( measureMode == MOVE_COORDINATE_MODE ) {
            if ( clickPoints.size() < 2 ) {
                // Reset the original measurement coordinates and repaint:
                measurement.setCoordinates(originalCoordinates);
                controller.redraw();
                // Display error dialog and return:
                Dialogs.error(controller,
                        "You didn't finish moving the coordinate (two full clicks are required).",
                        "Measurement Error");
                // There may be a next measurement to take:
                nextMeasurement();
                return;
            }
        } else {
            if ( clickPoints.size() < measurement.minNumberOfCoordinates() ) {
                // Clear the measurement points and repaint:
                measurement.clear();
                controller.redraw();
                // Display error dialog and return:
                Dialogs.error(controller,
                        "Not enough points were supplied.",
                        "Measurement Error");
                // There may be a next measurement to take:
                nextMeasurement();
                return;
            }
        }

        // Check that the points are good enough:
        if ( measureMode!=MOVE_COORDINATE_MODE && !measurement.checkCoordinates(clickPoints) ) {
            // Clear the measurement points and repaint:
            measurement.clear();
            controller.redraw();
            // Display error dialog and return:
            Dialogs.error(controller,
                    "The points supplied were not acceptable.",
                    "Measurement Error");
            // There may be a next measurement to take:
            nextMeasurement();
            return;
        }

        // May need further information from user:
        if ( measureMode!=MOVE_COORDINATE_MODE && (measurement instanceof CalibrationMeasurement) ) {
            // Ask for the calibration distance:
            if (controller.getDoCalibrationDistanceAuto()) {
                if (!controller.changeCalibrationDistance()) {
                    // User cancelled so clear the measurement points and repaint:
                    measurement.clear();
                    controller.redraw();
                    return;
                }
            }
        }

        
        // Replace the measurement object coordinates with the clicked points:
        // (if moving a single coordinate point then this will already have been done by the mouse click moonitor)
        if ( measureMode != MOVE_COORDINATE_MODE ) {
            measurement.setCoordinates(clickPoints.deepCopy());
        }

        // Redraw the sample information so that the measurement panel is altered:
        controller.drawCurrentSample(false);

        // There may be a next measurement to take:
        if (!nextMeasurement()) {
            // Do some cleanup (nullify some pointers):
            cleanUp();
            // Enable the JMorph menu:
            controller.checkClickableItemsEnabled();
        }

    }

    /** Cleans up some temporary working objects. */
    private void cleanUp() {
        measurement = null;
        clickPoints = null;
        currentPoint = null;
        originalCoordinates = null;
        // (do not clear the measureMode as it is required in the nextMeasurement method)
    }

    /** Performs the next required measurement (depends on the measurement mode).
     * @return True if the code continues to perform another measurement.
     */
    private boolean nextMeasurement() {

        // Check for single measurement mode:
        if ( measureMode!=ALL_MEASUREMENTS_MODE && measureMode!=ALL_SAMPLES_MODE ) { return false; }

        // Increment the measurement index:
        if (controller.getZoomBeforeCalibration()){
            // Perform zoom before calibration:
            measurementIndex++;
        } else {
            // Perform calibration before zoom:
            switch (measurementIndex) {
                case CALIBRATION_INDEX:
                    measurementIndex = ORIGIN_INDEX;
                    break;
                case ORIGIN_INDEX:
                    measurementIndex = ZOOM_INDEX;
                    break;
                case ZOOM_INDEX:
                    measurementIndex = 0; // first non-special-case measurement
                    break;
                default:
                    measurementIndex++;
            }
        }

        // Check the mode again:
        switch (measureMode) {
            case ALL_MEASUREMENTS_MODE:
                // Check if we are on the last measurement:
                if (measurementIndex == controller.numberOfMeasurements()) { return false; }
                break;
            case ALL_SAMPLES_MODE:
                // Check if we are on the last measurement:
                if (measurementIndex == controller.numberOfMeasurements()) {
                    
                    // Check if we are on the last sample:
                    //if (controller.getCurrentIndex()+1 == controller.numberOfSamples()) { return false; }
                    
                    // See if they want to re-measure the current sample (perhaps they made a mistake):
                    int response = Dialogs.question(controller,
                            "Do you want to re-measure this sample?",
                            "Measure Current Sample");
                    switch(response) {
                        case Dialogs.YES_OPTION:
                            // Continue measuring without moving to the next sample:
                            break;
                        case Dialogs.NO_OPTION:
                            
                            // User may want to skip some samples:
                            int resp = Dialogs.NO_OPTION;
                            while (resp == Dialogs.NO_OPTION) {
                                // Check if we are on the last sample:
                                if (controller.getCurrentSampleIndex()+1 == controller.numberOfSamples()) { return false; }
                                // Move to next sample:
                                controller.nextSample();
                                // Ask if user wants to measure this sample:
                                resp = Dialogs.question(
                                        controller,
                                        "Do you want to measure this sample (use NO to skip)?",
                                        "Measure Current Sample");
                                if (resp == Dialogs.CANCEL_OPTION) { return false; }
                            }
                            break;
                            
                        default:
                            // Stop measuring:
                            return false;
                    }
                    
                    // Reset the measurement index:
                    if (controller.getZoomBeforeCalibration()){
                        // Perform zoom before calibration:
                        measurementIndex = ZOOM_INDEX;
                    } else {
                        // Perform calibration before zoom:
                        measurementIndex = CALIBRATION_INDEX;
                    }
                    
                }   break;
            default:
                Dialogs.codeError(controller,"unexpected entry into else block in SampleImagePanel.nextMeasurement");
                return false;
        }

        // Start the next measurement (or the first measurement for the new current sample):
        switch (measurementIndex) {
            case ZOOM_INDEX:
                startMeasuring(
                        controller.getCurrentSample().getZoom(),
                        measurementIndex,
                        measureMode);
                break;
            case CALIBRATION_INDEX:
                startMeasuring(
                        controller.getCurrentSample().getCalibration(),
                        measurementIndex,
                        measureMode);
                break;
            case ORIGIN_INDEX:
                startMeasuring(
                        controller.getCurrentSample().getOrigin(),
                        measurementIndex,
                        measureMode);
                break;
            default:
                startMeasuring(
                        controller.getCurrentSample().getMeasurementList().get(measurementIndex),
                        measurementIndex,
                        measureMode);
                break;
        }

        return true;

    }
    
    public void mouseClick(MyPoint2D p) { // p should be in image coordinates
        if ( clickPointEnabled ) {
            // Store the clicked point:
            currentPoint = p;
            // Add new clicked point to local record:
            clickPoints.add(currentPoint);
            // If moving a point and it's the first mouse click then need to find the closest point:
            if ( measureMode==MOVE_COORDINATE_MODE && moveCoordinateIndex<0 ) {
                // Find the closest measurement coordinate point to the clicked point:
                moveCoordinateIndex = measurement.getCoordinates().findClosest(currentPoint);
            }
            // Combine the clickPoints and the current point into a new vector object:
            MyPoint2DVector tempPoints = makeTempPoints();
            // Replace the measurement object coordinates with those coordinates:
            measurement.setCoordinates(tempPoints);
            // Check if we have enough points to stop measuring:
            if ( measureMode == MOVE_COORDINATE_MODE ) {
                if ( clickPoints.size() >= 2 ) {
                    stopMeasuring();
                }
            } else {
                if ( clickPoints.size() >= measurement.maxNumberOfCoordinates() ) {
                    stopMeasuring();
                }
            }
        }
    }

    public void mouseMove(MyPoint2D p) { // p should be in image coordinates
        // Store the current cursor location:
        currentPoint = p;
        if ( clickPointEnabled ) {
            // Combine the clickPoints and the current point into a new vector object:
            MyPoint2DVector tempPoints = makeTempPoints();
            // Replace the measurement object coordinates with those coordinates:
            measurement.setCoordinates(tempPoints);
            // Repaint:
            controller.drawCurrentSample(clickPointEnabled);
            controller.redraw();
            // Update the cursor location bar:
            controller.updateCursorBar(currentPoint, (measurement instanceof OriginMeasurement));
        } else {
            controller.updateCursorBar(currentPoint,false);
        }
    }

    /** A utility method for the mouse move and click methods.
     * Fills the temporary working point vectors as required by the measurement mode.
     */
    private MyPoint2DVector makeTempPoints() {
        // Combine the clickPoints and the current point into a new vector object:
        MyPoint2DVector tempPoints = new MyPoint2DVector();
        if ( measureMode == MOVE_COORDINATE_MODE ) {
            // Add the original measurement coordinate points:
            tempPoints.addAll(originalCoordinates.deepCopy());
            if (moveCoordinateIndex>=0) {
                // Remove the specified point:
                tempPoints.remove(moveCoordinateIndex);
                if (currentPoint!=null) {
                    // Add in the current point at that same location:
                    tempPoints.add(moveCoordinateIndex,currentPoint.deepCopy());
                }
            }
        } else {
            if (clickPoints!=null) { tempPoints.addAll(clickPoints.deepCopy()); }
            if (currentPoint!=null) { tempPoints.add(currentPoint.deepCopy()); }
        }
        return tempPoints;
    }

    public void keyType(char c) {
        /* If the user closes the instruction dialog with enter/return key
         * then this key listener may fire prematurely so only allow the
         * space bar to be used to stop measuring: */
        if ( clickPointEnabled && c==' ' ) {
            stopMeasuring();
        }
    }
    
    // Methods required by the SampleImagePanel class:
    public boolean shouldPaintMeasurement(Measurement m) {
        return ( clickPointEnabled && currentPoint!=null && measureMode!=MOVE_COORDINATE_MODE && m==measurement );
    }
    public boolean measuringZoomBox() {
        if ( clickPointEnabled && measurement!=null ) {
            return (measurement instanceof ZoomMeasurement);
        } else {
            return false;
        }
    }
    
}
