package jmorph.menutasks;

import dialogs.Dialogs;
import jmorph.JMorph;
import jmorph.measurements.OutlineMeasurement;

/**
 * @author Peter
 */
public final class ChangeFourierAnalysisMethodMenuTask extends ControlledMenuTask {
    
    public ChangeFourierAnalysisMethodMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Select Fourier analysis method"; }

    @Override
    public String tip() { return "Change the method used for Fourier analysis"; }

    @Override
    public String title() { return "Select Fourier Analysis Method"; }

    @Override
    public boolean check() { return true; }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Display the dialog:
        String prompt = "Select the method you want to use:";
        String[] options = {"Radius-vs-Theta","Tangent-vs-ArcLength","None"};
        int response,j0,method;
        switch(controller.getFourierAnalysisMethod()) {
            case OutlineMeasurement.FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA:
                j0 = 0;
                break;
            case OutlineMeasurement.FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH:
                j0 = 1;
                break;
            default:
                j0 = 2;
                break;
        }
        response = Dialogs.question(controller,prompt,title(),options[0],options[1],options[2],options[j0]);
        switch (response) {
            case Dialogs.YES_OPTION:
                method = OutlineMeasurement.FOURIER_ANALYSIS_METHOD_RADIUS_VS_THETA;
                break;
            case Dialogs.NO_OPTION:
                method = OutlineMeasurement.FOURIER_ANALYSIS_METHOD_TANGENT_VS_ARCLENGTH;
                break;
            default:
                method = OutlineMeasurement.FOURIER_ANALYSIS_METHOD_NONE;
                break;
        }

        // Save the choice & update the outline measurements:
        controller.setFourierAnalysisMethod(method);
        // Redraw the current sample:
        controller.drawCurrentSample(false);
        
    }
    
}
