/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.samePosition;

import comparators.Comparators;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.label.LabelDescriptor;

/**
 * Comparator class which compares the tiles of two images that are in the same
 * position in order to return the minimum difference.
 * This comparator will show which image has the most similar tile another
 * of the query image which is in the same position.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Minimum extends Comparators {
    
    /**
     * Compares the same position tiles of two images so as to get the minimum
     * difference between two of them.
     * @param t1 grid descriptor of the query image
     * @param t2 grid descriptor of the second image
     * @return minimum difference between two tiles
     */
    @Override
    public Double apply(GriddedDescriptor t1, GriddedDescriptor t2) {
        // Minimum to return
        Double minimum = Double.POSITIVE_INFINITY;
        // Store the differences of every comparison
        ArrayList<Double> differences = new ArrayList();
        // Two tiles difference
        Double diff = 0.0;
        // If the descriptors have the same size
        if (t1.getNumTiles() == t2.getNumTiles()) {
            // Same position
            for (int index = 0; index < t1.getNumTiles(); index++) {
                try{
                    MediaDescriptor d1, d2;
                    d1 = (MediaDescriptor)t1.getTileDescriptor(index);
                    d2 = (MediaDescriptor)t2.getTileDescriptor(index);
                    // The comparison will be done only if the first descriptor is not empty
                    String descriptorD1 = d1.toString().substring(1, d1.toString().length()-1);
                    if (!descriptorD1.isEmpty()) {
                        if (d1 instanceof LabelDescriptor.ImageLabelDescriptor && d2 instanceof LabelDescriptor.ImageLabelDescriptor) {
                            ((LabelDescriptor.ImageLabelDescriptor) d1).setComparator(Comparators.getLabelComparator());
                        }
                        diff = Math.abs((Double) d1.compare(d2));
                        differences.add(diff);
                    }
                    else differences.add(Double.POSITIVE_INFINITY);
                }
                catch(ClassCastException e){
                    throw new InvalidParameterException("The comparison can't be interpreted as a double value.");
                }
                catch(Exception e){
                    e.getStackTrace();
                    //throw new InvalidParameterException("The descriptors can't be compared");
                }
            }
            
            // When the comparison couldn't be done
            System.out.println("\nDifferences: " + differences.toString());
            if (!differences.isEmpty()) minimum = Collections.min(differences);
        }
        
        return minimum;
    }
}
