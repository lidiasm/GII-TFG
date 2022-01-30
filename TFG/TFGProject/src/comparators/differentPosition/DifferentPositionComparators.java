/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators.differentPosition;

import comparators.Comparators;

/**
 * Class which represents those descriptors which don't take the position into
 * account to make the query. That's why they can apply simple or double inclusion.
 * 
 * @author Lidia Sánchez Mérida
 */
public abstract class DifferentPositionComparators extends Comparators {
    
    /**
     * True if double inclusion will be applied, false if it's simple inclusion
     */
    private static boolean doubleInclusion = false;
    
    /**
     * Enables or disables the double inclusion.
     * @param doubleI true if the double inclusion option is selected, false
     * if it's not.
     */
    public static void setDoubleInclusion(boolean doubleI) {
        doubleInclusion = doubleI;
    }
    
    /**
     * Gets the selected inclusion.
     * @return true if it's double inclusion, false if it's simple inclusion.
     */
    public static boolean isDoubleInclusion() {
        return doubleInclusion;
    }
}
