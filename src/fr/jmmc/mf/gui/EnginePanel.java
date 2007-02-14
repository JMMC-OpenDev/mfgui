/*
 * EnginePanel.java
 *
 * Created on 28 septembre 2006, 13:43
 */

package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ReportDialog;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;



import java.util.logging.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;


/**
 *
 * @author  mella
 */
public class EnginePanel extends javax.swing.JPanel {
    Logger logger = Logger.getLogger("jmmc.mf.gui.EnginePanel");
    
    /** Creates new form EnginePanel */
    public EnginePanel(){
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        getYogaVersionButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        getAvailableModelsButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        getYogaVersionButton.setAction(MainFrame.getYogaVersionAction);
        getYogaVersionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getYogaVersionButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(getYogaVersionButton, gridBagConstraints);

        runButton.setAction(MainFrame.runFitAction);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        add(runButton, gridBagConstraints);

        getAvailableModelsButton.setAction(MainFrame.getModelListAction);
        getAvailableModelsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getAvailableModelsButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(getAvailableModelsButton, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void getAvailableModelsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getAvailableModelsButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_getAvailableModelsButtonActionPerformed

    private void getYogaVersionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getYogaVersionButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_getYogaVersionButtonActionPerformed
    
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed

        // TODO: get xml deocument for current settings and make it processed by run.php
        logger.warning("We should send xml to run.php...");
    }//GEN-LAST:event_runButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton getAvailableModelsButton;
    private javax.swing.JButton getYogaVersionButton;
    private javax.swing.JButton runButton;
    // End of variables declaration//GEN-END:variables
    
}
