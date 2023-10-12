package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class ChangeNormalizationIndexMenuTask extends ControlledMenuTask {
    
    public ChangeNormalizationIndexMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Change outline normalization index"; }

    @Override
    public String tip() { return "Change the normalization index for outline measurement orientation"; }

    @Override
    public String title() { return "Change Outline Normalization Index"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
            
        // Check for the required information:
        if (!check()) { return; }

        // Dialog title:
        String prompt = "Enter the normalization index for orientation:";
        prompt += System.lineSeparator() + "(use 2 for ellipsoidal shapes; the reconstructed";
        prompt += System.lineSeparator() + " outlines may not line up with a non-zero index)";

        // Loop until user supplies an acceptable value or cancels:
        int currentValue = controller.getOutlineNormalizationIndex();
        while (true) {

            // Ask for the number of coefficients:
            String numberString = Dialogs.input(controller,prompt,title(),
                        Integer.toString(currentValue));

            // Check the response:
            if (numberString == null) { return; } // user cancelled
            numberString = numberString.trim();
            String[] ss = numberString.split("[ ]+");
            String errorMessage = controller.checkOutlineNormalizationIndex(-100) + " Please try again.";
            if (ss.length!=1) {
              Dialogs.error(controller,errorMessage,title());
              continue;
            }

            // Convert the response to double:
            try {

                double d = Double.parseDouble(ss[0].trim());
                int newValue = (int)d;
                // Check for non-integer value:
                if ( (d-newValue)!=0 ) {
                    // Display error dialog:
                    Dialogs.error(controller,errorMessage,title());
                } else {
                    // Check if value has changed:
                    if (newValue==currentValue) { return; }
                    // Check for value out of range:
                    String msg = controller.checkOutlineNormalizationIndex(newValue);
                    if (msg==null) { // the value is acceptable
                        // Set the normalization index:
                        controller.setOutlineNormalizationIndex(newValue);
                        // Redraw the current sample:
                        controller.drawCurrentSample(false);
                        // Return successfully;
                        return;
                    } else {
                        // Display error dialog:
                        msg += " Please try again.";
                        Dialogs.error(controller,msg,title());
                    }
                } // if
                
            } catch (NumberFormatException e) {
                // Display error dialog:
                Dialogs.error(controller,errorMessage,title());
            } // try-catch

        } // while loop
        
    }
    
}
