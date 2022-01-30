/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comparators;

import jmr.descriptor.Comparator;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.label.LabelDescriptor;

/**
 * Class which contains all the common properties and methods to every comparators.
 * These are related to apply the current inner comparator of the label descriptor.
 * 
 * @author Lidia Sánchez Mérida
 */
public abstract class Comparators implements Comparator<GriddedDescriptor, Double> {
    
    /**
     * The inner comparator of the label descriptor.
     */
    private static Comparator<LabelDescriptor, Double> labelComparator = 
         new LabelDescriptor.ImageLabelDescriptor.InclusionComparator();
    
    /**
     * Gets the current inner comparator of the label descriptor.
     * @return current inner comparator of label descriptor.
     */
    public static Comparator<LabelDescriptor, Double> getLabelComparator() {
        return labelComparator;
    }
    
    /**
     * Sets the inner comparator of the label descriptor depending on the selected
     * options. 
     * @param index the index of the selected inner label comparator.
     * @param weightComparator the index of the weight label comparator.
     * @param weightInclusion true if it's only inclusion, false if it's equality.
     */
    public static void setLabelComparator(int index, int weightComparator, boolean weightInclusion) {   
        switch(index) {
            case 0:
                labelComparator = new LabelDescriptor.ImageLabelDescriptor.SoftEqualComparator();
                break;
            case 1:
                labelComparator = new LabelDescriptor.ImageLabelDescriptor.EqualComparator();
                break;
            case 2:
                labelComparator = new LabelDescriptor.ImageLabelDescriptor.InclusionComparator();
                break;
            case 3:
                switch(weightComparator) {
                    case 0:
                        labelComparator = new LabelDescriptor.ImageLabelDescriptor.
                            WeightBasedComparator(LabelDescriptor.WeightBasedComparator.TYPE_MIN, weightInclusion);
                        break;
                    case 1:
                        labelComparator = new LabelDescriptor.ImageLabelDescriptor.
                            WeightBasedComparator(LabelDescriptor.WeightBasedComparator.TYPE_MAX, weightInclusion);
                        break;
                    case 2:
                        labelComparator = new LabelDescriptor.ImageLabelDescriptor.
                            WeightBasedComparator(LabelDescriptor.WeightBasedComparator.TYPE_MEAN, weightInclusion);
                        break;
                    case 3:
                        labelComparator = new LabelDescriptor.ImageLabelDescriptor.
                            WeightBasedComparator(LabelDescriptor.WeightBasedComparator.TYPE_EUCLIDEAN, weightInclusion);
                        break;
                }
                break;
        }
    }

}
