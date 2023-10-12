package jmorph.menutasks;

import jmorph.JMorph;

/**
 * @author Peter
 */
public final class ChangeAllOrderMenuTask extends ControlledMenuTask {
    
    public ChangeAllOrderMenuTask(JMorph con) {
        super(con);
    }
    
    @Override
    public String text() { return "Change zoom/calibration order"; }

    @Override
    public String tip() { return "Change the order of zooming and calibration when all measurements are performed sequentially"; }

    @Override
    public String title() { return "Change Zoom/Calibration Order"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        if (!check()) { return; }
        controller.selectAllOrder(title());
    }
    
}
