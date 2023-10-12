package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class ChangeResamplingPowerMenuTask extends ControlledMenuTask {
    
    public ChangeResamplingPowerMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Change outline resampling power"; }

    @Override
    public String tip() { return "Change the power-of-two to use for outline resampling"; }

    @Override
    public String title() { return "Change Outline Resampling Power"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Loop until user supplies an acceptable value or cancels:
        int currentValue = controller.getOutlineResamplingPower();
        while (true) {

            // Ask for the power:
            String powerString = Dialogs.input(controller,
               "Enter the resampling power-of-two:",title(),
               Integer.toString(currentValue));

            // Check the response:
            if (powerString == null) { return; } // user cancelled
            powerString = powerString.trim();
            String[] ss = powerString.split("[ ]+");
            String errorMessage = controller.checkOutlineResamplingPower(-100) + " Please try again.";
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
                    Dialogs.error(controller,errorMessage, title());
                } else {
                    // Check if value has changed:
                    if (newValue==currentValue) { return; }
                    // Check for value out of range:
                    String msg = controller.checkOutlineResamplingPower(newValue);
                    if (msg==null) { // the value is acceptable
                        // Set the resampling power:
                        controller.setOutlineResamplingPower(newValue);
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
