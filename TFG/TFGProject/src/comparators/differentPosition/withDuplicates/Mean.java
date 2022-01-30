/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.differentPosition.withDuplicates;

import comparators.differentPosition.DifferentPositionComparators;
import java.util.ArrayList;
import java.util.OptionalDouble;
import jmr.descriptor.GriddedDescriptor;

/**
 * Comparator class which compares each tile of an image to others of another
 * image so as to calculate the mean of all the differences. Firstly it'll 
 * calculate the minimum differences between the tiles of the two images.
 * 
 * This comparator can be applied whether the two images has the same number of
 * tiles or not.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Mean extends DuplicateComparators {
    
    /**
     * Calculates the mean based on the minimum differences calculated.
     * @param t1 grid descriptor of the image A.
     * @param t2 grid descriptor of the image B.
     * @return the mean of the minimum differences.
     */
    @Override
    public Double apply(GriddedDescriptor t1, GriddedDescriptor t2) {
       Double mean = 0.0;
       // Calculate the minimum differences for the first time
       ArrayList<Double> minimumDifferences = calculateMinimumDifferences(t1, t2);
       // Calculate the first mean 
       OptionalDouble avg = minimumDifferences.stream().mapToDouble(Double::doubleValue).average();
       mean = avg.getAsDouble();
       
       // Double inclusion
        if (DifferentPositionComparators.isDoubleInclusion()) {
            // Calculate the minimum differences for second first time
            minimumDifferences = calculateMinimumDifferences(t2, t1);
            // Calculate the second mean
            OptionalDouble avg2 = minimumDifferences.stream().mapToDouble(Double::doubleValue).average();
            // Maximum of the means
            if (mean < avg2.getAsDouble()) mean = avg.getAsDouble();
        }
        
        return mean;
    }   
}
