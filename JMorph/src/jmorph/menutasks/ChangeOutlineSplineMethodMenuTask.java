package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class ChangeOutlineSplineMethodMenuTask extends ControlledMenuTask {
    
    public ChangeOutlineSplineMethodMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Select outline spline method"; }

    @Override
    public String tip() { return "Change the type of spline used for outline measurements"; }

    @Override
    public String title() { return "Select Outline Spline Method"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Display the dialog:
        String prompt = "Select the spline you want to use:";
        //Object[] options = {};
        int response;
        if (controller.getUseCircleSpline()) {
            response = Dialogs.question(controller,prompt,title(),"circle-preserving","Catmull–Rom","Cancel","circle-preserving");
        } else {
            response = Dialogs.question(controller,prompt,title(),"circle-preserving","Catmull–Rom","Cancel","Catmull–Rom");
        }
        boolean use;
        switch (response) {
            case Dialogs.YES_OPTION:
                use = true;
                break;
            case Dialogs.NO_OPTION:
                use = false;
                break;
            default:
                return;
        }

        // Update the area and outline measurements:
        controller.setUseCircleSpline(use);

        // Redraw the current sample:
        controller.drawCurrentSample(false);
        
    }
    
}
