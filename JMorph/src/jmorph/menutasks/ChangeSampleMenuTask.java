package jmorph.menutasks;

import dialogs.Dialogs;
import dialogs.ListDialog;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class ChangeSampleMenuTask extends ControlledMenuTask {
    
    private final int mode;
    
    public ChangeSampleMenuTask(JMorph con, int m) {
        super(con);
        mode = m;
    }
    
    @Override
    public String text() {
        switch (mode) {
            case -2: return "First sample";
            case -1: return "Previous sample";
            case  1: return "Next sample";
            case  2: return "Last sample";
            default: return "Select sample";
        }
    }

    @Override
    public String tip() {
        switch (mode) {
            case -2: return "Move to the first sample";
            case -1: return "Move to the previous sample";
            case  1: return "Move to the next sample";
            case  2: return "Move to the last sample";
            default: return "Select the sample to work with, e.g. perform measurements on";
        }
    }

    @Override
    public String title() {
        switch (mode) {
            case -2: return "First Sample";
            case -1: return "Previous Sample";
            case  1: return "Next Sample";
            case  2: return "Last Sample";
            default: return "Select Sample";
        }
    }

    @Override
    public boolean check() {
        if ( controller.numberOfSamples() <= 1 ) { return false; }
        int currentSample = controller.getCurrentSampleIndex();
        int lastSample = controller.numberOfSamples() - 1;
        switch (mode) {
            case -2: return (currentSample!=0);
            case -1: return (currentSample!=0);
            case  1: return (currentSample!=lastSample);
            case  2: return (currentSample!=lastSample);
            default: return true;
        }
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the current sample index:
        int currentSampleIndex = controller.getCurrentSampleIndex();
        int lastSampleIndex = controller.numberOfSamples() - 1;
        
        // Check the mode:
        int newSampleIndex;
        switch (mode) {
            case -2: // first sample
                newSampleIndex = 0;
                break;
            case -1: // previous sample
                newSampleIndex = Math.max( currentSampleIndex-1 , 0 );
                break;
            case  1: // next sample
                newSampleIndex = Math.min( currentSampleIndex+1 , lastSampleIndex );
                break;
            case  2: // last sample
                newSampleIndex = lastSampleIndex;
                break;
            default: // Ask the user to select the sample:
                // Get the list of names:
                String[] names = controller.sampleVectorNameList();
                // Create list selection dialog:
                String prompt = "Select the sample to display:";
                if (names.length<=8) {
                    // Use a pull-down menu dialog:
                    newSampleIndex = Dialogs.selection(controller,prompt,title(),names, currentSampleIndex );
                } else {
                    // Use a list dialog:
                    int[] selectedNames = new int[1];
                    selectedNames[0] = controller.getCurrentSampleIndex();
                    ListDialog ld = new ListDialog(controller,prompt,title(),names,false,selectedNames);
                    // Check the response:
                    newSampleIndex = ld.getSelectedIndex();
                    if (newSampleIndex<0) { return; } // user cancelled
                }
                // Check for user cancelling:
                if (newSampleIndex<0) {return;}
                break;
        } // switch

        // Check that it changed:
        if ( newSampleIndex == controller.getCurrentSampleIndex() ) { return; }

        // Set the current sample:
        controller.setCurrentSampleIndex(newSampleIndex);

        // Check if we need to enable or disable any menu items:
        controller.checkClickableItemsEnabled();

        // Display the new sample:
        controller.drawCurrentSample(false);

        
    }
    
}
