package jmorph;

import dialogs.Dialogs;
import java.awt.Component;

/** Contains static methods for some dialogs that interact with the user.
 * @author Peter
 */
public class InteractionDialogs {
    
    // Instruction dialog responses:
    public static final int INSTRUCTIONS_OK_OPTION = 1; /** The value returned by some measuring instruction dialogs when the ok option is selected. */
    public static final int INSTRUCTIONS_SKIP_OPTION = 2; /** The value returned by some measuring instruction dialogs when the skip option is selected. */
    public static final int INSTRUCTIONS_CANCEL_OPTION = 3; /** The value returned by some measuring instruction dialogs when the cancel option is selected. */

    /** Provides a modal dialog with instructions on how to measure the measurement.
     * @param con The JMorph window.
     * @param skipFlag If false then user can select an OK button or a CANCEL button.
     * If true then an additional SKIP button is provided.
     * @param prompt
     * @param title
     * @return INSTRUCTIONS_OK_OPTION, INSTRUCTIONS_SKIP_OPTION, or INSTRUCTIONS_CANCEL_OPTION.
     */
    public static int instructions(Component con, boolean skipFlag, String prompt, String title) {

        // Display the dialog:
        int response;
        if (skipFlag) {
            response = Dialogs.question(con,prompt,title,"Continue","Skip","Stop");
            switch (response) {
                case Dialogs.YES_OPTION:
                    return INSTRUCTIONS_OK_OPTION;
                case Dialogs.NO_OPTION:
                    return INSTRUCTIONS_SKIP_OPTION;
                case Dialogs.CANCEL_OPTION:
                    return INSTRUCTIONS_CANCEL_OPTION;
                case Dialogs.CLOSED_OPTION:
                    return INSTRUCTIONS_CANCEL_OPTION;
                default: // should never happen
                    return INSTRUCTIONS_CANCEL_OPTION;
            }
        } else {
            // Use OK and CANCEL options:
            response = Dialogs.confirm(con,prompt,title);
            // Translate response to the options defined in this class:
            switch (response) {
                case Dialogs.OK_OPTION:
                    return INSTRUCTIONS_OK_OPTION;
                case Dialogs.CANCEL_OPTION:
                    return INSTRUCTIONS_CANCEL_OPTION;
                case Dialogs.CLOSED_OPTION:
                    return INSTRUCTIONS_CANCEL_OPTION;
                default: // should never happen
                    return INSTRUCTIONS_CANCEL_OPTION;
            }
        }

    }
    
}
