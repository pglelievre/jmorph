package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;
import jmorph.MeasurementVector;

/**
 * @author Peter
 */
public final class DefineMeasurementMenuTask extends ControlledMenuTask {
    
    public DefineMeasurementMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Define new measurement"; }

    @Override
    public String tip() { return "Define a new measurement to add to the list"; }

    @Override
    public String title() { return "Define New Measurement"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; } 

        // Dialog title:
        String prompt = "Select the type of measurement:";

        // Get the list of all allowed types:
        String[] allTypes = MeasurementVector.getTypes();

        // Create list selection dialog:
        int selection = Dialogs.selection(controller,prompt,title(),allTypes,2); // the initial selection should be the "outline" type

        // Check for user cancelling:
        if (selection<0) { return; }

        // Extract selected type string:
        String typeString = allTypes[selection];
        if (typeString==null) { return; }
        
        // Ask for the name for the new measurement until it is correctly defined:
        String name;
        while (true) {
            // Ask for the name:
            name = Dialogs.input(controller,"Enter a name for the measurement:",title());
            // Check the response:
            if (name == null) { return; } // user cancelled
            // Check for spaces in the name:
            if ( name.contains(" ") ) {
                Dialogs.error(controller,"The name can not contain spaces.",title());
            } else {
                break;
            }
        }

        // Add the measurement to the controller and sample measurement lists:
        controller.addNewMeasurement(typeString,name);

        // Enable or disable menu items:
        controller.checkClickableItemsEnabled();

        // Repaint the current sample:
        controller.drawCurrentSample(false);

        // Display success dialog:
        Dialogs.inform(controller,"Measurement added successfully.",title());
        
    }
    
}
