/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author carcassi
 */
public class ShowImage extends javax.swing.JFrame {

    /**
     * Creates new form ShowImage
     */
    public ShowImage() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new org.epics.graphene.ImagePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(imagePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setImage(Image image) {
        //getContentPane().setSize(image.getWidth(this), image.getHeight(this));
        imagePanel.setImage(image);
        pack();
    }

    private static void showImage(final Image image) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ShowImage frame = new ShowImage();
                frame.setImage(image);
                frame.setVisible(true);
            }
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        Histogram1D hist = new Hist1DT2();
        BufferedImage image = new BufferedImage(hist.getImageWidth(), hist.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Histogram1DRenderer renderer = new Histogram1DRenderer();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, hist);
        showImage(image);
//        ImageIO.write(image, "png", new File("hist1dtest.png"));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.epics.graphene.ImagePanel imagePanel;
    // End of variables declaration//GEN-END:variables
}