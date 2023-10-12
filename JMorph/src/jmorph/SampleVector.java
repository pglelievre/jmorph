package jmorph;

import dialogs.Dialogs;
import fileio.FileUtils;
import gui.HasImage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/** A list of samples on which to take morphometric measurements.
 * @author Peter Lelievre
 */
public class SampleVector {

    // ------------------ Properties -------------------

    private int currentIndex = 0; // the current sample being viewed
    
    // Components used for temporary tasks:
    private boolean userCancelled;
    private JDialog progressDialog;
    
    // Favour composition over inheritence!
    private final ArrayList<Sample> vector = new ArrayList<>();

    // ------------------ Constructor ------------------

    public SampleVector() {}

    // ------------------- Deep Copy ------------------

    /** Replaces (via a deep copy) the measurement list for each sample in the list.
     * @param mList The new measurement list.
     * @param i0 The starting index to replace the measurement list from.
     * You should always replace all the measurements (set i0=0) unless you have just added
     * some new samples to the list, and therefore don't want to overwrite any measurements
     * already defined for the existing samples. However, if i0/=0 then you should make sure
     * that the measurement list added to any new samples contains the same measurements as
     * the measurement list in the existing samples.
     */
    public void replaceMeasurementLists(MeasurementVector mList, int i0) {

        // Loop over each sample, starting with the indicated index:
        for ( int i=i0 ; i<size() ; i++ ) {

            // Get the ith sample:
            Sample s = get(i);

            // Replace the measurement list with a deep copy:
            s.setMeasurementList(mList.deepCopy());

        }

    }
    
    // -------------------- Getters --------------------
    
    /** Getter for a particular sample in the list.
     * @param i
     * @return  */
    public Sample get(int i) {
        return vector.get(i);
    }

    /** Getter for the current sample index.
     * @return  */
    public int getCurrentIndex() { return currentIndex; }
    
    /** Getter for the current Sample object or null if no samples exist.
     * @return  */
    public Sample getCurrentSample() {
        // Check for no samples loaded:
        if (size()==0) {
            return null;
        } else {
            return get(currentIndex);
        }
    }
    
    public boolean getUserCancelledWhileLoading() { return userCancelled; }

    // -------------------- Setters --------------------

    /** Setter for the current sample index.
     * @param i */
    public void setCurrentSampleIndex(int i) {
        // Check for out of range value:
        if ( i<0 || i>=size() ) { return; }
        // Set the new current index value:
        currentIndex = i;
    }

    /** Sets the calibration distance for all the samples.
     * @param len The new calibration distance.
     */
    public void setCalibrationDistanceAll(double len) {
        // Loop over each sample:
        for (int i=0 ; i<size() ; i++ ) {
            // Tell the sample to set the calibration length:
            Sample s = get(i);
            s.setCalibrationDistance(len);
        }
    }

    /** Sets the outline resampling power for all the samples.
     * @param p The new value for the resampling power.
     */
    public void setResamplingPower(int p) {
        // Loop over each sample:
        for (int i=0 ; i<size() ; i++ ) {
            // Tell the sample to set the resampling power:
            Sample s = get(i);
            s.setResamplingPower(p);
        }
    }

    /** Sets the highest Fourier coefficients used in outline reconstructions for all the samples.
     * @param n The new highest coefficient to use.
     */
    public void setHighestFFTCoefficient(int n) {
        // Loop over each sample:
        for (int i=0 ; i<size() ; i++ ) {
            // Tell the sample to set the number of FFT coefficients:
            Sample s = get(i);
            s.setHighestFFTCoefficient(n);
        }
    }

    /** Sets the normalization index used in outline analyses for all the samples.
     * @param n The new normalization index value to use.
     */
    public void setNormalizationIndex(int n) {
        // Loop over each sample:
        for (int i=0 ; i<size() ; i++ ) {
            // Tell the sample to set the normalizaiton index:
            Sample s = get(i);
            s.setNormalizationIndex(n);
        }
    }

    /** Sets the type of spline used for spline outline measurements for all the samples.
     * @param use Set to true to use a cirle-preserving spline, false for a Kochanek–Bartels spline.
     */
    public void setUseCircleSpline(boolean use) {
        // Loop over each sample:
        for (int i=0 ; i<size() ; i++ ) {
            // Tell the sample to set the outline spline:
            Sample s = get(i);
            s.setUseCircleSpline(use);
        }
    }

    /** Sets the method of Fourier outline anaylsis for all the samples.
     * @param method Should be one of the methods defined in the jmorph.measurements.Outline class.
     */
    public void setFourierAnalysisMethod(int method) {
        // Loop over each sample:
        for (int i=0 ; i<size() ; i++ ) {
            // Tell the sample to set the method of Fourier outline analysis:
            Sample s = get(i);
            s.setFourierAnalysisMethod(method);
        }
    }

    /** Sets the painting point width for all measurements for all the samples.
     * @param w The point width.
     */
    public void setPointWidth(int w) {
        for (int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.setPointWidth(w);
        }
    }

    /** Sets the painting line width for all measurements for all the samples.
     * @param w The line width.
     */
    public void setLineWidth(int w) {
        for (int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.setLineWidth(w);
        }
    }

//    /** Sets a sample's painting point size for the calibration origin measurements for all the samples.
//     * @param w The point width.
//     */
//    public void setOriginWidth(int w) {
//        for (int i=0 ; i<size() ; i++ ) {
//            Sample s = get(i);
//            s.setOriginWidth(w);
//        }
//    }

    /** Sets a sample's painting colour for calibration measurements (Calibration and Origin) for all the samples.
     * @param col The new painting colour.
     */
    public void setCalibrationColour(Color col) {
        for (int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.setCalibrationColour(col);
        }
    }

    /** Sets a measurement's painting colour(s) for all the samples.
     * @param im The index of the measurement to use.
     * @param col1 The new primary painting colour for the measurement.
     * @param col2 The new secondary painting colour for the measurment.
     */
    public void setMeasurementColor(int im, Color col1, Color col2) {
        for (int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.setMeasurementColour(im,col1,col2);
        }
    }

    // -------------------- Public Methods --------------------

    /** Returns the number of samples in the list.
     * @return  */
    public int size() {
        return vector.size();
    }

    /** Clears all samples from the list. */
    public void clear() {
        vector.clear();
    }

    /** Removes all samples from the list at and beyond the specified index so that the resulting length is n.
     * @param n */
    public void clearFrom(int n) {
        while (vector.size()>n) {
            vector.remove(vector.size()-1);
        }
    }

    /** Removes the first n samples from the list.
     * @param n */
    public void clearTo(int n) {
        for ( int i=0 ; i<n ; i++ ) {
            vector.remove(0);
        }
    }

    /** Removes specified samples from the list.
     * @param selection The indices to remove.
     * If any of the indices don't exist (are out of range) then they are ignored.
     */
    public void removeSamples(int[] selection) {

        // Sort the selection:
        Arrays.sort(selection); // increasing order

        // Loop over each selection, from highest index to lowest:
        for ( int i=selection.length-1 ; i>=0 ; i-- ) {
            try {
                vector.remove(selection[i]);
            } catch ( ArrayIndexOutOfBoundsException e ) {
                // just ignore the error
            }
        }
        
        // Reset the IDs:
        resetIDs();

    }

    /** Removes a measurement from the measurement list of each sample.
     * @param index The index of the measurement to remove.
     */
    public void removeMeasurement(int index) {
        // Loop over each sample:
        for ( int i=0 ; i<size() ; i++ ) {
            // Remove the measurement from the ith sample:
            get(i).getMeasurementList().remove(index);
        }
    }

    /** Adds a measurement to the measurement list in each sample.
     * @param typeString The type of new measurement.
     * @param name A name for the new measurement.
     */
    public void addNewMeasurement(String typeString, String name) {
        // Loop over each sample:
        for ( int i=0 ; i<size() ; i++ ) {
            // Add the measurement to the ith sample:
            get(i).getMeasurementList().addNew(typeString,name);
        }
    }
    
    /** Copies the current sample n-1 times.
     * @param n */
    public void splitCurrentSample(int n) {
        
        // Check samples exist:
        if (size()==0) { return; } 
        
        // Get the current sample:
        Sample currentSample = get(currentIndex);
        
        // Copy n-1 times:
        for (int i=1 ; i<n ; i++) {
            Sample newSample = currentSample.deepCopy(false); // deep copy current sample (except for HasImage object)
            vector.add(currentIndex+i,newSample); // add new sample to the list
        }
        
        // Reset the IDs:
        resetIDs();
        
    }
    
    /** *  Adds new samples to the list and sets the sample files to those in the supplied array of files. Does NOT clear the sample list.
     * @param con
     * @param readNow
     * @param title
     * @param files Array of files where sample images exist.
     */
    public void addSamplesFromFiles(final Frame con, boolean readNow, String title, final File[] files) {
        
        // Initialization:
        userCancelled = false;
        
        // Determine whether we need a progress bar or not:
        boolean showProg;
        if ( readNow ) {
            // Calculate the size of the files:
            int nFiles = files.length;
            long byteSum = 0;
            for ( int i=0 ; i<nFiles ; i++ ) {
                long b = files[i].length();
                byteSum += b;
            }
            // Check the size of the files and see if the progress bar is required:
            //showProg = !( byteSum<=10000000L || nFiles<=1 ); // HARDWIRE 10Mb
            showProg = ( byteSum>10000000L && nFiles>1 ); // HARDWIRE 10Mb
        } else {
            showProg = false;
        }
        
        // If we aren't showing the progress then we use a different method than if we are:
        if (!showProg) {
            readFiles(files,readNow);
            return;
        }
        
        // Modal progress bar from here:
        // http://www.coding-dude.com/wp/java/modal-progress-bar-dialog-java-swing/
        progressDialog = new JDialog(con, "Loading Sample Image Files", true); // true make the dialog modal
        final JLabel statusLabel = new JLabel("Please wait ..."); // a label to indicate the state of the processing
        final JProgressBar progressBar = new JProgressBar(0,files.length);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelListener());
        progressBar.setIndeterminate(false); // a determinate progress bar
        progressDialog.add(BorderLayout.NORTH,statusLabel);
        progressDialog.add(BorderLayout.CENTER,progressBar);
        progressDialog.add(BorderLayout.SOUTH,cancelButton);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // prevent the user from closing the dialog
        progressDialog.setSize(300,90);
        progressDialog.setResizable(false);
        progressDialog.setLocationRelativeTo(con);

        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                readFilesTask(files,statusLabel,progressBar);
                return null;
            }
            @Override
            protected void done() {
                progressDialog.dispose(); // close the modal dialog
            }
        };

        sw.execute(); // starts the processing on a separate thread
        progressDialog.setVisible(true); // blocks user input as long as the processing task is working
        
    }
    
    private void readFiles(File[] files, boolean readNow) {
        // Loop over the files:
        for (File file : files) {
            // Process the current file:
            readFile(file,readNow);
        }
    }
    
    private void readFilesTask(File[] files, JLabel statusLabel, JProgressBar progressBar) {
        // Loop over the files:
        for (int i=0 ; i<files.length ; i++ ) {
            // Process the current file:
            readFile(files[i],true); // read the image file now
            // Check if progress bar still exists:
            if (progressDialog==null) {
                continue;
            } else {
                if (!progressDialog.isDisplayable()) {
                    continue;
                }
            }
            // Update the progress bar:
            String message = String.format("Loaded %d of %d files.",i+1,files.length);
            statusLabel.setText(message);
            progressBar.setValue(i+1);
        }
    }
    private class CancelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            userCancelled = true;
            // Close the dialog:
            progressDialog.dispose(); //close the modal dialog
        }
    }
    
    private void readFile(File file, boolean readNow) {
        // Check if a sample already exists with the current image file:
        HasImage existingHasImage = findFile(file);
        Sample s;
        if ( existingHasImage == null ) { // current image file NOT already linked to a sample
            // Create the sample and possibly read the image file:
            s = new Sample(file,readNow);
        } else { // current image file IS already linked to a sample
            // Create the sample and link to existing HasImage object:
            s = new Sample(file,existingHasImage);
        }
        // Add the new sample object to the vector of samples:
        vector.add(s);
    }
    
    /** Searches for a sample with the current file and returns the HasImage object if found, null otherwise.
     * @param file
     * @return 
     */
    private HasImage findFile(File file) {
        // Loop over each sample:
        for ( int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            // Compare supplied file against that for current sample:
            if ( s.compareFile(file) ) {
                return s.getHasImage();
            }
        }
        // If not found then return null:
        return null;
    }
    
    /** Exports the sample measurements to a comma-separated-variable text file.
     * @param file The file to write to.
     * @param header
     * @return True if file written successfully, false otherwise.
     */
    public boolean exportCSV(File file, String header) {

        // Check that samples exist:
        if (size()==0) { return false; }

        // Open the file for writing:
        BufferedWriter writer = FileUtils.openForWriting(file);
        if (writer==null) { return false; }
        
        // Loop over each sample:
        String textLine;
        for ( int i=-1 ; i<size() ; i++ ) {
            if (i<0) {
                // Write the header:
                textLine = header;
            } else {
                // Get the ith sample:
                Sample s = get(i);
                // Write the ith sample to the file:
                textLine = s.writeMeasurementsCSV();
                if (textLine==null) {
                    // Close the file and return false:
                    FileUtils.close(writer);
                    // TODO: could also delete the file.
                    return false;
                }
            }
            if ( !FileUtils.writeLine(writer,textLine) ) {
                FileUtils.close(writer);
                return false;
            }
        }

        // Close the file for writing:
        FileUtils.close(writer);
        
        // Return successfully:
        return true;

    }

    /** Exports the sample outline to a file, if it has been measured.
     * The files names are called [sample]_[measurement].txt" where [sample] is the
     * original name of the sample image file (minus extension) and
     * [measurement] is the name of the area, spline or outline measurement used for the outline.
     * @param path Path name for saving the outline files to.
     * @param index Index of the outline (area, spline or outline measurement) in the measurement list.
     * @return True if the files are written successfully.
     */
    public boolean exportOutlines(int index, File path) {

        // Check that samples exist:
        if (size()==0) { return false; }

        // Loop over each sample:
        Boolean ok = true;
        for ( int i=0 ; i<size() ; i++ ) {
            // Write the sample outline file if possible:
            ok = ( ok && get(i).exportOutline(path,index) );
        }
        return ok;

    }

    /** Marks all measurements in the measurement lists of every sample.
     * @param mark The value to mark all the measurements with.
     */
    public void markMeasurements(boolean mark) {
        for ( int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.markMeasurements(mark);
        }
    }

    /** Marks a measurement in the measurement list of every sample.
     * @param im The index of the measurement to mark.
     * @param mark The value to mark the measurement with.
     */
    public void markMeasurement(int im, boolean mark) {
        for ( int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.markMeasurement(im,mark);
        }
    }
    
    // These three assume that if one calibration/origin/zoom measurement is marked then all are!
    public boolean getCalibrationMarked() { return get(0).getCalibration().getMarked(); }
    public boolean getOriginMarked() { return get(0).getOrigin().getMarked(); }
    public boolean getZoomMarked() { return get(0).getZoom().getMarked(); }

    /** Marks the Calibration measurement for every sample.
     * @param mark The value to mark the Calibration measurement with.
     */
    public void markCalibration(boolean mark) {
        for ( int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.markCalibration(mark);
        }
    }

    /** Marks the Origin measurement for every sample.
     * @param mark The value to mark the Origin measurement with.
     */
    public void markOrigin(boolean mark) {
        for ( int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.markOrigin(mark);
        }
    }

    /** Marks the Zoom measurement for every sample.
     * @param mark The value to mark the Zoom measurement with.
     */
    public void markZoom(boolean mark) {
        for ( int i=0 ; i<size() ; i++ ) {
            Sample s = get(i);
            s.markZoom(mark);
        }
    }

    /** Returns a string array containing the sample names.
     * @return  */
    public String[] nameList() {

        // Create new object to return:
        String[] names = new String[size()];

        // Loop over each sample:
        for (int i=0 ; i<size() ; i++ ) {
            // Get the short name for the current sample:
            names[i] = (i+1) + ": " + get(i).shortName();
        }

        // Return the new object:
        return names;

    }
    
    /** Resets the IDs in the samples, e.g. if a sample is removed or added.
     */
    public void resetIDs() {
        // Create working objects:
        int n = size();
        ArrayList<File> files = new ArrayList<>();
        int[] counts1 = new int[n];
        int[] counts2 = new int[n];
        Arrays.fill(counts1,0); // initialization before summation
        Arrays.fill(counts2,0); // initialization before summation
        // Loop over each sample:
        for (int i=0 ; i<n ; i++) {
            Sample s = get(i);
            // Get the image file:
            File f = s.getFile();
            // Find the file in the list:
            int j = files.indexOf(f);
            if (j>=0) { // file is already in the list
                // Increment the first counter:
                counts1[j]++;
            } else { // name not in the list yet
                // Add the file to the list and increment the first counter:
                files.add(f);
                counts1[files.size()-1] = 1;
            }
        }
        // Loop over each sample again:
        for (int i=0 ; i<n ; i++) {
            Sample s = get(i);
            // Get the image file:
            File f = s.getFile();
            // Find the file in the list:
            int j = files.indexOf(f);
            // Check the first counter:
            if (counts1[j]>1) { // the file is duplicated
                // Increment the second counter and set the ID to that count:
                counts2[j]++;
                s.setID(counts2[j]);
            } else { // the file is not duplicated
                // Set the ID to zero:
                s.setID(0);
            }
        }
    }

}
