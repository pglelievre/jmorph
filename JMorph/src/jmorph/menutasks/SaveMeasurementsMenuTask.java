package jmorph.menutasks;

import dialogs.Dialogs;
import java.io.File;
import javax.swing.JFileChooser;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class SaveMeasurementsMenuTask extends ControlledMenuTask {
    
    public SaveMeasurementsMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Save measurement definitions"; }

    @Override
    public String tip() { return "Save the measurement definitions to a file"; }

    @Override
    public String title() { return "Save Measurement Definitions"; }

    @Override
    public boolean check() {
        return controller.hasMeasurements();
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Ask for the file:
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory( controller.getSaveDirectory() );
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(false);
        int response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }

        // Set the save directory to the chosen directory:
        controller.setSaveDirectory( chooser.getCurrentDirectory() );

        // Save the measurements to the file:
        if (controller.measurementVectorWriteFile(file)) {
            // Display successful load:
            Dialogs.inform(controller,"Measurements saved successfully.",title());
        } else {
            // Display error message:
            Dialogs.error(controller,"Failed to write the file.",title());
        }
        
    }
    
}
