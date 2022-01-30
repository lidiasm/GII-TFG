/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.differentPosition.withDuplicates;

import comparators.differentPosition.DifferentPositionComparators;
import java.awt.Dimension;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.label.LabelDescriptor;
import comparators.Comparators;

/**
 * Comparator class which compares each tile of an image to others of another
 * image so as to get the minimum difference. Firstly it'll calculate the minimum
 * differences between the tiles of the two images, and then, it'll get the minimum
 * of them. This comparator can be applied whether the two images has the same number of
 * tiles or not.
 * 
 * It also makes a query based on a selected tag related to a search object in a
 * specific position or combination of two.
 * 
 * @author Lidia Sánchez Mérida
 */
public class Minimum extends DuplicateComparators {
    
    /**
     * The indexes of the tiles to make the query.
     */
    private static ArrayList<Integer> indexes = null;
    
    /**
     * Gets the indexes of the tiles to make the query of a search object in a
     * specific position.
     * @param dim the dimension of the grid descriptor.
     * @param pos the position in which the search object has to be.
     * @return 
     */
    private static ArrayList<Integer> getIndexes(String pos, String dim) {
        ArrayList<Integer> indexesToSearch = new ArrayList();
        switch(pos) {
            case "Up":
                switch(dim) {
                    case "2x1":
                    case "3x1":
                        indexesToSearch.add(0);
                        break;
                    case "2x2":
                        indexesToSearch.add(0);
                        indexesToSearch.add(2);
                        break;
                    case "2x3":
                        indexesToSearch.add(0);
                        indexesToSearch.add(2);
                        indexesToSearch.add(4);
                        break;
                    case "3x2":
                        indexesToSearch.add(0);
                        indexesToSearch.add(3);
                        break;
                    case "3x3":
                        indexesToSearch.add(0);
                        indexesToSearch.add(3);
                        indexesToSearch.add(6);
                        break;
                }
                break;
            case "Down":
                switch(dim) {
                    case "2x1":
                        indexesToSearch.add(1);
                        break;
                    case "2x2":
                        indexesToSearch.add(1);
                        indexesToSearch.add(3);
                        break;
                    case "2x3":
                        indexesToSearch.add(1);
                        indexesToSearch.add(3);
                        indexesToSearch.add(5);
                        break;
                    case "3x1":
                        indexesToSearch.add(2);
                        break;
                    case "3x2":
                        indexesToSearch.add(2);
                        indexesToSearch.add(5);
                        break;
                    case "3x3":
                        indexesToSearch.add(2);
                        indexesToSearch.add(5);
                        indexesToSearch.add(8);
                        break;
                }
                break;
            case "Left":
                switch(dim) {
                    case "1x2":
                    case "1x3":
                        indexesToSearch.add(0);
                        break;
                    case "2x2":
                    case "2x3":
                        indexesToSearch.add(0);
                        indexesToSearch.add(1);
                        break;
                    case "3x2":
                    case "3x3":
                        indexesToSearch.add(0);
                        indexesToSearch.add(1);
                        indexesToSearch.add(2);
                        break;
                }
                break;
            case "Right":
                switch(dim) {
                    case "1x2":
                        indexesToSearch.add(1);
                        break;
                    case "1x3":
                        indexesToSearch.add(2);
                        break;
                    case "2x2":
                        indexesToSearch.add(2);
                        indexesToSearch.add(3);
                        break;
                    case "2x3":
                        indexesToSearch.add(4);
                        indexesToSearch.add(5);
                        break;
                    case "3x2":
                        indexesToSearch.add(3);
                        indexesToSearch.add(4);
                        indexesToSearch.add(5);
                        break;
                    case "3x3":
                        indexesToSearch.add(6);
                        indexesToSearch.add(7);
                        indexesToSearch.add(8);
                        break;
                }
                break;
            case "Centre":
                switch(dim) {
                    case "1x3":
                        indexesToSearch.add(1);
                        break;
                    case "2x3":
                        indexesToSearch.add(2);
                        indexesToSearch.add(3);
                        break;
                    case "3x3":
                        indexesToSearch.add(3);
                        indexesToSearch.add(4);
                        indexesToSearch.add(5);
                        break;
                }
                break;
        }
        
        return indexesToSearch;
    }
    
    /**
     * Calculates the tiles which are in both specific positions at the same
     * time. So as to do that it'll apply an intersection set operation.
     * @param indexes1 the indexes of the tiles which are in the first position.
     * @param indexes2 the indexes of the tiles which are in the second position.
     */
    private static void ANDOperator(ArrayList<Integer> indexes1, ArrayList<Integer> indexes2) {
        for (Integer index1: indexes1) {
            if (indexes2.contains(index1))
                indexes.add(index1);
        }
    }
    
    /**
     * Calculates the tiles which are in one of the two specific positions. In 
     * order to do that it'll apply an union set operation.
     * @param indexes1 the indexes of the tiles which are in the first position.
     * @param indexes2 the indexes of the tiles which are in the second position.
     */
    private static void OROperator(ArrayList<Integer> indexes1, ArrayList<Integer> indexes2) {
        Set<Integer> set = new HashSet();
        set.addAll(indexes1);
        set.addAll(indexes2);
        indexes = new ArrayList(set);
    }
    
    /**
     * Sets the indexes of the tiles to make the query in order to search a 
     * specific tag located in a specific position or combination of two.
     * @param dimension the grid dimension of the image to compare to.
     * @param position1 the first position.
     * @param position2 the second position.
     * @param andOperator true if an intersection will be applied, false if it'll
     * be an union operation.
     */
    public static void setIndexes(Dimension dimension, String position1,
    String position2, boolean andOperator) {
        
        indexes = new ArrayList();
        // Convert the dimension to string
        int row = (int) dimension.getHeight();
        int col = (int) dimension.getWidth();
        String gridDimension = Integer.toString(row)+"x"+Integer.toString(col);

        // Get the indexes from the first position
        ArrayList<Integer> indexesPos1 = getIndexes(position1, gridDimension);
        // Get the indexes from the second position if it exists
        if (!"".equals(position2)) {
            ArrayList<Integer> indexesPos2 = getIndexes(position2, gridDimension);
            // AND operator: common indexes
            if (andOperator) Minimum.ANDOperator(indexesPos1, indexesPos2);
            // OR operator: all indexes without duplicates
            else Minimum.OROperator(indexesPos1, indexesPos2);
        }
        // Only the first position
        else indexes = indexesPos1;
    }

    /**
     * Calculates the minimum difference for each pair of tiles, one from the
     * image query and another from the second image.
     * @param a grid descriptor of the image A
     * @param b grid descriptor of the image B
     * @return minimum differences 
     */
    @Override
    protected ArrayList<Double> calculateMinimumDifferences(GriddedDescriptor a, GriddedDescriptor b) {
        // Store all the differences from the comparisons of one tile
        ArrayList<Double> differences;
        // Store the minimum difference of each tile of the image query
        ArrayList<Double> minimumDifferences = new ArrayList();
        // Two tiles difference
        Double diff = 0.0;
        
        for (int aIndex=0; aIndex<a.getNumTiles(); aIndex++) {
            differences = new ArrayList();
            // First descriptor can't be empty
            String descriptor1 = a.getTileDescriptor(aIndex).toString().
                    substring(1, a.getTileDescriptor(aIndex).toString().length()-1);
            if (!descriptor1.isEmpty()) {
                // Query image comparison
                if (indexes == null) {
                    for (int bIndex = 0; bIndex < b.getNumTiles(); bIndex++) {
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
                // Search in the database
                else {
                    System.out.println("Indexes: " + indexes);
                    for (Integer bIndex: indexes) {
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
            }
            
            // When the comparison couldn't be done
            if (!differences.isEmpty()) minimumDifferences.add(Collections.min(differences));
            else minimumDifferences.add(Double.POSITIVE_INFINITY);
        }
        return minimumDifferences;
    }
    
    /**
     * Gets the minimum between the minimum differences calculated.
     * @param t1 grid descriptor of the image A
     * @param t2 grid descriptor of the image B
     * @return the minimum difference between minimum differences
     */
    @Override
    public Double apply(GriddedDescriptor t1, GriddedDescriptor t2) {
       Double minimum = 0.0;
       // Calculate the minimum differences for the first time
       ArrayList<Double> minimumDifferences = calculateMinimumDifferences(t1, t2);
       // Get the first minimum
       minimum = Collections.min(minimumDifferences);
       
       // Double inclusion
        if (DifferentPositionComparators.isDoubleInclusion()) {
            // Calculate the minimum differences for the second time
            minimumDifferences = calculateMinimumDifferences(t2, t1);
            // Get the second minimum
            Double min2 = Collections.min(minimumDifferences);
            // Maximum of minimums
            if (minimum < min2) minimum = min2;
        }
        // Delete the indexes of the search when it's done
        indexes = null;
        
        return minimum;
    }   
}
