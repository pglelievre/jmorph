package jmorph.menutasks;

import tasks.MenuTask;
import jmorph.JMorph;

/** A task connected to a JMorph controller.
 * @author Peter
 */
public abstract class ControlledMenuTask implements MenuTask {
    
    @SuppressWarnings("ProtectedField")
    protected JMorph controller;
    
    public ControlledMenuTask(JMorph con) {
        super();
        controller = con;
    }

}
