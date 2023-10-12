package jmorph.menutasks;

import jmorph.JMorph;
import jmorph.Sample;

/**
 * @author Peter
 */
public final class ClearZoomMenuTask extends ControlledMenuTask {
    
    public ClearZoomMenuTask(JMorph con) {
        super(con);
    }
    
    @Override
    public String text() { return "Clear zoom"; }

    @Override
    public String tip() { return "Clear the zoom box for the current sample"; }

    @Override
    public String title() { return "Clear zoom"; }
    
    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.currentSampleImageExists() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }
        
        // Get the current sample:
        Sample s = controller.getCurrentSample();
        
        // Check if the sample has been zoomed already:
        if (s.isZoomed()) {
            // Clear the zoom box:
            s.clearZoom();
            // Repaint the current sample so the entire image is now showing:
            controller.drawCurrentSample(false);
        }
        
    }
    
}
