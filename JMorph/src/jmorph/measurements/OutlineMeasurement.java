package jmorph.measurements;

import dialogs.Dialogs;
import geometry.MyPoint2D;
import geometry.MyPolygon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import jmorph.hangle.HAngle;
import jmorph.hangle.HCurve;
import jmorph.maths.Complex;
import jmorph.maths.ComplexVector;
import jmorph.signalprocessing.FFT;
import jmorph.splines.PeriodicCubicSpline;
import jmorph.splines.PeriodicSegments;
import paint.PaintingUtils;

/** An outline measurement that allows Fourier outline analysis.
 * The area, outline length, clockwise/counterclockwise measurement direction
 * and coefficients from a Fourier outline analysis are calculated.
 * @author Peter Lelievre
 */
public class OutlineMeasurement extends SplineMeasurement {

    // ------------------ Properties -------------------

    public static final int FOURIER_ANALYSIS_METHOD_NONE = 0; /** The value specifying that no Fourier outline analysis is to be performed. */
    public static final int FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA = 1; /** The value specifying that the Fourier outline analysis should work on radius-vs-theta data. */
    public static final int FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH = 2; /** The value specifying that the Fourier outline analysis should work on tangent-vs-arclength data (as per hangle). */

    public static final int OUTLINE_RESAMPLING_POWER_DEFAULT = 6; /** The default resampling power (of 2). */
    public static final int OUTLINE_HIGHEST_FFT_COEFFICIENT_DEFAULT = 10; /** The default highest Fourier coefficient to use when reconstructing the outline. */
    public static final int OUTLINE_NORMALIZATION_INDEX_DEFAULT = 0; /** The default normalization index (an hangle parameter). */
    public static final int FOURIER_ANALYSIS_METHOD_DEFAULT = FOURIER_ANALYSIS_METHOD_NONE; /** The default method of Fourier outline analysis. */
    
    private ComplexVector fourierCoefficients = null; /** Fourier coefficients for the (theta,radius) information. */
    private double outlineLength = -1.0; /** This is required when reconstructing the outline using hcurve. */
    private int resampPower = OUTLINE_RESAMPLING_POWER_DEFAULT; /** The number of resampled points is 2^resampPower. Should be less than Spline.INTERP_POWER. */
    private int highestFFTCoefficient = OUTLINE_HIGHEST_FFT_COEFFICIENT_DEFAULT; /** The highest Fourier coefficient to use when reconstructing the outline. */
    private int normalizationIndex = OUTLINE_NORMALIZATION_INDEX_DEFAULT; /** The normalization index (an hangle parameter). */
    private int fourierAnalysisMethod = FOURIER_ANALYSIS_METHOD_DEFAULT; /** The method of Fourier outline analysis to use. */
    private MyPolygon coordsResamp = null; /** Resampled coordinate points. */
    private MyPolygon coordsRecon = null; /** Coordinate points reconstructed from the Fourier outline analysis. */
    private double[] thetaResamp = null; /** Interpolated (theta,radius) information. */
    private double[] radiusResamp = null; /** Interpolated (theta,radius) information. */
    private double[] radiusRecon = null; /** Radius information reconstructed from the Fourier analysis. */

    // ------------------- Constructor ------------------

    public OutlineMeasurement() {
        super();
    }

    // ------------------- Deep Copy ------------------

    /** Deep copies the object.
     * @return A new object copied from this one.
     */
    @Override
    public OutlineMeasurement deepCopy() {
        // Create the new object:
        OutlineMeasurement out = new OutlineMeasurement();
        // Perform default deep copy:
        super.deepCopyTo(out);
        // Clear all the outline analysis properties:
        clearOutlineAnalysis();
        // Return the object:
        return out;
    }

    // -------------------- Setters -------------------

    /** Sets the resampling power.
     * The number of resampled points is 2^resampPower.
     * @param p The resampling power. Should be less than Spline.INTERP_POWER.
     */
    public void setResamplingPower(int p){
        // Make sure the power is positive:
        if (p<=0) { return; }
        // Make sure the power is less than INTERP_POWER:
        if (p>=INTERP_POWER) { return; }
        // Set the value:
        resampPower = p;
        // Clear the resampling information and anything that relies on it:
        clearResamp();
    }

    /** Sets the higest Fourier coefficient to use when reconstructing the outline.
     * @param n The highest coefficient. Should be no greater than the resampling power / 2.
     */
    public void setHighestFFTCoefficient(int n){
        // Make sure the number is non-negative:
        if (n<0) { return; }
        // Make sure the number is no greater than the resampling power / 2:
        int nmax = (int)Math.pow( 2 , resampPower-1 ); // nmax = 2^(p-1) = 2^p * 2^-1 = 2^p / 2
        if (n>nmax) { return; }
        // Set the value:
        highestFFTCoefficient = n;
        // Clear the Fourier outline reconstruction information and anything that relies on it:
        clearRecon();
        //clearFourier(); // the external run of hcurve relies on files written during the external run of hangle.
    }

    /** Sets the normalization index (an hangle parameter).
     * @param n The normalization index.
     */
    public void setNormalizationIndex(int n){
        // Make sure the number is non-negative:
        if (n<0) { return; }
        // Set the value:
        normalizationIndex = n;
        // Clear the Fourier outline reconstruction information and anything that relies on it:
        clearFourier();
    }

    /** Sets the method of Fourier outline analysis to use.
     * @param method The method to use. This should be one of
     * FOURIER_ANALYSIS_METHOD_NONE,
     * FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH
     * or FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA.
     */
    public void setFourierAnalysisMethod(int method){
        // Check the value:
        if ( method!=FOURIER_ANALYSIS_METHOD_NONE
                && method!=FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH
                && method!=FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA ) { return; }
        // Set the choice:
        fourierAnalysisMethod = method;
        // Clear the resampling information and anything that relies on it:
        clearResamp();
    }

    // -------------------- Implemented/Overridden Methods -------------------

    /** Clears all the outline interpolation properties and anything that depends on them. */
    @Override
    public void clearInterp(){
        coordsInterp = null;
        clearResamp();
    }

    /** Provides a description of the measurement type.
     * @return A description of the measurement type.
     */
    @Override
    public String typeString() {
        return "outline";
    }

    /** Provides the title for a dialog with instructions on how to measure the measurement.
     * @return The dialog title.
     */
    @Override
    public String instructionTitle() {
        return "Measure Outline " + name;
    }

    /** Provides the prompt for a dialog with instructions on how to measure the measurement.
     * @return The measurement instructions.
     */
    @Override
    public String instructionPrompt() {
        return "Click on many points around the outline and then hit the SPACE BAR.";
    }

    /** Checks if a measurement can be calibrated using the supplied calibration information.
     * @param factor The calibration factor.
     * @return True if the measurement can be calibrated, false otherwise.
     */
    @Override
    protected boolean canBeCalibrated(double factor, AffineTransform trans) {
        //return ( factor!=NULL_CALIBRATION_DISTANCE ); // needs factor only (THIS WAS PREVIOUS METHOD BEFORE ADDING AREA AND CENTROID INFORMATION TO CSV AND DISPLAY OUTPUT
        return ( factor!=NULL_CALIBRATION_DISTANCE && trans!=null ); // needs both factor and origin
    }
    protected boolean canBeCalibratedAnything(double factor, AffineTransform trans) {
        return ( factor!=NULL_CALIBRATION_DISTANCE );
    }
    protected boolean canBeCalibratedArea(double factor, AffineTransform trans) {
        return ( factor!=NULL_CALIBRATION_DISTANCE );
    }
    protected boolean canBeCalibratedCentroid(double factor, AffineTransform trans) {
        return ( factor!=NULL_CALIBRATION_DISTANCE && trans!=null ); // needs both factor and origin
    }
    protected boolean canBeCalibratedLength(double factor, AffineTransform trans) {
        return ( factor!=NULL_CALIBRATION_DISTANCE ); // needs factor only
    }

    /** Provides a string that holds the value of the calculated and calibrated measurement.
     * The intention is that this string should be used for display purposes.
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @param longDisplay Set to true to display more thorough information.
     * @return A string representation of the calculated and calibrated measurement.
     */
    @Override
    public String calculateStringForDisplay(double factor, AffineTransform trans, boolean longDisplay) {

        // Check for unmeasured measurement:
        if (!isMeasured()) {
            return "not measured";
        }

        // Check if anything can be calibrated:
        if (!canBeCalibratedAnything(factor,trans)) {
            return "sample not calibrated";
        }

        // Make sure all calculations are preformed as required before exporting:
        runBeforePainting(false); // any failure can be ignored because fourierCoefficients==null on failure

        // Initialize the return string before adding to it:
        String s = "";

        // Check if everything can be calibrated:
        if (!canBeCalibrated(factor,trans)) {
            s += System.lineSeparator() + "   (not everything can be displayed"
                    + System.lineSeparator() + "   because sample not calibrated)";
        }
        
        // Write the area:
        if (canBeCalibratedArea(factor,trans)) {
            double a = calibratedArea(factor);
            s = s + System.lineSeparator() + "   " + Float.toString((float)a) + " (area)";
        }

        // Write the centroid as (x,y):
        if (canBeCalibratedCentroid(factor,trans)) {
            MyPoint2D p = calculateCentroid();
            p.transform(trans);
            s = s + System.lineSeparator() + "   (" + Float.toString((float)p.getX()) + "," + Float.toString((float)p.getY()) + ") (centroid)";
        }

        // Write the outline length:
        if (canBeCalibratedLength(factor,trans)) {
            double len = calibratedLength(factor);
            s = s + System.lineSeparator() + "   " + Float.toString((float)len) + " (outline length)";
        }

        // Write the direction of measurement:
        if (coordsOrig.isClockwise()) {
            s += System.lineSeparator() + "   Measured counter-clockwise";
        } else {
            s += System.lineSeparator() + "   Measured clockwise";
        }

        // Return early if not displaying the long information.
        if (!longDisplay) { return s; }
        
        // Now write all the FFT coefficients:
        if (fourierCoefficients==null) {
            s += System.lineSeparator() + "   (Fourier analysis not performed)";
        } else {
            int n = highestFFTCoefficient + 1;
            s += System.lineSeparator() + "   Fourier coefficient amplitudes (normalized by 2nd):";
            double d2;
            if (fourierAnalysisMethod==FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH) {
                d2 = fourierCoefficients.get(0).abs();
                n -= 2;
            } else {
                d2 = 2.0*fourierCoefficients.get(2).abs();
            }
            for ( int j=0;j<n;j++ ) {
                int k;
                double d;
                if (fourierAnalysisMethod==FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH) {
                    k = j + 2;
                    d = fourierCoefficients.get(j).abs();
                } else {
                    k = j;
                    d = 2.0*fourierCoefficients.get(j).abs();
                }
                d /= d2;
                s = s + System.lineSeparator() + "      " + k + ": " + Float.toString((float)d);
            }
        }

        // Return the string:
        return s;

    }

    /** Provides a string with the measurement name and any additionally required characters.
     * The intention is that this string should be used when writing to a CSV file but commas should NOT be added.
     * @return A string containing the measurement name and any additionally required characters.
     */
    @Override
    public String nameForExportCSV() {

        // Add in the outline length to the output string:
        String s = getName() + " (area),(length),(clockwise)";

        // Add in the Fourier coefficient indices:
        int jmax = highestFFTCoefficient;
        if (fourierAnalysisMethod==FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH) {
            // If the specified highest FFT coefficient is less than 2 then we are keeping all coefficients:
            if (highestFFTCoefficient<2) {
                jmax = fourierCoefficients.size();
            }
        }
        int k;
        for ( int j=0 ; j<=jmax ; j++ ) { // the equals sign is important here because the highest coefficient is indexed from zero
            if (fourierAnalysisMethod==FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH) {
                k = j+2;
            } else {
                k = j;
            }
            //s = s + ",(Amp" + k + "),(Phase" + k + ")";
            s = s + ",(Real" + k + "),(Imag" + k + ")";
        }

        // Return the string:
        return s;
        
    }

    /** Provides a string that holds the value of the calculated and calibrated measurement.
     * The intention is that this string should be used when writing to a CSV file
     * (some extra commas should be added where required but NOT at the start or end).
     * @param factor A calibration factor for the measurement's coordinate points.
     * @param trans A calibration transform for the measurement's coordinate points.
     * @return A string representation of the calculated and calibrated measurement.
     */
    @Override
    public String calculateStringForExportCSV(double factor, AffineTransform trans) {

        String s = "";

        // Check for unmeasured measurement:
        if (isMeasured()) {

            // Make sure all calculations are preformed as required before exporting:
            if (runBeforePainting(false)) {
            
                // Write the area:
                if (canBeCalibratedArea(factor,trans)) {
                    double a = calibratedArea(factor);
                    s += Double.toString(a);
                } else {
                    s += "sample not calibrated";
                }
                s += ",";

                // Write the outline length:
                if (canBeCalibratedLength(factor,trans)) {
                    double len = calibratedLength(factor);
                    s += Double.toString(len);
                } else {
                    s += "sample not calibrated";
                }
                s += ",";
                
            } else {
                s += "angle-vs-theta not possible,,";
            }

            // Write the measurement direction to the string:
            s += !coordsOrig.isClockwise();

        } else { // not measured
            s = "not measured,,";
        }

        // Add in all of the Fourier coefficients (regardless of the highestFFTCoefficient specified):
        int jmax = highestFFTCoefficient;
        if (fourierAnalysisMethod==FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH) {
            // If the specified highest FFT coefficient is less than 2 then we are keeping all coefficients:
            if (highestFFTCoefficient<2) {
                jmax = fourierCoefficients.size();
            }
        }
        Complex com;
        for ( int j=0 ; j<=jmax ; j++ ) { // the equals sign is important here because the highest coefficient is indexed from zero
            if (fourierCoefficients==null) {
                s += ",,";
            } else {
                com = fourierCoefficients.get(j);
                //s = s + "," + com.abs() + "," + com.phase();
                s = s + "," + com.real() + "," + com.imag();
            }
        }
        
        // Return the string:
        return s;

    }

    /** This method should perform any calculations that are required prior to painting.
     * @param measuring True if this measurement is currently being measured, false otherwise.
     * @return Returns false if Fourier analysis method can not be used on the current sample.
     */
    @Override
    public boolean runBeforePainting(boolean measuring) {
        // Make sure all required outline analysis steps have been performed:
        isClosed = !measuring;
        if (coordsInterp==null) { fillInterp(); }
        if (!measuring) {
            if (coordsRecon==null) {
                if (!fillRecon()) {
                    return false;
                }
            }
        }
        return true;
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

        // Only paint some items if fully measured:
        if (isMeasured()) {

            // Calculate the centroid position:
            if (measuring) {
                if (coordsOrig==null) { fillCoordsOrig(); }
                p = coordsOrig.com();
            } else {
                if (coordsInterp==null) { fillInterp(); }
                p = coordsInterp.com(); // position of polygon centroid
            }

            // Draw a point at the polygon centroid
            g2.setPaint(getPrimaryColour());
            PaintingUtils.paintPoint(g2,trans,p,getPointWidth(),true);

        }

        // Always draw interpolated outline if it exists, whether measuring or not:
        GeneralPath path = new GeneralPath();
        if (coordsInterp==null) {
            // Draw an un-closed path around the measurement coordinates:
            coordinates.addToPath(path);
        } else {
            // Draw a closed path around the interpolated points:
            addInterpToPath(path);
        }

        // Transform the path from sample to panel coordinates:
        path.transform(trans);

        // Draw the path:
        g2.setPaint(getPrimaryColour());
        g2.draw(path);

        // Draw resampled coordinates as individual filled circles:
        MyPoint2D pt;
        if ( !measuring && fourierAnalysisMethod!=FOURIER_ANALYSIS_METHOD_NONE && coordsResamp!=null ) {
            g2.setPaint(getPrimaryColour());
            PaintingUtils.paintPoints(g2,trans,coordsResamp,getPointWidth(),true);
        }

        // Draw a path around the outline reconstructed from the FFT analysis:
        if ( !measuring && fourierAnalysisMethod!=FOURIER_ANALYSIS_METHOD_NONE && coordsRecon!=null) {
            g2.setPaint(getSecondaryColour());
            path = new GeneralPath();
            addReconToPath(path);
            path.transform(trans);
            g2.draw(path);
            PaintingUtils.paintPoint(g2,trans,coordsRecon.get(0),getPointWidth()+6,true);
        }

        // Draw measurement coordinates as individual filled circles:
        if (coordinates!=null) {
            g2.setPaint(Color.BLACK);
            PaintingUtils.paintPoints(g2,trans,coordinates,getPointWidth(),true);
            if (!measuring) {
                PaintingUtils.paintPoint(g2,trans,coordinates.get(0),getPointWidth()+4,true);
            }
        }

    }

    // -------------------- New Private/Protected Methods -------------------

    /** Clears all the outline resampling properties and anything that depends on them. */
    private void clearResamp(){
        coordsResamp = null;
        thetaResamp = null;
        radiusResamp = null;
        clearFourier();
    }

    /** Clears all the Fourier coefficients and associated properties. */
    private void clearFourier(){
        fourierCoefficients = null;
        outlineLength = -1.0;
        clearRecon();
    }

    /** Clears all the Fourier outline reconstruction properties. */
    private void clearRecon(){
        coordsRecon = null;
        radiusRecon = null;
    }

    /** Adds the reconstructed outline coordinates in the vector to a general path object for plotting.
     * The first point is added using path.moveTo() and the subsequent points are added using path.lineTo().
     * The path is then closed.
     * @param path The path to add the reconstructed outline points to.
     */
    private void addReconToPath(GeneralPath path) {

        if (coordsRecon==null) { return; } // nothing to add

        // Add the first point:
        MyPoint2D p = coordsRecon.get(0);
        path.moveTo((float)p.getX(),(float)p.getY());

        // Loop over the other points:
        for ( int j=1 ; j<coordsRecon.size() ; j++ ) {
            p = coordsRecon.get(j);
            path.lineTo((float)p.getX(),(float)p.getY());
        }

        // Close the path:
        path.closePath();

    }

    /** Provides the uncalibrated outline length.
     * @return The uncalibrated outline length.
     */
    private double calculateLength() {

        // Make sure required information exists:
        if (coordsInterp==null) { fillInterp(); }

        // Calculate the length of the outline:
        if (coordsInterp==null) {
            if (coordsOrig==null) { fillCoordsOrig(); }
            // Here we are using the original coordinates:
            return coordsOrig.length();
        } else {
            // Here we are using the spline interpolated coordinates:
            return outlineSpline.splineLength();
        }

    }

    /** Provides the calibrated outline length.
     * @return The calibrated outline length.
     */
    private double calibratedLength(double factor) {
        // Calculate the outline length:
        double len = calculateLength();
        // The outline length needs to be multipled by the factor:
        return factor*len;
    }

    /** Performs the outline resampling. */
    private boolean fillResamp() {

        // There are three possible ways to continue:
        switch(fourierAnalysisMethod) {
            case FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA:
                if (resampleRadiusVsTheta()) {
                    break;
                } else {
                    return false;
                }
            case FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH:
                resampleTangentVsArcLength();
                break;
            default:
                // Clear the information that depends on this information:
                clearResamp();
                return true;
        }

        // Check that the resampled coordinates are counter-clockwise:
        // (if they aren't then this is an error in the code!)
        if (coordsResamp==null) { return true; }
        if (coordsResamp.isClockwise()) {
            clearResamp();
        }
        return true;

    }

    /** Resamples the theta-vs-radius cubic spline at a number of equally spaced theta values. */
    private boolean resampleRadiusVsTheta(){

        // Make sure required information exists:
        if (coordsInterp==null) { fillInterp(); }
        if (coordsInterp==null) { return true; }

        // Clear the information that depends on this information:
        clearResamp();

        // Resampling requires a second interpolation.
        // The first step is to calculate radius vs. theta for the interpolated points:
        int n = coordsInterp.size();
        double[] theta = new double[n];
        double[] radius = new double[n];
        MyPoint2D pc = coordsInterp.com(); // centre of mass of the outline
        MyPoint2D p;
        double dx,dy;
        for ( int j=0 ; j<n ; j++ ) {
            p = coordsInterp.get(j);
            // The radius is the distance between the coordinate point and the centroid:
            radius[j] = MyPoint2D.distanceBetweenPoints(p,pc);
            // Theta is the polar angle:
            dx = p.getX() - pc.getX();
            dy = p.getY() - pc.getY();
            theta[j] = Math.atan2(dy,dx);
        }

        // Figure out if the outline is clockwise or counterclockwise:
        boolean clockwise = coordsInterp.isClockwise();

        // If clockwise then need to make the values monotonically increasing
        // (except for a single jump):
        if (clockwise) {
            Double[] temp = new Double[n];
            for ( int j=0 ; j<n ; j++ ) {
                temp[j] = theta[j];
            }
            for ( int j=0 ; j<n ; j++ ) {
                theta[j] = temp[n-j-1];
            }
            for ( int j=0 ; j<n ; j++ ) {
                temp[j] = radius[j];
            }
            for ( int j=0 ; j<n ; j++ ) {
                radius[j] = temp[n-j-1];
            }
            //temp = null;
        }

        // Find where the theta values cross over and make them monotonic:
        int it = PeriodicSegments.fixCrossOver(theta);
        if (it==-2) {
            //Dialogs.error(null,"You can not use radius vs. theta processing on this sample.","Error");
            return false;
        } else if (it==-1) {
            //Dialogs.codeError(null,"Monotonicity failed when resampling radius vs. theta");
            return false;
        }

        // Perform a cubic interpolation at spaced theta values:
        PeriodicCubicSpline cSpline = new PeriodicCubicSpline( n , theta[0] , theta[0] + 2.0*Math.PI , theta , radius );
        if (!cSpline.isDefined()) {
            //Dialogs.codeError(null,"Failed to create PeriodicCubic object when resampling radius vs. theta");
            return false;
        }
        n = (int)Math.pow(2,resampPower);
        double thetaStep = 2.0*Math.PI / n;
        coordsResamp = new MyPolygon();
        thetaResamp = new double[n];
        radiusResamp = new double[n];
        double r,x,y;
        double t = theta[0]; // always start at first user-supplied coordinate point
        for ( int j=0 ; j<n ; j++ ) {
            r = cSpline.interpolate(t);
            x = pc.getX() + r*Math.cos(t);
            y = pc.getY() + r*Math.sin(t);
            p = new MyPoint2D(x,y);
            coordsResamp.add(p);
            thetaResamp[j] = t;
            radiusResamp[j] = r;
            t += thetaStep;
        }
        
        // Return successfully:
        return true;

        /*
        // Write thetaResamp,radiusResamp to a file (THIS IS FOR VISUALIZATION PURPOSES AND SHOULD NOT BE MAINTAINED):
        String textLine;
        File file = new File("/Users/Peter/Desktop/tr.txt");
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) { return; }
        for (int i=0 ; i<radiusResamp.length ; i++ ) {
            t = thetaResamp[i];
            r = radiusResamp[i];
            textLine = Double.toString(t) + " " + Double.toString(r);
            if ( !FileUtils.writeLine(writer,textLine) ) { break; }
        }
        FileUtils.close(writer);
        file = new File("/Users/Peter/Desktop/xz.txt");
        writer = FileUtils.openForWriting(file);
        if (writer==null) { return; }
        for (int i=0 ; i<coordsResamp.size() ; i++ ) {
            p = coordsResamp.get(i);
            textLine = Double.toString(p.getX()) + " " + Double.toString(p.getY());
            if ( !FileUtils.writeLine(writer,textLine) ) { break; }
        }
        FileUtils.close(writer);
        */

    }

    /** Resamples the theta-vs-radius cubic spline at a number of equally spaced theta values. */
    private void resampleTangentVsArcLength(){

         // Make sure required information exists:
        if (coordsInterp==null) { fillInterp(); }
        if (coordsInterp==null) { return; }

        // Clear the information that depends on this information:
        clearResamp();

        // Resampling is as simple as extracting the interpolated points:
        coordsResamp = new MyPolygon();
        int n = (int)Math.pow(2,resampPower);
        int dn = (int)Math.pow(2,INTERP_POWER-resampPower);
        MyPoint2D p;
        boolean clockwise = coordsInterp.isClockwise();

        // Always add the first point:
        p = coordsInterp.get(0);
        coordsResamp.add(p);

        // Now add the rest of the points in a way that ensures the resampling is counter-clockwise:
        for ( int j=1 ; j<n ; j++ ) {
            // Extract the required point:
            if (clockwise) {
                p = coordsInterp.get( (n-j)*dn );
            } else {
                p = coordsInterp.get(     j*dn );
            }
            // Add it to the resampled coordinates:
            coordsResamp.add(p);
        }

    }

    /** Calculates the Fourier cooefficients. */
    private boolean fillFourier() {
        // There are three possible ways to continue:
        switch(fourierAnalysisMethod) {
            case FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA:
                if (calculateRadiusVsTheta()) {
                    break;
                } else {
                    return false;
                }
            case FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH:
                //calculateTangentVsArcLengthExternal();
                if (calculateTangentVsArcLength()) {
                    break;
                } else {
                    return false;
                }
            default:
                // Clear the information that depends on this information:
                clearFourier();
        }
        return true;
    }

    /** Performs FFT on radius vs theta function. */
    private boolean calculateRadiusVsTheta() {

         // Make sure required information exists:
        if ( coordsInterp==null ) { fillInterp(); }
        if ( coordsInterp==null ) { return true; }
        if ( coordsResamp==null || thetaResamp==null || radiusResamp==null ) {
            if (!fillResamp()) {
                return false;
            }
        }
        if ( coordsResamp==null || thetaResamp==null || radiusResamp==null ) { return true; }

        // Clear the information that depends on this information:
        clearFourier();

        // Calculate the centroid of the interpolated outline:
        MyPoint2D pc = coordsInterp.com();

        // Insert the double values into a complex object:
        int n = radiusResamp.length;
        Complex[] com = new Complex[n];
        for ( int j=0 ; j<n ; j++ ) {
            com[j] = new Complex( radiusResamp[j] , 0.0 );
        }

        // Calculate the FFT:
        Complex[] fc = FFT.fft(com);
        fourierCoefficients = new ComplexVector();
        fourierCoefficients.add(fc);
        
        // Return successfully:
        return true;

    }

    /** Performs FFT on tangent vs arc length function. */
    private boolean calculateTangentVsArcLength() {

         // Make sure required information exists:
        if (coordsResamp==null) {
            if (!fillResamp()) {
                return false;
            }
        }
        if (coordsResamp==null) { return true; }

        // Clear the information that depends on this information:
        clearFourier();

        // Calculate the FFT:
        HAngle.Out out = HAngle.run(coordsResamp,normalizationIndex);
        fourierCoefficients = out.coeffs;
        outlineLength = out.length;

        // Check for a warning message and display it if there is one:
        if (out.message!=null) {
            Dialogs.warning(null,out.message,"HAngle Warning");
        }
        
        // Return successfully:
        return true;

    }

    /** Reconstructs the outline from the Fourier coefficients. */
    private boolean fillRecon(){
        // There are three possible ways to continue:
        switch(fourierAnalysisMethod) {
            case FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA:
                if (reconstructRadiusVsTheta()) {
                    break;
                } else {
                    return false;
                }
            case FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH:
                //reconstructTangentVsArcLengthExternal();
                if (reconstructTangentVsArcLength()) {
                    break;
                } else {
                    return false;
                }
            default:
                // Clear the information that depends on this information:
                clearRecon();
        }
        return true;
    }

    /** Reconstructs the outline from the Fourier coefficients for the radius vs theta method. */
    private boolean reconstructRadiusVsTheta(){

         // Make sure required information exists:
        if ( coordsInterp==null ) { fillInterp(); }
        if ( coordsInterp==null ) { return true; }
        if ( thetaResamp==null ) {
            if (!fillResamp()) {
                return false;
            }
        }
        if ( thetaResamp==null ) { return true; }
        if ( fourierCoefficients==null ) {
            if (!fillFourier()) {
                return false;
            }
        }
        if ( fourierCoefficients==null ) { return true; }

        // Clear the information that depends on this information:
        clearRecon();

        // Copy the fourierCoefficients object so it can be overwritten below:
        int n = fourierCoefficients.size();
        Complex[] newFFT = new Complex[n];
        Complex cm;
        for ( int j=0 ; j<n ; j++ ) {
            cm = fourierCoefficients.get(j);
            newFFT[j] = new Complex( cm.real() , cm.imag() );
        }
        int nfft = n/2 + 1; // number of non-duplicate FFT coefficients

        // Keep only the first several FFT coefficients:
        for ( int j=(highestFFTCoefficient+1);j<nfft;j++ ) {
            newFFT[j] = new Complex(0.0,0.0);
            newFFT[n-j] = new Complex(0.0,0.0);
        }

        // Keep only the zeroth and one other of the coefficients:
/*      for ( int j=1;j<nfft;j++ ) {
            if ( j!=highestFFTCoefficient ) {
                newFFT[j] = new Complex(0.0,0.0);
                newFFT[n-j] = new Complex(0.0,0.0);
            }
        }
*/
        // Perform the inverse transform to get new radius values:
        Complex[] com = FFT.ifft(newFFT);
        radiusRecon = new double[n];
        for ( int j=0 ; j<n ; j++ ) {
            // There are imaginary values in the reconstruction due to machine precision problems. This can be safely ignored.
            // http://www.engineeringproductivitytools.com/stuff/T0001/PT10.HTM
            radiusRecon[j] = com[j].real();
        }

        // Convert the new (theta,radius) information to (x,y) coordinates:
        coordsRecon = new MyPolygon();
        MyPoint2D pc = coordsInterp.com();
        double x,y,t,r;
        for ( int j=0;j<n;j++ ) {
            // Extract current theta and radius:
            t = thetaResamp[j];
            r = radiusRecon[j];
            // Translate from polar back to Cartesian coordinates:
            x = pc.getX() + r*Math.cos(t);
            y = pc.getY() + r*Math.sin(t);
            coordsRecon.add( new MyPoint2D(x,y) );
        }
        
        // Return successfully:
        return true;

    }

    /** Reconstructs the outline from the Fourier coefficients for the tangent vs arc length method. */
    private boolean reconstructTangentVsArcLength(){

         // Make sure required information exists:
        if (fourierCoefficients==null) {
            if (!fillFourier()) {
                return false;
            }
        }
        if (fourierCoefficients==null) { return true; }

        // Clear the information that depends on this information:
        clearRecon();

        // Perform the inverse transform to get new coordinates:
        coordsRecon = HCurve.run(fourierCoefficients,highestFFTCoefficient);

        // Transform the reconstructed outline coordinates:
        transformHCurveReconstruction(outlineLength);
        
        // Return successfully:
        return true;

    }

    /** The output coordinates from the hcurve program need to be transformed.
     * @param outlineLength The length of the outline.
     */
    private void transformHCurveReconstruction( double outlineLength ) {

        // Make sure required information exists:
        if ( coordsResamp==null ) { return; }

        // Scale the coordinates to undo the normalization:
        AffineTransform trans = new AffineTransform();
        double scal = outlineLength / (2.0*Math.PI);
        trans.scale(scal,scal);
        coordsRecon.transform(trans);

        // Line up the centre of masses:
        trans = new AffineTransform();
        MyPoint2D pc = coordsInterp.com();
        MyPoint2D qc = coordsRecon.com();
        double dx = pc.getX() - qc.getX();
        double dy = pc.getY() - qc.getY();
        trans.translate(dx,dy);
        coordsRecon.transform(trans);
        qc.transform(trans);

        // Rotate so that the first point of the outline lines up with the first selected point:
        if (normalizationIndex!=0) { return; } // don't rotate if normalizing the outline phase
        trans = new AffineTransform();
        MyPoint2D p0 = coordsInterp.get(0);
        MyPoint2D q0 = coordsRecon.get(0);
        double pt = Math.atan2( p0.getY()-pc.getY() , p0.getX()-pc.getX() );
        double qt = Math.atan2( q0.getY()-qc.getY() , q0.getX()-qc.getX() );
        trans.rotate( pt-qt , qc.getX() , qc.getY() );
        coordsRecon.transform(trans);

    }

}
