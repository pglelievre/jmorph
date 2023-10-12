package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class SplitSampleMenuTask extends ControlledMenuTask {
    
    public SplitSampleMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Split multi-sample image"; }

    @Override
    public String tip() { return "Split a multi-sample image into multiple samples"; }

    @Override
    public String title() { return "Split Multi-Sample Image"; }

    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.currentSampleImageExists() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Loop until user supplies an acceptable value or cancels:
        while (true) {
        
            // Ask the user how many samples there are in the current image:
            String response = Dialogs.input(controller,"Enter the number of samples in this image:",title());

            // Check the response:
            if (response==null) { return; } // user cancelled
            response = response.trim();
            String[] ss = response.split("[ ]+");
            String errorMessage = "You must enter a single positive integer. Please try again.";
            if (ss.length!=1) {
                Dialogs.error(controller,errorMessage,title());
                continue;
            }

            // Convert the response to int:
            try {
                int n = Integer.parseInt(ss[0].trim());
                // Check for non-positive value:
                if (n<=0) {
                    // Display error dialog:
                    Dialogs.error(controller,errorMessage,title());
                } else if (n==1) {
                    return; // nothing to do
                } else { // value was acceptable
                    // Copy the sample n-1 times:
                     controller.splitCurrentSample(n);
                     // Check if we need to enable or disable any menu items:
                     controller.checkClickableItemsEnabled();
                     // Display the status and measurement information:
                     controller.updateSampleBar();
                     controller.updateInfoPanel();
                    // Done:
                    return;
                } // if
            } catch (NumberFormatException e) {
                // Display error dialog:
                Dialogs.error(controller,errorMessage,title());
            } // try-catch

        } // while loop
        
    }
    
}
