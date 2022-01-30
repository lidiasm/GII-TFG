/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.differentPosition.noDuplicates;

import comparators.differentPosition.DifferentPositionComparators;
import java.util.LinkedHashMap;
import java.util.Set;
import jmr.descriptor.GriddedDescriptor;

/**
 * Comparator class which compares the tiles of an image with other tiles of another
 * different image to calculate the minimum difference between them.
 * First it will calculate the minimum difference for each
 * tile of the query image, but in this case, there won't be tiles of the query
 * image connected to the same tile of the another image. Then it will return
 * the minimum between them.
 * 
 * This comparator can be applied whether the two images has the same number of
 * tiles or not.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Minimum extends NoDuplicateComparators {
    
    /**
     * Calculates the minimum difference between the couples and the differences
     * they produced.
     * @return the minimum difference.
     */
    private Double calculateMinimumCouple() {
        Double minimum = Double.POSITIVE_INFINITY;
        Set<Integer> keys = couples.keySet();
        for (Integer key: keys) {
            if (minimum > couples.get(key).getValue())
                minimum = couples.get(key).getValue();
        }
        
        return minimum;
    }

    /**
     * Gets the minimum between the minimum differences calculated.
     * @param d1 grid descriptor of the image A
     * @param d2 grid descriptor of the image B
     * @return the minimum difference between minimum differences
     */
    @Override
    public Double apply(GriddedDescriptor d1, GriddedDescriptor d2) {
        couples = new LinkedHashMap();
        // Calculate the minimum differences along the couples of tiles which
        // produced it for the first time.
        getMinimumPartners(d1, d2);
        // First minimum
        Double minimum = calculateMinimumCouple();
        // Double inclusion
        if (DifferentPositionComparators.isDoubleInclusion()) {
            couples = new LinkedHashMap();
            // Calculate the minimum differences along the couples of tiles which
            // produced it for the second time.
            getMinimumPartners(d2, d1);
            // Second minimum
            Double minimum2 = calculateMinimumCouple();
            // Maximum of minimums
            if (minimum < minimum2) minimum = minimum2;
        }
        
        return minimum;
    }
}

