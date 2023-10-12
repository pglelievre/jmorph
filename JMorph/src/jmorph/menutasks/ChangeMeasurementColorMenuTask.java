package jmorph.menutasks;

import java.awt.Color;
import javax.swing.JColorChooser;
import jmorph.JMorph;
import jmorph.measurements.Measurement;

/**
 * @author Peter
 */
public final class ChangeMeasurementColorMenuTask extends ControlledMenuTask {
    
    public ChangeMeasurementColorMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Change measurement colour"; }

    @Override
    public String tip() { return "Change the painting colour for a specific measurement"; }

    @Override
    public String title() { return "Change Measurement Colour"; }

    @Override
    public boolean check() {
        return ( controller.hasMeasurements() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Ask for the measurement:
        int im = controller.chooseMeasurementDialog("Select the measurement to change:",title());
        // Check the response:
        if (im<0) { return; }

        // Get the measurement:
        Measurement m = controller.getMeasurement(im);

        // Check if a secondary colour is needed:
        String title;
        if (m.usesSecondaryColour()) {
            title = "Select the primary measurement colour";
        } else {
            title = "Select the measurement colour";
        }

        // Ask for the colour:
        Color col1 = JColorChooser.showDialog(controller,title,m.getPrimaryColour());
        // Check response:
        if (col1 == null) { return; }

        // Check if a secondary colour is needed:
        Color col2 = null;
        if (m.usesSecondaryColour()) {
            // Ask for the colour:
            title = "Select the secondary measurement colour";
            col2 = JColorChooser.showDialog(controller,title,m.getSecondaryColour());
            // Check response:
            if (col2 == null) { return; }
        }

        // Tell the measurement and sample lists to set the colour(s) for the selected measurement:
        controller.setMeasurementColor(im,col1,col2);

        // Repaint current sample:
        controller.drawCurrentSample(false);
        
    }
    
}
