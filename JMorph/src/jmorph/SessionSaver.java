package jmorph;

import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.io.BufferedWriter;
import java.io.File;
import jmorph.measurements.Measurement;

/** Static methods for saving the jmorph session.
 * Everytime the version is changed a new save method should be added to this class.
 * The names of the older static methods should be suffixed with their version numbers.
 * However, I'm maintaining the single method below as long as possible.
 * @author Peter Lelievre
 */
public class SessionSaver {

    /** Saves the existing session to an ascii file.
     * @param controller
     * @param file
     * @return  */
    public static boolean saveSessionAscii1(JMorph controller, File file) {

        int version = 1; // THIS VALUE MUST BE THE SAME AS THE METHOD PREFIX
        //String title = "Save Session"; // a title for some dialogs

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) {
            return false;
        }

        // Put everything below in an infinite loop that we can jump out of when something goes wrong:
        boolean ok = true;
        while(true) {

            // Write the floored version number:
            String textLine = Integer.toString(version);
            if (!FileUtils.writeLine(writer,textLine)) {
                ok = false;
                break;
            }

            // Write the number of samples and number of measurements:
            textLine = controller.numberOfSamples() + " " + controller.numberOfMeasurements();
            if (!FileUtils.writeLine(writer,textLine)) {
                ok = false;
                break;
            }

            // Write a list of measurement types and names:
            for ( int j=0 ; j<controller.numberOfMeasurements() ; j++ ) {

                // Get the current measurement:
                Measurement m = controller.getMeasurement(j);

                // Write the measurement type and name:
                textLine = m.typeString();
                if (textLine == null) { // unknown type
                    ok = false;
                    break;
                }
                textLine = textLine + " " + m.getName();
                if (!FileUtils.writeLine(writer,textLine)) {
                    ok = false;
                    break;
                }
                if (!ok) {break;}

            }
            if (!ok) {break;}

            // Write a list of sample file names:
            for ( int i=0; i<controller.numberOfSamples() ; i++ ) {

                // Get the current sample:
                Sample s = controller.getSample(i);

                // Write the sample file name:
                textLine = s.fileURIString();
                if (!FileUtils.writeLine(writer,textLine)) {
                    ok = false;
                    break;
                }
                if (!ok) {break;}

            }
            if (!ok) {break;}

            // Write a list of sample calibration lengths:
            for ( int i=0; i<controller.numberOfSamples() ; i++ ) {

                // Get the current sample:
                Sample s = controller.getSample(i);

                // Write the calibration length:
                textLine = Double.toString( s.getCalibrationDistance() );
                if (!FileUtils.writeLine(writer,textLine)) {
                    ok = false;
                    break;
                }
                if (!ok) {break;}

            }
            if (!ok) {break;}

            // Loop over each sample:
            for ( int i=0; i<controller.numberOfSamples() ; i++ ) {

                // Get the current sample:
                Sample s = controller.getSample(i);

                // Loop over each measurement (including doing three extra for the zoom, calibration and origin:
                MeasurementVector mList = s.getMeasurementList();
                for ( int j=-3 ; j<controller.numberOfMeasurements() ; j++ ) {

                    // Get the current measurement:
                    Measurement m;
                    switch(j) {
                        case -3:
                            m = s.getZoom();
                            break;
                        case -2:
                            m = s.getCalibration();
                            break;
                        case -1:
                            m = s.getOrigin();
                            break;
                        default:
                            m = mList.get(j);
                            break;
                    }

                    // Get the coordinates:
                    MyPoint2DVector coords = m.getCoordinates();

                    // Write the number of coordinates:
                    int n = coords.size();
                    textLine = Integer.toString(n);
                    if (!FileUtils.writeLine(writer,textLine)) {
                        ok = false;
                        break;
                    }
                    if (!ok) {break;}

                    // Write the list of coordinates:
                    for ( int k=0 ; k<n ; k++ ) {
                        MyPoint2D p = coords.get(k);
                        textLine = p.getX() + " " + p.getY();
                        if (!FileUtils.writeLine(writer,textLine)) {
                            ok = false;
                            break;
                        }
                        if (!ok) {break;}
                    }
                    if (!ok) {break;}

                }
                if (!ok) {break;}

            }
            
            // Write current sample index:
            textLine = Integer.toString( controller.getCurrentSampleIndex() );
            if (!FileUtils.writeLine(writer,textLine)) {
                ok = false;
                break;
            }
            // Write plotting defaults:
            textLine = controller.getPointWidth() + " " + controller.getLineWidth();
            if (!FileUtils.writeLine(writer,textLine)) {
                ok = false;
                break;
            }
            textLine = Integer.toString(controller.getCalibrationColor().getRGB());
            if (!FileUtils.writeLine(writer,textLine)) { ok = false; break; }
            
            // Write boolean options:
            textLine = controller.getDisplayMeasurements() + " "
                    + controller.getDisplayInfoPanel() + " "
                    + controller.getDisplayFFTCoefficients() + " "
                    + controller.getZoomBeforeCalibration() + " "
                    + controller.getUseCircleSpline() + " "
                    + controller.getDoCalibrationDistanceAuto();
            if (!FileUtils.writeLine(writer,textLine)) {
                ok = false;
                break;
            }
            // Write integer options:
            textLine = controller.getOutlineResamplingPower() + " "
                    + controller.getOutlineHighestFFTCoefficient() + " "
                    + controller.getOutlineNormalizationIndex() + " "
                    + controller.getFourierAnalysisMethod();
            if (!FileUtils.writeLine(writer,textLine)) {
                ok = false;
                break;
            }
            
            // Break from outer while loop:
            break;

        }

        // Close the file:
        FileUtils.close(writer);

        // Return:
        return ok;


    }

}
