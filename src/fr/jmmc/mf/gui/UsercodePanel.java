/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Usercode;

/**
 * Panel used to show the common code declared by user.
 * @author mella
 */
public class UsercodePanel extends javax.swing.JPanel {

    Usercode current;
    SettingsModel settingsModel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMyModelButton;
    private javax.swing.JTextArea commonCodeTextArea;
    private javax.swing.JButton helpButton1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    /** Creates new form UsercodePanel */
    public UsercodePanel(SettingsViewerInterface viewer) {
        initComponents();
        // build help button
        //TODO link to doc section : helpButton1.setAction(new ShowHelpAction(("ENDtt_Usercode")));
    }

    public void show(SettingsModel s, Usercode u) {
        settingsModel = s;
        current = u;
        if (u.getCommon()!= null)
        {
            commonCodeTextArea.setText(u.getCommon().getContent());
        }        
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        helpButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        commonCodeTextArea = new javax.swing.JTextArea();
        addMyModelButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Usercode panel"));
        setLayout(new java.awt.GridBagLayout());

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(helpButton1, gridBagConstraints);

        commonCodeTextArea.setColumns(20);
        commonCodeTextArea.setRows(5);
        commonCodeTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                commonCodeTextAreaCaretUpdate(evt);
            }
        });
        jScrollPane2.setViewportView(commonCodeTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(jScrollPane2, gridBagConstraints);

        addMyModelButton.setText("create user model...");
        addMyModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMyModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(addMyModelButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void commonCodeTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_commonCodeTextAreaCaretUpdate
        current.getCommon().setContent(commonCodeTextArea.getText());
    }//GEN-LAST:event_commonCodeTextAreaCaretUpdate

    private void addMyModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMyModelButtonActionPerformed
       settingsModel.addUserModel();
    }//GEN-LAST:event_addMyModelButtonActionPerformed
}
