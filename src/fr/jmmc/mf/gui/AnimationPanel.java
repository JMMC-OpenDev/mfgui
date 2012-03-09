/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Show one frame able to show a list of images files.
 * FIXME check that timer referenced by swing does not leak s.a. LogbackGui
 * 
 * @author mella
 */
public class AnimationPanel extends javax.swing.JPanel  implements ActionListener, ChangeListener{

    /** Timer used for animation */
    Timer timer;
    /** default auto refresh period = 5 second */
    private static final int REFRESH_PERIOD = 1000;
    /** File stack */
    File[] files;
    /** File names stack */
    String[] filenames;
    /** Image stack */
    Image[] images;
    /** index of displayed image */
    int imageIndex = 0;
    /** Component which handles image */
    ImageIcon imageIcon=null;

    /** Creates new form AnimationPanel */
    public AnimationPanel(File[] inputFiles, String[] inputFilenames) {
        initComponents();        
        
        /* Init and launch the timer that will throw actionEvent */
        timer = new Timer(REFRESH_PERIOD, this);
        timer.setInitialDelay(0);
        timer.start();

        /* Init the images related arrays */
        files = new File[inputFiles.length];
        images = new Image[inputFiles.length];
        filenames = inputFilenames;

        for (int i = 0; i < inputFiles.length; i++) {
            File file = inputFiles[i];
            files[i] = file;
            /* FIXME check if illegal state exception should be handled */
            images[i] = UtilsClass.getImage(file);
        }

        /* Init bounds of jSlider */
        imageSlider.setMinimum(0);
        imageSlider.setMaximum(files.length - 1);
        imageSlider.setMinorTickSpacing(1);
        imageSlider.setMajorTickSpacing(1);

        /* Set one iconImage on the swing component which will load the images */
        imageIcon = new ImageIcon(images[0]);
        imageLabel.setIcon(imageIcon);
        
        /* throw actionEvent for changes on some swing elements*/
        animateCheckBox.addActionListener(this);
        imageSlider.addChangeListener(this);        
    }
    
    /**
     * Handle event comming from swing components or timer     
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            /* Get new image index using slider reference index */
            imageIndex = (imageSlider.getValue() + 1) % files.length;

            /* Update slider position that will throw one changeEvent  to continue proper handling */
            imageSlider.setValue(imageIndex);
        } else if (e.getSource() == animateCheckBox) {
            if (animateCheckBox.isSelected()) {
                timer.start();
            } else {
                timer.stop();
            }
        }
    }

    /**
     * Handle Change event of the slider. This method perform image change/update.
     * 
     * @param e the change event
     */
    public void stateChanged(ChangeEvent e) {        
        imageIndex = imageSlider.getValue();
        /* Update and redraw image */
        imageIcon.setImage(images[imageIndex]);
        filenameLabel.setText(filenames[imageIndex]);
        imageLabel.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        animateCheckBox = new javax.swing.JCheckBox();
        imageLabel = new javax.swing.JLabel();
        imageSlider = new javax.swing.JSlider();
        filenameLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        animateCheckBox.setSelected(true);
        animateCheckBox.setText("animate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(animateCheckBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(imageLabel, gridBagConstraints);

        imageSlider.setPaintTicks(true);
        imageSlider.setSnapToTicks(true);
        imageSlider.setMaximumSize(new java.awt.Dimension(50, 52));
        imageSlider.setPreferredSize(new java.awt.Dimension(100, 52));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(imageSlider, gridBagConstraints);

        filenameLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(filenameLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox animateCheckBox;
    private javax.swing.JLabel filenameLabel;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JSlider imageSlider;
    // End of variables declaration//GEN-END:variables
    
}