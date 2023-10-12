package jmorph;

import fileio.FileUtils;
import geometry.MyPoint2D;
import geometry.MyPoint2DVector;
import gui.HasImage;
import java.awt.Color;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import jmorph.measurements.CalibrationMeasurement;
import jmorph.measurements.Measurement;
import jmorph.measurements.OriginMeasurement;
import jmorph.measurements.ZoomMeasurement;

/** A sample on which to take morphometric measurements.
 * @author Peter Lelievre
 */
public final class Sample {

    // ------------------ Properties -------------------

    private HasImage hasImage = null; // the image and associated file
    private ZoomMeasurement zoom = null; // the zoom information
    private CalibrationMeasurement calibration = null; // the calibration information
    private OriginMeasurement origin = null; // the origin
    private double calibrationDistance = Measurement.NULL_CALIBRATION_DISTANCE; // the user-inputted calibration length
    private MeasurementVector measurementList = null; // the list of measurements for this sample
    private int id = 0; // this is used to distinguish between samples if a multi-sample image is split

    // ------------------ Constructors -------------------

    public Sample() {} // Should only be used by deep copy method.
    
    public Sample(File f, boolean readNow) {
        hasImage = new HasImage(f,readNow);
        initialize();
    }
    
    public Sample(File f, HasImage h) {
        hasImage = h; // a copy of the pointer only
        initialize();
    }
    
    private void initialize() {
        zoom = new ZoomMeasurement();
        zoom.setName("Zoom");
        calibration = new CalibrationMeasurement();
        calibration.setName("Calibration");
        origin = new OriginMeasurement();
        origin.setName("Origin");
    }
    
    // -------------------- Deep Copy --------------------
    
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public Sample deepCopy(boolean deepHasImage) {
        Sample copy = new Sample();
        if (deepHasImage) {
            copy.hasImage = this.hasImage.deepCopy(); // deep copy
        } else {
            copy.hasImage = this.hasImage; // shallow copy
        }
        copy.zoom = this.zoom.deepCopy();
        copy.calibration = this.calibration.deepCopy();
        copy.origin = this.origin.deepCopy();
        copy.calibrationDistance = this.calibrationDistance;
        copy.measurementList = this.measurementList.deepCopy();
        copy.id = this.id;
        return copy;
    }

    // -------------------- Getters --------------------
    
    /** Returns a pointer to the HasImage object.
     * @return  */
    public HasImage getHasImage() {
        return hasImage;
    }
    
    /** Returns the file for the sample image.
     * @return  */
    public File getFile() {
        return hasImage.getFile();
    }
    
    /** Returns the image associated with the file or null if an error occurs.
     * @return  */
    public BufferedImage getImage() {
        return hasImage.getImage();
    }

    ///** Returns the tried state for the sample image.
    // * @return  */
    //public boolean getTried() {
    //    return hasImage.getTried();
    //}
    
    /** Getter for the measurement list associated with the sample.
     * @return  */
    public MeasurementVector getMeasurementList() { return measurementList; }

    /** Getter for the calibration distance entered by the user (in real-space units).
     * @return  */
    public double getCalibrationDistance() { return calibrationDistance; }

    /** Getter for the Zoom object which specifes how much of the sample image should be displayed.
     * @return  */
    public ZoomMeasurement getZoom() { return zoom; }

    /** Getter for the Calibration object.
     * That object, along with the Origin object, allows transformation
     * from sample image pixel coordinates to real-space measurement coordinates.
     * @return 
     */
    public CalibrationMeasurement getCalibration() { return calibration; }

    /** Getter for the Origin object.
     * That object, along with the Calibration object, allows transformation
     * from sample image pixel coordinates to real-space measurement coordinates.
     * @return 
     */
    public OriginMeasurement getOrigin() { return origin; }

    /** Getter for the MyPoint2D object containing the origin coordinates for the sample.
     * @return The origin coordinates or null if the user has not defined the origin.
     */
    public MyPoint2D getOriginPoint() {

        // Check if the origin has been measured:
        if (!hasOrigin()) { return null; }

        // Extract the user-specified (clicked) origin point:
        MyPoint2DVector coords = origin.getCoordinates();
        return coords.get(0);

    }

    // -------------------- Setters --------------------
    
    public void setID(int i) { id = i; }

    /** Setter for the measurement list associated with the sample.
     * @param mList */
    public void setMeasurementList(MeasurementVector mList) { measurementList = mList; }

    /** Setter for the calibration distance entered by the user.
     * @param distance The calibration distance (in real-space units).
     */
    public void setCalibrationDistance(double distance) {
        // Distance must be >0:
        if (distance<=0) { return; }
        calibrationDistance = distance;
    }

    /** Sets the outline resampling power.
     * @param p The new value for the resampling power.
     */
    public void setResamplingPower(int p) {
        // Tell the measurement list to set the resampling power:
        measurementList.setResamplingPower(p);
    }

    /** Sets the highest Fourier coefficients used in outline reconstructions.
     * @param n The new highest coefficient to use.
     */
    public void setHighestFFTCoefficient(int n) {
        // Tell the measurement list to set the number of FFT coefficients:
        measurementList.setHighestFFTCoefficient(n);
    }

    /** Sets the normalization index used in outline analyses.
     * @param n The new normalization index value to use.
     */
    public void setNormalizationIndex(int n) {
        // Tell the measurement list to set the normalization index:
        measurementList.setNormalizationIndex(n);
    }

    /** Sets the type of spline used for spline outline measurements.
     * @param use Set to true to use a cirle-preserving spline, false for a Kochanek–Bartels spline.
     */
    public void setUseCircleSpline(boolean use) {
        // Tell the measurement list to set the outline spline:
        measurementList.setUseCircleSpline(use);
    }

    /** Sets the method of Fourier outline anaylsis.
     * @param method Should be one of the methods defined in the jmorph.measurements.Outline class.
     */
    public void setFourierAnalysisMethod(int method) {
        // Tell the measurement list to set the method of Fourier outline analysis:
        measurementList.setFourierAnalysisMethod(method);
    }

    /** Sets a sample's painting point width for all measurements.
     * @param w The point width.
     */
    public void setPointWidth(int w) {
        zoom.setPointWidth(w);
        calibration.setPointWidth(w);
        origin.setPointWidth(w);
        measurementList.setPointWidth(w);
    }

    /** Sets a sample's painting line width for all measurements.
     * @param w The line width.
     */
    public void setLineWidth(int w) {
        zoom.setLineWidth(w);
        calibration.setLineWidth(w);
        origin.setLineWidth(w);
        measurementList.setLineWidth(w);
    }

//    /** Sets a sample's painting point size for the calibration origin measurement.
//     * @param w The point width.
//     */
//    public void setOriginWidth(int w) {
//        origin.setPointWidth(w);
//    }

    /** Sets a sample's painting colour for calibration measurements (Calibration and Origin).
     * @param col The new painting colour.
     */
    public void setCalibrationColour(Color col) {
        calibration.setPrimaryColour(col);
        origin.setPrimaryColour(col);
    }

    /** Sets a measurement's painting colour(s).
     * @param im The index of the measurement to use.
     * @param col1 The new primary painting colour for the measurement.
     * @param col2 The new secondary painting colour for the measurment.
     */
    public void setMeasurementColour(int im, Color col1, Color col2) {
        measurementList.setMeasurementColor(im,col1,col2);
    }

    // -------------------- Public Methods --------------------
    
    /** Returns true if the file for this sample is the same as the supplied file.
     * @param file
     * @return  */
    public boolean compareFile(File file) {
        return hasImage.compareFile(file);
    }

    /** Returns the short name of the sample (the file name minus path and extension, plus any id>0).
     * @return  */
    public String shortName() {
        String name = hasImage.getName();
        if (id>0) { name = name + " (" + id + ")"; }
        return name;
    }

    /** Returns the long name of the sample (the file name minus extension, plus any id>0).
     * @return  */
    public String longName() {
        String name = hasImage.getRoot();
        if (id>0) { name = name + " (" + id + ")"; }
        return name;
    }
    
    /** Returns a string with the file URI in it, or "null".
     * @return  */
    public String fileURIString() {
        return hasImage.fileURIString();
    }

    /** Returns a string that holds the value of the calibration distance.
     * If the calibration distance has not been entered then returns "not supplied yet".
     * @return 
     */
    public String calibrationDistanceString() {
        // Check for null length value:
        if (calibrationDistance == Measurement.NULL_CALIBRATION_DISTANCE) {
            return "not supplied yet";
        } else {
            // Convert double to string:
            return Double.toString(calibrationDistance);
        }
    }

    /** Clears the zoom box for this sample. */
    public void clearZoom() {
        zoom.clear(); // clears the zoom Measurement object
    }

    /** Returns true if the sample image has been zoomed.
     * @return  */
    public boolean isZoomed() {
        return zoom.isMeasured();
    }

    /** Returns true if the sample image has been calibrated.
     * The sample image is considered to have been calibrated if
     * 1) the Calibration measurement has been defined (the user clicked on two points), and
     * 2) the calibration distance has been defined (ther user entered a real-space distance).
     * @return 
     */
    public boolean isCalibrated() {
        return (  calibration.isMeasured()
                && calibrationDistance!=Measurement.NULL_CALIBRATION_DISTANCE );
    }

    /** Returns true if the origin for the sample image has been specified.
     * @return  */
    public boolean hasOrigin() {
        return ( origin.isMeasured() );
    }

    /** Returns the calibration factor to multiply the sample measurements by to calibrate them.
     * The sample measurements are in sample image pixel units.
     * The calibration factor scales those to real-space units.
     * If the sample has not been calibrated then Measurement.NULL_CALIBRATION_LENGTH is returned.
     * @return  */
    public double calibrationFactor() {
        
        // Check the sample has been calibrated:
        if (!isCalibrated()) {
            return Measurement.NULL_CALIBRATION_DISTANCE;
        }

        // Calculate the measurement:
        double calc = calibration.length();

        // Return the calibration factor:
        return calibrationDistance / calc; // = (real-space length) / (image pixel length)

    }

    /** Calculates the calibration transform for measurement points.
     * This transforms from sample image pixel coordinates to real-space measurements.
     * The factor returned from method calibrationFactor is used as the scaling
     * and the origin point is used for translation.
     * @return trans The affine transform, or null if the sample has not been
     * calibrated or the origin has not been specified.
     */
    public AffineTransform calibrationTransform() {
        // Check that both the calibration factor and the origin have been defined:
        if ( !isCalibrated() || !hasOrigin()) { return null; }
        // Get the origin and calibration factor:
        MyPoint2D p0 = getOriginPoint();
        double factor = calibrationFactor();
        /* New transformations are added to the right of the matrix
         * and therefore come earlier, which is completely non-intuitive.
         * Hence, I have to add them in the opposite order in which they
         * should be performed. */
        AffineTransform trans = new AffineTransform();
        // Multiply by calibration factor:
        trans.scale(factor,factor);
        // Subtract origin (which is in sample image pixel coordinates):
        trans.translate(-p0.getX(),-p0.getY());
        // Return the transform
        return trans;
    }
    
    /** Returns a CSV text string with the measurement information typeset within it and no newline character(s).
     * @return  */
    public String writeMeasurementsCSV() {

        // Write the measurements in the measurement list:
        AffineTransform trans = calibrationTransform();
        String t = measurementList.writeMeasurementsCSV(calibrationFactor(),trans);
        if (t==null) { return null; }

        // Add sample name at start of the string and return:
        t = longName() + "," + t;
        return t;

    }

    /** Exports the sample outline to a file, if it has been measured.
     * The file name is called [sample]_[measurement].txt" where [sample] is the
     * original name of the sample image file (minus extension) and
     * [measurement] is the name of the area, spline or outline measurement used for the outline.
     * @param path Path name for saving the outline file to.
     * @param index Index of the outline (area, spline or outline measurement) in the measurement list.
     * @return True if the file is written successfully.
     */
    public boolean exportOutline(File path, int index) {

        // Make sure this sample has been calibrated and the origin defined:
        if ( !isCalibrated() || !hasOrigin() ) { return false; }

        // Dialog title:
        //String title = "Export Sample Outlines";

        // Set the name of the file:
        String fileName = path.getAbsolutePath()
                + File.separator
                + hasImage.getName()
                + "_"
                + measurementList.get(index).getName()
                + ".txt";

        // Open the file for writing:
        File f = new File(fileName);
        BufferedWriter writer = FileUtils.openForWriting(f);
        if (writer==null) { return false; }

        // Extract the coordinates of the required measurement:
        MyPoint2DVector coords = measurementList.get(index).getCoordinates();

        // Get the required transform:
        AffineTransform trans = calibrationTransform();

        // Write the coordinates:
        for ( int i=0 ; i<coords.size() ; i++ ) {

            // Extract the ith coordinate point into a new point object:
            MyPoint2D p = coords.get(i).deepCopy();

            // Transform the coordinate point on the new point object:
            p.transform(trans);

            // Write the point to the file:
            String textLine = 
                    Double.toString(p.getX()) + "  " +
                    Double.toString(p.getY());
            if ( !FileUtils.writeLine(writer,textLine) ) { return false; }
            
        }

        // Close the file for writing:
        FileUtils.close(writer);
        
        // Return successfully:
        return true;

    }

    /** Marks all measurements in the sample's measurement list.
     * @param mark The value to mark all the measurements with.
     */
    public void markMeasurements(boolean mark) {
        measurementList.markMeasurements(mark);
    }

    /** Marks a measurement in the sample's measurement list.
     * @param im The index of the measurement to mark.
     * @param mark The value to mark the measurement with.
     */
    public void markMeasurement(int im, boolean mark) {
        measurementList.markMeasurement(im,mark);
    }

    /** Marks the sample's Zoom measurement.
     * @param mark The value to mark the Zoom measurement with.
     */
    public void markZoom(boolean mark) {
        zoom.setMarked(mark);
    }

    /** Marks the sample's Calibration measurement.
     * @param mark The value to mark the Calibration measurement with.
     */
    public void markCalibration(boolean mark) {
        calibration.setMarked(mark);
    }

    /** Marks the sample's Origin measurement.
     * @param mark The value to mark the Origin measurement with.
     */
    public void markOrigin(boolean mark) {
        origin.setMarked(mark);
    }

}