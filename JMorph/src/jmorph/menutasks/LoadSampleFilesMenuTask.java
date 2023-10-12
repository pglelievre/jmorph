package jmorph.menutasks;

import dialogs.Dialogs;
import filters.ImageFilter;
import java.io.File;
import javax.swing.JFileChooser;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class LoadSampleFilesMenuTask extends ControlledMenuTask {
    
    public LoadSampleFilesMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Load sample images"; }

    @Override
    public String tip() { return "Load one or more files containing images of the samples"; }

    @Override
    public String title() { return "Load Sample Images"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        String t; // temporary string variable

        // Create confirmation dialog if required:ß
        boolean overwrite; // overwrite samples already in the list or just add new ones?
        int response;
        if (!controller.hasSamples()) {
            overwrite = true;
        } else {
            response = Dialogs.question(controller,"There are already samples. How do you want to continue?",
                    title(),"Overwrite","Add","Cancel");
            // Check answer:
            switch (response) {
                case Dialogs.YES_OPTION:
                    overwrite = true;
                    break;
                case Dialogs.NO_OPTION:
                    overwrite = false;
                    break;
                default:
                    return;
            }
        }

        // Ask for the list of files:
        JFileChooser chooser = new JFileChooser();
        ImageFilter imageFilter = new ImageFilter();
        File loadDirectory = controller.getOpenDirectory();
        chooser.setCurrentDirectory(loadDirectory);
        chooser.addChoosableFileFilter(imageFilter);
        chooser.setFileFilter(imageFilter);
        //chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(true);
        response = chooser.showOpenDialog(controller);

        // Check response and get the list of files:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File[] files = chooser.getSelectedFiles();

        // Check response:
        if (files==null) { return; }

        // Set the load directory to the chosen directory:
        loadDirectory = chooser.getCurrentDirectory();
        controller.setOpenDirectory(loadDirectory);

        // Keep track of some information that may be needed later:
        int currentSampleIndex = controller.getCurrentSampleIndex(); // needed in case the user cancels
        int existingSize = controller.numberOfSamples(); // number of samples already in the list before adding new ones

        // Add new samples to the sample list:
        controller.addSamplesFromFiles(title(),files);
        
        // Check if user cancelled on the progress bar while loading:
        boolean cancelled = controller.getUserCancelledWhileLoading();
        if (cancelled) {
            // Remove all but previously existing samples:
            controller.clearSampleVectorFrom(existingSize);
        }
        
        // Remove existing samples if overwriting:
        if ( !cancelled & overwrite ) {
            controller.clearSampleVectorTo(existingSize);
        }

        // Reset the IDs:
        controller.resetSampleVectorIDs();
        
        // Copy the measurement list to all the new sample objects:
        if (!cancelled) {
            if (overwrite) {
                controller.replaceMeasurementLists(0);
            } else {
                controller.replaceMeasurementLists(existingSize);
            }
        }

        // Set the current sample number:
        if (cancelled) {
            controller.setCurrentSampleIndex(currentSampleIndex); // set to what it was before
        } else {
            if (overwrite) {
                controller.setCurrentSampleIndex(0); // the first (new) sample becomes the current sample
            } else {
                controller.setCurrentSampleIndex(existingSize); // the first new sample (after all the old ones) becomes the current sample
            }
        }

        // Enable or disable menu items:
        controller.checkClickableItemsEnabled();

        // Repaint the current sample:
        controller.drawCurrentSample(false);

        // Display message:
        if (cancelled) {
            t = "No files loaded.";
            Dialogs.inform(controller,t,title());
        } else {
            t = "All files loaded.";
            Dialogs.inform(controller,t,title());
        }
        
    }
    
}
