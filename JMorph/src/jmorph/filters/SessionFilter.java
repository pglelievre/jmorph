package jmorph.filters;

import fileio.FileUtils;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/** File type filter for jmorph session files.
 * @author Peter Lelievre
 */
public class SessionFilter extends FileFilter {

    // -------------------- Properties -------------------

    public final static String JMS = "jms";

    // -------------------- Public Methods -------------------

    /** Checks if a file contains the specified jmorph session extension.
     * @param f The file to check.
     * @return True if the file contains the specified jmorph session extension, false otherwise.
     */
    @Override
    public boolean accept(File f) {

        // Do not accept directories:
        if (f.isDirectory()) { return false; }

        // Accept ses and dat files:
        String extension = FileUtils.getExtension(f);
        if (extension != null) {
            return extension.equals(JMS);
        }

        return false;

    }

    /** Returns a description for this filter.
     * @return A description for this filter.
     */
    @Override
    public String getDescription() { return "JMorph session files"; }

}