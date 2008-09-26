package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import java.util.logging.*;

import javax.swing.JButton;
import javax.swing.JOptionPane;


/*
 * LITproApplet.java
 *
 * Created on 31 octobre 2006, 16:01
 */

/**
 *
 * @author  mella
 */
public class LITproApplet extends javax.swing.JApplet {
    final static String rcsId = "$Id: LITproApplet.java,v 1.8 2008-09-26 08:26:34 mella Exp $";

    // Variables declaration - do not modify
    private javax.swing.JButton startButton;

    /** Initializes the applet LITproApplet */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        initComponents();
                    }
                });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** This method is called from within the init() method to
     * initialize the form.
     */
    private void initComponents() {
        startButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        startButton.setText("Start LITpro GUI");
        startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startButtonActionPerformed(evt);
                }
            });

        getContentPane().add(startButton, new java.awt.GridBagConstraints());
    }

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Logger logger = Logger.getLogger("fr.jmmc.mf");
            logger.setLevel(java.util.logging.Level.ALL);
            logger.info("Starting ModelFitting");
            logger.info("Rev:" + rcsId);

            // Create a specific console handler
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(java.util.logging.Level.ALL);
            //logger.addHandler(handler);                      

            MFGui myFrame = new MFGui(null);
            myFrame.setVisible(true);
        } catch (Exception e) {
            new FeedbackReport(null, true, e);
        }
    }

    // End of variables declaration
}
