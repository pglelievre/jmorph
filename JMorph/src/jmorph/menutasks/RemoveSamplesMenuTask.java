package jmorph.menutasks;

import dialogs.ListDialog;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class RemoveSamplesMenuTask extends ControlledMenuTask {
    
    public RemoveSamplesMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Remove samples"; }

    @Override
    public String tip() { return "Remove one or more of the samples from the sample list"; }

    @Override
    public String title() { return "Remove Samples"; }

    @Override
    public boolean check() {
        return controller.hasSamples();
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Get the current sample index:
        int currentIndex = controller.getCurrentSampleIndex();

        // Get the list of names:
        String[] names = controller.sampleVectorNameList();

        // Create list selection dialog:
        String prompt = "Select the samples to remove:";
        int[] selectedNames = new int[1];
        selectedNames[0] = currentIndex;
        ListDialog ld = new ListDialog(controller,prompt,title(),names,true,selectedNames);
        int[] selection = ld.getSelectedIndices();

        // Check for user cancelling:
        if (selection==null) {return;}

        // Check that there are samples selected to remove:
        if (selection.length==0) {return;}

        // Check if the current sample is one of those being removed,
        // and if so, set the current sample to the first.
        for ( int i=0 ; i<selection.length ; i++ ) {
            if (selection[i]==currentIndex) {
                controller.setCurrentSampleIndex(0);
                break;
            }
        }

        // Remove the selected sample(s):
        controller.removeSamples(selection);

        // Check if we need to enable or disable any menu items:
        controller.checkClickableItemsEnabled();

        // Display the new sample:
        controller.drawCurrentSample(false);
        
    }
    
}
