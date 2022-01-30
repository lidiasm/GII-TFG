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
 * Comparator class which compares each tile of an image to others of another
 * image so as to get the maximum difference. Firstly it'll calculate the minimum
 * differences between the tiles of the two images if any of them has been related
 * to another tile. If that happens then the couple with the minimum difference will
 * stay and the separated query tile will search for another single tile. At the 
 * end it will get the maximum difference of a couple.
 * 
 * This comparator can be applied whether the two images has the same number of
 * tiles or not.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Maximum extends NoDuplicateComparators {
 
    /**
     * Calculates the maximum difference between the couples and the differences
     * they produced.
     * @return the maximum difference.
     */
    private Double calculateMaximumCouple() {
        Double maximum = 0.0;
        Set<Integer> keys = couples.keySet();
        for (Integer key: keys) {
            if (maximum < couples.get(key).getValue())
                maximum = couples.get(key).getValue();
        }
        
        return maximum;
    }

    /**
     * Gets the maximum between the minimum differences calculated.
     * @param d1 grid descriptor of the image A
     * @param d2 grid descriptor of the image B
     * @return the maximum difference between minimum differences
     */
    @Override
    public Double apply(GriddedDescriptor d1, GriddedDescriptor d2) {
        couples = new LinkedHashMap();
        // Calculate the minimum differences along the couples of tiles which
        // produced it for the first time.
        getMinimumPartners(d1, d2);
        // First maximum
        Double maximum = calculateMaximumCouple();
        // Double inclusion
        if (DifferentPositionComparators.isDoubleInclusion()) {
            couples = new LinkedHashMap();
            // Calculate the minimum differences along the couples of tiles which
            // produced it for the second time.
            getMinimumPartners(d2, d1);
            // Second maximum
            Double maximum2 = calculateMaximumCouple();
            // Maximum of maximums
            if (maximum < maximum2) maximum = maximum2;
        }
        
        return maximum;
    }
}
