/*
 * TargetsPanel.java
 *
 * Created on 6 decembre 2006, 11:33
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;

import javax.swing.ListModel;


/**
 *
 * @author  mella
 */
public class TargetsPanel extends javax.swing.JPanel
{
    /**
     * DOCUMENT ME!
     */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.TargetsPanel");

    /**
     * DOCUMENT ME!
     */
    Targets current = null;

    /**
     * DOCUMENT ME!
     */
    SettingsViewerInterface settingsViewer;
    private SettingsModel rootSettingsModel;


   /** Creates new form TargetsPanel */
    public TargetsPanel(SettingsViewerInterface viewer)
    {
        settingsViewer = viewer;
        initComponents();
    }

    /**
     * DOCUMENT ME!
     *
     * @param t DOCUMENT ME!
     * @param s DOCUMENT ME!
     */
    public void show(Targets t, SettingsModel s)
    {
        logger.fine("Showing data for " + t);
        current               = t;
        rootSettingsModel     = s;
        targetList.setModel(settingsViewer.getSettingsModel().getTargetListModel());
        targetNameComboBox.setModel(settingsViewer.getSettingsModel().oiTargets);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        targetList = new javax.swing.JList();
        addTargetButton = new javax.swing.JButton();
        targetNameComboBox = new javax.swing.JComboBox();
        removeTargetButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Target list"));
        setLayout(new java.awt.GridBagLayout());

        targetList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                targetListValueChanged(evt);
            }
        });
        targetList.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                targetListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(targetList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);

        addTargetButton.setText("Add new target");
        addTargetButton.setEnabled(false);
        addTargetButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addTargetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addTargetButton, gridBagConstraints);

        targetNameComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
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
        removeTargetButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                removeTargetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        add(removeTargetButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
//GEN-FIRST:event_targetListMouseClicked
    private void targetListMouseClicked(java.awt.event.MouseEvent evt)
    { 

        if (evt.getClickCount() == 2)
        {
            settingsViewer.showSettingElement(targetList.getSelectedValue());
        }
    }//GEN-LAST:event_targetListMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
//GEN-FIRST:event_removeTargetButtonActionPerformed
    private void removeTargetButtonActionPerformed(java.awt.event.ActionEvent evt)
    { 
        logger.entering("" + this.getClass(), "removeTargetButtonActionPerformed");

        int[] indices = targetList.getSelectedIndices();

        for (int i = 0; i < indices.length; i++)
        {
            int indice = indices[i] - i;
            current.removeTarget(indice);
            settingsViewer.getSettingsModel().getTargetListModel().removeElementAt(indice);
        }

        removeTargetButton.setEnabled(false);
        // fire tree event to refresh
        settingsViewer.getSettingsModel().fireUpdate();
    }//GEN-LAST:event_removeTargetButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
//GEN-FIRST:event_targetNameComboBoxActionPerformed
    private void targetNameComboBoxActionPerformed(java.awt.event.ActionEvent evt)
    { 

        if ((targetNameComboBox.getItemCount() > 0) &&
                (targetNameComboBox.getSelectedIndex() != -1))
        {
            addTargetButton.setEnabled(true);
        }
        else
        {
            addTargetButton.setEnabled(false);
        }
    }//GEN-LAST:event_targetNameComboBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
//GEN-FIRST:event_addTargetButtonActionPerformed
    private void addTargetButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        logger.fine("Adding on target to current settings");

        Target t           = new Target();
        String targetIdent = "" + targetNameComboBox.getSelectedItem();
        /*   + "_"
           +rootSettingsModel.getNewTargetId();*/
        t.setIdent(targetIdent);
        t.setNormalize(true);

        // Add list of currently files that contain this target        
        ListModel targetFiles = settingsViewer.getSettingsModel()
                                              .getFileListModelForOiTarget(targetIdent);

        for (int i = 0; i < targetFiles.getSize(); i++)
        {
            FileLink fileLink = new FileLink();
            fileLink.setFileRef(targetFiles.getElementAt(i));
            t.addFileLink(fileLink);
            logger.fine("Adding default reference to file :" + targetFiles.getElementAt(i));
        }

        current.addTarget(t);                
        // fire tree event to refresh
        settingsViewer.getSettingsModel().fireUpdate();
        // display view of added target automatically 
        settingsViewer.showSettingElement(t);
    }//GEN-LAST:event_addTargetButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
//GEN-FIRST:event_targetListValueChanged
    private void targetListValueChanged(javax.swing.event.ListSelectionEvent evt)
    { 

        if (targetList.getSelectedIndex() != -1)
        {
            removeTargetButton.setEnabled(true);
        }
        else
        {
            removeTargetButton.setEnabled(false);
        }
    }//GEN-LAST:event_targetListValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTargetButton;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton removeTargetButton;
    private javax.swing.JList targetList;
    private javax.swing.JComboBox targetNameComboBox;
    // End of variables declaration//GEN-END:variables

 
}
