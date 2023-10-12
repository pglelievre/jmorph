package jmorph.gui;

import gui.TextBar;
import jmorph.Sample;

/** The TextBar object that provides information about the current section.
 * @author Peter
 */
public final class SampleTextBar extends TextBar {
    private static final long serialVersionUID = 1L;
    
    public void updateText(int numberOfSamples, int index, Sample sample) {

        // Create the text and tool tip strings:
        String text,tip;
        if ( numberOfSamples==0 || sample==null ) {
            text = "No samples loaded.";
            tip = text;
        } else {
            // Sample i of n: name
            String s = "Sample " + (index+1) + " of " + numberOfSamples + ": ";
            text = s + sample.shortName();
            tip  = s + sample.longName();
        }
        
        // Display the text in the sample bar:
        setText(text);
        
        // Set the tool tip text in case the window is resized too small to see the whole path:
        setToolTipText(tip);
        
    }
    
}
