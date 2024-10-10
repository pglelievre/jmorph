package jmorph;

import fileio.PreviousSession;
import geometry.MyPoint2D;
import gui.JFrameExit;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import jmorph.filters.SessionFilter;
import jmorph.measurements.Measurement;
import jmorph.menutasks.ChangeCalibrationDistanceMenuTask;
import jmorph.menutasks.ChangeSampleMenuTask;

/** The JMorph window.
 * @author Peter Lelievre
 */
public final class JMorph extends JFrameExit {
    private static final long serialVersionUID = 1L;

    // ------------------ Properties -------------------

    // Objects that manage various components of the application:
    private final PreviousSession fileIOManager = new PreviousSession( Paths.get(System.getProperty("user.dir"),"JMorphPreviousSessionFile.txt").toFile() ); // manages reading and writing tasks
    private final ModelManager modelManager; // the model components (plc, sections, groups)
    private final ViewManager viewManager; // the GUI components
    private final InteractionManager interactionManager; // manages user interaction with the GUI
    
    // File reading/loading options:
    public static final int READ_AND_STORE_ALL=0, READ_AND_STORE_AS=1, READ_AND_STORE_ONE=2;
    private int readAndStore = READ_AND_STORE_ALL;
    
    // ------------------ Main Method ------------------

    public static void main(String[] args) {
        // Launch the JMorph window:
        //SwingUtilities.invokeLater(() -> {
            JMorph jMorph = new JMorph();
        //});
    }

    // ------------------ Constructor ------------------

    /** Creates the window and sets up the menu items. */
    public JMorph() {
        // Create the window with a title:
        super("JMorph : digital morphometrics on sample images","JMorph");
        // Check for a previous session information file and read it if it exists:
        fileIOManager.readPreviousSessionFile();
        // Display the about information:
        about();
        // Make required objects:
        modelManager = new ModelManager();
        interactionManager = new InteractionManager(this);
        viewManager = new ViewManager(this);
        // Resize the window to some percentage of the screen size and centre the window on the screen:
        resizeAndCentre();
        // Disable some menus on startup:
        checkClickableItemsEnabled();
        // Initialize the display:
        drawCurrentSample(false);
        // Make the window visible:
        setVisible(true);
        requestFocusInWindow();
    }
    private void resizeAndCentre() {
        // Resize the window to some percentage of the screen size:
        Dimension dim = getToolkit().getScreenSize();
        //setSize( win.width , (int)(0.75*win.height) );
        setSize((int)(0.75*dim.width) , (int)(0.50*dim.height) );
        // Centre the window on the screen:
        Dimension siz = this.getSize();
        setLocation((int)((dim.width  - siz.width )*0.5),
                (int)((dim.height - siz.height)*0.5));
    }
    
    @Override
    protected void runBeforeExit() { fileIOManager.writePreviousSessionFile(); }
    
    // -------------------- Implemented methods from class JFrameExit --------------------
    
    private String versionNumberString() {
        return "1.0"; // you must be able to parse this as a double
    }
    @Override
    protected String versionString() {
        return versionNumberString(); // I could add more information here e.g. a date.
    }
    @Override
    protected String rulesString() {
        return "JMorph is freely available under the MIT License." + System.lineSeparator()
                + "Users should obtain permission for use of" + System.lineSeparator()
                + "HANGLE (Crampton & Haines, 1996) if applicable.";
    }
    @Override
    protected String authorString() {
        return "Authors: Peter Lelievre, Melissa Grey";
    }
    @Override
    protected String contactString() {
        return "Contact: plelievre@mta.ca";
    }
    
//    public int versionInt() {
//        double d =  Double.parseDouble( versionNumberString() );
//        return (int)Math.floor(d);
//    }
    
    // -------------------- Getters: --------------------
    
    public boolean getReadAll() {
        return ( readAndStore == READ_AND_STORE_ALL );
    }
    
    public boolean getReadOne() {
        return ( (readAndStore == READ_AND_STORE_ONE) );
    }

    // -------------------- Methods required in the SampleImagePanel class (should not be used elsewhere) --------------------
    
    public void nextSample() { new ChangeSampleMenuTask(this,1).execute(); }
    
    public boolean changeCalibrationDistance() {
        ChangeCalibrationDistanceMenuTask task = new ChangeCalibrationDistanceMenuTask(this,false);
        task.execute();
        return task.getSuccess();
    }

    // ------------------- Methods called by listeners -------------------
    
    public void keyType(char c) { interactionManager.keyType(c); }
    public void mouseClick(MyPoint2D p) { interactionManager.mouseClick(p); }
    public void mouseMove(MyPoint2D p) { interactionManager.mouseMove(p); }
    
    // -------------------- Methods associated with menu tasks that are too small to bother creating MenuTask classes for --------------------
    
    public void readAndStoreAll() {
        readAndStore = READ_AND_STORE_ALL;
        checkClickableItemsEnabled();
    }
    public void readAndStoreAs()  {
        readAndStore = READ_AND_STORE_AS;
        checkClickableItemsEnabled();
    }
    public void readAndStoreOne() {
        readAndStore = READ_AND_STORE_ONE;
        checkClickableItemsEnabled();
    }
            
    // (below are all methods that start some sort of interaction with the SampleImagePanel)
    
    public void measureAllMeasurements() {
        if (getZoomBeforeCalibration()){
            // Start with zoom box for current sample:
            startMeasuring(
                    getCurrentSample().getZoom(),
                    MouseInteractionManager.ZOOM_INDEX,
                    MouseInteractionManager.ALL_MEASUREMENTS_MODE);
        } else {
            // Start with calibration for current sample:
            startMeasuring(
                    getCurrentSample().getCalibration(),
                    MouseInteractionManager.CALIBRATION_INDEX,
                    MouseInteractionManager.ALL_MEASUREMENTS_MODE);
        }
    }
    public void measureAllSamples() {
        if (getZoomBeforeCalibration()){
            // Start with zoom box for current sample:
            startMeasuring(
                    getCurrentSample().getZoom(),
                    MouseInteractionManager.ZOOM_INDEX,
                    MouseInteractionManager.ALL_SAMPLES_MODE);
        } else {
            // Start with calibration for current sample:
            startMeasuring(
                    getCurrentSample().getCalibration(),
                    MouseInteractionManager.CALIBRATION_INDEX,
                    MouseInteractionManager.ALL_SAMPLES_MODE);
        }
    }
    public void measureCalibration() {
        startMeasuring(
                getCurrentSample().getCalibration(),
                MouseInteractionManager.CALIBRATION_INDEX,
                MouseInteractionManager.SINGLE_MEASUREMENT_MODE);
    }
    public void measureOrigin() {
        startMeasuring(
                getCurrentSample().getOrigin(),
                MouseInteractionManager.ORIGIN_INDEX,
                MouseInteractionManager.SINGLE_MEASUREMENT_MODE);
    }
    public void measureZoom() {
        startMeasuring(
                getCurrentSample().getZoom(),
                MouseInteractionManager.ZOOM_INDEX,
                MouseInteractionManager.SINGLE_MEASUREMENT_MODE);
    }

    // ------------------- Wrappers -------------------
    
    // Wrappers for the InteractionManager class:
    public boolean getDoCalibrationDistanceAuto() { return interactionManager.getDoCalibrationDistanceAuto(); }
    public boolean getZoomBeforeCalibration() { return interactionManager.getZoomBeforeCalibration(); }
    public void setDoCalibrationDistanceAuto(boolean b) { interactionManager.setDoCalibrationDistanceAuto(b); }
    public void setZoomBeforeCalibration(boolean b) { interactionManager.setZoomBeforeCalibration(b); }
    public void selectAllOrder(String title){ interactionManager.selectAllOrder(title); }
    public boolean measuringZoomBox() { return interactionManager.measuringZoomBox(); }
    public boolean shouldPaintMeasurement(Measurement m) { return interactionManager.shouldPaintMeasurement(m); }
    public void startMeasuring(Measurement m, int i, int mode) { interactionManager.startMeasuring(m,i,mode); }
    public boolean isMeasuring() { return interactionManager.isMeasuring(); }
    
    // Wrappers for the ViewManager class:
    public void drawCurrentSample(boolean measuring) { viewManager.drawCurrentSample(measuring); }
    public void redraw() { viewManager.redraw(); }
    public void calculateTightFitTransform() { viewManager.calculateTightFitTransform(); }
    public void setSampleImagePanelCursor(Cursor cursor) { viewManager.setCursor(cursor); }
    public void requestFocusInSampleImagePanel() { viewManager.requestFocusInSampleImagePanel(); }
    public void updateCursorBar(MyPoint2D p, boolean isOrigin) { viewManager.updateCursorBar(p,isOrigin); }
    public void updateInfoPanel() { viewManager.updateInfoPanel(); }
    public void updateSampleBar() { viewManager.updateSampleBar(); }
    public final void checkClickableItemsEnabled() { viewManager.checkClickableItemsEnabled(); }
    //public void disableClickableItems() { viewManager.disableClickableItems(); }
    public void addInfoPanel() { viewManager.addInfoPanel(); }
    public void removeInfoPanel() { viewManager.removeInfoPanel(); }
    public Color getCalibrationColor() { return viewManager.getCalibrationColor(); }
    public boolean getDisplayFFTCoefficients(){ return viewManager.getDisplayFFTCoefficients(); }
    public boolean getDisplayInfoPanel() { return viewManager.getDisplayInfoPanel(); }
    public boolean getDisplayMeasurements() { return viewManager.getDisplayMeasurements(); }
    public int getLineWidth() { return viewManager.getLineWidth(); }
    public int getPointWidth() { return viewManager.getPointWidth(); }
    public void selectCalibrationColor() { viewManager.selectCalibrationColor(); }
    public void selectLineWidth() { viewManager.selectLineWidth(); }
    public void selectPointWidth() { viewManager.selectPointWidth(); }
    public void setDisplayFFTCoefficients(boolean b) { viewManager.setDisplayFFTCoefficients(b); }
    public void setDisplayInfoPanel(boolean b) { viewManager.setDisplayInfoPanel(b); }
    public void setDisplayMeasurements(boolean b) { viewManager.setDisplayMeasurements(b); }
    public void toggleFFTCoefficients() { viewManager.toggleFFTCoefficients(); }
    public void toggleInformationPanel() { viewManager.toggleInformationPanel(); }
    public void toggleMeasurements() { viewManager.toggleMeasurements(); }
    
    // Wrappers for the ViewManager and ModelManager classes:
    public void setCalibrationColor(Color col, boolean loading) {
        if (loading) { viewManager.setCalibrationColor(col); }
        modelManager.setCalibrationColor(col);
    }
    public void setLineWidth(int w, boolean loading) {
        if (loading) { viewManager.setLineWidth(w); }
        modelManager.setLineWidth(w);
    }
    public void setPointWidth(int w, boolean loading) {
        if (loading) { viewManager.setPointWidth(w); }
        modelManager.setPointWidth(w);
    }
    
    // Wrappers for the OpenAndSave class:
    public boolean chooseOpenSession(String title) { return fileIOManager.chooseOpenSession(this,title,new SessionFilter()); }
    public boolean chooseSaveSession(String title) { return fileIOManager.chooseSaveSession(this,title,new SessionFilter(),true); }
    public File getOpenDirectory() { return fileIOManager.getOpenDirectory(); }
    public File getSaveDirectory() { return fileIOManager.getSaveDirectory(); }
    public void setOpenDirectory(File f) { fileIOManager.setOpenDirectory(f); }
    public void setSaveDirectory(File f) { fileIOManager.setSaveDirectory(f); }
    public File getSessionFile() { return fileIOManager.getSessionFile(); }
    public void setSessionFile(File f) { fileIOManager.setSessionFile(f); }

    // Wrappers for the OpenAndSave and ModelManager class:
    public boolean exportOutlines(int index) { return modelManager.exportOutlines(index,getSaveDirectory()); }
    
    // Wrappers for the ModelManager class that should be used in the SessionLoader class only:
    public void setSampleList(SampleVector sv) { modelManager.setSampleList(sv); }
    public void setMeasurementList(MeasurementVector mv) { modelManager.setMeasurementList(mv); }
    
    // Wrappers for the ModelManager class:
    public int getFourierAnalysisMethod() { return modelManager.getFourierAnalysisMethod(); }
    public int getOutlineHighestFFTCoefficient() { return modelManager.getOutlineHighestFFTCoefficient(); }
    public int getOutlineNormalizationIndex() { return modelManager.getOutlineNormalizationIndex(); }
    public int getOutlineResamplingPower() { return modelManager.getOutlineResamplingPower(); }
    public boolean getUseCircleSpline() { return modelManager.getUseCircleSpline(); }
    public String checkOutlineResamplingPower(int p) { return modelManager.checkOutlineResamplingPower(p); }
    public String checkOutlineHighestFFTCoefficient(int n) { return modelManager.checkOutlineHighestFFTCoefficient(n); }
    public String checkOutlineNormalizationIndex(int n) { return modelManager.checkOutlineNormalizationIndex(n); }
    public void setFourierAnalysisMethod(int method) { modelManager.setFourierAnalysisMethod(method); }
    public void setOutlineHighestFFTCoefficient(int n) { modelManager.setOutlineHighestFFTCoefficient(n); }
    public void setOutlineNormalizationIndex(int n) { modelManager.setOutlineNormalizationIndex(n); }
    public void setOutlineResamplingPower(int p) { modelManager.setOutlineResamplingPower(p); }
    public void setUseCircleSpline(boolean use) { modelManager.setUseCircleSpline(use); }
    public int chooseMeasurementDialog(String prompt, String title) { return modelManager.chooseMeasurementDialog(this,prompt,title); }
    public int chooseMeasurementDialogMasked(int[] mask, String prompt, String title) { return modelManager.chooseMeasurementDialogMasked(this,mask,prompt,title); }
    public void addNewMeasurement(String typeString, String name) { modelManager.addNewMeasurement(typeString,name); }
    public void markMeasurements(boolean b) { modelManager.markMeasurements(b); }
    public void markMeasurement(int i, boolean b) { modelManager.markMeasurement(i,b); }
    public void removeMeasurement(int i) { modelManager.removeMeasurement(i); }
    public void setMeasurementColor(int im, Color col1, Color col2) { modelManager.setMeasurementColor(im,col1,col2); }
    public void addSamplesFromFiles(String title, File[] files) { modelManager.addSamplesFromFiles(this,getReadAll(),title,files); }
    public void clearSampleVector() { modelManager.clearSampleVector(); }
    public void clearSampleVectorFrom(int n) { modelManager.clearSampleVectorFrom(n); }
    public void clearSampleVectorTo(int n) { modelManager.clearSampleVectorTo(n); }
    public boolean exportCSV(File file) { return modelManager.exportCSV(file); }
    public double getCalibrationDistance() { return modelManager.getCalibrationDistance(); }
    public String getCalibrationDistanceString() { return modelManager.getCalibrationDistanceString(); }
    public int getCurrentSampleIndex() { return modelManager.getCurrentSampleIndex(); }
    public boolean getUserCancelledWhileLoading() { return modelManager.getUserCancelledWhileLoading(); }
    public Sample getCurrentSample() { return modelManager.getCurrentSample(); }
    public Sample getSample(int i) { return modelManager.getSample(i); }
    public BufferedImage getCurrentSampleImage() { return modelManager.getCurrentSampleImage(); }
    public boolean hasSamples() { return modelManager.hasSamples(); }
    public boolean currentSampleImageExists() { return modelManager.currentSampleImageExists(); }
    public boolean currentSampleIsCalibrated() { return modelManager.currentSampleIsCalibrated(); }
    public boolean getCalibrationMarked() { return modelManager.getCalibrationMarked(); }
    public boolean getOriginMarked() { return modelManager.getOriginMarked(); }
    public boolean getZoomMarked() { return modelManager.getZoomMarked(); }
    public void markCalibration(boolean b) { modelManager.markCalibration(b); }
    public void markOrigin(boolean b) { modelManager.markOrigin(b); }
    public void markZoom(boolean b) { modelManager.markZoom(b); }
    public int numberOfSamples() { return modelManager.numberOfSamples(); }
    public void removeSamples(int[] selection) { modelManager.removeSamples(selection); }
    public void replaceMeasurementLists(int i0) { modelManager.replaceMeasurementLists(i0); }
    public void resetSampleVectorIDs() { modelManager.resetSampleVectorIDs(); }
    public String[] sampleVectorNameList() { return modelManager.sampleVectorNameList(); }
    public void setCalibrationDistance(double d, boolean doAll) { modelManager.setCalibrationDistance(d,doAll); }
    public void setCurrentSampleIndex(int i) { modelManager.setCurrentSampleIndex(i,getReadOne()); }
    public void splitCurrentSample(int n) { modelManager.splitCurrentSample(n); }
    public Measurement getMeasurement(int i) { return modelManager.getMeasurement(i); }
    public boolean hasMeasurements() { return modelManager.hasMeasurements(); }
    public String[] measurementVectorNameAndTypeList() { return modelManager.measurementVectorNameAndTypeList(); }
    public String measurementVectorReadFile(File f) { return modelManager.measurementVectorReadFile(f); }
    public boolean measurementVectorWriteFile(File f) { return modelManager.measurementVectorWriteFile(f); }
    public int numberOfMeasurements() { return modelManager.numberOfMeasurements(); }
    public int[] typeStringMask(String[] typeStrings) { return modelManager.typeStringMask(typeStrings); }

}
