package jmorph;

import geometry.MyPoint2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import jmorph.gui.ButtonBar;
import jmorph.gui.CursorTextBar;
import jmorph.gui.MenuBar;
import jmorph.gui.PaintingOptions;
import jmorph.gui.SampleImagePanel;
import jmorph.gui.SampleInfoPanel;
import jmorph.gui.SampleTextBar;

/** Manages the visible part of the JMorph application.
 * This would be like the View component of the MVC architecture
 * Basically I've just encapsulated all the main GUI objects in this class.
 * @author Peter
 */
public class ViewManager {

    private JMorph controller;
    private MenuBar menuBar;
    private ButtonBar toolBar;
    private CursorTextBar cursorBar;
    private SampleTextBar sampleBar;
    private SampleInfoPanel infoPanel;
    private SampleImagePanel imagePanel;
    private PaintingOptions paintingOptions;
    // TODO: do I need these here?
    private JPanel samplePanel;
    private JPanel statusBar;
    
    public ViewManager(JMorph con) {
        // Set the controller:
        controller = con;
        // Make and add the objects:
        makeObjects();
        addObjects();
    }
    
    private void makeObjects() {
        // Instantiate the painting options:
        paintingOptions = new PaintingOptions(controller);
        // Instantiate the menu:
        menuBar = new MenuBar(controller);
        // Create the tool bar panel:
        toolBar = new ButtonBar(controller);
        toolBar.setBorder(BorderFactory.createEtchedBorder());
        // Create the cursor location text bar:
        cursorBar = new CursorTextBar(controller);
        cursorBar.setBorder(BorderFactory.createEtchedBorder());
        // Create the current sample bar:
        sampleBar = new SampleTextBar();
        sampleBar.setBorder(BorderFactory.createEtchedBorder());
        // Create the measurement information panel:
        infoPanel = new SampleInfoPanel();
        //infoPanel.setBorder(BorderFactory.createEtchedBorder()); // don't need it with the scroll pane in there
        // Create the sample image display panel:
        imagePanel = new SampleImagePanel(controller);
        imagePanel.setBorder(BorderFactory.createEtchedBorder());
        // Place the current sample and point panels within a single panel:
        statusBar = new JPanel();
        // Place the image and info panels within a single panel:
        samplePanel = new JPanel();
    }

    // Adds the panels.
    private void addObjects() {
        // Put on the menu:
        controller.setJMenuBar(menuBar);
        // Get the content pane:
        Container contentPane = controller.getContentPane();
        contentPane.removeAll();
        // Add the current sample and cursor location panels to a single panel:
        statusBar.setLayout(new GridLayout(1,2));
        statusBar.add(cursorBar);
        statusBar.add(sampleBar);
        // Add the image and information panels to a single panel:
        samplePanel.setLayout(new GridLayout(1,2));
        samplePanel.add(imagePanel);
        if (getDisplayInfoPanel()) {
            samplePanel.add(infoPanel);
        }
        // Add the sample panel, status bar and tool bar to the window:
        contentPane.setLayout(new BorderLayout());
        contentPane.add(statusBar,BorderLayout.NORTH);
        contentPane.add(samplePanel,BorderLayout.CENTER);
        contentPane.add(toolBar,BorderLayout.SOUTH);

    }

    /** Draws the current sample (and associated measurements), making sure any measurement calculations have been performed.
     * @param measuring */
    public void drawCurrentSample(boolean measuring) {
        // Tell the measurement list of the current sample to make sure
        // any measurement calculations have been performed as required before painting.
        if (controller.hasSamples()) {
            Sample s = controller.getCurrentSample();
            if (controller.hasMeasurements()) {
                MeasurementVector m = s.getMeasurementList();
                m.runBeforePainting(measuring);
            }
        }
        // Display the sample image in the sample image panel:
        imagePanel.repaint();
        // Display the status and measurement information:
        updateSampleBar();
        updateInfoPanel();
        // Clear the cursor location bar:
        cursorBar.setText(" ");
    }
    
    // Wrappers for the SampleImagePanel class:
    public void redraw() { imagePanel.repaint(); }
    public void calculateTightFitTransform() { imagePanel.calculateTightFitTransform(); }
    public void setCursor(Cursor cursor) { imagePanel.setCursor(cursor); }
    public void requestFocusInSampleImagePanel() { imagePanel.requestFocusInWindow(); }
    
    // Wrappers for the SampleInfoPanel, CursorTextBar and SampleBar classes:
    public void updateCursorBar(MyPoint2D p, boolean isOrigin) { cursorBar.updateCursor(p,isOrigin); }
    public void updateInfoPanel() {
        infoPanel.updateText(controller.numberOfSamples(),controller.getCurrentSampleIndex(),controller.getCurrentSample(),controller.getDisplayFFTCoefficients());
    }
    public void updateSampleBar() {
        sampleBar.updateText(controller.numberOfSamples(),controller.getCurrentSampleIndex(),controller.getCurrentSample());
    }
    
    // Wrappers for the MenuBar and ButtonBar classes:
    public final void checkClickableItemsEnabled() {
        menuBar.checkItemsEnabled();
        //toolBar.setEnabled(true);
        toolBar.checkItemsEnabled();
    }
    //public void disableClickableItems() {
    //    menuBar.setEnabled(false);
    //    toolBar.setEnabled(false);
    //}
    
    // Wrappers for the samplePanel (JPanel class):
    public void addInfoPanel() {
        samplePanel.add(infoPanel);
        samplePanel.revalidate();
    }
    public void removeInfoPanel() {
        samplePanel.remove(infoPanel);
        samplePanel.revalidate();
    }

    // Wrappers for the PaintingOptions class:
    public Color getCalibrationColor() { return paintingOptions.getCalibrationColor(); }
    public boolean getDisplayFFTCoefficients(){ return paintingOptions.getDisplayFFTCoefficients(); }
    public boolean getDisplayInfoPanel() { return paintingOptions.getDisplayInfoPanel(); }
    public boolean getDisplayMeasurements() { return paintingOptions.getDisplayMeasurements(); }
    public int getLineWidth() { return paintingOptions.getLineWidth(); }
    public int getPointWidth() { return paintingOptions.getPointWidth(); }
    public void selectCalibrationColor() { paintingOptions.selectCalibrationColor(); }
    public void selectLineWidth() { paintingOptions.selectLineWidth(); }
    public void selectPointWidth() { paintingOptions.selectPointWidth(); }
    public void setCalibrationColor(Color col) { paintingOptions.setCalibrationColor(col); }
    public void setDisplayFFTCoefficients(boolean b) { paintingOptions.setDisplayFFTCoefficients(b); }
    public void setDisplayInfoPanel(boolean b) { paintingOptions.setDisplayInfoPanel(b); }
    public void setDisplayMeasurements(boolean b) { paintingOptions.setDisplayMeasurements(b); }
    public void setLineWidth(int w) { paintingOptions.setLineWidth(w); }
    public void setPointWidth(int w) { paintingOptions.setPointWidth(w); }
    public void toggleFFTCoefficients() { paintingOptions.toggleFFTCoefficients(); }
    public void toggleInformationPanel() { paintingOptions.toggleInformationPanel(); }
    public void toggleMeasurements() { paintingOptions.toggleMeasurements(); }
    
}
