/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.samePosition;

import comparators.Comparators;
import java.security.InvalidParameterException;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.label.LabelDescriptor;

/**
 * Comparator class which compares the tiles of two images that are in the same
 * position in order to calculate the total mean between the tiles of two images.
 * This comparator will show which image has the most similar tiles to the tiles
 * of a query image on average.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Mean extends Comparators {

    /**
     * Compares the same position tiles of two images so as to calculate the 
     * mean of all the differences calculated.
     * @param t1 grid descriptor of the query image.
     * @param t2 grid descriptor of the second image.
     * @return mean of all the differences calculated.
     */
    @Override
    public Double apply(GriddedDescriptor t1, GriddedDescriptor t2) {
        // Mean to return
        Double average = 0.0;
        Double diff = 0.0;
        // If only the descriptors have the same size
        if (t1.getNumTiles() == t2.getGrid().getNumTiles()) {
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
                        average += diff;
                    }
                    else average += Double.POSITIVE_INFINITY;
                }
                catch(ClassCastException e){
                    throw new InvalidParameterException("The comparison can't be interpreted as a double value.");
                }
                catch(Exception e){
                    throw new InvalidParameterException("The descriptors can't be compared");
                }
            }
            
            return (average / t1.getNumTiles());
        }
        // Descriptors don't have the same size
        else 
            return Double.POSITIVE_INFINITY;
    }
}
