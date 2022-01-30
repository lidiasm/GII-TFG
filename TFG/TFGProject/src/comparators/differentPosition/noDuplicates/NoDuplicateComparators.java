/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.differentPosition.noDuplicates;

import comparators.differentPosition.DifferentPositionComparators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javafx.util.Pair;
import comparators.Comparators;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.label.LabelDescriptor;

/**
 * Abstract comparator class which contains the common attributes and methods
 * of the no duplicate comparators.
 * 
 * @author Lidia Sánchez Mérida
 */
public abstract class NoDuplicateComparators extends DifferentPositionComparators {
    
    /**
     * Couples and its difference (a_i, (b_j, d_ij))
     */
    protected Map<Integer, Pair<Integer, Double>> couples;
    
    /**
     * Gets the minimum differences between each query tile and every tile of 
     * the images to compare to. 
     * @param g1 grid descriptor of the query image.
     * @param g2 grid descriptor of the image to compare to the query image.
     */
    protected void getMinimumPartners(GriddedDescriptor g1, GriddedDescriptor g2) {
        // Store the differences between a query tile and an other imagen tile
        ArrayList<Pair<Integer, Double> > differences;
        // The difference from the comparison
        Double diff = 0.0;
        
        // Indexes to come back if a past query tile is alone
        int g1_index, newIndex = -1;
        ArrayList<Integer> currentIndexes = new ArrayList();
        
        // Store if a query tile has been updated
        boolean update = false;
        
        for (g1_index = 0; g1_index < g1.getNumTiles(); g1_index++) {
            differences = new ArrayList();
            // We do the comparison if both descriptors have tags
            String descriptor1 = g1.getTileDescriptor(g1_index).toString().
                    substring(1, g1.getTileDescriptor(g1_index).toString().length()-1);
            if (!descriptor1.isEmpty()) {
                for (int g2_index = 0; g2_index < g2.getNumTiles(); g2_index++) {
                    MediaDescriptor d1, d2;
                    d1 = (MediaDescriptor)g1.getTileDescriptor(g1_index);
                    d2 = (MediaDescriptor)g2.getTileDescriptor(g2_index);
                    if (d1 instanceof LabelDescriptor.ImageLabelDescriptor && d2 instanceof LabelDescriptor.ImageLabelDescriptor) {
                        ((LabelDescriptor.ImageLabelDescriptor) d1).setComparator(Comparators.getLabelComparator());
                    }
                    diff = Math.abs((Double) d1.compare(d2));
                    // Store the difference and the tile of the image to compare with
                    differences.add(new Pair(g2_index, diff));
                }
            }
            
            // If the index returned is bigger than -1 then a query tile is alone
            if (!differences.isEmpty()) newIndex = checkCouples(differences, g1_index);
            
            // We search a new partner for the alone tile query
            if (newIndex != -1) {
                currentIndexes.add(g1_index);
                g1_index = newIndex-1; 
                update = true;
            }
            // We go back to the maximum current index
            else if (update) {
                g1_index = Collections.max(currentIndexes);
                currentIndexes = new ArrayList();
                update = false;
            }
        }
    }
    
    /**
     * Gets the couple of a specific tile related to a image to compare to.
     * @param partnerTile a specific tile
     * @return the couple if it's found, null if it's not.
     */
    private Pair<Integer, Pair<Integer, Double> > getCouple(int partnerTile) {
        Pair<Integer, Pair<Integer, Double> > couple = null;
        for (Integer key: couples.keySet()) {
            if (couples.get(key).getKey().equals(partnerTile)) {
                couple = new Pair(key, couples.get(key));
                break;
            }
        }
        return couple;
    }
    
    /**
     * Gets the minimum difference produced by a tile of the image to compare to
     * the query image.
     * @param differences the differences and the tiles related to them.
     * @return the minimum difference and the tile which produces it.
     */
    private Pair<Integer, Double> getTheMinimumDifference(ArrayList<Pair<Integer, Double> > differences) {
        Pair<Integer, Double> minimum = new Pair(0, Double.POSITIVE_INFINITY);
        for (Pair<Integer, Double> partner: differences) {
            if (minimum.getValue() >= partner.getValue()) {
                minimum = new Pair(partner.getKey(), partner.getValue());
            }  
        }
        return minimum;
    }
    
    /**
     * Calculates the couple in which any tile member can be in another couple.
     * @param minimums the differences and the tiles related to them
     * @param queryIndex current query tile
     * @return -1 all went ok, > -1 if a query tile has been separated from its partner.
     */
    private int checkCouples(ArrayList<Pair<Integer, Double> > differences, int queryIndex) {
        int indexQueryAlone = -1;
        Pair<Integer, Double> minimum = getTheMinimumDifference(differences);
        boolean encontrado = false;
        while (!differences.isEmpty() && !encontrado) {
            // Search for a possible partner of the pretended partner
            Pair<Integer, Pair<Integer, Double> > partnerCouple = getCouple(minimum.getKey());
            
            // Query tile has already a partner
            if (couples.containsKey(queryIndex) && partnerCouple == null) {
                if (minimum.getValue() < couples.get(queryIndex).getValue()) {
                    couples.replace(queryIndex, minimum);
                    encontrado = true;
                }
                else {
                    differences.remove(minimum);
                    minimum = getTheMinimumDifference(differences);
                }
            }
            // Future partner tile has already a partner
            else if (!couples.containsKey(queryIndex) && partnerCouple != null) {
                if (minimum.getValue() < partnerCouple.getValue().getValue()) {
                    couples.remove(partnerCouple.getKey());
                    couples.put(queryIndex, minimum);
                    indexQueryAlone = partnerCouple.getKey();
                    encontrado = true;
                }
                else {
                    differences.remove(minimum);
                    minimum = getTheMinimumDifference(differences);
                }
            }
            // None has already a partner
            else if (!couples.containsKey(queryIndex) && partnerCouple == null) {
                couples.put(queryIndex, minimum);
                encontrado = true;
            }
        }
        
        return indexQueryAlone;
    }
}
