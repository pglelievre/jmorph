package jmorph.menutasks;

import dialogs.Dialogs;
import java.io.File;
import javax.swing.JFileChooser;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class LoadMeasurementsMenuTask extends ControlledMenuTask {
    
    public LoadMeasurementsMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Load measurement definitions"; }

    @Override
    public String tip() { return "Load a file containing measurement definitions"; }

    @Override
    public String title() { return "Load Measurement Definitions"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Create confirmation dialog if required:
        int response;
        if (controller.hasMeasurements()) {
            response = Dialogs.yesno(controller,"This will clear all current measurements. Do you wish to continue?",title());
            // Check answer:
            if (response != Dialogs.YES_OPTION) { return; }
        }

        // Ask for the file:
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory( controller.getOpenDirectory() );
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        response = chooser.showOpenDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File file = chooser.getSelectedFile();

        // Check response:
        if (file==null) { return; }
        
        // Set the load directory to the chosen directory:
        controller.setOpenDirectory( chooser.getCurrentDirectory() );

        // Load the measurements from the file:
        String err = controller.measurementVectorReadFile(file);
        if (err==null) {
            // Copy the measurement list to all the sample objects:
            controller.replaceMeasurementLists(0);
            // Check if we need to enable or disable any menu items:
            controller.checkClickableItemsEnabled();
            // Repaint the current sample:
            controller.drawCurrentSample(false);
            // Display successful load:
            Dialogs.inform(controller,"Measurements loaded successfully.",title());
        } else {
            // Display error message:
            Dialogs.error(controller,err,title());
        }
        
    }
    
}
