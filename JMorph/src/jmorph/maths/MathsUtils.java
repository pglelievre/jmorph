package jmorph.maths;

/** Contains some static methods for basic mathematical tasks.
 * @author Peter Lelievre
 */
public class MathsUtils {

    /** Calculates the dot product of two arrays.
     * @param n The number of values to use from the arrays.
     * @param a The first array in the dot product calculation.
     * @param b The second array in the dot product calculation.
     * @return 
     */
    public static double dotProduct(int n, double[] a, double[] b) {
        double d = 0.0;
        for ( int i=0 ; i<n ; i++ ) {
            d += a[i]*b[i];
        }
        return d;
    }

}
