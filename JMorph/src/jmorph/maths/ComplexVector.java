package jmorph.maths;

import java.util.ArrayList;
import java.util.Arrays;

/** A Vector of Complex objects.
 * Most of the methods in this class are wrappers for methods of the same name in the Java Vector class.
 * @author Peter Lelievre
 */
public class ComplexVector {

    // -------------------- Properties -------------------

    // Favour composition over inheritence!
    private ArrayList<Complex> vector = new ArrayList<>();

    // ------------------- Constructors ------------------

    public void ComplexVector() {}

    // -------------------- Public Methods -------------------

    /** Returns the size of the vector.
     * @return The size of the vector.
     */
    public int size() {
        return vector.size();
    }

    /** Returns a specified element of the vector.
     * @param i The index of the requested element.
     * @return The specified element of the vector.
     */
    public Complex get(int i) {
        return vector.get(i);
    }

    /** Adds an element to the end of the vector.
     * @param c The Complex object to add to the end of the vector.
     */
    public void add(Complex c) {
        vector.add(c);
    }

    /** Adds many elements to the end of the vector.
     * @param cc An array of Complex objects to add to the end of the vector.
     */
    public void add(Complex[] cc) {
        vector.addAll(Arrays.asList(cc));
        //for ( int i=0 ; i<cc.length ; i++ ) {
        //   vector.add(cc[i]);
        //}
    }

}