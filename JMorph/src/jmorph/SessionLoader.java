package jmorph;

import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import jmorph.measurements.Measurement;
import jmorph.measurements.OutlineMeasurement;
import jmorph.measurements.SplineMeasurement;
import paint.Paintable;

/** Static methods for loading a previously saved session.
 * Every time the floored version number (e.g. 1.*) is changed, a new load method should be added to this class.
 * The floored version number should only be changed if a new load method is completely required!
 * The names of the older static methods should be suffixed with their version numbers.
 * @author Peter Lelievre
 */
@SuppressWarnings("PublicInnerClass")
public class SessionLoader {
    
    /** Loads a previously saved ascii session file.
     * @param controller
     * @param file
     * @return Less than or equal to 0 if an error occurs.
     */
    public static LoadSessionReturnObject loadSessionAscii(JMorph controller, File file) {
        // First try the current loadVersion:
        LoadSessionReturnObject out = loadSessionAscii1(controller,file);
        if (out.message!=null) { return out; }
        return out;
    }
    
    @SuppressWarnings("PublicField")
    public static class LoadSessionReturnObject {
        public int version = 0;
        public String message = null; // set to non-null if there is a problem
        public LoadSessionReturnObject(int v, String s) {
            version = v;
            message = s;
        }
    }

    /** Loads a previously saved ascii session file.
     * @param controller
     * @param file
     * @return An error flag:
 0 no error occurred,
 -1 failed to load file,
 -2 session file was from older loadVersion.
     */
    public static LoadSessionReturnObject loadSessionAscii1(JMorph controller, File file) {

        int loadVersion = 1; // THIS VALUE MUST BE THE SAME AS THE METHOD PREFIX
        String title = "Load Session"; // a title for some dialogs

        // We will be constructing some new objects as we read the file:
        SampleVector newSampleList = new SampleVector();
        MeasurementVector newMeasurementList = new MeasurementVector();
        
        // We may be setting some options:
        int currentIndex = -1;
        int pointWidth = Paintable.DEFAULT_POINT_WIDTH;
        int lineWidth = Paintable.DEFAULT_LINE_WIDTH;
        Color calibrationColor = Color.CYAN;
        boolean displayMeasurements = true;
        boolean displayInfoPanel = true;
        boolean displayFFTCoefficients = false;
        boolean zoomBeforeCalibration = false;
        boolean useCircleSpline = SplineMeasurement.USE_CIRCLE_SPLINE_DEFAULT;
        boolean doCalibrationDistanceAuto = true;
        int outlineResamplingPower = OutlineMeasurement.OUTLINE_RESAMPLING_POWER_DEFAULT;
        int outlineHighestFFTCoefficient = OutlineMeasurement.OUTLINE_HIGHEST_FFT_COEFFICIENT_DEFAULT;
        int outlineNormalizationIndex = OutlineMeasurement.OUTLINE_NORMALIZATION_INDEX_DEFAULT;
        int fourierAnalysisMethod = OutlineMeasurement.FOURIER_ANALYSIS_METHOD_DEFAULT;

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) {
            return new LoadSessionReturnObject(0,"Opening file for reading.");
        }

        // Put everything below in an infinite loop that we can jump out of when something goes wrong:
        boolean ok = true;
        String message = "Unspecified file format error.";
        while(true) {
            String textLine;
            String[] ss;

            // Read the floored loadVersion number:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; message="Reading floored version number."; break; }
            int version;
            textLine = textLine.trim();
            ss = textLine.split("[ ]+");
            if (ss.length<1) { ok=false; message="Not enough values on floored version line."; break; }
            try {
                version = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { ok=false; message="Parsing floored version number."; break; }
            if (!ok) { break; }

            // Check against the current loadVersion:
            if ( version != loadVersion ) {
                // Close the file:
                FileUtils.close(reader);
                // Return unsuccessfully:
                return new LoadSessionReturnObject(loadVersion,"Unexpected version number.");
            }

            // Read the number of samples and number of measurements:
            textLine = FileUtils.readLine(reader); // reads the line
            if (textLine==null) { ok=false; message="Reading number of samples and measurements."; break; }
            int nSamples, nMeasurements;
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",3);
            try {
                nSamples = Integer.parseInt(ss[0].trim()); // converts to integer
                nMeasurements = Integer.parseInt(ss[1].trim()); // converts to integer
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; message="Parsing number of samples and measurements."; break; }
            if (!ok) { break; }

            // Read the measurement types and names, building up the new measurement list object as we go:
            for ( int j=0 ; j<nMeasurements ; j++ ) {

                // Read the measurement type and name:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading measurement type and name."; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",3);
                String typeString = ss[0];
                String name = ss[1];

                // Add the measurement to the list:
                ok = newMeasurementList.addNew(typeString,name);
                if (!ok) { message="Adding new measurement."; break; }

            }
            if (!ok) {break;}

            // Read the sample file names:
            File[] fList = new File[nSamples];
            for ( int i=0 ; i<nSamples ; i++ ) {
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading sample file names."; break; }
                try {
                    URI uri = new URI(textLine);
                    fList[i] = new File(uri);
                } catch (URISyntaxException e) { ok=false; message="Converting sample file name to URI."; break; }
                if (!ok) {break;}
            }
            if (!ok) {break;}

            // Clear the sample list (probably not neccessary but just in case):
            newSampleList.clear();

            // Add samples to the new sample list:
            newSampleList.addSamplesFromFiles(controller,controller.getReadAll(),title,fList);
            
            // Check if user cancelled on the progress bar while loading:
            if (newSampleList.getUserCancelledWhileLoading()) {
                // Try to close the file (exceptions are caught and ignored):
                FileUtils.close(reader);
                // Return unsuccessfully:
                return new LoadSessionReturnObject(loadVersion,"Loading cancelled by user.");
            }
            
            // Reset the IDs:
            newSampleList.resetIDs();
            
            // Copy the measurement list to all the sample objects:
            newSampleList.replaceMeasurementLists(newMeasurementList,0);
            nSamples = newSampleList.size(); // shouldn't change but better safe than sorry
            
            // Read the sample calibration lengths, setting them into the samples as we go:
            for ( int i=0 ; i<nSamples ; i++ ) {

                // Read the calibration length:
                textLine = FileUtils.readLine(reader);
                if (textLine==null) { ok=false; message="Reading calibration length."; break; }
                textLine = textLine.trim();
                ss = textLine.split("[ ]+",2);
                double len;
                try {
                    len = Double.parseDouble(ss[0].trim()); // converts to double
                } catch (NumberFormatException e) { ok=false; message="Parsing calibration length."; break; }
                if (!ok) { break; }

                // Set the calibration length:
                newSampleList.get(i).setCalibrationDistance(len);

            }
            if (!ok) {break;}

            // Loop over each sample:
            for ( int i=0; i<nSamples ; i++ ) {

                // Get the current sample:
                Sample s = newSampleList.get(i);

                // Loop over each measurement (including doing three extra for the zoom, calibration and origin:
                MeasurementVector mList = s.getMeasurementList();
                for ( int j=-3 ; j<nMeasurements ; j++ ) {

                    // Get the current measurement:
                    Measurement m;
                    m = switch (j) {
                        case -3 -> s.getZoom();
                        case -2 -> s.getCalibration();
                        case -1 -> s.getOrigin();
                        default -> mList.get(j);
                    };

                    // Read the number of coordinates:
                    textLine = FileUtils.readLine(reader);
                    if (textLine==null) { ok=false; message="Reading number of measurement coordinates."; break; }
                    textLine = textLine.trim();
                    ss = textLine.split("[ ]+",2);
                    int n;
                    try {
                        n = Integer.parseInt(ss[0].trim()); // converts to int
                    } catch (NumberFormatException e) { ok=false; message="Parsing number of measurement coordinates."; break; }
                    if (!ok) { break; }

                    // Read the coordinates:
                    MyPoint2DVector coords = new MyPoint2DVector();
                    for ( int k=0 ; k<n ; k++ ) {
                        textLine = FileUtils.readLine(reader);
                        if (textLine==null) { ok=false; message="Reading measurement coordinates."; break; }
                        textLine = textLine.trim();
                        ss = textLine.split("[ ]+",3);
                        double x,y;
                        try {
                            x = Double.parseDouble(ss[0].trim()); // converts to double
                            y = Double.parseDouble(ss[1].trim()); // converts to double
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) { ok=false; message="Parsing measurement coordinates."; break; }
                        if (!ok) { break; }
                        MyPoint2D p = new MyPoint2D(x,y);
                        coords.add(p);
                    }
                    if (!ok) {break;}

                    // Set the coordinates into the current measurement:
                    m.setCoordinates(coords); // don't need to deep copy because coords is set to a new object above

                }
                if (!ok) {break;}

            }
            if (!ok) {break;}
            
            // Read current sample index:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) {
                // This and the information below was not always written before,
                // so don't throw an error now.
                break;
            }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",2);
            try {
                currentIndex = Integer.parseInt(ss[0].trim()); // converts to integer
            } catch (NumberFormatException e) { ok=false; message="Parsing current sample index."; break; }
            if (!ok) { break; }
            
            // Read plotting defaults:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; message="Reading point width and line width plotting defaults."; break; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",3);
            try {
                pointWidth = Integer.parseInt(ss[0].trim()); // converts to integer
                lineWidth  = Integer.parseInt(ss[1].trim()); // converts to integer
            } catch (NumberFormatException e) { ok=false; message="Parsing point width and line width plotting defaults."; break; }
            if (!ok) { break; }
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; message="Reading calibration colour plotting default."; break; }
            try {
                calibrationColor = new Color(Integer.parseInt(textLine.trim())); // parse from RGB string
            } catch (NumberFormatException e) { ok=false; message="Parsing calibration colour plotting default."; break; }
            if (!ok) { break; }
            
            // Read boolean options:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; message="Reading boolean plotting options."; break; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",7);
            try {
                displayMeasurements       = Boolean.parseBoolean(ss[0].trim()); // converts to boolean;
                displayInfoPanel          = Boolean.parseBoolean(ss[1].trim());
                displayFFTCoefficients    = Boolean.parseBoolean(ss[2].trim());
                zoomBeforeCalibration     = Boolean.parseBoolean(ss[3].trim());
                useCircleSpline           = Boolean.parseBoolean(ss[4].trim());
                doCalibrationDistanceAuto = Boolean.parseBoolean(ss[5].trim());
            } catch (NumberFormatException e) { ok=false; message="Parsing boolean plotting options."; break; }
            if (!ok) { break; }
            
            // Read integer options:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { ok=false; message="Reading integer plotting options."; break; }
            textLine = textLine.trim();
            ss = textLine.split("[ ]+",5);
            try {
                outlineResamplingPower       = Integer.parseInt(ss[0].trim()); // converts to integer
                outlineHighestFFTCoefficient = Integer.parseInt(ss[1].trim());
                outlineNormalizationIndex    = Integer.parseInt(ss[2].trim());
                fourierAnalysisMethod        = Integer.parseInt(ss[3].trim());
            } catch (NumberFormatException e) { ok=false; message="Parsing integer plotting options."; break; }
            if (!ok) { break; }
            
            // Read from outer while loop:
            break;

        }

        // Close the file:
        FileUtils.close(reader);

        // Check for a problem:
        if (!ok) { return new LoadSessionReturnObject(0,message); }
        
        // Reset the JMorph sample list and measurement list:
        controller.setSampleList(newSampleList);
        controller.setMeasurementList(newMeasurementList);
        
        // Deal with other options:
        if (currentIndex>=0) {
            controller.setCurrentSampleIndex(currentIndex);
            controller.setPointWidth(pointWidth,true);
            controller.setLineWidth(lineWidth,true);
            controller.setCalibrationColor(calibrationColor,true);
            controller.setDisplayMeasurements(displayMeasurements);
            controller.setDisplayInfoPanel(displayInfoPanel);
            controller.setDisplayFFTCoefficients(displayFFTCoefficients);
            controller.setZoomBeforeCalibration(zoomBeforeCalibration);
            controller.setUseCircleSpline(useCircleSpline);
            controller.setDoCalibrationDistanceAuto(doCalibrationDistanceAuto);
            controller.setOutlineResamplingPower(outlineResamplingPower);
            controller.setOutlineHighestFFTCoefficient(outlineHighestFFTCoefficient);
            controller.setOutlineNormalizationIndex(outlineNormalizationIndex);
            controller.setFourierAnalysisMethod(fourierAnalysisMethod);
        }
        
        // Return successfully:
        return new LoadSessionReturnObject(loadVersion,null);

    }

}
