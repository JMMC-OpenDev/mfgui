/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Results;

public class ResultsPanel extends javax.swing.JPanel {

  Results current;
  SettingsViewerInterface viewer = null;
  SettingsModel settingsModel = null;

  /** Creates new form ResultPanel */
  public ResultsPanel(SettingsViewerInterface viewer) {
    this.viewer = viewer;
    initComponents();

    // build help button
    helpButton1.setAction(new ShowHelpAction(("BEG_Results")));
  }

  public void show(Results r, SettingsModel s) {
    current = r;
    settingsModel = s;
  }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        helpButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Results Panel"));
        setLayout(new java.awt.GridBagLayout());

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(helpButton1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton helpButton1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
