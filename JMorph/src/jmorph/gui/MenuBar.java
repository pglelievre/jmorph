package jmorph.gui;

import gui.MenuTaskMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import jmorph.JMorph;
import jmorph.menutasks.*;
import tasks.MenuTask;

/** The menu on the JMorph window.
 * @author Peter Lelievre.
 */
public final class MenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;

    // JMorph JFrame that the menu bar is associated with:
    private final JMorph controller;

    // Second level menu items:
    private JMenuItem miAbout, miExit,
            miAllMeasurements, miAllSamples,
            miCalibrate, miOrigin,
            miZoom, miPointWidth, miLineWidth, miCalibrationColor, miRedraw,
            miToggleMeasurements, miToggleInformationPanel, miToggleFFTCoefficients,
            miReadAndStoreAll, miReadAndStoreAs, miReadAndStoreOne;
    private MenuTaskMenuItem miLoadSession, miSaveSession, miLoadMeasurements, miSaveMeasurements, miLoadImages, miExport, miOutlines,
            miSplitSample, miChooseSample, miFirst, miPrevious, miNext, miLast, miRemoveSamples,
            miNewMeasurement, miRemoveMeasurement, miMoveCoordinate, miAllOrder, 
            miClearCalibration, miCalibrationDistance, miCalibrationDistanceAll,
            miChooseMeasurement, miChooseAllMeasurements, miClearZoom,
            miResamplingPower, miHighestFFTCoefficient, miNormalizationIndex, miOutlineSpline, miFourierAnalysisMethod,
            miMeasurementColor;
    private final ArrayList<MenuTaskMenuItem> menuTaskMenuItems = new ArrayList<>();
    private final ArrayList<JMenuItem> jMenuItems = new ArrayList<>();

    /** Makes the menu bar.
     * @param con JMorph window (JFrame extension) to place the menu on.
     */
    public MenuBar(JMorph con) {
        super();
        this.controller = con;
        makeMenuItems();
        addMenuItems();
    }
    
    private void makeMenuItems() {

        MenuListener listener = new MenuListener();

        // Build the JMorph menu items:
        miAbout = makeMenuItem("About","Display information about JMorph",listener);
        miExit = makeMenuItem("Exit","Exit JMorph without saving",listener);

        // Build the file menu items:
        miLoadMeasurements = makeMenuTaskMenuItem(new LoadMeasurementsMenuTask(controller),listener);
        miSaveMeasurements = makeMenuTaskMenuItem(new SaveMeasurementsMenuTask(controller),listener);
        miLoadImages = makeMenuTaskMenuItem(new LoadSampleFilesMenuTask(controller),listener);
        miLoadSession = makeMenuTaskMenuItem(new LoadSessionMenuTask(controller),listener);
        miSaveSession = makeMenuTaskMenuItem(new SaveSessionMenuTask(controller),listener);
        miExport = makeMenuTaskMenuItem(new ExportCSVMenuTask(controller),listener);
        miOutlines = makeMenuTaskMenuItem(new ExportOutlinesMenuTask(controller),listener);

        // Build the sample navigation menu items:
        miChooseSample = makeMenuTaskMenuItem(new ChangeSampleMenuTask(controller, 0),listener);
        miFirst        = makeMenuTaskMenuItem(new ChangeSampleMenuTask(controller,-2),listener);
        miPrevious     = makeMenuTaskMenuItem(new ChangeSampleMenuTask(controller,-1),listener);
        miNext         = makeMenuTaskMenuItem(new ChangeSampleMenuTask(controller, 1),listener);
        miLast         = makeMenuTaskMenuItem(new ChangeSampleMenuTask(controller, 2),listener);
        miSplitSample = makeMenuTaskMenuItem(new SplitSampleMenuTask(controller),listener);
        miRemoveSamples = makeMenuTaskMenuItem(new RemoveSamplesMenuTask(controller),listener);

        // Build the measure menu items:
        miChooseMeasurement = makeMenuTaskMenuItem(new SelectMeasurementMenuTask(controller),listener);
        miChooseAllMeasurements = makeMenuTaskMenuItem(new SelectAllMeasurementsMenuTask(controller),listener);
        miAllMeasurements = makeMenuItem("Perform all measurements on current sample","Perform all measurements on the current sample sequentially",listener);
        miAllSamples = makeMenuItem("Perform all measurements on all samples","Perform all measurements on all samples sequentially",listener);
        miNewMeasurement = makeMenuTaskMenuItem(new DefineMeasurementMenuTask(controller),listener);
        miRemoveMeasurement = makeMenuTaskMenuItem(new RemoveMeasurementMenuTask(controller),listener);
        miMoveCoordinate = makeMenuTaskMenuItem(new MoveMeasurementCoordinateMenuTask(controller),listener);

        // Build the calibration menu items:
        miAllOrder = makeMenuTaskMenuItem(new ChangeAllOrderMenuTask(controller),listener);
        miClearCalibration = makeMenuTaskMenuItem(new ClearCalibrationMenuTask(controller),listener);
        miCalibrationDistance = makeMenuTaskMenuItem(new ChangeCalibrationDistanceMenuTask(controller,false),listener);
        miCalibrationDistanceAll = makeMenuTaskMenuItem(new ChangeCalibrationDistanceMenuTask(controller,true),listener);
        miCalibrate = makeMenuItem("Calibrate current sample","Specify (via mouse click) the calibration line for the current sample",listener);
        miOrigin = makeMenuItem("Select calibration origin","Specify (via mouse click) the calibration origin point for the current sample",listener);

        // Build the Fourier analysis menu items:
        miResamplingPower = makeMenuTaskMenuItem(new ChangeResamplingPowerMenuTask(controller),listener);
        miHighestFFTCoefficient = makeMenuTaskMenuItem(new ChangeHighestFFTCoefficientMenuTask(controller),listener);
        miNormalizationIndex = makeMenuTaskMenuItem(new ChangeNormalizationIndexMenuTask(controller),listener);
        miOutlineSpline = makeMenuTaskMenuItem(new ChangeOutlineSplineMethodMenuTask(controller),listener);
        miFourierAnalysisMethod = makeMenuTaskMenuItem(new ChangeFourierAnalysisMethodMenuTask(controller),listener);

        // Build the display menu items:
        miZoom = makeMenuItem("Zoom","Specify (via mouse click) the zoom box for the current sample",listener);
        miClearZoom = makeMenuTaskMenuItem(new ClearZoomMenuTask(controller),listener);
        miPointWidth = makeMenuItem("Point width","Change the plotting width for points",listener);
        miLineWidth = makeMenuItem("Line width","Change the plotting width for lines",listener);
        miCalibrationColor = makeMenuItem("Calibration Color","Change the plotting colour for the calibration overlays",listener);
        miMeasurementColor = makeMenuTaskMenuItem(new ChangeMeasurementColorMenuTask(controller),listener);
        miToggleMeasurements = makeMenuItem("Show/hide measurements","Toggle plotting of the measurement overlays",listener);
        miToggleInformationPanel = makeMenuItem("Show/hide information panel","Show or hide the information panel on the right",listener);
        miToggleFFTCoefficients = makeMenuItem("Show/hide FFT coefficients","Toggle printing of the FFT coefficiens in the information panel",listener);
        miRedraw = makeMenuItem("Redraw current sample","Update the plotting of the current sample (in case something has gone wrong with the GUI)",listener);
        
        // Build the advanced menu items:
        miReadAndStoreAll = makeMenuItem("all at once","Read all images as soon as available and store all in memory.",listener);
        miReadAndStoreAs  = makeMenuItem("as encountered","Read images as needed and store all in memory.",listener);
        miReadAndStoreOne = makeMenuItem("never store","Read images as needed but never store in memory.",listener);
 
    }
    private MenuTaskMenuItem makeMenuTaskMenuItem(MenuTask task, ActionListener listener) {
        MenuTaskMenuItem mi = new MenuTaskMenuItem(task);
        mi.addActionListener(listener);
        menuTaskMenuItems.add(mi);
        return mi;
    }
    
    private JMenuItem makeMenuItem(String text, String tip, ActionListener al) {
        JMenuItem mi = new JMenuItem(text);
        mi.setToolTipText(tip);
        mi.addActionListener(al);
        jMenuItems.add(mi);
        return mi;
    }
    
    private void addMenuItems() {
        
        // Build the JMorph menu:
        JMenu mainMenu = new JMenu("JMorph");
        this.add(mainMenu);
        mainMenu.add(miAbout);
        mainMenu.add(miExit);

        // Build the file menu:
        JMenu fileMenu = new JMenu("File");
        this.add(fileMenu);
        fileMenu.add(miLoadMeasurements);
        fileMenu.add(miSaveMeasurements);
        fileMenu.add(miLoadImages);
        fileMenu.add(miLoadSession);
        fileMenu.add(miSaveSession);
        fileMenu.add(miExport);
        fileMenu.add(miOutlines);

        // Build the sample navigation menu:
        JMenu samplesMenu = new JMenu("Sample");
        this.add(samplesMenu);
        samplesMenu.add(miSplitSample);
        samplesMenu.add(miChooseSample);
        samplesMenu.add(miFirst);
        samplesMenu.add(miPrevious);
        samplesMenu.add(miNext);
        samplesMenu.add(miLast);
        samplesMenu.add(miRemoveSamples);

        // Build the measure menu:
        JMenu measureMenu = new JMenu("Measure");
        this.add(measureMenu);
        measureMenu.add(miChooseMeasurement);
        measureMenu.add(miChooseAllMeasurements);
        measureMenu.add(miAllMeasurements);
        measureMenu.add(miAllSamples);
        measureMenu.add(miNewMeasurement);
        measureMenu.add(miRemoveMeasurement);
        measureMenu.add(miMoveCoordinate);

        // Build the calibration menu:
        JMenu calibrationMenu = new JMenu("Calibration");
        this.add(calibrationMenu);
        calibrationMenu.add(miAllOrder);
        calibrationMenu.add(miClearCalibration);
        calibrationMenu.add(miCalibrationDistance);
        calibrationMenu.add(miCalibrationDistanceAll);
        calibrationMenu.add(miCalibrate);
        calibrationMenu.add(miOrigin);

        // Build the Fourier analysis menu:
        JMenu outlineMenu = new JMenu("Outlines");
        this.add(outlineMenu);
        outlineMenu.add(miResamplingPower);
        outlineMenu.add(miHighestFFTCoefficient);
        outlineMenu.add(miNormalizationIndex);
        outlineMenu.add(miOutlineSpline);
        outlineMenu.add(miFourierAnalysisMethod);

        // Build the display menu:
        JMenu displayMenu = new JMenu("Display");
        this.add(displayMenu);
        displayMenu.add(miZoom);
        displayMenu.add(miClearZoom);
        displayMenu.add(miPointWidth);
        displayMenu.add(miLineWidth);
        displayMenu.add(miCalibrationColor);
        displayMenu.add(miMeasurementColor);
        displayMenu.add(miToggleMeasurements);
        displayMenu.add(miToggleInformationPanel);
        displayMenu.add(miToggleFFTCoefficients);
        displayMenu.add(miRedraw);
        
        // Build the advanced menu:
        JMenu advancedMenu = new JMenu("Advanced");
        this.add(advancedMenu);
        JMenu readAndStoreMenu = new JMenu("Read and store images");
        advancedMenu.add(readAndStoreMenu);
        readAndStoreMenu.add(miReadAndStoreAll);
        readAndStoreMenu.add(miReadAndStoreAs);
        readAndStoreMenu.add(miReadAndStoreOne);
        
    }

    /** Action listener for menu items. */
    private class MenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src==null) { return; }
            // Deal with the MenuTaskMenuItems:
            if (src instanceof MenuTaskMenuItem tmi) { // cast
                // cast
                tmi.execute();
                return;
            }
            if (src == miExit) { controller.exit(); }
            else if (src == miAbout) { controller.about(); }
            else if (src == miToggleMeasurements) { controller.toggleMeasurements(); }
            else if (src == miToggleInformationPanel) { controller.toggleInformationPanel(); }
            else if (src == miRedraw) { controller.drawCurrentSample(false); }
            else if (src == miToggleFFTCoefficients) { controller.toggleFFTCoefficients(); }
            else if (src == miZoom) { controller.measureZoom(); }
            else if (src == miPointWidth) { controller.selectPointWidth(); }
            else if (src == miLineWidth) { controller.selectLineWidth(); }
            else if (src == miCalibrationColor) { controller.selectCalibrationColor(); }
            else if (src == miCalibrate) { controller.measureCalibration(); }
            else if (src == miOrigin) { controller.measureOrigin(); }
            else if (src == miAllMeasurements) { controller.measureAllMeasurements(); }
            else if (src == miAllSamples) { controller.measureAllSamples(); }
            else if (src == miReadAndStoreAll) { controller.readAndStoreAll(); }
            else if (src == miReadAndStoreAs ) { controller.readAndStoreAs(); }
            else if (src == miReadAndStoreOne) { controller.readAndStoreOne(); }
        }
    }

    /** Enables or disables some menu items based on the inputs. */
    public void checkItemsEnabled() {
        
        boolean measuring = controller.isMeasuring();
        
        // Deal with the MenuTaskMenuItems:
        for (int i=0 ; i<menuTaskMenuItems.size() ; i++) {
            if (measuring) { 
                menuTaskMenuItems.get(i).setEnabled(false);
            } else {
                menuTaskMenuItems.get(i).checkEnabled();
            }
        }
        
        // Deal with the other menu items:
        if (measuring) {
            for (int i=0 ; i<jMenuItems.size() ; i++) {
                jMenuItems.get(i).setEnabled(false);
            }
            return;
        }
        
        // Get some information from the controller:
        boolean hasSamples = controller.hasSamples();
        boolean hasMeasurements = controller.hasMeasurements();
        boolean imageExists = controller.currentSampleImageExists();
        
        // Create some booleans based on the information above:
        boolean hasBoth = ( hasSamples && hasMeasurements );
        boolean hasEverything = ( hasBoth && imageExists );
        
        // Some items are always available:
        miAbout.setEnabled(true);
        miExit.setEnabled(true);
        miRedraw.setEnabled(true);
        miToggleInformationPanel.setEnabled(true);
        miToggleFFTCoefficients.setEnabled(true);
        miPointWidth.setEnabled(true);
        miLineWidth.setEnabled(true);
        miCalibrationColor.setEnabled(true);
        
        // Some items are only available there are samples and an image exists for the current sample:
        miCalibrate.setEnabled(imageExists);
        miOrigin.setEnabled(imageExists);
        miZoom.setEnabled(imageExists);
        
        // Some items are only available if there are samples and measurements:
        miAllSamples.setEnabled(hasBoth);
        miAllMeasurements.setEnabled(hasBoth);
        
        // Some items are only available if there are samples and measurements and an image exists for the current sample:
        miToggleMeasurements.setEnabled(hasEverything);
        
        // Change the text of the show/hide menu items:
        if (controller.getDisplayMeasurements()) {
            miToggleMeasurements.setText("Hide measurements");
        } else {
            miToggleMeasurements.setText("Show measurements");
        }
        if (controller.getDisplayInfoPanel()) {
            miToggleInformationPanel.setText("Hide information panel");
        } else {
            miToggleInformationPanel.setText("Show information panel");
        }
        if (controller.getDisplayFFTCoefficients()) {
            miToggleFFTCoefficients.setText("Hide FFT coefficients");
        } else {
            miToggleFFTCoefficients.setText("Show FFT coefficients");
        }
        
        // These things can only have one selection amongst themselves:
        if (controller.getReadAll()) {
            miReadAndStoreAll.setEnabled(false);
            miReadAndStoreAs.setEnabled(true);
            miReadAndStoreOne.setEnabled(true);
        } else if (controller.getReadOne()) {
            miReadAndStoreAll.setEnabled(true);
            miReadAndStoreAs.setEnabled(true);
            miReadAndStoreOne.setEnabled(false);
        } else {
            miReadAndStoreAll.setEnabled(true);
            miReadAndStoreAs.setEnabled(false);
            miReadAndStoreOne.setEnabled(true);
        }
        
    }

}
