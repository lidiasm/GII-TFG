/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.differentPosition.withDuplicates;

import comparators.differentPosition.DifferentPositionComparators;
import java.util.ArrayList;
import java.util.Collections;
import jmr.descriptor.GriddedDescriptor;

/**
 * Comparator class which compares each tile of an image to others of another
 * image so as to get the maximum difference. Firstly it'll calculate the minimum
 * differences between the tiles of the two images, and then, it'll get the maximum
 * of them.
 * This comparator can be applied whether the two images has the same number of
 * tiles or not.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Maximum extends DuplicateComparators {
    
    /**
     * Gets the maximum between the minimum differences calculated.
     * @param t1 grid descriptor of the image A
     * @param t2 grid descriptor of the image B
     * @return the maximum difference between minimum differences
     */
    @Override
    public Double apply(GriddedDescriptor t1, GriddedDescriptor t2) {
       Double maximum = 0.0;
       // Calculate the minimum differences for the first time
       ArrayList<Double> minimumDifferences = calculateMinimumDifferences(t1, t2);
       // Get the maximum difference
       maximum = Collections.max(minimumDifferences);
        // Double inclusion
        if (DifferentPositionComparators.isDoubleInclusion()) {
            // Calculate the minimum differences for the second time
            minimumDifferences = calculateMinimumDifferences(t2, t1);
            // Get the second maximum
            Double max2 = Collections.max(minimumDifferences);
            // Maximum of maximums
            if (maximum < max2) maximum = max2;
        }
        
        return maximum;
    }   
}
