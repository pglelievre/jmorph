package jmorph.gui;

import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import jmorph.MeasurementVector;
import jmorph.Sample;
import jmorph.measurements.Measurement;

/** Panel for providing sample information.
 *
 * @author Peter Lelievre
 */
public final class SampleInfoPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // ------------------ Properties ------------------

    private JTextArea textArea;

    // ------------------ Constructor ------------------

    /** Creates the panel. */
    public SampleInfoPanel() {
        super();
        wrapped();
    }
    private void wrapped() {
        // Create the text area:
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(this.getBackground());
        // Create a scrollable pane with the text area in it:
        JScrollPane scroller = new JScrollPane(textArea);
        this.setLayout(new GridLayout(1,1));
        this.add(scroller);
    }

    // -------------------- Public Methods --------------------
    
    public void updateText(int numberOfSamples, int index, Sample sample, boolean displayFFTCoefficients) {

        String s;
        
        // Check for no samples:
        if (numberOfSamples==0) {
            s = "No samples loaded.";
        } else {
            
            // Check for no sample image:
            if ( sample.getImage() == null ) {

                s = "Failed to read sample file:" + System.lineSeparator() + System.lineSeparator() +
                        sample.fileURIString() + System.lineSeparator() + System.lineSeparator() +
                        "Make sure the file still exists in the same directory." + System.lineSeparator() +
                        "You may have to change the .jms JMorph session file in a" + System.lineSeparator() +
                        "text editor to fix the image paths, then reload the session." + System.lineSeparator() + System.lineSeparator() +
                        "This can also happen for some CMYK colour space and TIFF format images, perhaps others." + System.lineSeparator() +
                        "I suggest you save your files as RGB PNG files and try again." + System.lineSeparator() +
                        "Sorry, support for all image file formats is difficult to implement.";
                
            } else {

                s = "----- Measurements -----";

                // Get the calibration factor and affine transform:
                double factor = sample.calibrationFactor();
                AffineTransform trans = sample.calibrationTransform();

                // Measurement information:
                MeasurementVector mList = sample.getMeasurementList(); // extracted measurement list
                for ( int i=0 ; i<mList.size() ; i++ ) {
                    Measurement m = mList.get(i); // the ith measurement in the measurement list
                    String sfd = m.calculateStringForDisplay(factor,trans,displayFFTCoefficients);
                    if (sfd==null) {
                        sfd = "calculation problem";
                    }
                    s = s + System.lineSeparator() + System.lineSeparator() + m.getName() + ": " + sfd;
                }

            }
        }

        // Display the text:
        textArea.setText(s);

    }

}
