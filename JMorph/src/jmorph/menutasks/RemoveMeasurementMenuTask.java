package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class RemoveMeasurementMenuTask extends ControlledMenuTask {
    
    public RemoveMeasurementMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Remove measurement"; }

    @Override
    public String tip() { return "Remove a measurement from the list"; }

    @Override
    public String title() { return "Remove Measurement"; }

    @Override
    public boolean check() {
        return controller.hasMeasurements();
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Ask which measurement to remove:
        int i = controller.chooseMeasurementDialog("Select the measurement to remove:",title());
        // Check the response:
        if (i<0) { return; }

        // Create confirmation dialog if required:
        int response = Dialogs.yesno(controller,
                "Are you sure you want to remove that measurement?",title());

        // Check answer:
        if (response != Dialogs.YES_OPTION) { return; }

        // Remove the measurement from the controller and sample measurement lists:
        controller.removeMeasurement(i);

        // Enable or disable menu items:
        controller.checkClickableItemsEnabled();

        // Repaint the current sample:
        controller.drawCurrentSample(false);

        // Display success dialog:
        Dialogs.inform(controller,"Measurement removed successfully.",title());
        
    }
    
}
