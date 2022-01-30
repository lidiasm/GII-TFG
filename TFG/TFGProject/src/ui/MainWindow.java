/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import comparators.Comparators;
import comparators.differentPosition.DifferentPositionComparators;
import descriptor.LabelGriddedDescriptor;
import comparators.differentPosition.withDuplicates.Minimum;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jmr.db.ListDB;
import jmr.descriptor.Comparator;
import jmr.descriptor.GriddedDescriptor;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.color.MPEG7ColorStructure;
import jmr.descriptor.color.MPEG7ScalableColor;
import jmr.descriptor.color.SingleColorDescriptor;
import jmr.descriptor.label.LabelDescriptor.ImageLabelDescriptor;
import jmr.grid.SquareGrid;
import jmr.learning.KerasClassifier;
import org.w3c.dom.Document;

/**
 * Class which represents the main window of the app.
 *
 * @author Lidia Sánchez Mérida
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * The image which is recently opened.
     */
    private BufferedImage image;
    /**
     * The inner window which contains the current query image.
     */
    private ImageInnerWindow queryImageInnerWindow = null;
    /**
     * An image filter used to select and open a image file.
     */
    private FileNameExtensionFilter imagesFilter = new FileNameExtensionFilter(
            "Imágenes [jpg, bmp, gif, png, wbmp, jpeg, JPG]",
            "jpg", "bmp", "gif", "png", "wbmp", "jpeg", "JPG");
    /**
     * The grid descriptor of the query image.
     */
    private GriddedDescriptor<BufferedImage> queryDescriptor;
    /**
     * The database of descriptors.
     */
    private ListDB<BufferedImage> database = null;
    /**
     * The current comparator of the app.
     */
    private Comparator<GriddedDescriptor, Double> comparator = null;
    /**
     * XML file of the CNN.
     */
    private File cnnFile = null;
    

    /**
     * Creates new form MainWindow.
     */
    public MainWindow() {
        initComponents();
        // We set a title for the main window
        this.setTitle("CBIR Protoype");

        // Button legends
        this.openButton.setToolTipText("Open an image.");
        this.compareButton.setToolTipText("Compare images.");
        this.comparatorComboB.setToolTipText("Comparators.");
        this.positionComboB.setToolTipText("Positions of the tiles to compare.");
        this.inclusionComboB.setToolTipText("Types of inclusion for the comparison.");
        this.queryGridComboB.setToolTipText("Grid dimension for the query image.");
        this.imagesGridComboB.setToolTipText("Grid dimension for the images"
                + " to compare with the query image.");
        this.duplicatesCheckB.setToolTipText("If it's checked the 'NoDuplicates "
                + "comparator' will be selected. If it's not the 'WithDuplicates"
                + " Comparator will be selected.");
        this.queryImageCheckBox.setToolTipText("Select the query image.");
        this.newDB.setToolTipText("New database.");
        this.openDB.setToolTipText("Open a database.");
        this.addItemToDB.setToolTipText("Add item.");
        this.saveDB.setToolTipText("Save database.");
        this.closeDB.setToolTipText("Close database.");
        this.settingsButton.setToolTipText("Comparison Settings.");
        this.thresholdComboB.setToolTipText("Threshold of the label classifier.");
        this.labelComparatorsComboB.setToolTipText("Inner comparatos of the label descriptor.");
        this.weightComparatorsComboB.setToolTipText("Weight label comparators.");
        this.inclusionTypeComboB.setToolTipText("Weight label type of inclusion.");
        this.searchComboBox.setToolTipText("Labels of the objects which can be recognised.");
        this.andOperator.setToolTipText("Turns two position into one.");
        this.orOperator.setToolTipText("Both positions.");
        this.firstSearchPositionComboB.setToolTipText("First position to be search.");
        this.secondSearchPositionComboB.setToolTipText("Second position to be search.");
        this.searchInDB.setToolTipText("Search the label selected.");
        
        this.weightComparatorsComboB.setVisible(false);
        this.inclusionTypeComboB.setVisible(false);
        this.secondSearchPositionComboB.setVisible(false);
        this.infoPanel.setVisible(false);
        this.openedImagesButton.setSelected(true);

        // Default selected descriptor: label descriptor
        this.meanColourCheck.setSelected(true);
        this.scalableCheck.setSelected(false);
        this.structureCheck.setSelected(false);
        this.labelDescriptorCheck.setSelected(false);
        this.duplicatesCheckB.setSelected(true);
        this.labelDescriptorCheck.setSelected(false);
        // Full screen
        this.setExtendedState(MAXIMIZED_BOTH);
    }

    /**
     * Gets the check box related to the selected image.
     * @return a check box.
     */
    public javax.swing.JCheckBox getQueryImageCheckBox() {
        return queryImageCheckBox;
    }
    
    /**
     * Sets the message of the state toolbar when an important action is done
     *
     * @param message the information about an operation
     */
    public void setStateToolbar(String message) {
        this.stateToolbar.setText(message);
    }
    
    /**
     * Sets the text of the output panel.
     * @param text the text to be shown in the output panel.
     */
    public void setOutputText(String text) {
        String oldText = outputText.getText();
        this.outputText.setText(oldText + "\n" + text + "\n");
    }

    /**
     * Gets the last image inner window. This method is used in order to
     * position the image inner windows in waterfall shape.
     * @return the last image inner window if it exists, null if it doesn't.
     */
    private ImageInnerWindow getLastImageInnerWindow() {
        // Get all the inner windows
        JInternalFrame innerFrames[] = desktop.getAllFrames();
        // Search for the last image inner window
        for (int index = 0; index < innerFrames.length; index++) {
            if (innerFrames[index] instanceof ImageInnerWindow) {
                return (ImageInnerWindow) innerFrames[index];
            }
        }

        return null;
    }
    
    /**
     * Sets the items which can be search and fill in the comboBox with them.
     * So as to do that it reads the xml file of the classifier.
     */
    private void setSearchItemsDictionary() {
        //File xmlFile = new File("C:\\Users\\info\\Desktop\\TFG\\Proyectos\\TFG\\External libraries\\cnn.resnet50.xml");
        // Read the XML with the labels
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            if (cnnFile != null) {
                Document doc = dBuilder.parse(cnnFile);
                doc.getDocumentElement().normalize();
                // Fill in the comboBox with all the labels
                for (int index = 0; index < 1000; index++) {
                    this.searchComboBox.addItem(doc.getElementsByTagName("item").item(index).getTextContent());
                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Creates a new inner window with its image.
     * @return the new inner window created.
     */
    private ImageInnerWindow createInnerWindow(URL url) {
        // Create a new inner image window
        ImageInnerWindow innerImage = new ImageInnerWindow(this, url);
        // Get the last inner image window
        ImageInnerWindow lastInnerImageWindow = getLastImageInnerWindow();
        // If there is a current inner image window we open the images
        // in a cascade way since its position
        if (lastInnerImageWindow != null) {
            // Position of the current inner image window
            int locationX = lastInnerImageWindow.getLocation().x;
            int locationY = lastInnerImageWindow.getLocation().y;
            // Cascade
            innerImage.setLocation(locationX + 20, locationY + 20);
        }

        // Add the new inner window to the desktop
        this.desktop.add(innerImage);

        return innerImage;
    }

    /**
     * Opens a file which contains an image and puts it in a inner window.
     * @param file image file.
     * @param title title of the future inner window.
     */
    public void openImage(File file, String title) {
        try {
            // Read the file
            image = ImageIO.read(file);
            // Create the inner window with the image opened
            ImageInnerWindow newInnerW = createInnerWindow(file.toURI().toURL());
            // Set the image opened
            newInnerW.getImageCanvas().setImage(image);
            // Set the innew window's title
            String imageTitle = file.getName();

            if (!"".equals(title)) {
                imageTitle += ("-" + title);
            }

            newInnerW.setTitle(imageTitle);
            // Show the new inner window with the image opened
            newInnerW.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An error occurred while the file was opening",
                    "ERROR OPENING FILE", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the images which have been compared to the query image in
     * ascending order. The images will be in a list which will be contained in
     * a inner window.
     * @param images map which has the images ordered by their differences.
     */
    public void openImages(Map<Double, ArrayList<URL>> images) {
        ArrayList<URL> orderedImages = new ArrayList();
        // Get the ordered images to draw them
        for (Map.Entry<Double, ArrayList<URL>> img : images.entrySet()) {
            orderedImages.addAll(img.getValue());
        }
        // Open just 20 images
        List<URL> open;
        if (orderedImages.size() > 20) open = orderedImages.subList(0, 15);
        else open = orderedImages;
        
        // Create a special inner window to show the list of images
        ImageListInnerWindow listInnerW = new ImageListInnerWindow(this);
        // Add the images to the list image in order to show them
        for (URL img : open) {
            listInnerW.add(img, "");
        }

        // Get the current image list inner window
        JInternalFrame imageListInnerW = desktop.getSelectedFrame();
        // Open the inner window by its side
        if (imageListInnerW != null) {
            listInnerW.setLocation(imageListInnerW.getWidth() + 10, imageListInnerW.getLocation().y);
        }
        // Add the inner window to the desktop
        this.desktop.add(listInnerW);
        // Set the inner window visible
        listInnerW.setVisible(true);
    }
    
    /**
     * Loads the classifier so as to use the label descriptor. In order to do
     * that the user has to select a xml file with the labels of the CNN.
     * @throws Exception 
     */
    private void setClassifier() throws Exception {
        // Classifier
        //String path = "C:\\Users\\info\\Desktop\\TFG\\Proyectos\\TFG\\External libraries\\cnn.resnet50.xml";
        // Create the dialog in order to select a image file
        JFileChooser dlg = new JFileChooser();
        FileNameExtensionFilter file = new FileNameExtensionFilter(
            "CNN etiquetas [xml]", "xml");
        // Image filter
        dlg.setFileFilter(file);
        // Open the dialog
        int resp = dlg.showOpenDialog(this);

        if (resp == JFileChooser.APPROVE_OPTION) {
            // Get the selected files
            cnnFile = dlg.getSelectedFile();
            // Check the extensions of the files
            int indexPoint = cnnFile.getName().lastIndexOf('.');
            String extension = cnnFile.getName().substring(indexPoint + 1);
            // XML file
            if (cnnFile.toString().contains(extension)) {
                KerasClassifier classifier = KerasClassifier.loadModel(cnnFile);
                // Show the probability of the tag
                classifier.setWeighted(true);
                // How specific the classifier will be
                classifier.setThreshold(Double.parseDouble((String) this.thresholdComboB.getSelectedItem()));
                ImageLabelDescriptor.setDefaultClassifier(classifier);
                // Set the dictionary of search items
                this.setSearchItemsDictionary();
            }
            else {
                cnnFile = null;
                JOptionPane.showMessageDialog(null, "You have to choose a XML file.",
                    "ERROR OPENINGN CNN FILE", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates a grid descriptor of an image based on the current selected
     * descriptor.
     * @param image the image which is about to have a grid descriptor.     
     * @param dimension nxn tiles
     * @return the grid descriptor of the image
     */
    private GriddedDescriptor<BufferedImage> newGridDescriptor(BufferedImage image, Dimension dimension) throws Exception {
        // Store the descriptor of the image
        GriddedDescriptor<BufferedImage> gridDescriptor = null;
        
        // Grid descriptor for mean colour
        if (meanColourCheck.isSelected()) {
            gridDescriptor = new GriddedDescriptor(image, dimension, SingleColorDescriptor.class);
        } // Grid descriptor for MPEG7ScalableColor
        else if (scalableCheck.isSelected()) {
            gridDescriptor = new GriddedDescriptor(image, dimension, MPEG7ScalableColor.class);
        } // Grid descriptor for MPEG7ColorStructure
        else if (structureCheck.isSelected()) {
            gridDescriptor = new GriddedDescriptor(image, dimension, MPEG7ColorStructure.class);
        } // Grid descriptor for image label descriptor
        else if (labelDescriptorCheck.isSelected()) {
            //setClassifier();
            gridDescriptor = new GriddedDescriptor(image, dimension, ImageLabelDescriptor.class);
        } else {
            JOptionPane.showMessageDialog(null, "You have to choose one descriptor to set the grid",
                    "ERROR COMPARING", JOptionPane.ERROR_MESSAGE);
        }
        
        return gridDescriptor;
    }

    /**
     * Creates the grid descriptor of an image. The size of the grid will depend
     * on if the image is the query image or not.
     * @param image the image which we're going to create its grid descriptor.
     * @param isQuery true if it's the query image, false if it's not.
     * @return the grid descriptor of the image.
     * @throws Exception
     */
    private GriddedDescriptor<BufferedImage> createGridDescriptor(BufferedImage image, boolean isQuery) throws Exception {
        // Store the grid descriptor of the image
        GriddedDescriptor<BufferedImage> descriptor = null;

        // Grid descriptor for the query image
        if (isQuery) {
            descriptor = this.newGridDescriptor(image,
                new Dimension(ImageCanvas.getColGridQuery(), ImageCanvas.getRowGridQuery()));
            SquareGrid g = (SquareGrid) descriptor.getGrid();
        } 
        // Grid descriptor for the images to compare to the query image.
        else {
            // If the option "same position" is selected the grid of every image
                // will be the same.
            if (this.positionComboB.getSelectedIndex() == 0) {
                ImageCanvas.setRowGridOtherImages(ImageCanvas.getRowGridQuery());
                ImageCanvas.setColGridOtherImages(ImageCanvas.getColGridQuery());
            }
            descriptor = this.newGridDescriptor(image,
                new Dimension(ImageCanvas.getColGridOtherImages(), ImageCanvas.getRowGridOtherImages()));
        }

        return descriptor;
    }
    
    /**
     * Sets the comparator of the app depending on the comparison settings.
     */
    private void setCurrentComparator() {
        
        // Get the inner label comparator
        if (this.labelDescriptorCheck.isSelected()) 
            Comparators.setLabelComparator(this.labelComparatorsComboB.getSelectedIndex(),
                this.weightComparatorsComboB.getSelectedIndex(),
                this.inclusionTypeComboB.getSelectedIndex()==0);
        
        switch (this.comparatorComboB.getSelectedIndex()) {
            // Minimum
            case 0:
                // Same position
                if (this.positionComboB.getSelectedIndex() == 0) {
                    System.out.println("Mínimo igual posición");
                    comparator = new comparators.samePosition.Minimum();
                } 
                // Different position
                else {
                    // Double or simple inclusion
                    DifferentPositionComparators.setDoubleInclusion(this.inclusionComboB.getSelectedIndex() == 1);
                    // No duplicates
                    if (!this.duplicatesCheckB.isSelected()) {
                        System.out.println("Mínimo sin duplicar");
                        comparator = new comparators.differentPosition.noDuplicates.Minimum();
                    } 
                    // With duplicates
                    else {
                        System.out.println("Mínimo diferente posición");
                        comparator = new comparators.differentPosition.withDuplicates.Minimum();
                    }
                }
                break;
            // Maximum
            case 1:
                // Same position
                if (this.positionComboB.getSelectedIndex() == 0) {
                    System.out.println("Máximo igual posición");
                    comparator = new comparators.samePosition.Maximum();
                } 
                // Different position
                else {
                    // Double or simple inclusion
                    DifferentPositionComparators.setDoubleInclusion(this.inclusionComboB.getSelectedIndex() == 1);
                    // No duplicates
                    if (!this.duplicatesCheckB.isSelected()) {
                        System.out.println("Máximo sin duplicar");
                        comparator = new comparators.differentPosition.noDuplicates.Maximum();
                    } 
                    // With duplicates
                    else {
                        System.out.println("Máximo diferente posición");
                        comparator = new comparators.differentPosition.withDuplicates.Maximum();
                    }
                }
                break;
            // Mean
            case 2:
                // Same position
                if (this.positionComboB.getSelectedIndex() == 0) {
                    System.out.println("Media igual posición");
                    comparator = new comparators.samePosition.Mean();
                } 
                // Different position
                else {
                    // Double or simple inclusion
                    DifferentPositionComparators.setDoubleInclusion(this.inclusionComboB.getSelectedIndex() == 1);
                    // No duplicates
                    if (!this.duplicatesCheckB.isSelected()) {
                        System.out.println("Media sin repetidos");
                        comparator = new comparators.differentPosition.noDuplicates.Mean();
                    } 
                    // With duplicates
                    else {
                        System.out.println("Media diferente posición");
                        comparator = new comparators.differentPosition.withDuplicates.Mean();
                    }
                }
                break;
        }
    }
    
    /**
     * Searchs for the image of a specific inner window. If it's in the database
     * we will choose the descriptor with the same grid as the active grid in the
     * app.
     * @return the grid descriptor of the image if this exists, null if it's not.
     */
    private GriddedDescriptor containsItem(ImageInnerWindow item) {
        // Search for the query image and its grid descriptor
        for (int index = 0; index < database.size(); index++) {
            if (database.get(index).getLocator().equals(item.getURL())) {
                SquareGrid s = (SquareGrid) ((GriddedDescriptor) database.get(index).get(0)).getGrid();
                // If there are several descriptors related to an image 
                // we return that which has the same grid as the active grid in the app
                if (s.getGridHeight() == ImageCanvas.getRowGridQuery() &&
                    s.getGridWidth() == ImageCanvas.getColGridQuery())
                        return (GriddedDescriptor) database.get(index).get(0);
            }
        }
        return null;
    }
    
    /**
     * Shows the grid descriptor label of the clicked image. 
     * @param img the selected image to create its grid label descriptor.
     * @return the label string related to the selected image.
     */
    public String showLabel(BufferedImage img) {
        // String to return
        String labels = "";
        // Grid descriptor 
        GriddedDescriptor g = null;
        
        try {
            // If the image's not null we create its grid descriptor
            if (img != null) {
                g = (GriddedDescriptor) this.createGridDescriptor(img, true);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (g != null) {
            String descriptor = "";
            for (int index = 0; index < g.getNumTiles(); index++) {
                descriptor = g.getTileDescriptor(index).toString().
                    substring(1, g.getTileDescriptor(index).toString().length()-1);
                if (!descriptor.isEmpty()) {
                    labels += "["+index+"] = "+g.getTileDescriptor(index).toString()+"  ";
                }
            }
        }
        else labels = "There's no labels.";
        
        return labels;
    }
    
    /**
     * Makes a query based on a query image or on a selected label. Then it
     * opens the list of images to show the result of the comparison.
     * @throws Exception 
     */
    private void ownQuery(boolean isASearch) throws Exception {
        // Store the images in ascending order
        Map<Double, ArrayList<URL>> differences = new TreeMap();
        
        for (int index = 0; index < database.size(); index++) {
            System.out.println("\nImage: " + (index + 1));
            // Compare the two images
            try {
                System.out.println("Query: " + queryDescriptor.toString() +
                    " , Other: " + database.get(index).get(0).toString());
                
                // If it's a search we set the search settings
                if (isASearch) {
                    // Get the dimension of the item
                    SquareGrid g = (SquareGrid) ((GriddedDescriptor) database.get(index).get(0)).getGrid();

                    // We add the second position if it's visible. That's why the
                    //  AND or OR operation are selected
                    String position2 = "";
                    if (this.secondSearchPositionComboB.isVisible())
                        position2 = (String) this.secondSearchPositionComboB.getSelectedItem();

                    // Create the tiles in which we're searching for the search tag in
                    //  the specific position 
                    Minimum.setIndexes(new Dimension(g.getGridWidth(),g.getGridHeight()),
                        this.firstSearchPositionComboB.getSelectedItem().toString(), position2,
                        this.andOperator.isSelected());
                }
                
                // Compare
                Double difference = (Double) queryDescriptor.compare(database.get(index).get(0));
                System.out.println("Final difference: " + difference);

                // If the differences isn't in the map we add it
                if (!differences.containsKey(difference)) {
                    ArrayList<URL> differenceImages = new ArrayList();
                    differenceImages.add(database.get(index).getLocator());
                    differences.put(difference, differenceImages);
                } 
                //If it's in the map we store the file which has the same
                // difference as others
                else {
                    differences.get(difference).add(database.get(index).getLocator());
                }
            }
            catch(Exception e){
                throw new Exception("Error comparison");
            }
        }

        // Finally we open the images
        openImages(differences);
        this.repaint();
    }
    
    /**
     * Sets the comparison settings befote making the query. If the query is based
     * on an query image first it will look for its descriptor in the database.
     * If it's not it will be created based on the descriptor of the items which
     * are in the database and on the current selected grid in the app.
     * @param search true if the query is based on a selected label, false if it's
     * based on a query image.
     * @throws Exception 
     */
    private void compareImageTilesByDatabase(boolean search) throws Exception {
        // Get the query grid descriptor if it's not a search
        if (!search) {
            // Try to get the query descriptor in the database
            queryDescriptor = containsItem(queryImageInnerWindow);
            // If it's not it'll be created
            if (queryDescriptor == null) {
                if (queryImageInnerWindow != null) {
                    // Set the descriptor of the database
                    if (this.meanColourCheck.isSelected()) {
                        GriddedDescriptor.setDefaultTileDescriptorClass(SingleColorDescriptor.class);
                    } 
                    else if (this.scalableCheck.isSelected()) {
                        GriddedDescriptor.setDefaultTileDescriptorClass(MPEG7ScalableColor.class);
                    } 
                    else if (this.structureCheck.isSelected()) {
                        GriddedDescriptor.setDefaultTileDescriptorClass(MPEG7ColorStructure.class);
                    } 
                    else if (this.labelDescriptorCheck.isSelected()) {
                        try {
                            //setClassifier();
                        } catch (Exception ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        GriddedDescriptor.setDefaultTileDescriptorClass(ImageLabelDescriptor.class);
                    }
                    // Set the size of the descriptor
                    GriddedDescriptor.setDefaultGridSize(new Dimension(
                        ImageCanvas.getColGridQuery(), ImageCanvas.getRowGridQuery()));
                    // Add to the database
                    database.add(queryImageInnerWindow.getImageCanvas().getImage(),
                        queryImageInnerWindow.getURL());
                    // Get the descriptor created
                    queryDescriptor = containsItem(queryImageInnerWindow);
                }
            }
            // Set the comparators
            setCurrentComparator();
            if (comparator != null) {
                // Set the general comparator
                ((GriddedDescriptor) queryDescriptor).setComparator(comparator);
                System.out.println("Inner label comparator: " + Comparators.getLabelComparator().toString());
            }
        }
        ownQuery(search);
    }

    /**
     * Makes a query based on a query image so as to compare it to the images
     * which are opened in the app. In order to do that the descriptor of every
     * image will be creted.
     */
    private void compareImageTilesByOpenedImages() throws Exception {
        // Store the images in ascending order
        Map<Double, ArrayList<URL>> differences = new TreeMap();
        // Create the grid descriptor of the query image
        queryDescriptor = this.createGridDescriptor(
            queryImageInnerWindow.getImageCanvas().getImage(), true);
        // Store the grid descriptors of the several images
        MediaDescriptor imagesGD;
        
        // Set the comparator if it's different from the default one
        setCurrentComparator();
        if (comparator != null) {
            // Set the general comparator
            ((GriddedDescriptor) queryDescriptor).setComparator(comparator);
        }
        
        // Get all the inner windows which are in the app
        JInternalFrame[] innerWindows = desktop.getAllFrames();
        // Compare each image in a image inner window with the query image
        for (int i = 0; i < innerWindows.length; i++) {
            if (innerWindows[i] instanceof ImageInnerWindow) {
                System.out.println("\nImage: " + innerWindows[i].getTitle());
                // Create the grid descriptor of the open image file
                imagesGD = createGridDescriptor(((ImageInnerWindow) innerWindows[i]).getImageCanvas().getImage(), false);
                // Compare the two images
                Double difference = (Double) queryDescriptor.compare(imagesGD);
                System.out.println("Final difference: " + difference);

                // If the differences isn't in the map we add it
                if (!differences.containsKey(difference)) {
                    ArrayList<URL> differenceImages = new ArrayList();
                    differenceImages.add( ((ImageInnerWindow)innerWindows[i]).getURL() );
                    differences.put(difference, differenceImages);
                } // If it's in the map we store the file which has the same difference as others
                else {
                    differences.get(difference).add( ((ImageInnerWindow)innerWindows[i]).getURL() );
                }
            }
        }

        // Finally we open the images
        openImages(differences);
        this.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutDialog = new javax.swing.JDialog();
        aboutScreen = new javax.swing.JPanel();
        labelProgramName = new javax.swing.JLabel();
        labelProgramVersion = new javax.swing.JLabel();
        labelProgramAuthor = new javax.swing.JLabel();
        compareSettingsButtonGroup = new javax.swing.ButtonGroup();
        settingsDialog = new javax.swing.JDialog();
        comparatorComboB = new javax.swing.JComboBox<>();
        dialogTitle = new javax.swing.JLabel();
        positionComboB = new javax.swing.JComboBox<>();
        inclusionComboB = new javax.swing.JComboBox<>();
        duplicatesCheckB = new javax.swing.JCheckBox();
        acceptSettingsButton = new javax.swing.JButton();
        queryGridComboB = new javax.swing.JComboBox<>();
        imagesGridComboB = new javax.swing.JComboBox<>();
        databaseButton = new javax.swing.JRadioButton();
        openedImagesButton = new javax.swing.JRadioButton();
        popupMenuPanelOutput = new javax.swing.JPopupMenu();
        clear = new javax.swing.JMenuItem();
        descriptorsGroup = new javax.swing.ButtonGroup();
        upperToolBar = new javax.swing.JToolBar();
        imagePanel = new javax.swing.JPanel();
        openButton = new javax.swing.JButton();
        closeAllButton = new javax.swing.JButton();
        comparisonPanel = new javax.swing.JPanel();
        compareButton = new javax.swing.JButton();
        queryImageCheckBox = new javax.swing.JCheckBox();
        settingsButton = new javax.swing.JButton();
        labelDescriptorSettings = new javax.swing.JPanel();
        thresholdComboB = new javax.swing.JComboBox<>();
        labelComparatorsComboB = new javax.swing.JComboBox<>();
        weightComparatorsComboB = new javax.swing.JComboBox<>();
        inclusionTypeComboB = new javax.swing.JComboBox<>();
        bdPanel = new javax.swing.JPanel();
        newDB = new javax.swing.JButton();
        addItemToDB = new javax.swing.JButton();
        saveDB = new javax.swing.JButton();
        openDB = new javax.swing.JButton();
        closeDB = new javax.swing.JButton();
        searchDbPanel = new javax.swing.JPanel();
        searchComboBox = new javax.swing.JComboBox<>();
        searchInDB = new javax.swing.JButton();
        firstSearchPositionComboB = new javax.swing.JComboBox<>();
        secondSearchPositionComboB = new javax.swing.JComboBox<>();
        andOperator = new javax.swing.JCheckBox();
        orOperator = new javax.swing.JCheckBox();
        lowerToolbar = new javax.swing.JPanel();
        stateToolbar = new javax.swing.JLabel();
        showOutput = new javax.swing.JLabel();
        separatePanel = new javax.swing.JSplitPane();
        desktop = new javax.swing.JDesktopPane();
        infoPanel = new javax.swing.JTabbedPane();
        outputPanel = new javax.swing.JPanel();
        scrollEditorOutput = new javax.swing.JScrollPane();
        outputText = new javax.swing.JEditorPane();
        menu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openOption = new javax.swing.JMenuItem();
        closeAllOption = new javax.swing.JMenuItem();
        descriptorsMenu = new javax.swing.JMenu();
        meanColourCheck = new javax.swing.JCheckBoxMenuItem();
        scalableCheck = new javax.swing.JCheckBoxMenuItem();
        structureCheck = new javax.swing.JCheckBoxMenuItem();
        labelDescriptorCheck = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutOption = new javax.swing.JMenuItem();

        aboutDialog.setAlwaysOnTop(true);
        aboutDialog.setMinimumSize(new java.awt.Dimension(400, 350));
        aboutDialog.setModal(true);

        aboutScreen.setBackground(new java.awt.Color(204, 255, 255));

        labelProgramName.setFont(new java.awt.Font("Amiri", 0, 24)); // NOI18N
        labelProgramName.setForeground(new java.awt.Color(0, 0, 102));
        labelProgramName.setText("Programa: Pruebas TFG");

        labelProgramVersion.setFont(new java.awt.Font("Amiri", 0, 24)); // NOI18N
        labelProgramVersion.setForeground(new java.awt.Color(0, 0, 102));
        labelProgramVersion.setText("Versión 1.0");

        labelProgramAuthor.setFont(new java.awt.Font("Amiri", 0, 24)); // NOI18N
        labelProgramAuthor.setForeground(new java.awt.Color(0, 0, 102));
        labelProgramAuthor.setText("Autora: Lidia Sánchez Mérida");

        javax.swing.GroupLayout aboutScreenLayout = new javax.swing.GroupLayout(aboutScreen);
        aboutScreen.setLayout(aboutScreenLayout);
        aboutScreenLayout.setHorizontalGroup(
            aboutScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aboutScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelProgramName, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelProgramVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelProgramAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        aboutScreenLayout.setVerticalGroup(
            aboutScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelProgramName, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelProgramVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelProgramAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(203, Short.MAX_VALUE))
        );

        labelProgramVersion.getAccessibleContext().setAccessibleName("");

        aboutDialog.getContentPane().add(aboutScreen, java.awt.BorderLayout.CENTER);

        comparatorComboB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        comparatorComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "At least 1", "Most of them", "General" }));
        comparatorComboB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comparatorComboBActionPerformed(evt);
            }
        });

        dialogTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        dialogTitle.setText("Comparison Settings");

        positionComboB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        positionComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Same position", "Different position" }));
        positionComboB.setSelectedIndex(1);
        positionComboB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionComboBActionPerformed(evt);
            }
        });

        inclusionComboB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        inclusionComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Inclusion", "Equality" }));

        duplicatesCheckB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        duplicatesCheckB.setText("Duplicates");

        acceptSettingsButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        acceptSettingsButton.setText("Accept");
        acceptSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptSettingsButtonActionPerformed(evt);
            }
        });

        queryGridComboB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        queryGridComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1x1", "1x2", "1x3", "2x1", "2x2", "2x3", "3x1", "3x2", "3x3" }));
        queryGridComboB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryGridComboBActionPerformed(evt);
            }
        });

        imagesGridComboB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        imagesGridComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1x1", "1x2", "1x3", "2x1", "2x2", "2x3", "3x1", "3x2", "3x3" }));
        imagesGridComboB.setSelectedIndex(4);
        imagesGridComboB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imagesGridComboBActionPerformed(evt);
            }
        });

        compareSettingsButtonGroup.add(databaseButton);
        databaseButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        databaseButton.setText("Database");
        databaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseButtonActionPerformed(evt);
            }
        });

        compareSettingsButtonGroup.add(openedImagesButton);
        openedImagesButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        openedImagesButton.setText("Opened image windows");
        openedImagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openedImagesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout settingsDialogLayout = new javax.swing.GroupLayout(settingsDialog.getContentPane());
        settingsDialog.getContentPane().setLayout(settingsDialogLayout);
        settingsDialogLayout.setHorizontalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dialogTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(settingsDialogLayout.createSequentialGroup()
                        .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comparatorComboB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(queryGridComboB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(settingsDialogLayout.createSequentialGroup()
                                .addComponent(imagesGridComboB, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(positionComboB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(duplicatesCheckB)
                            .addComponent(inclusionComboB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(settingsDialogLayout.createSequentialGroup()
                            .addComponent(databaseButton)
                            .addGap(176, 176, 176))
                        .addComponent(openedImagesButton)))
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(acceptSettingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(133, 133, 133))
        );
        settingsDialogLayout.setVerticalGroup(
            settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsDialogLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(dialogTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(positionComboB, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comparatorComboB)
                    .addComponent(inclusionComboB))
                .addGap(18, 18, 18)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(imagesGridComboB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(duplicatesCheckB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(queryGridComboB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(settingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseButton)
                    .addComponent(openedImagesButton))
                .addGap(18, 18, 18)
                .addComponent(acceptSettingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addGap(22, 22, 22))
        );

        popupMenuPanelOutput.setAlignmentY(0.0F);
        popupMenuPanelOutput.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        clear.setText("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });
        popupMenuPanelOutput.add(clear);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        upperToolBar.setRollover(true);
        upperToolBar.setMaximumSize(new java.awt.Dimension(400, 200));
        upperToolBar.setPreferredSize(new java.awt.Dimension(712, 70));

        imagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Images", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        imagePanel.setMaximumSize(new java.awt.Dimension(140, 32767));
        imagePanel.setMinimumSize(new java.awt.Dimension(150, 100));
        imagePanel.setPreferredSize(new java.awt.Dimension(130, 68));

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/openIcon.png"))); // NOI18N
        openButton.setFocusable(false);
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        closeAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/closeIcon.png"))); // NOI18N
        closeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, imagePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(openButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeAllButton)
                .addGap(16, 16, 16))
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanelLayout.createSequentialGroup()
                .addGroup(imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(openButton)
                    .addComponent(closeAllButton))
                .addGap(0, 46, Short.MAX_VALUE))
        );

        upperToolBar.add(imagePanel);

        comparisonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Comparison", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        comparisonPanel.setMaximumSize(new java.awt.Dimension(260, 70));

        compareButton.setText("Compare");
        compareButton.setFocusable(false);
        compareButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        compareButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        compareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compareButtonActionPerformed(evt);
            }
        });

        queryImageCheckBox.setText("Query Image");
        queryImageCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryImageCheckBoxActionPerformed(evt);
            }
        });

        settingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/settingsIcon.png"))); // NOI18N
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout comparisonPanelLayout = new javax.swing.GroupLayout(comparisonPanel);
        comparisonPanel.setLayout(comparisonPanelLayout);
        comparisonPanelLayout.setHorizontalGroup(
            comparisonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comparisonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(compareButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(queryImageCheckBox)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        comparisonPanelLayout.setVerticalGroup(
            comparisonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comparisonPanelLayout.createSequentialGroup()
                .addGroup(comparisonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(compareButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(queryImageCheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap())
        );

        upperToolBar.add(comparisonPanel);

        labelDescriptorSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Label Descriptor Settings", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        labelDescriptorSettings.setMaximumSize(new java.awt.Dimension(400, 32767));

        thresholdComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0.1", "0.15", "0.2", "0.25", "0.3", "0.35", "0.4", "0.45", "0.5" }));
        thresholdComboB.setSelectedIndex(2);

        labelComparatorsComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Soft Inclusion", "Equal", "Inclusion", "Weight" }));
        labelComparatorsComboB.setSelectedIndex(2);
        labelComparatorsComboB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelComparatorsComboBActionPerformed(evt);
            }
        });

        weightComparatorsComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minimum", "Maximum", "Mean", "Euclidean D." }));
        weightComparatorsComboB.setSelectedIndex(2);

        inclusionTypeComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Inclusion", "Equality" }));

        javax.swing.GroupLayout labelDescriptorSettingsLayout = new javax.swing.GroupLayout(labelDescriptorSettings);
        labelDescriptorSettings.setLayout(labelDescriptorSettingsLayout);
        labelDescriptorSettingsLayout.setHorizontalGroup(
            labelDescriptorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelDescriptorSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(thresholdComboB, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelComparatorsComboB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(weightComparatorsComboB, 0, 104, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inclusionTypeComboB, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        labelDescriptorSettingsLayout.setVerticalGroup(
            labelDescriptorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(thresholdComboB, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
            .addComponent(labelComparatorsComboB)
            .addComponent(weightComparatorsComboB)
            .addComponent(inclusionTypeComboB)
        );

        upperToolBar.add(labelDescriptorSettings);

        bdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Database", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        bdPanel.setMaximumSize(new java.awt.Dimension(250, 32767));
        bdPanel.setPreferredSize(new java.awt.Dimension(250, 66));

        newDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/new DB.png"))); // NOI18N
        newDB.setPreferredSize(new java.awt.Dimension(25, 25));
        newDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDBActionPerformed(evt);
            }
        });

        addItemToDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/add DB.png"))); // NOI18N
        addItemToDB.setPreferredSize(new java.awt.Dimension(25, 25));
        addItemToDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemToDBActionPerformed(evt);
            }
        });

        saveDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/save DB.png"))); // NOI18N
        saveDB.setPreferredSize(new java.awt.Dimension(25, 25));
        saveDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDBActionPerformed(evt);
            }
        });

        openDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/open DB.png"))); // NOI18N
        openDB.setPreferredSize(new java.awt.Dimension(25, 25));
        openDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openDBActionPerformed(evt);
            }
        });

        closeDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/close DB.png"))); // NOI18N
        closeDB.setMaximumSize(new java.awt.Dimension(28, 28));
        closeDB.setMinimumSize(new java.awt.Dimension(28, 28));
        closeDB.setPreferredSize(new java.awt.Dimension(28, 28));
        closeDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeDBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bdPanelLayout = new javax.swing.GroupLayout(bdPanel);
        bdPanel.setLayout(bdPanelLayout);
        bdPanelLayout.setHorizontalGroup(
            bdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(openDB, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newDB, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addItemToDB, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveDB, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeDB, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );
        bdPanelLayout.setVerticalGroup(
            bdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(openDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(newDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(addItemToDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(closeDB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
            .addComponent(saveDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        upperToolBar.add(bdPanel);

        searchDbPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "DB Search", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));
        searchDbPanel.setMaximumSize(new java.awt.Dimension(460, 32767));
        searchDbPanel.setPreferredSize(new java.awt.Dimension(460, 68));

        searchInDB.setText("Search");
        searchInDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchInDBActionPerformed(evt);
            }
        });

        firstSearchPositionComboB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Up", "Down", "Left", "Right", "Centre" }));
        firstSearchPositionComboB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstSearchPositionComboBActionPerformed(evt);
            }
        });

        secondSearchPositionComboB.setPreferredSize(new java.awt.Dimension(58, 20));

        andOperator.setText("AND");
        andOperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                andOperatorActionPerformed(evt);
            }
        });

        orOperator.setText("OR");
        orOperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orOperatorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchDbPanelLayout = new javax.swing.GroupLayout(searchDbPanel);
        searchDbPanel.setLayout(searchDbPanelLayout);
        searchDbPanelLayout.setHorizontalGroup(
            searchDbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchDbPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(firstSearchPositionComboB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(searchDbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(orOperator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(andOperator))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(secondSearchPositionComboB, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchInDB, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        searchDbPanelLayout.setVerticalGroup(
            searchDbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchDbPanelLayout.createSequentialGroup()
                .addGroup(searchDbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchComboBox)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchDbPanelLayout.createSequentialGroup()
                        .addComponent(andOperator)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(orOperator))
                    .addComponent(firstSearchPositionComboB, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(searchDbPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(searchInDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(secondSearchPositionComboB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        upperToolBar.add(searchDbPanel);

        getContentPane().add(upperToolBar, java.awt.BorderLayout.PAGE_START);

        lowerToolbar.setMaximumSize(new java.awt.Dimension(32767, 70));
        lowerToolbar.setPreferredSize(new java.awt.Dimension(1439, 70));

        stateToolbar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        showOutput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/desplegar20.png"))); // NOI18N
        showOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showOutputMousePressed(evt);
            }
        });

        javax.swing.GroupLayout lowerToolbarLayout = new javax.swing.GroupLayout(lowerToolbar);
        lowerToolbar.setLayout(lowerToolbarLayout);
        lowerToolbarLayout.setHorizontalGroup(
            lowerToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowerToolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lowerToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stateToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 1504, Short.MAX_VALUE)
                    .addGroup(lowerToolbarLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(showOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        lowerToolbarLayout.setVerticalGroup(
            lowerToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lowerToolbarLayout.createSequentialGroup()
                .addComponent(showOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stateToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(lowerToolbar, java.awt.BorderLayout.PAGE_END);

        separatePanel.setDividerLocation(1.0);
        separatePanel.setDividerSize(3);
        separatePanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        separatePanel.setPreferredSize(new java.awt.Dimension(0, 0));
        separatePanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                separatePanelPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout desktopLayout = new javax.swing.GroupLayout(desktop);
        desktop.setLayout(desktopLayout);
        desktopLayout.setHorizontalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1522, Short.MAX_VALUE)
        );
        desktopLayout.setVerticalGroup(
            desktopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        separatePanel.setTopComponent(desktop);

        infoPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        infoPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        outputPanel.setLayout(new java.awt.BorderLayout());

        scrollEditorOutput.setMinimumSize(new java.awt.Dimension(0, 0));

        outputText.setMinimumSize(new java.awt.Dimension(0, 0));
        outputText.setPreferredSize(new java.awt.Dimension(0, 0));
        outputText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                outputTextMouseReleased(evt);
            }
        });
        scrollEditorOutput.setViewportView(outputText);

        outputPanel.add(scrollEditorOutput, java.awt.BorderLayout.CENTER);

        infoPanel.addTab("Output", outputPanel);

        separatePanel.setBottomComponent(infoPanel);

        getContentPane().add(separatePanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        openOption.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/openIcon.png"))); // NOI18N
        openOption.setText("Open");
        openOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openOptionActionPerformed(evt);
            }
        });
        fileMenu.add(openOption);

        closeAllOption.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/closeIcon.png"))); // NOI18N
        closeAllOption.setText("Close All");
        closeAllOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllOptionActionPerformed(evt);
            }
        });
        fileMenu.add(closeAllOption);

        menu.add(fileMenu);

        descriptorsMenu.setText("Descriptors");

        descriptorsGroup.add(meanColourCheck);
        meanColourCheck.setSelected(true);
        meanColourCheck.setText("Mean Colour");
        meanColourCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meanColourCheckActionPerformed(evt);
            }
        });
        descriptorsMenu.add(meanColourCheck);

        descriptorsGroup.add(scalableCheck);
        scalableCheck.setText("Scalable Colour");
        scalableCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scalableCheckActionPerformed(evt);
            }
        });
        descriptorsMenu.add(scalableCheck);

        descriptorsGroup.add(structureCheck);
        structureCheck.setText("Colour Structure");
        structureCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                structureCheckActionPerformed(evt);
            }
        });
        descriptorsMenu.add(structureCheck);

        descriptorsGroup.add(labelDescriptorCheck);
        labelDescriptorCheck.setText("Label Descriptor");
        labelDescriptorCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelDescriptorCheckActionPerformed(evt);
            }
        });
        descriptorsMenu.add(labelDescriptorCheck);

        menu.add(descriptorsMenu);

        helpMenu.setText("Help");

        aboutOption.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/helpIcon.png"))); // NOI18N
        aboutOption.setText("About");
        aboutOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutOptionActionPerformed(evt);
            }
        });
        helpMenu.add(aboutOption);

        menu.add(helpMenu);

        setJMenuBar(menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Opens a dialog to allow the user to select a file. It will open it in a
     * inner window if it only contains an image with a right extension.
     * @param evt 
     */
    private void openOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openOptionActionPerformed
        // TODO add your handling code here:
        // Create the dialog in order to select a image file
        JFileChooser dlg = new JFileChooser();
        // Multiple selection
        dlg.setMultiSelectionEnabled(true);
        // Image filter
        dlg.setFileFilter(imagesFilter);
        // Open the dialog
        int resp = dlg.showOpenDialog(this);

        if (resp == JFileChooser.APPROVE_OPTION) {
            // Get the selected files
            File files[] = dlg.getSelectedFiles();
            // Check the extensions of the files
            for (File file : files) {
                int indexPoint = file.getName().lastIndexOf('.');
                String extension = file.getName().substring(indexPoint + 1);
                if (imagesFilter.toString().contains(extension)) {
                    openImage(file, "");
                }
            }
        }
    }//GEN-LAST:event_openOptionActionPerformed

    /**
     * Opens a dialog to allow the user to select a file. It will open it in a
     * inner window if it only contains an image with a right extension.
     * @param evt 
     */
    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        // TODO add your handling code here:
        this.openOptionActionPerformed(evt);
    }//GEN-LAST:event_openButtonActionPerformed

    /**
     * Shows a panel which has the information related to the app.
     * @param evt 
     */
    private void aboutOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutOptionActionPerformed
        // TODO add your handling code here:
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutOptionActionPerformed

    private void campoAncho1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoAncho1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoAncho1ActionPerformed

    /**
     * Changes the state of the toolbar to show that the mean colour descriptor
     * has been selected.
     * @param evt 
     */
    private void meanColourCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meanColourCheckActionPerformed
        // TODO add your handling code here:
        if (this.meanColourCheck.isSelected()) this.setStateToolbar("  Mean Colour descriptor selected.");
        this.labelDescriptorSettings.setVisible(false);
    }//GEN-LAST:event_meanColourCheckActionPerformed

    /**
     * Changes the state of the toolbar to show that the scable colour descriptor
     * has been selected.
     * @param evt 
     */
    private void scalableCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scalableCheckActionPerformed
        // TODO add your handling code here:
        if (this.scalableCheck.isSelected()) this.setStateToolbar("  Scalable Colour descriptor selected.");
        this.labelDescriptorSettings.setVisible(false);
    }//GEN-LAST:event_scalableCheckActionPerformed

    /**
     * Changes the state of the toolbar to show that the colour structure descriptor
     * has been selected.
     * @param evt 
     */
    private void structureCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_structureCheckActionPerformed
        // TODO add your handling code here:
        if (this.structureCheck.isSelected()) this.setStateToolbar("  Colour Structure descriptor selected.");
        this.labelDescriptorSettings.setVisible(false);
    }//GEN-LAST:event_structureCheckActionPerformed

    /**
     * Closes every inner window which is opened in the app.
     * @param evt 
     */
    private void closeAllOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllOptionActionPerformed
        // TODO add your handling code here:
        JInternalFrame[] innerWindows = desktop.getAllFrames();
        for (int i = 0; i < innerWindows.length; i++) {
            (innerWindows[i]).dispose();
        }
    }//GEN-LAST:event_closeAllOptionActionPerformed
    
    /**
     * Changes the state of the toolbar to show that the label descriptor has 
     * been selected.
     * @param evt 
     */
    private void labelDescriptorCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelDescriptorCheckActionPerformed
        // TODO add your handling code here:
        if (this.labelDescriptorCheck.isSelected()) this.setStateToolbar("  Label descriptor selected.");
        this.labelDescriptorSettings.setVisible(this.labelDescriptorCheck.isSelected());
        if (this.labelDescriptorCheck.isSelected()) try {
            setClassifier();
        } catch (Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_labelDescriptorCheckActionPerformed

    /**
     * Creates a new database of grid descriptors if there isn't already one. 
     * @param evt 
     */
    private void newDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newDBActionPerformed
        // TODO add your handling code here:
        if (database != null) {
            JOptionPane.showMessageDialog(null, "A database is already created.",
                "ERROR CREATING NEW DB", JOptionPane.ERROR_MESSAGE);
        }
        else {
            Class descriptorClass[] = new Class[1];
            // Grid descriptor by default for every image
            descriptorClass[0] = GriddedDescriptor.class;

            // Create the new bd
            database = new ListDB(descriptorClass);
            this.setStateToolbar("  New database created correctly.");
        }
    }//GEN-LAST:event_newDBActionPerformed
    
    /**
     * Adds a new item to the database if there's one opened and if the new item
     * isn't already in the database. If it's not its grid descriptor will be 
     * created depending on the selected descriptor of the app.
     * @param evt 
     */
    private void addItemToDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemToDBActionPerformed
        // TODO add your handling code here:
        if (database != null) {
            // Set the descriptor
            if (this.meanColourCheck.isSelected()) {
                GriddedDescriptor.setDefaultTileDescriptorClass(SingleColorDescriptor.class);
            } 
            else if (this.scalableCheck.isSelected()) {
                GriddedDescriptor.setDefaultTileDescriptorClass(MPEG7ScalableColor.class);
            } 
            else if (this.structureCheck.isSelected()) {
                GriddedDescriptor.setDefaultTileDescriptorClass(MPEG7ColorStructure.class);
            } 
            else if (this.labelDescriptorCheck.isSelected()) {
                try {
                    //setClassifier();
                } catch (Exception ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                GriddedDescriptor.setDefaultTileDescriptorClass(ImageLabelDescriptor.class);
            }
            
            // Get all the inner windows which only contains one image
            JInternalFrame frames[] = desktop.getAllFrames();
            ImageInnerWindow innerWindow;
            for (JInternalFrame frame : frames) {
                if (frame instanceof ImageInnerWindow) {
                    innerWindow = (ImageInnerWindow) frame;
                    GriddedDescriptor.setDefaultGridSize(new Dimension(
                        ImageCanvas.getColGridQuery(), ImageCanvas.getRowGridQuery()));
                    // Add the image to the db
                    database.add(innerWindow.getImageCanvas().getImage(), innerWindow.getURL());
                }
            }

            if (!database.isEmpty()) {
                this.databaseButton.setSelected(true);
                this.imagesGridComboB.setEnabled(false);
                this.openedImagesButton.setSelected(false);
                // Unable to select a different descriptor
                this.labelDescriptorCheck.setEnabled(false);
                this.meanColourCheck.setEnabled(false);
                this.scalableCheck.setEnabled(false);
                this.structureCheck.setEnabled(false);
                this.labelDescriptorCheckActionPerformed(evt);
                this.setStateToolbar("  Items added correctly.");
            }
        }
    }//GEN-LAST:event_addItemToDBActionPerformed

    /**
     * Saves the current database in a file called database.db which will be in
     * the specific folder. 
     * @param evt 
     */
    private void saveDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDBActionPerformed
        // TODO add your handling code here:
        // Default path
        String path = "C:\\Users\\info\\Documents\\BDs\\";
        // Default name
        String nameDBFile = "database";
        // Fichero
        File dbFile = new File(path + nameDBFile + ".db");
        // Try to write the file
        try {
            database.save(dbFile);
            this.setStateToolbar("  Database saved correctly.");
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        } 
    }//GEN-LAST:event_saveDBActionPerformed

    /**
     * Opens a dialog to allow the user to select a database file. Then it'll
     * open the database and load all its items.
     * @param evt 
     */
    private void openDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openDBActionPerformed
        // TODO add your handling code here:
        // Create the dialog in order to select a image file
        JFileChooser dlg = new JFileChooser();
        // DB filter
        FileNameExtensionFilter bdFilter = new FileNameExtensionFilter(
                "Data base files [db]", "db");
        // Apply the filter
        dlg.setFileFilter(bdFilter);
        // Open the dialog
        int resp = dlg.showOpenDialog(this);

        if (resp == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File file = dlg.getSelectedFile();
            int indexPoint = file.getName().lastIndexOf('.');
            String extension = file.getName().substring(indexPoint + 1);
            // If it has a properly extension it'll be opened
            if (bdFilter.toString().contains(extension)) {
                try {
                    database = ListDB.open(file);
                    // Set the button
                    this.databaseButton.setSelected(true);
                    this.imagesGridComboB.setEnabled(false);
                    this.openedImagesButton.setSelected(false);
                    
                    // Same descriptor as the items of the database
                    String descriptorClass = ((GriddedDescriptor) database.get(0).get(0)).getTileDescriptorClass().toString();
                    if (descriptorClass.contains("ImageLabelDescriptor"))
                        this.labelDescriptorCheck.setSelected(true);
                    else if (descriptorClass.contains("SingleColorDescriptor"))
                        this.meanColourCheck.setSelected(true);
                    else if (descriptorClass.contains("MPEG7ScalableColor"))
                        this.scalableCheck.setSelected(true);
                    else if (descriptorClass.contains("MPEG7ColorStructure"))
                        this.structureCheck.setSelected(true);

                    // Unable to select a different descriptor
                    this.labelDescriptorCheck.setEnabled(false);
                    this.meanColourCheck.setEnabled(false);
                    this.scalableCheck.setEnabled(false);
                    this.structureCheck.setEnabled(false);
                    this.labelDescriptorCheckActionPerformed(evt);
                    System.out.println("\nDB size: " + database.size());
                    //System.out.println("\nDB\n" + database.toString());
                    this.setStateToolbar("  Database opened successfully");
                    
                } catch (IOException | ClassNotFoundException ex) {
                    System.err.println(ex);
                }
            } // If it hasn't then a message will be showed
            else {
                JOptionPane.showMessageDialog(null, "The file is not right. Please select a database file (.db).",
                        "ERROR OPENING DB FILE", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_openDBActionPerformed

    /**
     * Closes the current database.
     * @param evt 
     */
    private void closeDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeDBActionPerformed
        // TODO add your handling code here:
        // We clear it if it already exists
        if (database != null) {
            database.clear();
            database = null;
            this.labelDescriptorCheck.setEnabled(true);
            this.meanColourCheck.setEnabled(true);
            this.scalableCheck.setEnabled(true);
            this.structureCheck.setEnabled(true);
            this.databaseButton.setSelected(false);
            this.openedImagesButton.setSelected(true);
            if (this.positionComboB.getSelectedIndex() == 1) this.imagesGridComboB.setEnabled(true);
            else this.imagesGridComboB.setEnabled(false);
            this.setStateToolbar("  Database closed correctly.");
        }
    }//GEN-LAST:event_closeDBActionPerformed

    /**
     * Sets the dimension of the grid related to the images to compare to the
     * query image.
     * @param evt 
     */
    private void imagesGridComboBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imagesGridComboBActionPerformed
        // TODO add your handling code here:
        if (null != (String) this.imagesGridComboB.getSelectedItem()) {
            // Set the grid dimension for this inner window
            switch ((String) this.imagesGridComboB.getSelectedItem()) {
                case "1x1":
                    ImageCanvas.setRowGridOtherImages(1);
                    ImageCanvas.setColGridOtherImages(1);
                    break;
                case "1x2":
                    ImageCanvas.setRowGridOtherImages(1);
                    ImageCanvas.setColGridOtherImages(2);
                    break;
                case "1x3":
                    ImageCanvas.setRowGridOtherImages(1);
                    ImageCanvas.setColGridOtherImages(3);
                    break;
                case "2x1":
                    ImageCanvas.setRowGridOtherImages(2);
                    ImageCanvas.setColGridOtherImages(1);
                    break;
                case "2x2":
                    ImageCanvas.setRowGridOtherImages(2);
                    ImageCanvas.setColGridOtherImages(2);
                    break;
                case "2x3":
                    ImageCanvas.setRowGridOtherImages(2);
                    ImageCanvas.setColGridOtherImages(3);
                    break;
                case "3x1":
                    ImageCanvas.setRowGridOtherImages(3);
                    ImageCanvas.setColGridOtherImages(1);
                    break;
                case "3x2":
                    ImageCanvas.setRowGridOtherImages(3);
                    ImageCanvas.setColGridOtherImages(2);
                    break;
                case "3x3":
                    ImageCanvas.setRowGridOtherImages(3);
                    ImageCanvas.setColGridOtherImages(3);
                    break;
                default:
                break;
            }
        }
    }//GEN-LAST:event_imagesGridComboBActionPerformed

    /**
     * Sets the dimension of the grid which is related to the query image.
     * @param evt 
     */
    private void queryGridComboBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryGridComboBActionPerformed
        // TODO add your handling code here:
        if (null != (String) this.queryGridComboB.getSelectedItem()) {
            // Set the grid dimension for this inner window
            switch ((String) this.queryGridComboB.getSelectedItem()) {
                case "1x1":
                    ImageCanvas.setRowGridQuery(1);
                    ImageCanvas.setColGridQuery(1);
                    break;
                case "1x2":
                    ImageCanvas.setRowGridQuery(1);
                    ImageCanvas.setColGridQuery(2);
                    break;
                case "1x3":
                    ImageCanvas.setRowGridQuery(1);
                    ImageCanvas.setColGridQuery(3);
                    break;
                case "2x1":
                    ImageCanvas.setRowGridQuery(2);
                    ImageCanvas.setColGridQuery(1);
                    break;
                case "2x2":
                    ImageCanvas.setRowGridQuery(2);
                    ImageCanvas.setColGridQuery(2);
                    break;
                case "2x3":
                    ImageCanvas.setRowGridQuery(2);
                    ImageCanvas.setColGridQuery(3);
                    break;
                case "3x1":
                    ImageCanvas.setRowGridQuery(3);
                    ImageCanvas.setColGridQuery(1);
                    break;
                case "3x2":
                    ImageCanvas.setRowGridQuery(3);
                    ImageCanvas.setColGridQuery(2);
                    break;
                case "3x3":
                    ImageCanvas.setRowGridQuery(3);
                    ImageCanvas.setColGridQuery(3);
                    break;
                default:
                break;
            }
        }
    }//GEN-LAST:event_queryGridComboBActionPerformed

    /**
     * When it's checked then the image of the current selected inner window will
     * be the query image.
     * @param evt 
     */
    private void queryImageCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryImageCheckBoxActionPerformed
        // TODO add your handling code here:
        if (this.queryImageCheckBox.isSelected()) {
            JInternalFrame window = desktop.getSelectedFrame();
            // Check if the current selected window only has one image, not a list
            if (window instanceof ImageInnerWindow) {
                ImageInnerWindow innerWindow = (ImageInnerWindow) window;
                if (innerWindow != null) {
                    // The last query image it isn't a query anymore
                    if (queryImageInnerWindow != null) {
                        queryImageInnerWindow.getImageCanvas().setIsQueryImage(false);
                        queryImageInnerWindow.setBorder(null);
                    }
                    // Set the new query image
                    innerWindow.getImageCanvas().setIsQueryImage(true);
                    innerWindow.setBorder(javax.swing.BorderFactory.createLineBorder(Color.ORANGE, 5));
                    queryImageInnerWindow = innerWindow;
                }
            }
        }
        else {
            queryImageInnerWindow.getImageCanvas().setIsQueryImage(false);
        }
    }//GEN-LAST:event_queryImageCheckBoxActionPerformed

    /**
     * Starts the comparison calling the suitable method depending on if there is
     * a database or not.
     * @param evt 
     */
    private void compareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compareButtonActionPerformed
        // TODO add your handling code here:
        // You need to select the query image
        if (queryImageInnerWindow == null) {
            JOptionPane.showMessageDialog(null, "You have to choose the query image",
                "ERROR TRYING TO COMPARE", JOptionPane.ERROR_MESSAGE);
        } else if (!this.meanColourCheck.isSelected() && !this.scalableCheck.isSelected()
            && !this.structureCheck.isSelected() && !this.labelDescriptorCheck.isSelected()) {
            JOptionPane.showMessageDialog(null, "You have to choose a descriptor",
                "ERROR TRYING TO COMPARE", JOptionPane.ERROR_MESSAGE);
        } else {
            if (database != null && !database.isEmpty() && this.databaseButton.isSelected()) {
                try {
                    compareImageTilesByDatabase(false);
                } catch (Exception ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (openedImagesButton.isSelected()) {
                try {
                    // Enable to select a descriptor
                    this.labelDescriptorCheck.setEnabled(true);
                    this.meanColourCheck.setEnabled(true);
                    this.scalableCheck.setEnabled(true);
                    this.structureCheck.setEnabled(true);
                    compareImageTilesByOpenedImages();
                } catch (Exception ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_compareButtonActionPerformed

    /**
     * Sets some of the comparison settings depending on if the comparator takes
     * the position of the objects into account or not.
     * @param evt 
     */
    private void positionComboBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionComboBActionPerformed
        // TODO add your handling code here:
        if (this.positionComboB.getSelectedIndex() == 0) {
            this.duplicatesCheckB.setEnabled(false);
            this.inclusionComboB.setEnabled(false);
        }
        else {
            if (this.openedImagesButton.isSelected()) this.imagesGridComboB.setEnabled(true);
            this.duplicatesCheckB.setEnabled(true);
            this.inclusionComboB.setEnabled(true);
        }
    }//GEN-LAST:event_positionComboBActionPerformed

    /**
     * Opens the dialog with the comparison settings.
     * @param evt 
     */
    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        // TODO add your handling code here:
        // Open the dialog window
        if (database == null || database.isEmpty()) this.databaseButton.setEnabled(false);
        else {
            this.databaseButton.setEnabled(true);
            this.imagesGridComboB.setEnabled(false);
        }
        this.settingsDialog.setTitle("Comparison Settings");
        this.settingsDialog.setLocation(this.getWidth() / 3, this.getHeight() / 3);
        this.settingsDialog.setSize(400, 300);
        this.settingsDialog.setVisible(true);
    }//GEN-LAST:event_settingsButtonActionPerformed

    /**
     * Sets the current values of the comparison settings.
     * @param evt 
     */
    private void acceptSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptSettingsButtonActionPerformed
        // TODO add your handling code here:
        this.settingsDialog.setVisible(false);
    }//GEN-LAST:event_acceptSettingsButtonActionPerformed

    /**
     * Shows or hides some of the settings of the inner label comparator depending
     * on the selected values.
     * @param evt 
     */
    private void labelComparatorsComboBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelComparatorsComboBActionPerformed
        // TODO add your handling code here:
        this.weightComparatorsComboB.setVisible(this.labelComparatorsComboB.getSelectedIndex() == 3);
        this.inclusionTypeComboB.setVisible(this.labelComparatorsComboB.getSelectedIndex() == 3);
    }//GEN-LAST:event_labelComparatorsComboBActionPerformed

    /**
     * Shows or hides some of the settings of the comparators in the app depending
     * on the selected values.
     * @param evt 
     */
    private void comparatorComboBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comparatorComboBActionPerformed
        // TODO add your handling code here:
        if (this.comparatorComboB.getSelectedIndex() == 3) {
            this.positionComboB.setEnabled(false);
            this.duplicatesCheckB.setEnabled(false);
        }
        else this.positionComboB.setEnabled(true);
    }//GEN-LAST:event_comparatorComboBActionPerformed

    /**
     * Starts the query based on a selected label using the current database. So
     * as to do that it sets the classifier and then creates the grid descriptor
     * with the selected label. Then makes the query with that descriptor.
     * @param evt 
     */
    private void searchInDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchInDBActionPerformed
        // TODO add your handling code here:
        if (database != null) {
            try {
                // Set classifier
                //setClassifier();
                // Create the label and the griddeddescriptor for the search tag
                ImageLabelDescriptor tag = new ImageLabelDescriptor(this.searchComboBox.getSelectedItem().toString());
                System.out.println("\nSearch item: " + tag);
                Dimension d = new Dimension(1,1);
                BufferedImage bufferedImage = new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);
                GriddedDescriptor searchTag = new LabelGriddedDescriptor(bufferedImage, tag);
                
                // Set the general comparator: at least one tag in different position
                this.comparatorComboB.setSelectedIndex(0);
                this.positionComboB.setSelectedIndex(1);
                this.duplicatesCheckB.setSelected(true);
                // Inclusion label comparator
                this.labelComparatorsComboB.setSelectedIndex(2);
                // Set the comparator
                this.setCurrentComparator();
                searchTag.setComparator(comparator);
                queryDescriptor = searchTag;
                // Query with the tag
                this.compareImageTilesByDatabase(true);
            } catch (Exception ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_searchInDBActionPerformed

    /**
     * Gets the position in which the user has been clicked in order to show
     * or hide the output panel.
     * @param evt 
     */
    private void outputTextMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outputTextMouseReleased
        if(evt.isPopupTrigger()){
            Point p = this.scrollEditorOutput.getMousePosition();
            this.popupMenuPanelOutput.show(this.outputPanel,p.x,p.y);
        }
    }//GEN-LAST:event_outputTextMouseReleased

    /**
     * Shows or hides the output panel depeding if it was opened or not.
     * @param evt 
     */
    private void showOutputMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showOutputMousePressed
        // TODO add your handling code here:
        this.infoPanel.setVisible(true);
        float dividerLocation = (float)separatePanel.getDividerLocation()/separatePanel.getMaximumDividerLocation();
        // Overwhelmed
        if(dividerLocation>=1) {
            separatePanel.setDividerLocation(0.8);
        } else{
            separatePanel.setDividerLocation(1.0);
        }
    }//GEN-LAST:event_showOutputMousePressed

    /**
     * Removes the messages of the output panel.
     * @param evt 
     */
    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        this.outputText.setText("");
    }//GEN-LAST:event_clearActionPerformed

    /**
     * Changes the icon of the button which can show or hide the output panel
     * depending on if it's already opened or not.
     * @param evt 
     */
    private void separatePanelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_separatePanelPropertyChange
        if (evt.getPropertyName().equals("dividerLocation")) {
            float dividerLocation = (float) separatePanel.getDividerLocation() / separatePanel.getMaximumDividerLocation();
            if (dividerLocation >= 1) {
                showOutput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/desplegar20.png")));
            } else {
                showOutput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cerrar16.png")));
            }
        }
    }//GEN-LAST:event_separatePanelPropertyChange

    /**
     * Fills in the right second positions related to the math operator AND and
     * depending on the firts selected position so as to combine both to create
     * a right final postion.
     * @param evt 
     */
    private void andOperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_andOperatorActionPerformed
        // TODO add your handling code here:
        // AND selected
        if (andOperator.isSelected()) {
            // OR operator not selected
            this.orOperator.setSelected(false);
            // We fill in the second combo box depending on the selected item
            //   in the first combo box
            // Position Up or Down -> Left or Right
            switch (this.firstSearchPositionComboB.getSelectedIndex()) {
                case 0:
                case 1:
                    this.secondSearchPositionComboB.removeAllItems();
                    this.secondSearchPositionComboB.addItem("Left");
                    this.secondSearchPositionComboB.addItem("Right");
                    this.secondSearchPositionComboB.setVisible(true);
                    break;
                case 2:
                case 3:
                case 4:
                    this.secondSearchPositionComboB.removeAllItems();
                    this.secondSearchPositionComboB.addItem("Up");
                    this.secondSearchPositionComboB.addItem("Down");
                    this.secondSearchPositionComboB.setVisible(true);
                    break;
                default:
                    break;
            }
        }
        else this.secondSearchPositionComboB.setVisible(false);
    }//GEN-LAST:event_andOperatorActionPerformed

    /**
     * Fills in the right second positions related to the math operator OR and
     * depending on the firts selected position so as to combine both to create
     * a right final postion.
     * @param evt 
     */
    private void orOperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orOperatorActionPerformed
        // TODO add your handling code here:
        // OR selected
        if (orOperator.isSelected()) { 
            // AND operator not selected
            this.andOperator.setSelected(false);
        
            // Fill in the second position combo box depending on the selected
            //  item of the other position combo box
            this.secondSearchPositionComboB.removeAllItems();
            switch(this.firstSearchPositionComboB.getSelectedIndex()) {
                case 0:
                    this.secondSearchPositionComboB.addItem("Down");
                    this.secondSearchPositionComboB.addItem("Left");
                    this.secondSearchPositionComboB.addItem("Right");
                    this.secondSearchPositionComboB.addItem("Centre");
                    break;
                case 1:
                    this.secondSearchPositionComboB.addItem("Up");
                    this.secondSearchPositionComboB.addItem("Left");
                    this.secondSearchPositionComboB.addItem("Right");
                    this.secondSearchPositionComboB.addItem("Centre");
                    break;
                case 2:
                    this.secondSearchPositionComboB.addItem("Up");
                    this.secondSearchPositionComboB.addItem("Down");
                    this.secondSearchPositionComboB.addItem("Right");
                    this.secondSearchPositionComboB.addItem("Centre");
                    break;
                case 3:
                    this.secondSearchPositionComboB.addItem("Up");
                    this.secondSearchPositionComboB.addItem("Down");
                    this.secondSearchPositionComboB.addItem("Left");
                    this.secondSearchPositionComboB.addItem("Centre");
                    break;
                case 4:
                    this.secondSearchPositionComboB.addItem("Up");
                    this.secondSearchPositionComboB.addItem("Down");
                    this.secondSearchPositionComboB.addItem("Left");
                    this.secondSearchPositionComboB.addItem("Right");
                    break;
            }
            this.secondSearchPositionComboB.setVisible(true);
        }
        else this.secondSearchPositionComboB.setVisible(false);
    }//GEN-LAST:event_orOperatorActionPerformed

    /**
     * Calls the suitable method to fill in the second positions depending on
     * the selected math operator.
     * @param evt 
     */
    private void firstSearchPositionComboBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstSearchPositionComboBActionPerformed
        // TODO add your handling code here:
        if (this.andOperator.isSelected()) this.andOperatorActionPerformed(evt);
        else if (this.orOperator.isSelected()) this.orOperatorActionPerformed(evt);
    }//GEN-LAST:event_firstSearchPositionComboBActionPerformed

    /**
     * Closes all the opened images in the app.
     * @param evt 
     */
    private void closeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllButtonActionPerformed
        // TODO add your handling code here:
        this.closeAllOptionActionPerformed(evt);
    }//GEN-LAST:event_closeAllButtonActionPerformed

    /**
     * Disables the combo box related to the images to compare to the query image
     * because a database is being used.
     * @param evt 
     */
    private void databaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseButtonActionPerformed
        // TODO add your handling code here:
        this.imagesGridComboB.setEnabled(false);
    }//GEN-LAST:event_databaseButtonActionPerformed

    /**
     * Enables or disables the combo box related to the grid of the images to 
     * compare to the query image because any database is being used. It'll
     * depend on the selected comparator.
     * @param evt 
     */
    private void openedImagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openedImagesButtonActionPerformed
        // TODO add your handling code here:
        if (this.positionComboB.getSelectedIndex() == 1) this.imagesGridComboB.setEnabled(true);
        else this.imagesGridComboB.setEnabled(false);
    }//GEN-LAST:event_openedImagesButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog aboutDialog;
    private javax.swing.JMenuItem aboutOption;
    private javax.swing.JPanel aboutScreen;
    private javax.swing.JButton acceptSettingsButton;
    private javax.swing.JButton addItemToDB;
    private javax.swing.JCheckBox andOperator;
    private javax.swing.JPanel bdPanel;
    private javax.swing.JMenuItem clear;
    private javax.swing.JButton closeAllButton;
    private javax.swing.JMenuItem closeAllOption;
    private javax.swing.JButton closeDB;
    private javax.swing.JComboBox<String> comparatorComboB;
    private javax.swing.JButton compareButton;
    private javax.swing.ButtonGroup compareSettingsButtonGroup;
    private javax.swing.JPanel comparisonPanel;
    private javax.swing.JRadioButton databaseButton;
    private javax.swing.ButtonGroup descriptorsGroup;
    private javax.swing.JMenu descriptorsMenu;
    protected javax.swing.JDesktopPane desktop;
    private javax.swing.JLabel dialogTitle;
    private javax.swing.JCheckBox duplicatesCheckB;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JComboBox<String> firstSearchPositionComboB;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JComboBox<String> imagesGridComboB;
    private javax.swing.JComboBox<String> inclusionComboB;
    private javax.swing.JComboBox<String> inclusionTypeComboB;
    private javax.swing.JTabbedPane infoPanel;
    private javax.swing.JComboBox<String> labelComparatorsComboB;
    private javax.swing.JCheckBoxMenuItem labelDescriptorCheck;
    private javax.swing.JPanel labelDescriptorSettings;
    private javax.swing.JLabel labelProgramAuthor;
    private javax.swing.JLabel labelProgramName;
    private javax.swing.JLabel labelProgramVersion;
    private javax.swing.JPanel lowerToolbar;
    private javax.swing.JCheckBoxMenuItem meanColourCheck;
    private javax.swing.JMenuBar menu;
    private javax.swing.JButton newDB;
    private javax.swing.JButton openButton;
    private javax.swing.JButton openDB;
    private javax.swing.JMenuItem openOption;
    private javax.swing.JRadioButton openedImagesButton;
    private javax.swing.JCheckBox orOperator;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JEditorPane outputText;
    private javax.swing.JPopupMenu popupMenuPanelOutput;
    private javax.swing.JComboBox<String> positionComboB;
    private javax.swing.JComboBox<String> queryGridComboB;
    private javax.swing.JCheckBox queryImageCheckBox;
    private javax.swing.JButton saveDB;
    private javax.swing.JCheckBoxMenuItem scalableCheck;
    private javax.swing.JScrollPane scrollEditorOutput;
    private javax.swing.JComboBox<String> searchComboBox;
    private javax.swing.JPanel searchDbPanel;
    private javax.swing.JButton searchInDB;
    private javax.swing.JComboBox<String> secondSearchPositionComboB;
    public javax.swing.JSplitPane separatePanel;
    private javax.swing.JButton settingsButton;
    private javax.swing.JDialog settingsDialog;
    private javax.swing.JLabel showOutput;
    private javax.swing.JLabel stateToolbar;
    private javax.swing.JCheckBoxMenuItem structureCheck;
    private javax.swing.JComboBox<String> thresholdComboB;
    private javax.swing.JToolBar upperToolBar;
    private javax.swing.JComboBox<String> weightComparatorsComboB;
    // End of variables declaration//GEN-END:variables
}
