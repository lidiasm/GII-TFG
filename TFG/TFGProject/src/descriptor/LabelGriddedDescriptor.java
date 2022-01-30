/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package descriptor;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.label.LabelDescriptor.ImageLabelDescriptor;

/**
 * Class which represents a list of label descriptors. They're not related
 * to an image so they will be created by specifing the tags.
 * 
 * @author Lidia Sánchez Mérida
 */
public class LabelGriddedDescriptor extends GriddedDescriptor {
    
    /**
     * List of label descriptors
     */
    private ArrayList<ImageLabelDescriptor> labelDescriptors;
    
    /**
     * Constructs a new descriptor using an image, a grid and the class of the
     * descriptors. 
     * @param source any image.
     * @param grid the grid of the image.
     * @param descriptorClass the descriptor class of the descriptors.
     */
    public LabelGriddedDescriptor(BufferedImage source, Dimension grid, Class<? extends MediaDescriptor> descriptorClass) {
        super(source, grid, descriptorClass);
    }
    
    /**
     * Constructs a new descriptor using a image because it's required to use
     * the super construct, and a label descriptor.
     * @param source any image.
     * @param tag label descriptor.
     */
    public LabelGriddedDescriptor(BufferedImage source, ImageLabelDescriptor tag) {
        super(source, new Dimension(1,1), tag.getClass());
        labelDescriptors = new ArrayList();
        labelDescriptors.add(tag);
    }
    
    /**
     * Gets the tile descriptor by its index.
     * @param index the index of the tile descriptor which is going to be returned.
     * @return the tile descriptor which is in the index position.
     */
    @Override
    public ImageLabelDescriptor getTileDescriptor(int index) {
        return labelDescriptors.get(index);
    }
    
    /**
     * Gets the size of the descriptor list.
     * @return the number of descriptors.
     */
    @Override
    public int getNumTiles() {
        return labelDescriptors.size();
    }
    
    /**
     * Shows the descriptors of the list.
     * @return the label descriptors.
     */
    @Override
    public String toString() {
        return "Label Grid Descriptor: " + labelDescriptors.toString();
    }
}
