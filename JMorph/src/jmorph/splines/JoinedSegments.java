package jmorph.splines;

/** A of bunch of line segments all joined together.
 * @author Peter Lelievre.
 */
@SuppressWarnings("PublicInnerClass")
public class JoinedSegments {

    // ------------------ Properties -------------------

    private double[] segmentLengths = null; // the segment lengths
    private double totalLength = 0.0; // the total length of the segments (sum of segment lengths)

    // ------------------ Constructor -------------------

    /**
     * @param lens The segment lengths that are joined together.
     */
    public JoinedSegments(double[] lens) {
        segmentLengths = lens;
        calculateTotalLength();
    }

    // ------------------ Getters -------------------

    /** Returns the total length of the joined segments.
     * @return The sum of the joined segment lengths.
     */
    public double getTotalLength() {
        if (totalLength<=0.0) { calculateTotalLength(); }
        return totalLength;
    }

    // ------------------ Static Classes -------------------

    /** The class definition for the object returned by the findSegment method. */
    @SuppressWarnings("PublicField")
    public static class FindSegmentInfo {
        public int index = -1; /** An index of a segment in a JoinedSegments object. */
        public double location = -1; /** A location (normalized length) along a segment in a JoinedSegments object. */
        public FindSegmentInfo() {}
    }

    // ------------------ Public Methods -------------------

    /** Determines which segment is at a particular normalized location along the total length.
     * @param t The normalized location (should be on [0,1]).
     * @return A FindSegmentInfo object containing the index of the segment and normalized location along the segment; null if a problem occurs (e.g. if t not on [0,1]).
     */
    public FindSegmentInfo findSegment(double t) {

        // t should be on [0,1]
        if ( t<0.0 || t>1.0 ) { return null; }

        // Check for no segments:
        if (segmentLengths==null) { return null; }

        // Find the segment:
        double d1=0.0, d2; //=0.0; // real distances along the joined segments
        double t1=0.0, t2; //=0.0; // parametric distances along the joined segments
        int k=-1;
        for ( int i=0 ; i<segmentLengths.length ; i++ ) {
            d2 = d1 + segmentLengths[i];
            t2 = d2 / totalLength;
            if ( t1<=t && t<t2 ) {
                k = i;
                break;
            }
            d1 = d2;
            t1 = t2;
        }

        // Check for the above not working:
        if (k<0) { return null; }

        // Return the appropriate information:
        FindSegmentInfo out = new FindSegmentInfo();
        out.index = k;
        out.location = (t-t1)*totalLength/segmentLengths[k];
        return out;
        
    }

    // ------------------ Private Methods -------------------

    /** Calculates the total length of the joined segments (the sum of the segment lengths). */
    private void calculateTotalLength() {

        // Set the total length to zero:
        totalLength = 0.0;

        // Check for no segments:
        if (segmentLengths==null) { return; }

        // Sum the segment lengths:
        for ( int i=0 ; i<segmentLengths.length ; i++ ) {
            totalLength += segmentLengths[i];
        }

    }

}
