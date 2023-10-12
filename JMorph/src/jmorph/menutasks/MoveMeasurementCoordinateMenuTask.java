package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;
import jmorph.MouseInteractionManager;
import jmorph.measurements.Measurement;

/**
 * @author Peter
 */
public final class MoveMeasurementCoordinateMenuTask extends ControlledMenuTask {
    
    public MoveMeasurementCoordinateMenuTask(JMorph con) {
        super(con);
    }
    
    @Override
    public String text() { return "Move a measurement coordinate"; }

    @Override
    public String tip() { return "Move a single measurement coordinate for the current sample"; }

    @Override
    public String title() { return "Move a Measurement Coordinate"; }

    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.hasMeasurements() && controller.currentSampleImageExists() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Ask user to choose the measurement:
        int i = controller.chooseMeasurementDialog("Select the measurement to change:",title());
        if (i<0) { return; }
        Measurement m = controller.getCurrentSample().getMeasurementList().get(i);
        
        // Check that it has been measured:
        if ( !m.isMeasured() ) {
            Dialogs.error(controller,"Sorry, that measurement has not been measured yet.",title());
            return;
        }
        
        // Tell the image panel to move a single coordinate for that measurement:
        controller.startMeasuring(m,i,MouseInteractionManager.MOVE_COORDINATE_MODE);
        
    }
    
}
