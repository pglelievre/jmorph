package jmorph;

import dialogs.Dialogs;
import gui.HasImage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import jmorph.measurements.Measurement;
import jmorph.measurements.OutlineMeasurement;
import jmorph.measurements.SplineMeasurement;

/** The JMorph Model component of the MVC architecture.
/** Contains all the model information: PCL, groups, sections.
 * @author Peter
 */
public class ModelManager {

    // Measurement options:
    private int outlineResamplingPower = OutlineMeasurement.OUTLINE_RESAMPLING_POWER_DEFAULT;
    private int outlineHighestFFTCoefficient = OutlineMeasurement.OUTLINE_HIGHEST_FFT_COEFFICIENT_DEFAULT;
    private int outlineNormalizationIndex = OutlineMeasurement.OUTLINE_NORMALIZATION_INDEX_DEFAULT;
    private boolean useCircleSpline = SplineMeasurement.USE_CIRCLE_SPLINE_DEFAULT;
    private int fourierAnalysisMethod = OutlineMeasurement.FOURIER_ANALYSIS_METHOD_DEFAULT;
    
    // Main model components:
    private SampleVector sampleVector = new SampleVector(); // holds the samples (image files, measurements, etc.)
    private MeasurementVector measurementVector = new MeasurementVector(); // holds the measurements that the user is working with
    
    // Getters that should be used sparingly by the controller:
    public void setSampleList(SampleVector s) { sampleVector = s; }
    public void setMeasurementList(MeasurementVector m) { measurementVector = m; }
    public boolean getUserCancelledWhileLoading() { return sampleVector.getUserCancelledWhileLoading(); }
    
    // Getters for the measurement options:
    public int getFourierAnalysisMethod() { return fourierAnalysisMethod; }
    public int getOutlineHighestFFTCoefficient() { return outlineHighestFFTCoefficient; }
    public int getOutlineNormalizationIndex() { return outlineNormalizationIndex; }
    public int getOutlineResamplingPower() { return outlineResamplingPower; }
    public boolean getUseCircleSpline() { return useCircleSpline; }
    
    // Checkers for the measurement options:
    public String checkOutlineResamplingPower(int p) {
        int pmax = SplineMeasurement.INTERP_POWER - 1;
        if ( p<4 || p>pmax ) {
            return "You must enter a single integer value on [4," + pmax + "].";
        } else {
            return null;
        }
    }
    public String checkOutlineHighestFFTCoefficient(int n) {
        int nmax = (int)Math.pow( 2 , getOutlineResamplingPower() - 1 ); // nmax = 2^(p-1) = 2^p * 2^-1 = 2^p / 2
        if ( n<0 || n>nmax ) {
            return "You must enter a single integer value on [0," + nmax + "].";
        } else {
            return null;
        }
    }
    public String checkOutlineNormalizationIndex(int n) {
        int nmax = getOutlineHighestFFTCoefficient();
        if ( n<0 || n>nmax ) {
            return "You must enter a single integer value on [0," + nmax + "].";
        } else {
            return null;
        }
    }

    // Setters for the measurement options (these also affect the SampleVector and MeasurementVector classes):
    public void setFourierAnalysisMethod(int method) {
        fourierAnalysisMethod = method;
        measurementVector.setFourierAnalysisMethod(method);
        sampleVector.setFourierAnalysisMethod(method);
    }
    public void setOutlineResamplingPower(int p) {
        if ( checkOutlineResamplingPower(p) != null ) { return; }
        outlineResamplingPower = p;
        // May have to reset the maximum FFT coefficient:
        int nmax = (int)Math.pow( 2 , p-1 ); // nmax = 2^(p-1) = 2^p * 2^-1 = 2^p / 2
        if ( outlineHighestFFTCoefficient > nmax ) {
            setOutlineHighestFFTCoefficient(nmax);
        }
        measurementVector.setResamplingPower(p);
        sampleVector.setResamplingPower(p);
    }
    public void setOutlineHighestFFTCoefficient(int n) {
        if ( checkOutlineHighestFFTCoefficient(n) != null ) { return; }
        outlineHighestFFTCoefficient = n;
        measurementVector.setHighestFFTCoefficient(n);
        sampleVector.setHighestFFTCoefficient(n);
    }
    public void setOutlineNormalizationIndex(int n) {
        if ( checkOutlineNormalizationIndex(n) != null ) { return; }
        outlineNormalizationIndex = n;
        measurementVector.setNormalizationIndex(n);
        sampleVector.setNormalizationIndex(n);
    }
    public void setUseCircleSpline(boolean use) {
        useCircleSpline = use;
        measurementVector.setUseCircleSpline(use);
        sampleVector.setUseCircleSpline(use);
    }
    
    /** User dialog for choosing the measurement to measure.
     * @param con
     * @param prompt Prompt string for dialog.
     * @param title Title string for dialog.
     * @return The index of the chosen measurement in the vector of measurements or -1 if user cancels.
     */
    public int chooseMeasurementDialog(Component con, String prompt, String title) {
        return chooseMeasurementDialogMasked(con,null,prompt,title);
    }

    /** Like chooseMeasurementDialog but allows user to specify a mask.
     * This can be used in concert with the typeStringMask method of the measurement list object.
     * @param con
     * @param mask Array of integers to mask the choices.
     * @param prompt Prompt string for dialog.
     * @param title Title string for dialog.
     * @return The index of the chosen measurement in the vector of measurements or -1 if user cancels.
     */
    public int chooseMeasurementDialogMasked(Component con, int[] mask, String prompt, String title) {
        // Check number of measurements:
        if (numberOfMeasurements()<=0) { return -1; }
        // Get the list of names:
        String[] allNames = measurementVectorNameAndTypeList();
        // Mask the list of names:
        String[] names;
        int n;
        if (mask==null) {
            n = 0;
        } else {
            n = mask.length;
        }
        if (n<=0) {
            names = allNames;
        } else {
            names = new String[n];
            for ( int i=0 ; i<n ; i++ ) {
                names[i] = allNames[mask[i]];
            }
        }
        // Create list selection dialog:
        return Dialogs.selection(con,prompt,title,names,0);
    }
    
    public void setCurrentSampleIndex(int i, boolean clearImage) {
        // We may need to clear the current sample image before we change the current sample:
        if (clearImage) {
            // Check if the sample image for the old (existing) and new (after execution) current samples are different:
            HasImage oldHasImage = sampleVector.getCurrentSample().getHasImage();
            HasImage newHasImage = sampleVector.get(i).getHasImage();
            if ( oldHasImage != newHasImage ) { // they are different
                // Clear the image of the old current sample:
                oldHasImage.clearImage();
            }
        }
        // Set the current sample index:
        sampleVector.setCurrentSampleIndex(i);
    }
    
    // Wrappers for the SampleVector and MeasurementVector classes:
    public void addNewMeasurement(String typeString, String name) {
        measurementVector.addNew(typeString,name);
        sampleVector.addNewMeasurement(typeString,name);
    }
    public void markMeasurements(boolean b) {
        measurementVector.markMeasurements(b);
        sampleVector.markMeasurements(b);
    }
    public void markMeasurement(int i, boolean b) {
        measurementVector.markMeasurement(i,b);
        sampleVector.markMeasurement(i,b);
    }
    public void removeMeasurement(int i) {
        measurementVector.remove(i);
        sampleVector.removeMeasurement(i);
    }
    public void setLineWidth(int w) {
        measurementVector.setLineWidth(w);
        sampleVector.setLineWidth(w);
    }
    public void setMeasurementColor(int im, Color col1, Color col2) {
        measurementVector.setMeasurementColor(im,col1,col2);
        sampleVector.setMeasurementColor(im,col1,col2);
    }
    public void setPointWidth(int w) {
        measurementVector.setPointWidth(w);
        sampleVector.setPointWidth(w);
    }
    
    // Wrappers for the SampleVector class:
    public void addSamplesFromFiles(Frame con, boolean readNow, String title, File[] files) { sampleVector.addSamplesFromFiles(con,readNow,title,files); }
    public void clearSampleVector() { sampleVector.clear(); }
    public void clearSampleVectorFrom(int n) { sampleVector.clearFrom(n); }
    public void clearSampleVectorTo(int n) { sampleVector.clearTo(n); }
    public boolean exportCSV(File file) { return sampleVector.exportCSV(file,measurementVector.headerForExportCSV()); }
    public boolean exportOutlines(int index, File dir) { return sampleVector.exportOutlines(index,dir); }
    public double getCalibrationDistance() { return getCurrentSample().getCalibrationDistance(); }
    public String getCalibrationDistanceString() { return getCurrentSample().calibrationDistanceString(); }
    public int getCurrentSampleIndex() { return sampleVector.getCurrentIndex(); }
    public Sample getCurrentSample() { return sampleVector.getCurrentSample(); }
    public Sample getSample(int i) { return sampleVector.get(i); }
    public BufferedImage getCurrentSampleImage() {
        // Check there is a current sample:
        Sample s = getCurrentSample();
        if (s==null) { return null; } // no samples loaded or no current sample
        // Get the image:
        return s.getImage();
    }
    public boolean hasSamples() { return ( numberOfSamples() != 0 ); }
    public boolean currentSampleImageExists() { return ( getCurrentSampleImage() != null ); }
    public boolean currentSampleIsCalibrated() { return ( getCurrentSample().isCalibrated() ); }
    public boolean getCalibrationMarked() { return sampleVector.getCalibrationMarked(); }
    public boolean getOriginMarked() { return sampleVector.getOriginMarked(); }
    public boolean getZoomMarked() { return sampleVector.getZoomMarked(); }
    public void markCalibration(boolean b) { sampleVector.markCalibration(b); }
    public void markOrigin(boolean b) { sampleVector.markOrigin(b); }
    public void markZoom(boolean b) { sampleVector.markZoom(b); }
    public int numberOfSamples() { return sampleVector.size(); }
    public void removeSamples(int[] selection) { sampleVector.removeSamples(selection); }
    public void replaceMeasurementLists(int i0) { sampleVector.replaceMeasurementLists(measurementVector,i0); }
    public void resetSampleVectorIDs() { sampleVector.resetIDs(); }
    public String[] sampleVectorNameList() { return sampleVector.nameList(); }
    public void setCalibrationColor(Color col) { sampleVector.setCalibrationColour(col); }
    public void setCalibrationDistance(double d, boolean doAll) {
        if (doAll) {
            sampleVector.setCalibrationDistanceAll(d);
        } else {
            getCurrentSample().setCalibrationDistance(d);
        }
    }
    public void splitCurrentSample(int n) { sampleVector.splitCurrentSample(n); }
    
    // Wrappers for the MeasurementVector class:
    public Measurement getMeasurement(int i) { return measurementVector.get(i); }
    public boolean hasMeasurements() { return ( numberOfMeasurements() != 0 ); }
    public String[] measurementVectorNameAndTypeList() { return measurementVector.nameAndTypeList(); }
    public String measurementVectorReadFile(File f) { return measurementVector.readFile(f); }
    public boolean measurementVectorWriteFile(File f) { return measurementVector.writeFile(f); }
    public int numberOfMeasurements() { return measurementVector.size(); }
    public int[] typeStringMask(String[] typeStrings) { return measurementVector.typeStringMask(typeStrings); }
    
}
