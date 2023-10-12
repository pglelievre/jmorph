package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;
import jmorph.SessionLoader;

/**
 * @author Peter
 */
public final class LoadSessionMenuTask extends ControlledMenuTask {
    
    public LoadSessionMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Load session"; }

    @Override
    public String tip() { return "Load a previously saved JMorph session"; }

    @Override
    public String title() { return "Load Session"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        // Check for the required information:
        if (!check()) { return; }
        // Warn the user:
        if ( controller.hasSamples() || controller.hasMeasurements() ) {
            int response = Dialogs.confirm(controller,"This will overwrite the current session. Do you want to continue?",title());
            if ( response != Dialogs.OK_OPTION ) { return; }
        }
        // Ask for the name of the saved session file:
        boolean ok = controller.chooseOpenSession(title());
        if (!ok) { return; }
        // Load the session file:
        int err = SessionLoader.loadSessionAscii1(controller,controller.getSessionFile());
        // Check for error:
        switch(err) {
            case -2:
                // Inform the user:
                Dialogs.error(controller,"Sorry, that session was from an earlier version of JMorph.",title());
                break;
            case -1:
                Dialogs.error(controller,"Failed to load session.",title());
                break;
            default:
                // Make sure the measurement list is copied to all the sample objects:
                //sampleVector.replaceMeasurementLists(measurementVector,0);
                // Enable or disable menu items:
                controller.checkClickableItemsEnabled();
                // Repaint the current sample:
                controller.drawCurrentSample(false);
                // Show success dialog:
                Dialogs.inform(controller,"Session loaded successfully.",title());
        }
    }
    
}
