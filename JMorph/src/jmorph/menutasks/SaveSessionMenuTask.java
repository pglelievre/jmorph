package jmorph.menutasks;

import dialogs.Dialogs;
import fileio.FileUtils;
import java.io.File;
import jmorph.JMorph;
import jmorph.SessionSaver;
import jmorph.filters.SessionFilter;

/**
 * @author Peter
 */
public final class SaveSessionMenuTask extends ControlledMenuTask {
    
    public SaveSessionMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Save session"; }

    @Override
    public String tip() { return "Save the current JMorph session"; }

    @Override
    public String title() { return "Save Session"; }

    @Override
    public boolean check() {
     return ( controller.hasSamples() || controller.hasMeasurements() );
    }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Ask for the file name for saving:
        boolean ok = controller.chooseSaveSession(title());
        if (!ok) { return; }
        // Give the file the .fms extension:
        File sessionFile = controller.getSessionFile();
        String root = FileUtils.getRoot(sessionFile);
        sessionFile = new File( root + "." + SessionFilter.JMS );
        controller.setSessionFile(sessionFile);
        // Check for file overwrite:
        if (sessionFile.exists()) {
            int response = Dialogs.confirm(controller,"Overwrite the existing file?",title());
            if (response != Dialogs.OK_OPTION) { return; }
        }
        // Save ascii file:
        ok = SessionSaver.saveSessionAscii1(controller,sessionFile);
        // Display:
        if (ok) {
            Dialogs.inform(controller,"Session saved successfully.",title());
        } else {
            Dialogs.error(controller,"Failed to save session.",title());
        }
    }
    
}
