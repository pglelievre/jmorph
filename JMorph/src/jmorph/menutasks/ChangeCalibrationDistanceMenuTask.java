package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class ChangeCalibrationDistanceMenuTask extends ControlledMenuTask {
    
    private boolean doAll;
    private boolean success;
    
    public ChangeCalibrationDistanceMenuTask(JMorph con, boolean b) {
        super(con);
        doAll = b;
        success = false;
    }
    
    public boolean getSuccess() { return success; }
    
    @Override
    public String text() {
        if (doAll) {
            return "Change calibration distance for all samples";
        } else {
            return "Change calibration distance for current sample";
        }
    }

    @Override
    public String tip() { return "Change the spatial distance corresponding to the calibration line"; }

    @Override
    public String title() {
        if (doAll) {
            return "Change Calibration Distance for all Samples";
        } else {
            return "Change Calibration Distance for Current Sample";
        }
    }

    @Override
    public boolean check() {
        return controller.hasSamples();
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Loop until user supplies an acceptable value or cancels:
        success = false;
        double currentValue = controller.getCalibrationDistance();
        while (true) {

            // Ask for the length:
            String lengthString = Dialogs.input(controller,
                    "Enter the calibration distance:",title(),
                    controller.getCalibrationDistanceString());

            // Check the response:
            if (lengthString == null) { return; } // user cancelled
            lengthString = lengthString.trim();
            String[] ss = lengthString.split("[ ]+");
            String errorMessage = "You must enter a single positive value. Please try again.";
            if (ss.length!=1) {
                Dialogs.error(controller,errorMessage,title());
                continue;
            }

            // Convert the response to double:
            try {
                double newValue = Double.parseDouble(ss[0].trim());
                // Check for non-positive value:
                if (newValue<=0) {
                    // Display error dialog:
                    Dialogs.error(controller,errorMessage,title());
                } else { // value was acceptable
                    // Check if value has changed:
                    if (newValue==currentValue) {
                        success = true; // user didn't cancel
                        return;
                    }
                    // Set the length for the current sample and possibly all samples:
                    controller.setCalibrationDistance(newValue,doAll);
                    // Update the info panel because the measurement values will change with the calibration:
                    controller.updateInfoPanel();
                    // Return successfully;
                    success = true;
                    return;
                } // if
            } catch (NumberFormatException e) {
                // Display error dialog:
                Dialogs.error(controller,errorMessage,title());
            } // try-catch

        } // while loop
        
    }
    
}
