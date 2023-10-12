package jmorph;

import fileio.FileUtils;
import java.awt.Color;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import jmorph.measurements.*;

/** A vector of measurements for use in JMorph and associated methods.
 * @author Peter Lelievre
 */
public class MeasurementVector {

    // ------------------ Properties -------------------

    // Favour composition over inheritence!
    private ArrayList<Measurement> vector = new ArrayList<>();

    // ------------------ Constructor ------------------

    public MeasurementVector() {}

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    public MeasurementVector deepCopy() {

        // Create a new object:
        MeasurementVector out = new MeasurementVector();

        // Deep copy over the measurement objects:
        for ( int i=0 ; i<this.size() ; i++ ) {
            Measurement m = this.get(i).deepCopy();
            out.add(m);
        }

        // Return the new object:
        return out;

    }

    // -------------------- Getters --------------------

    /** Getter for a measurement in the list.
     * @param i The index of the measurement.
     * @return The specified measurement.
     */
    public Measurement get(int i) {
        return vector.get(i);
    }

    // -------------------- Static Methods --------------------

    /** Supplies the available measurement types in a String array.
     * @return  */
    public static String[] getTypes() {
        String[] allTypes = new String[9];
        allTypes[0] = new AreaMeasurement().typeString(); //"area"; // These names MUST be identical to those returned
        allTypes[1] = new SplineMeasurement().typeString(); //"spline"; // in the typeString methods of the measurement classes.
        allTypes[2] = new OutlineMeasurement().typeString(); //"outline";
        allTypes[3] = new AngleMeasurement().typeString(); //"angle";
        allTypes[4] = new LandmarkMeasurement().typeString(); //"landmark";
        allTypes[5] = new LengthMeasurement().typeString(); //"length";
        allTypes[6] = new RadiusMeasurement().typeString(); //"radius";
        allTypes[7] = new SeparationMeasurement().typeString(); //"separation";
        allTypes[8] = new CountMeasurement().typeString(); //"count";
        return allTypes;
    }
    
    // -------------------- Setters --------------------

     /** Sets the outline resampling power.
     * @param p The new value for the resampling power.
     */
   public void setResamplingPower(int p) {
        // Loop over each measurement:
        for (int i=0 ; i<size() ; i++ ) {
            // Check if the measurement is an outline measurement:
            if ( get(i) instanceof OutlineMeasurement ) {
                // Tell the outline measurement to set its resampling power:
                OutlineMeasurement m = (OutlineMeasurement)get(i);
                m.setResamplingPower(p);
            }
        }
    }

     /** Sets the highest Fourier coefficients used in outline reconstructions.
     * @param n The new highest coefficient to use.
     */
   public void setHighestFFTCoefficient(int n) {
        // Loop over each measurement:
        for (int i=0 ; i<size() ; i++ ) {
            // Check if the measurement is an outline measurement:
            if ( get(i) instanceof OutlineMeasurement ) {
                // Tell the outline measurement to set its resampling power:
                OutlineMeasurement m = (OutlineMeasurement)get(i);
                m.setHighestFFTCoefficient(n);
            }
        }
    }

     /** Sets the normalization index used in outline analyses.
     * @param n The new normalization index value to use.
     */
   public void setNormalizationIndex(int n) {
        // Loop over each measurement:
        for (int i=0 ; i<size() ; i++ ) {
            // Check if the measurement is an outline measurement:
            if ( get(i) instanceof OutlineMeasurement ) {
                // Tell the outline measurement to set its normalization index:
                OutlineMeasurement m = (OutlineMeasurement)get(i);
                m.setNormalizationIndex(n);
            }
        }
    }

    /** Sets the type of spline used for spline outline measurements.
     * @param use Set to true to use a cirle-preserving spline, false for a Kochanek–Bartels spline.
     */
    public void setUseCircleSpline(boolean use) {

        // Loop over each measurement:
        for (int i=0 ; i<size() ; i++ ) {

            // Check if the measurement is a spline or outline measurement:
            if ( get(i) instanceof SplineMeasurement ) {

                // Tell the area measurement to set the outline spline:
                SplineMeasurement m = (SplineMeasurement)get(i);
                m.setUseCircleSpline(use);

            } else if ( get(i) instanceof OutlineMeasurement ) {

                // Tell the outline measurement to set the outline spline:
                OutlineMeasurement m = (OutlineMeasurement)get(i);
                m.setUseCircleSpline(use);

            }

        }

    }

    /** Sets the method of Fourier outline anaylsis.
     * @param method Should be one of the methods defined in the jmorph.measurements.Outline class.
     */
    public void setFourierAnalysisMethod(int method) {
        // Loop over each measurement:
        for (int i=0 ; i<size() ; i++ ) {
            // Check if the measurement is an outline measurement:
            if ( get(i) instanceof OutlineMeasurement ) {
                // Tell the outline measurement to set the method of Fourier outline analysis:
                OutlineMeasurement m = (OutlineMeasurement)get(i);
                m.setFourierAnalysisMethod(method);
            }
        }
    }

    /** Sets all measurements' painting point width.
     * @param w The point width.
     */
    public void setPointWidth(int w) {
        for ( int i=0 ; i<size() ; i++ ) {
            Measurement m = get(i);
            m.setPointWidth(w);
        }
    }

    /** Sets all measurements' painting line width.
     * @param w The line width.
     */
    public void setLineWidth(int w) {
        for ( int i=0 ; i<size() ; i++ ) {
            Measurement m = get(i);
            m.setLineWidth(w);
        }
    }

    /** Sets a measurement's painting colour(s).
     * @param im The index of the measurement to use.
     * @param col1 The new primary painting colour for the measurement.
     * @param col2 The new secondary painting colour for the measurment.
     */
    public void setMeasurementColor(int im, Color col1, Color col2) {
        Measurement m = get(im);
        m.setPrimaryColour(col1);
        if (m.usesSecondaryColour()) {
            m.setSecondaryColour(col2);
        }
    }
    
    // -------------------- Public Methods --------------------

    /** Returns the number of measurements in the list.
     * @return  */
    public int size() {
        return vector.size();
    }

    /** Removes a measurement from the list.
     * @param i The index of the measurement to remove.
     */
    public void remove(int i) {
        vector.remove(i);
    }

    /** Adds a new measurement object to the end of the measurement list.
     * @param typeString The measurement type to add.
     * @param name The name for the new measurement.
     * @return True if the type string was recognized.
     */
    public boolean addNew(String typeString, String name) {

        // Create a new measurement:
        Measurement m = makeNew(typeString,name);

        // Check it got created:
        if (m==null) {
            return false;
        } else {
            // Add it to the list:
            add(m);
            // Return successfully:
            return true;
        }

    }

    /** Returns an array of strings containing the names and type descriptions of the measurements.
     * Each array element is a string of the form "name (type)".
     * @return 
     */
    public String[] nameAndTypeList() {

        String[] names = new String[size()];

        // Loop over each measurement:
        for (int i=0 ; i<size() ; i++ ) {

            // Get the ith measurement:
            Measurement m = get(i);

            // Place the ith name and type description into the array of strings:
            names[i] = m.getName() + " (" + m.typeString() + ")";

        }

        return names;

    }

    /** Provides a mask for a specified measurement type.
     * @param typeStrings The measurement types to mask
     * (use the getTypes method to obtain a list of the available type strings).
     * @return mask The mask indices.
     */
    public int[] typeStringMask(String[] typeStrings) {

        // Loop through the measurements looking for the specified object types:
        // (the first time we just count the number found)
        int n = 0; // counter for measurements found fitting the specified type
        for (int i=0 ; i<size() ; i++) {
            for (String typeString : typeStrings) {
                if (typeString.compareTo(get(i).typeString()) == 0) {
                    n++; // increments counter
                    break; // from inner for loop over j
                }
            }
        }

        // Initialize the output:
        int[] mask = new int[n];

        // Loop through the measurements looking for the specified object types:
        // (the second time we add to the mask array)
        n = 0; // counter for measurements found fitting the specified type
        for (int i=0 ; i<size() ; i++) {
            for (String typeString : typeStrings) {
                if (typeString.compareTo(get(i).typeString()) == 0) {
                    mask[n] = i; // adds index into mask array
                    n++; // increments counter (happens second because first array index is 0 in Java)
                    break; // from inner for loop over j
                }
            }
        }

        // Return the mask array:
        return mask;

    }

    /** Reads measurement definitions from a file into the measurement list.
     * The first line of the file should specify the number of measurement definitions.
     * Each of the following lines contains a measurement type description followed by a name.
     * Only certain measurement type descriptions are recognized:
     *   "length", "linear",
     *   "angle", "angular",
     *   "area", "areal",
     *   "spline", "splined",
     *   "outline","fourier",
     *   "landmark", "point",
     *   "separation", "spacing",
     *   "radius", "radial".
     * @param file The file to read.
     * @return A message describing a file reading error (null if read successfully).
     */
    public String readFile(File file) {

        String textLine; // string for holding a line in the file
        StringTokenizer tokenizer;

        // Open the file for reading:
        BufferedReader reader = FileUtils.openForReading(file);
        if (reader==null) { return "Could not find the specified file."; }

        // Read the number of measurements:
        textLine = FileUtils.readLine(reader);
        if (textLine==null) { return "Failed to read the file"; }
        tokenizer = new StringTokenizer(textLine);
        textLine = tokenizer.nextToken(); // extracts the first token
        int nLines;
        try {
            nLines = Integer.parseInt(textLine.trim()); // converts to integer
        } catch (NumberFormatException e) {
            try { reader.close(); } catch (IOException ee) {}
            return "The number of measurements must be specified on the first line of the file.";
        }

        // Initialize the measurement list (clear it):
        vector.clear();
        
        // Loop over nLines lines in the file:
        for (int i=0 ; i<nLines ; i++ ) {

            // Read the current line:
            textLine = FileUtils.readLine(reader);
            if (textLine==null) { return "Failed to read the file. Make sure there are enough lines in the file."; }
            
            // Extract the parameters (type and name) for the current measurement:
            textLine = textLine.trim();
            String[] ss = textLine.split("[ ]+",2);
            String typeString = ss[0]; // measurement type
            String name = ss[1]; // measurement name = everything after the type specifier

            // Add the measurement to the list:
            Boolean ok = addNew(typeString.trim(),name);
            if (!ok) {
                try { reader.close(); } catch (IOException ee) {}
                vector.clear();
                return "Unknown measurement type: " + typeString + ".";
            }
        }

        // Close the file:
        try { reader.close(); } catch (IOException e) {}

        // Return succesfully:
        return null;

    }

    /** Writes the measurements to a file.
     * @param file The file to write to.
     * @return True if writing was successfull.
     */
    public boolean writeFile(File file) {

        if (size()==0) { return false; }

        String textLine; // string for holding a line in the file

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) { return false; }

        // Write the number of measurements:
        textLine = Integer.toString(size());
        if ( !FileUtils.writeLine(writer,textLine) ) { return false; }

        // Loop over each measurement:
        for (int i=0 ; i<size() ; i++ ) {

            // Write the current line:
            Measurement m = get(i);
            textLine = m.typeString();
            if (textLine == null) { // unknown type
                FileUtils.close(writer);
                return false;
            }
            textLine = textLine + " " + m.getName();
            if ( !FileUtils.writeLine(writer,textLine) ) { return false; }

        }

        // Close the file:
        FileUtils.close(writer);

        // Return succesfully:
        return true;

    }

    /** Returns a text string with the measurement names typeset within it,
     * separated by commas and with "Sample," at the start and no newline character(s).
     * @return 
     */
    public String headerForExportCSV() {

        if (size()==0) { return null; }

        String t = "Sample"; // string containing measurement names separated by commas

        // Loop over each measurement in the list:
        for ( int i=0 ; i<size() ; i++ ) {

            // Extract the ith measurement:
            Measurement m = get(i);

            // Add the name of the measurement to the output, separating the names by commas:
            t = t + "," + m.nameForExportCSV();

        }
        
        // Return the string:
        return t;
        
    }

    /** Returns a text string with the measurement information typeset within it, separated by commas.
     * @param factor A factor for calibrating the measurements.
     * @param trans A transform for calibrating the measurements.
     * @return The text string for writing to a CSV file.
     */
    public String writeMeasurementsCSV(double factor, AffineTransform trans) {

        if (size()==0) { return null; }

        String t = null; // string containing measurements separated by commas

        // Loop over each measurement in the list:
        for ( int i=0 ; i<size() ; i++ ) {

            // Get the ith measurement:
            Measurement m = get(i);

            // Get the value of the calculation as a string for export and
            // add it to the output, separating the values by commas:
            String sfe = m.calculateStringForExportCSV(factor,trans);
            if (sfe==null) { return null; }
            if (i==0) {
                t = sfe;
            } else {
                t += sfe;
            }

            // Don't bother the the final comma on the last measurement:
            if ( i < size()-1 ) {
                t += ",";
            }

        }

        // Return the string:
        return t;

    }

    /** Marks all measurements in the list.
     * @param mark The value to mark all the measurements with.
     */
    public void markMeasurements(boolean mark) {
        for ( int i=0 ; i<size() ; i++ ) {
            Measurement m = get(i);
            m.setMarked(mark);
        }
    }

    /** Marks a measurement in the list.
     * @param im The index of the measurement to mark.
     * @param mark The value to mark the measurement with.
     */
    public void markMeasurement(int im, boolean mark) {
        Measurement m = get(im);
        m.setMarked(mark);
    }

    /** Calls the runBeforePainting method of all measurements in the list.
     * @param measuring Set this to true if currently measuring.
     */
    public void runBeforePainting(boolean measuring) {
        for (int i=0 ; i<size() ; i++ ) {
            Measurement m = get(i);
            m.runBeforePainting(measuring);
        }
    }

    // -------------------- Private Methods --------------------

    /** Adds a measurement to the list.
     * @param m The measurement object to add.
     */
    private void add(Measurement m) {
        vector.add(m);
    }

    /** Takes a string specifying a measurement and creates a new instance
     * of a particular measurement object. The following strings are known types:
     *   "length", "linear",
     *   "angle", "angular",
     *   "area", "areal",
     *   "spline", "splined",
     *   "outline","fourier",
     *   "landmark", "point",
     *   "separation", "spacing",
     *   "radius", "radial"
     * @param typeString The string to convert.
     * @param name A name for the new measurement.
     * @return A new measurement object (null if typeString doesn't match any of above).
     */
    private static Measurement makeNew(String typeString, String name) {
        // Check the type:
        Measurement m;
        if (typeString.compareToIgnoreCase("ANGLE")==0) {
            m = new AngleMeasurement();
        } else if (typeString.compareToIgnoreCase("ANGULAR")==0) {
            m = new AngleMeasurement();
        } else if (typeString.compareToIgnoreCase("AREA")==0) {
            m = new AreaMeasurement();
        } else if (typeString.compareToIgnoreCase("AREAL")==0) {
            m = new AreaMeasurement();
        } else if (typeString.compareToIgnoreCase("SPLINE")==0) {
            m = new SplineMeasurement();
        } else if (typeString.compareToIgnoreCase("SPLINED")==0) {
            m = new SplineMeasurement();
        } else if (typeString.compareToIgnoreCase("OUTLINE")==0) {
            m = new OutlineMeasurement();
        } else if (typeString.compareToIgnoreCase("FOURIER")==0) {
            m = new OutlineMeasurement();
        } else if (typeString.compareToIgnoreCase("LANDMARK")==0) {
            m = new LandmarkMeasurement();
        } else if (typeString.compareToIgnoreCase("POINT")==0) {
            m = new LandmarkMeasurement();
        } else if (typeString.compareToIgnoreCase("LENGTH")==0) {
            m = new LengthMeasurement();
        } else if (typeString.compareToIgnoreCase("LINEAR")==0) {
            m = new LengthMeasurement();
        } else if (typeString.compareToIgnoreCase("RADIUS")==0) {
            m = new RadiusMeasurement();
        } else if (typeString.compareToIgnoreCase("RADIAL")==0) {
            m = new RadiusMeasurement();
        } else if (typeString.compareToIgnoreCase("SEPARATION")==0) {
            m = new SeparationMeasurement();
        } else if (typeString.compareToIgnoreCase("SPACING")==0) {
            m = new SeparationMeasurement();
        } else if (typeString.compareToIgnoreCase("COUNT")==0) {
            m = new CountMeasurement();
        } else if (typeString.compareToIgnoreCase("COUNTER")==0) {
            m = new CountMeasurement();
        } else {
            // Unknown type so return unsuccessfully:
            return null;
        }

        // Set the name and return the measurement:
        m.setName(name);
        return m;

    }

}
