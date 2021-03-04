/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.mf.ModelUtils;
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
    private javax.swing.JButton checkSyntaxButton;
    private fr.jmmc.mf.gui.util.YorickCodeEditor commonCodeEditor;
    private javax.swing.JButton helpButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton visitUserModelReposButton;
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
        if (u.getCommon() != null) {
            commonCodeEditor.setText(u.getCommon().getContent());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        helpButton1 = new javax.swing.JButton();
        addMyModelButton = new javax.swing.JButton();
        visitUserModelReposButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        commonCodeEditor = new fr.jmmc.mf.gui.util.YorickCodeEditor();
        checkSyntaxButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Usercode panel"));
        setLayout(new java.awt.GridBagLayout());

        helpButton1.setText("helpTBD");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(helpButton1, gridBagConstraints);

        addMyModelButton.setText("create user model...");
        addMyModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMyModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(addMyModelButton, gridBagConstraints);

        visitUserModelReposButton.setText("visit web repository...");
        visitUserModelReposButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitUserModelReposButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(visitUserModelReposButton, gridBagConstraints);

        commonCodeEditor.setColumns(20);
        commonCodeEditor.setRows(5);
        commonCodeEditor.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                commonCodeEditorCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(commonCodeEditor);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(jScrollPane1, gridBagConstraints);

        checkSyntaxButton.setText("check syntax");
        checkSyntaxButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(checkSyntaxButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addMyModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMyModelButtonActionPerformed
        settingsModel.addUserModel();
    }//GEN-LAST:event_addMyModelButtonActionPerformed

    private void visitUserModelReposButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitUserModelReposButtonActionPerformed
        ModelUtils.visitUsermodelsRepository();
    }//GEN-LAST:event_visitUserModelReposButtonActionPerformed

    private void commonCodeEditorCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_commonCodeEditorCaretUpdate
        current.getCommon().setContent(commonCodeEditor.getText());
    }//GEN-LAST:event_commonCodeEditorCaretUpdate
}
