/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Class which allows to show an image contained in a inner window.
 * 
 * @author Lidia Sánchez Mérida
 */
public class ImageCanvas extends javax.swing.JPanel {
    
    /**
     * Number of rows which the grid images to compare to the query has.
     */
    private static int rowGridOtherImages = 2;
    /**
     * Number of cols which the grid images to compare to the query has.
     */
    private static int colGridOtherImages = 2;
    
    /**
     * Number of rows which the grid query has.
     */
    private static int rowGridQuery = 1;
    /**
     * Number of cols which the grid query has.
     */
    private static int colGridQuery = 1;
    
    /**
     * Image width to draw it.
     */
    private static int width = 400;
    /**
     * Image height to draw it.
     */
    private static int height = 300;
    
    /**
     * True if it's the query image, false if it's not.
     */
    private boolean isQueryImage = false;
    
    /**
     * The image which is displayed in a inner window.
     */
    private BufferedImage image;
    
    /**
     * Creates new form CanvasImage.
     */
    public ImageCanvas() {
        initComponents();
    }

    /**
     * Gets the number of rows which the grid of the images to compare to the 
     * query image has.
     * @return number of rows of that grid.
     */
    public static int getRowGridOtherImages() {
        return rowGridOtherImages;
    }

    /**
     * Sets the number of rows which the grid of the images to compare to the
     * query image has.
     * @param rowGridOtherImages the new number of rows.
     */
    public static void setRowGridOtherImages(int rowGridOtherImages) {
        ImageCanvas.rowGridOtherImages = rowGridOtherImages;
    }

    /**
     * Gets the number of cols which the grid of the images to compare to the
     * query image has.
     * @return the number of cols of that grid.
     */
    public static int getColGridOtherImages() {
        return colGridOtherImages;
    }

    /**
     * Sets the number of cols which the grid of the images to compare to the 
     * query image has.
     * @param colGridOtherImages the new number of cols.
     */
    public static void setColGridOtherImages(int colGridOtherImages) {
        ImageCanvas.colGridOtherImages = colGridOtherImages;
    }

    /**
     * Gets the number of rows which the grid of the query image has.
     * @return the number of rows.
     */
    public static int getRowGridQuery() {
        return rowGridQuery;
    }

    /**
     * Sets the number of rows which the grid of the query image has.
     * @param rowGridQuery the new number of rows.
     */
    public static void setRowGridQuery(int rowGridQuery) {
        ImageCanvas.rowGridQuery = rowGridQuery;
    }

    /**
     * Gets the number of cols which the grid of the query has.
     * @return the number of cols.
     */
    public static int getColGridQuery() {
        return colGridQuery;
    }

    /**
     * Sets the number of cols which the grid of the query has.
     * @param colGridQuery the new number of cols.
     */
    public static void setColGridQuery(int colGridQuery) {
        ImageCanvas.colGridQuery = colGridQuery;
    }
    
    /**
     * Gets if an image is the query image or not.
     * @return true if it's the query image, false if it's not.
     */
    public boolean isQueryImage() {
        return isQueryImage;
    }
    
    /**
     * Sets if the current image is the new query image or isn't anymore.
     * @param newRole true if it's the new query image, false if it isn't the
     * query image anymore.
     */
    public void setIsQueryImage(boolean newRole) {
        isQueryImage = newRole;
    }
    
    /**
     * Gets the image displayed in the canvas.
     * @return the image displayed.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image displayed in the canvas with another.
     * @param image: the new image to be displayed.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        // If it's not null we show it
        if (image != null) {
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        }
    }
    
    /**
     * Draws the image in the canvas.
     * @param g: the graphic objetct which is used to draw the image.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the image
        if (image != null) {
            g.drawImage(image, 0, 0, width, height, this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
