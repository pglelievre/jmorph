package jmorph.menutasks;

import dialogs.ListDialog;
import jmorph.JMorph;

/**
 * @author Peter
 */
public final class SelectAllMeasurementsMenuTask extends ControlledMenuTask {
    
    public SelectAllMeasurementsMenuTask(JMorph con) { super(con); }
    
    @Override
    public String text() { return "Select measurements to work with"; }

    @Override
    public String tip() { return "Select which measurements to display and to include when measuring multiple measurements in sequence"; }

    @Override
    public String title() { return "Select Measurements to Work With"; }

    @Override
    public boolean check() {
        return ( controller.hasSamples() && controller.hasMeasurements() );
    }

    @Override
    public void execute() {
        
        // Check for the required information:
        if (!check()) { return; }

        // Generate the list of all measurements, including zoom and calibration measurements:
        String[] items0 = controller.measurementVectorNameAndTypeList() ;
        int nExtra = 4; // should equal the number of extra items added below
        int nItems = items0.length + nExtra;
        String[] items = new String[nItems];
        items[0] = "Zoom Box";
        items[1] = "Calibration Line";
        items[2] = "Calibration Distance (keyboard input)";
        items[3] = "Calibration Origin Point";
        System.arraycopy(items0, 0, items, nExtra, items0.length);
        //for ( int i=0 ; i<items0.length ; i++ ) {
        //    items[i+nex] = items0[i];
        //}
        //items0 = null; // no longer needed

        // Fill initial selection array:
        boolean[] marked = new boolean[nItems];
        int nMarked = 0;
        for ( int i=0 ; i<nItems ; i++ ) {
            switch(i) {
                case 0: // zoom
                    marked[i] = controller.getZoomMarked();
                    break;
                case 1: // calibration length
                    marked[i] = controller.getCalibrationMarked();
                    break;
                case 2: // calibration distance
                    marked[i] = controller.getDoCalibrationDistanceAuto();
                    break;
                case 3: // calibration origin
                    marked[i] = controller.getOriginMarked();
                    break;
                default: // user-supplied measurement
                    marked[i] = controller.getMeasurement(i-nExtra).getMarked(); // i-nex is index into measurement list
                    break;
            }
            if (marked[i]) { nMarked++; }
        }
        int[] init;
        if (nMarked==0) {
            init = new int[nItems];
            for ( int i=0 ; i<nItems ; i++ ) {
                init[i] = i;
            }
        } else {
            init = new int[nMarked];
            nMarked = 0;
            for ( int i=0 ; i<nItems ; i++ ) {
                if (marked[i]) {
                    init[nMarked] = i;
                    nMarked++;
                }
            }
        }
        
        // Display the dialog:
        String prompt = tip() + ":";
        ListDialog listDialog = new ListDialog(controller,prompt,title(),items,true,init);

        // Check the response:
        int[] selectedItems = listDialog.getSelectedIndices();
        if (selectedItems == null) { return; } // user cancelled
        //listDialog = null; // no longer needed

        // Unmark all measurements:
        controller.setDoCalibrationDistanceAuto(false);
        controller.markMeasurements(false);
        controller.markZoom(false);
        controller.markCalibration(false);
        controller.markOrigin(false);

        // Loop over each selected measurement and mark them as required:
        for ( int i=0 ; i<selectedItems.length ; i++ ) {
            int k = selectedItems[i]; // index into list
            switch(k) {
                case 0: // zoom
                    // Tell the sample list to mark the zoom measurement for all samples as required:
                    controller.markZoom(true);
                    break;
                case 1: // calibration length
                    // Tell the sample list to mark the calibration measurement for all samples as required:
                    controller.markCalibration(true);
                    break;
                case 2: // calibration distance
                    controller.setDoCalibrationDistanceAuto(true);
                    break;
                case 3: // calibration origin
                    // Tell the sample list to mark the origin measurement for all samples as required:
                    controller.markOrigin(true);
                    break;
                default: // user-supplied measurement
                    // Tell the measurement and sample lists to mark the measurement as required:
                    controller.markMeasurement(k-nExtra,true); // k-nex is index into measurement list
                    break;
            }
        }
        
        // Update the display if displaying all measurements:
        if (controller.getDisplayMeasurements()) {
            controller.drawCurrentSample(false);
        }
        
    }
    
}
