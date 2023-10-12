package jmorph.menutasks;

import dialogs.Dialogs;
import javax.swing.JFileChooser;
import jmorph.JMorph;
import jmorph.measurements.AreaMeasurement;
import jmorph.measurements.OutlineMeasurement;
import jmorph.measurements.SplineMeasurement;

/**
 * @author Peter
 */
public final class ExportOutlinesMenuTask extends ControlledMenuTask {
    
    public ExportOutlinesMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Export outlines"; }

    @Override
    public String tip() { return "Export the sample outlines to TXT files"; }

    @Override
    public String title() { return "Export Outlines"; }

    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.hasMeasurements() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Check if there are any area or outline measurements:
        String[] typeStrings = new String[3];
        typeStrings[0] = new AreaMeasurement().typeString(); //"area";
        typeStrings[1] = new SplineMeasurement().typeString(); //"spline";
        typeStrings[2] = new OutlineMeasurement().typeString(); //"outline";
        int[] mask = controller.typeStringMask(typeStrings);
        if (mask.length==0) {
            Dialogs.error(controller,"There are no outlines to use.",title());
            return;
        }

        // Ask the user to specify which area or outline measurement to use for the outline:
        int index = controller.chooseMeasurementDialogMasked(mask,"Select the outline to use:",title());
        if (index<0) { return; } // user cancelled

        // Get the root name of the file to write to:
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory( controller.getSaveDirectory() );
        chooser.setDialogTitle("Specify the path for the output files:");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int response = chooser.showSaveDialog(controller);

        // Check response and get the selected path:
        if (response != JFileChooser.APPROVE_OPTION) { return; }

        // Set the save directory to the chosen directory:
        controller.setSaveDirectory( chooser.getCurrentDirectory() );

        // Write the files:
        if (controller.exportOutlines(index)) {
            // Display success message:
            Dialogs.inform(controller,"Sample outlines exported.",title());
        } else {
            // Display warning message:
            Dialogs.warning(controller,"Some sample outlines were not exported.",title());
        }
        
    }
    
}
