package jmorph.menutasks;

import jmorph.JMorph;
import jmorph.Sample;

/**
 * @author Peter
 */
public final class ClearCalibrationMenuTask extends ControlledMenuTask {
    
    public ClearCalibrationMenuTask(JMorph con) {
        super(con);
    }
    
    @Override
    public String text() { return "Clear calibration"; }

    @Override
    public String tip() { return "Clear all calibration information for the current sample"; }

    @Override
    public String title() { return "Clear calibration"; }
    
    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.currentSampleImageExists() && controller.currentSampleIsCalibrated() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the current sample:
        Sample s = controller.getCurrentSample();
        
        // Clear the calibration:
        s.clearCalibration();

        // Repaint the current sample:
        controller.drawCurrentSample(false);
        
    }
    
}
