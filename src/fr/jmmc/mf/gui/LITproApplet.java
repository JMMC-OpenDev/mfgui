package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.*;

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
    final static String rcsId = "$Id: LITproApplet.java,v 1.4 2007-02-14 10:32:33 mella Exp $";

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
            ReportDialog.setDefaultComment("Please complete above" +
                " informations to improve this software.\n" + rcsId + "\n" +
                "---\n");

            MainFrame myFrame = new MainFrame();
            myFrame.setVisible(true);
        } catch (Exception e) {
            new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
            System.exit(1);
        }
    }

    // End of variables declaration
}
