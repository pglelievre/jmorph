package jmorph.menutasks;

import jmorph.JMorph;
import jmorph.MouseInteractionManager;
import jmorph.measurements.Measurement;

/**
 * @author Peter
 */
public final class SelectMeasurementMenuTask extends ControlledMenuTask {
    
    public SelectMeasurementMenuTask(JMorph con) {
        super(con);
    }
    
    @Override
    public String text() { return "Select measurement to perform"; }

    @Override
    public String tip() { return "Select the measurement to perform from a list"; }

    @Override
    public String title() { return "Select Measurement to Perform"; }

    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.hasMeasurements() && controller.currentSampleImageExists() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Ask user which measurement to perform:
        int i = controller.chooseMeasurementDialog("Select the measurement to perform:",title());
        if (i<0) { return; }
        Measurement m = controller.getCurrentSample().getMeasurementList().get(i);
        
        // Perform the measurement:
        controller.startMeasuring(m,i,MouseInteractionManager.SINGLE_MEASUREMENT_MODE);
        
    }
    
}
