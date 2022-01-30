/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.differentPosition.withDuplicates;

import comparators.Comparators;
import comparators.differentPosition.DifferentPositionComparators;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.label.LabelDescriptor;

/**
 * Abstract comparator class which contains the common methods of the duplicate
 * comparators.
 * 
 * @author Lidia Sánchez Mérida
 */
public abstract class DuplicateComparators extends DifferentPositionComparators {
    
    /**
     * Calculates the minimum difference for each pair of tiles, one from the
     * image query and another from the second image.
     * @param a grid descriptor of the image A
     * @param b grid descriptor of the image B
     * @return minimum differences 
     */
    protected ArrayList<Double> calculateMinimumDifferences(GriddedDescriptor a, GriddedDescriptor b) {
        // Store all the differences from the comparisons of one tile
        ArrayList<Double> differences;
        // Store the minimum difference of each tile of the image query
        ArrayList<Double> minimumDifferences = new ArrayList();
        // Two tiles difference
        Double diff = 0.0;
        // Compare every tile of each image
        for (int aIndex=0; aIndex<a.getNumTiles(); aIndex++) {
            differences = new ArrayList();
            // First descriptor can't be empty
            String descriptor1 = a.getTileDescriptor(aIndex).toString().
                    substring(1, a.getTileDescriptor(aIndex).toString().length()-1);
            if (!descriptor1.isEmpty()) {
                for (int bIndex=0; bIndex<b.getNumTiles(); bIndex++) {
                    try {
                        MediaDescriptor d1, d2;
                        d1 = (MediaDescriptor)a.getTileDescriptor(aIndex);
                        d2 = (MediaDescriptor)b.getTileDescriptor(bIndex);
                        if (d1 instanceof LabelDescriptor.ImageLabelDescriptor && d2 instanceof LabelDescriptor.ImageLabelDescriptor) {
                            ((LabelDescriptor.ImageLabelDescriptor) d1).setComparator(Comparators.getLabelComparator());
                        }
                        diff = Math.abs((Double) d1.compare(d2));
                        differences.add(diff);
                    }
                    catch(ClassCastException e){
                        throw new InvalidParameterException("The comparison can't be interpreted as a double value.");
                    }
                    catch(Exception e){
                        throw new InvalidParameterException("The descriptors can't be compared");
                    }
                }
            }
            // When the comparison couldn't be done
            if (!differences.isEmpty()) minimumDifferences.add(Collections.min(differences));
            else minimumDifferences.add(Double.POSITIVE_INFINITY);
        }
                
        return minimumDifferences;
    }
}
