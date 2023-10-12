package jmorph.gui;

import jmorph.JMorph;
import gui.CommonPaintingOptions;

/** Manages the painting options.
 * @author Peter
 */
public final class PaintingOptions extends CommonPaintingOptions {
    
    private JMorph controller;
    
    private boolean displayMeasurements = true; // whether or not to display the selected measurements in the sample image panel
    private boolean displayFFTCoefficients = false; // whether or not to write the FFT coefficients in the information panel
    private boolean displayInfoPanel = true; // whether or not to display the measurement information panel
    
    public PaintingOptions(JMorph con) {
        super(con);
        controller = con;
    }
    
    public boolean getDisplayMeasurements() { return displayMeasurements; }
    public boolean getDisplayFFTCoefficients(){ return displayFFTCoefficients; }
    public boolean getDisplayInfoPanel() { return displayInfoPanel; }
    
    public void setDisplayMeasurements(boolean b) { displayMeasurements=b; }
    public void setDisplayFFTCoefficients(boolean b) { displayFFTCoefficients = b; }
    public void setDisplayInfoPanel(boolean b) {
        displayInfoPanel = b;
        if (displayInfoPanel) {
            // Attach the toolPanel to the samplePanel:
            controller.addInfoPanel();
        } else {
            // Detach the infoPanel from the samplePanel:
            controller.removeInfoPanel();
        }
    }
    
    public void toggleMeasurements() {
        displayMeasurements = !displayMeasurements;
        controller.checkClickableItemsEnabled();
        controller.drawCurrentSample(false);
    }
    public void toggleFFTCoefficients() {
        displayFFTCoefficients = !displayFFTCoefficients;
        controller.checkClickableItemsEnabled();
        controller.drawCurrentSample(false);
    }
    public void toggleInformationPanel() {
        boolean b = !displayInfoPanel;
        setDisplayInfoPanel(b);
        controller.checkClickableItemsEnabled();
    }
    
    // Wrappers for the CommonPaintingOptions class:
    @Override
    public void selectCalibrationColor() {
        super.selectCalibrationColor();
        if (!getSuccess()) { return; }
        controller.setCalibrationColor(getCalibrationColor(),false);
        controller.drawCurrentSample(false);
    }
    @Override
    public void selectPointWidth() {
        super.selectPointWidth();
        if (!getSuccess()) { return; }
        controller.setPointWidth(getPointWidth(),false);
        controller.drawCurrentSample(false);
    }
    @Override
    public void selectLineWidth() {
        super.selectLineWidth();
        if (!getSuccess()) { return; }
        controller.setLineWidth(getLineWidth(),false);
        controller.drawCurrentSample(false);
    }
    
}
