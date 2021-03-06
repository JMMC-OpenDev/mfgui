/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetsPanel extends javax.swing.JPanel {

    static Logger logger = LoggerFactory.getLogger(TargetsPanel.class.getName());
    Targets current = null;
    SettingsViewerInterface settingsViewer;
    private SettingsModel rootSettingsModel;

    /** Creates new form TargetsPanel */
    public TargetsPanel(SettingsViewerInterface viewer) {
        settingsViewer = viewer;
        initComponents();
        // set help buttons
        helpButton1.setAction(new ShowHelpAction("_Targets_MT_ou_Add_new_target_Bt"));
    }

    public void show(Targets t, SettingsModel s) {
        logger.debug("Showing data for {}", t);
        current = t;
        rootSettingsModel = s;
        targetList.setModel(settingsViewer.getSettingsModel().getTargetListModel());
        targetNameComboBox.setModel(settingsViewer.getSettingsModel().oiTargets);

        // update UI
        targetNameComboBoxActionPerformed(null);
        targetListValueChanged(null);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        targetList = new javax.swing.JList();
        addTargetButton = new javax.swing.JButton();
        targetNameComboBox = new javax.swing.JComboBox();
        removeTargetButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Target list"));
        setLayout(new java.awt.GridBagLayout());

        targetList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                targetListValueChanged(evt);
            }
        });
        targetList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                targetListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(targetList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);

        addTargetButton.setText("Add new target");
        addTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTargetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addTargetButton, gridBagConstraints);

        targetNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetNameComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(targetNameComboBox, gridBagConstraints);

        removeTargetButton.setText("Remove");
        removeTargetButton.setEnabled(false);
        removeTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTargetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        add(removeTargetButton, gridBagConstraints);

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        add(helpButton1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
//GEN-FIRST:event_targetListMouseClicked
    private void targetListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            rootSettingsModel.setSelectionPath(
                    new TreePath(new Object[]{
                rootSettingsModel,
                current,
                targetList.getSelectedValue()}));
        }
    }//GEN-LAST:event_targetListMouseClicked
//GEN-FIRST:event_removeTargetButtonActionPerformed
    private void removeTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Object[] selecteds = targetList.getSelectedValues();
        for (int i = 0; i < selecteds.length; i++) {
            Object object = selecteds[i];
            rootSettingsModel.removeTarget((Target) object);
        }
        removeTargetButton.setEnabled(false);
    }//GEN-LAST:event_removeTargetButtonActionPerformed

//GEN-FIRST:event_targetNameComboBoxActionPerformed
    private void targetNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if ((targetNameComboBox.getItemCount() > 0)
                && (targetNameComboBox.getSelectedIndex() != -1)) {
            addTargetButton.setEnabled(true);
        } else {
            addTargetButton.setEnabled(false);
        }
    }//GEN-LAST:event_targetNameComboBoxActionPerformed

//GEN-FIRST:event_addTargetButtonActionPerformed
    private void addTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String targetIdent = targetNameComboBox.getSelectedItem().toString();
        rootSettingsModel.addTarget(targetIdent);
    }//GEN-LAST:event_addTargetButtonActionPerformed
//GEN-FIRST:event_targetListValueChanged
    private void targetListValueChanged(javax.swing.event.ListSelectionEvent evt) {

        if (targetList.getSelectedIndex() != -1) {
            removeTargetButton.setEnabled(true);
        } else {
            removeTargetButton.setEnabled(false);
        }
    }//GEN-LAST:event_targetListValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTargetButton;
    private javax.swing.JButton helpButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton removeTargetButton;
    private javax.swing.JList targetList;
    private javax.swing.JComboBox targetNameComboBox;
    // End of variables declaration//GEN-END:variables
}
