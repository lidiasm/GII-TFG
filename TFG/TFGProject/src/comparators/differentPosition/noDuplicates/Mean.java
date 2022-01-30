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
 * different image to calculate the average of them.
 * First it will calculate the minimum difference for each tile of the query
 * image, but in this case, there won't be tiles of the query image connected to
 * the same tile of the another image. Then it will return the mean of them.
 * 
 * This comparator can be applied whether the two images has the same number of
 * tiles or not.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Mean extends NoDuplicateComparators {
    
    /**
     * Calculates the average of all every difference of the couples
     * @return the average of all the differences of the couples
     */
    private Double calculateMean() {
        Double mean = 0.0;
        Set<Integer> keys = couples.keySet();
        for (Integer key: keys) {
            mean += couples.get(key).getValue();
        }
        
        return (mean/couples.size());
    }

    /**
     * /**
     * Calculates the average of all the differences of the couples
     * @param d1 the grid descriptor of the query image
     * @param d2 the grid descriptor of the image to compare with the query image
     * @return the average of the differences of every couple
     */
    @Override
    public Double apply(GriddedDescriptor d1, GriddedDescriptor d2) {
        couples = new LinkedHashMap();
        // Calculate the minimum differences along the couples of tiles which
        // produced it for the first time.
        getMinimumPartners(d1, d2);
        // First mean
        Double mean = calculateMean();
        // Double inclusion
        if (DifferentPositionComparators.isDoubleInclusion()) {
            couples = new LinkedHashMap();
            // Calculate the minimum differences along the couples of tiles which
            // produced it for the second time.
            getMinimumPartners(d2, d1);
            // Second mean
            Double mean2 = calculateMean();
            // Maximum of means
            if (mean < mean2) mean = mean2;
        }
        return mean;
    }
}
