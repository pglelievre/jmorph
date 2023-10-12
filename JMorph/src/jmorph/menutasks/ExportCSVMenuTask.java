package jmorph.menutasks;

import dialogs.Dialogs;
import fileio.FileUtils;
import java.io.File;
import javax.swing.JFileChooser;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class ExportCSVMenuTask extends ControlledMenuTask {
    
    public ExportCSVMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Export to CSV file"; }

    @Override
    public String tip() { return "Export the sample measurements to a CSV file"; }

    @Override
    public String title() { return "Export to CSV File"; }

    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.hasMeasurements() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Get the file to write to:
        JFileChooser chooser = new JFileChooser();
        File saveDirectory = controller.getSaveDirectory();
        chooser.setCurrentDirectory(saveDirectory);
        chooser.setDialogTitle(title());
        chooser.setMultiSelectionEnabled(false);
        int response = chooser.showSaveDialog(controller);

        // Check response and get the selected file:
        if (response != JFileChooser.APPROVE_OPTION) { return; }
        File file = chooser.getSelectedFile();

        // Set the save directory to the chosen directory:
        saveDirectory = chooser.getCurrentDirectory();
        controller.setSaveDirectory(saveDirectory);

        // Make sure the file extension is .csv:
        String root = FileUtils.getRoot(file);
        file = new File( root + ".csv" );
        Boolean ok = controller.exportCSV(file);

        // Write the file:
        if (ok) {
            // Display success message:
            Dialogs.inform(controller,"Sample measurements exported successfully.",title());
        } else {
            // Display error message:
            Dialogs.error(controller,"Failed to write the file.",title());
        }
        
    }
    
}
