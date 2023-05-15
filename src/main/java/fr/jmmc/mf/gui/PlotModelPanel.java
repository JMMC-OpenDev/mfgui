/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Residual;
import fr.jmmc.mf.models.Target;
import fr.jmmc.oitools.model.OIFitsFile;
import java.text.ParseException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Give access to plot function. This panel can be embedded into one target
 * panel or be displayed one time for all targets. It does not display the
 * target combo box when the
 * 
 */
public class PlotModelPanel extends javax.swing.JPanel {

    public SettingsModel settingsModel = null;
    private PlotPanel plotPanel;
    private int startValue = 0;
    private String lastObservable = null;

    /** Creates new form PlotPanel */
    public PlotModelPanel(PlotPanel plotPanel) {
        this.plotPanel = plotPanel;
        startValue = Preferences.getInstance().getPreferenceAsInt("user.fov")*2;
        initComponents();
        // Set default value for angle
        plotRadialAngleFormattedTextField1.setValue(0);
        // Fix #714 (still requires a preference setup?)
        pixscaleFormattedTextField.setText("0.10");
        
        try {
            fovFormattedTextField.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(PlotModelPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // build help button
        //helpButton1.setAction(new ShowHelpAction(("ENDtt_PlotImage_Bt")));
        helpButton1.setAction(new ShowHelpAction(("ENDtt_PlotImage_Bt")));
        helpButton2.setAction(new ShowHelpAction(("ENDtt_PlotUVmap_Bt")));
        helpButton3.setAction(new ShowHelpAction(("ENDtt_PlotRadial_Bt")));
    }

    public void show(SettingsModel s, Target t) {
        targetComboBox.setVisible(false);
        targetLabel.setVisible(false);
        show(s);
        if (t != null) {
            targetComboBox.setSelectedItem(t);
        }
        // update widget states
        updateAvailableObservables();
    }

    public void show(SettingsModel s) {
        settingsModel = s;
        Object[] targets = s.getTargets();
        targetComboBox.removeAllItems();
        for (int i = 0; i < targets.length; i++) {
            Object object = targets[i];
            targetComboBox.addItem(object);
        }
        // User can act only if settings is valid with a minimum of one target
        boolean hasOneTarget = targetComboBox.getItemCount() != 0;
        boolean valid = hasOneTarget && s.isValid();
        plotImageButton.setEnabled(valid);
        plotUVMapButton.setEnabled(valid);
        plotRadialButton.setEnabled(valid);
        // update widget states
        updateAvailableObservables();
    }

    // todo: remove duplicated code with same method shared between PlotPanel and PlotModelPanel
    private void updateAvailableObservables() {

        // Perform update only if widgets are visible
        if (!isVisible()) {
            return;
        }

        // disable widget to flag automatic action into radialComboBoxActionPerformed
        radialComboBox.setEnabled(false);

        // check that there is at least one target to plot or return
        radialComboBox.removeAllItems();
        Object[] selectedTargets = targetComboBox.getSelectedObjects();
        if (selectedTargets.length == 0) {
            return;
        }

        HashSet<String> set = new HashSet();
        for (Object object : selectedTargets) {
            Target target = (Target) object;

            // First check that file observavbles are allowed to be plotted
            HashSet<String> targetResidualsSet = new HashSet();
            if (target.getResiduals() == null) {
                targetResidualsSet.add("VIS2");
                targetResidualsSet.add("VISamp");
                targetResidualsSet.add("VISphi");
                targetResidualsSet.add("T3amp");
                targetResidualsSet.add("T3phi");
            } else {
                Residual[] residuals = target.getResiduals().getResidual();
                for (int i = 0; i < residuals.length; i++) {
                    Residual residual = residuals[i];
                    targetResidualsSet.add(residual.getName());
                }
            }

            FileLink[] filelinks = target.getFileLink();
            for (FileLink fileLink : filelinks) {
                fr.jmmc.mf.models.File selectedFile = (fr.jmmc.mf.models.File) fileLink.getFileRef();

                OIFitsFile oifile = settingsModel.getOIFitsFromFile(selectedFile);
                String obs = "VIS2";
                if (oifile.hasOiVis2() && targetResidualsSet.contains(obs)) {
                    set.add(obs);
                }
                obs = "VISamp";
                if (oifile.hasOiVis() && targetResidualsSet.contains(obs)) {
                    set.add(obs);
                }
                obs = "VISphi";
                if (oifile.hasOiVis() && targetResidualsSet.contains(obs)) {
                    set.add(obs);
                }
                obs = "T3amp";
                if (oifile.hasOiT3() && targetResidualsSet.contains(obs)) {
                    set.add(obs);
                }
                obs = "T3phi";
                if (oifile.hasOiT3() && targetResidualsSet.contains(obs)) {
                    set.add(obs);
                }
            }
        }

        // fill combobox
        for (String string : set) {
            radialComboBox.addItem(string);
        }

        // make last selection active if possible
        if (set.contains(lastObservable)) {
            radialComboBox.setSelectedItem(lastObservable);
        }

        radialComboBox.setEnabled(true);

        // Ensure that plot radial choices are consistent with selected observable
        boolean canOverplotModelFlag;
        if (radialComboBox.getSelectedIndex() == -1) {
            canOverplotModelFlag = false;
        } else {
            String observableType = radialComboBox.getSelectedItem().toString();
            canOverplotModelFlag = !(observableType.contains("T3") || residualsCheckBox.isSelected());
        }
        addModelCheckBox.setEnabled(canOverplotModelFlag);
        plotRadialAngleFormattedTextField1.setEnabled(canOverplotModelFlag);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        targetLabel = new javax.swing.JLabel();
        targetComboBox = new javax.swing.JComboBox();
        plotUVMapButton = new javax.swing.JButton();
        helpButton2 = new javax.swing.JButton();
        helpButton3 = new javax.swing.JButton();
        plotRadialButton = new javax.swing.JButton();
        radialComboBox = new javax.swing.JComboBox();
        plotRadialAngleFormattedTextField1 = new javax.swing.JFormattedTextField();
        residualsCheckBox = new javax.swing.JCheckBox();
        addModelCheckBox = new javax.swing.JCheckBox();
        pixscalLabel = new javax.swing.JLabel();
        pixscaleFormattedTextField = new javax.swing.JFormattedTextField();
        plotImageButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();
        fovLabel = new javax.swing.JLabel();
        fovFormattedTextField = new javax.swing.JFormattedTextField();
        fillerPanel1 = new javax.swing.JPanel();
        fillerPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Plot model panel"));
        setLayout(new java.awt.GridBagLayout());

        targetLabel.setText("Target:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(targetLabel, gridBagConstraints);

        targetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(targetComboBox, gridBagConstraints);

        plotUVMapButton.setText("Plot UV Map");
        plotUVMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotUVMapButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(plotUVMapButton, gridBagConstraints);

        helpButton2.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(helpButton2, gridBagConstraints);

        helpButton3.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(helpButton3, gridBagConstraints);

        plotRadialButton.setText("Plot Radial");
        plotRadialButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotRadialButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(plotRadialButton, gridBagConstraints);

        radialComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "VIS2", "VISamp", "VISphi", "T3amp", "T3phi" }));
        radialComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radialComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(radialComboBox, gridBagConstraints);

        plotRadialAngleFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(plotRadialAngleFormattedTextField1, gridBagConstraints);

        residualsCheckBox.setText("Residuals");
        residualsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                residualsCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(residualsCheckBox, gridBagConstraints);

        addModelCheckBox.setSelected(true);
        addModelCheckBox.setText("Overplot model with cut angle");
        addModelCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModelCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(addModelCheckBox, gridBagConstraints);

        pixscalLabel.setText("pixscale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(pixscalLabel, gridBagConstraints);

        pixscaleFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        pixscaleFormattedTextField.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(pixscaleFormattedTextField, gridBagConstraints);

        plotImageButton.setText("Plot image");
        plotImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotImageButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(plotImageButton, gridBagConstraints);

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(helpButton1, gridBagConstraints);

        fovLabel.setText("FoV (mas) :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(fovLabel, gridBagConstraints);

        fovFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        fovFormattedTextField.setText(""+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(fovFormattedTextField, gridBagConstraints);

        fillerPanel1.setLayout(new javax.swing.BoxLayout(fillerPanel1, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(fillerPanel1, gridBagConstraints);

        fillerPanel.setLayout(new javax.swing.BoxLayout(fillerPanel, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(fillerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void plotImageButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_plotImageButtonActionPerformed
    {//GEN-HEADEREND:event_plotImageButtonActionPerformed
        Target targetToPlot = (Target) targetComboBox.getSelectedItem();
        String fov = getFoV();
        String mfov = "-"+getFoV();
        plotPanel.plotModelImage(targetToPlot, mfov, fov, mfov, fov, pixscaleFormattedTextField.getText());
    }//GEN-LAST:event_plotImageButtonActionPerformed

    private String getFoV() {
        System.out.println("fovFormattedTextField.getValue() = " + fovFormattedTextField.getValue());        
        double d = ((Number)fovFormattedTextField.getValue()).doubleValue();        
        return ""+ (d/2);
    }

    private void plotUVMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotUVMapButtonActionPerformed
        Target targetToPlot = (Target) targetComboBox.getSelectedItem();
        plotPanel.plotModelUVMap(targetToPlot);
}//GEN-LAST:event_plotUVMapButtonActionPerformed

    private void plotRadialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotRadialButtonActionPerformed
        String observableType = radialComboBox.getSelectedItem().toString();
        plotPanel.plotModelRadial((Target) targetComboBox.getSelectedItem(),
                observableType, residualsCheckBox.isSelected(),
                addModelCheckBox.isSelected() && addModelCheckBox.isEnabled(),
                plotRadialAngleFormattedTextField1.getText());
}//GEN-LAST:event_plotRadialButtonActionPerformed
    
    private void targetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetComboBoxActionPerformed
        // We have to ensure that plot radial choices are consistent with fitter setup
        radialComboBox.removeAllItems();
        Target selectedTarget = (Target) targetComboBox.getSelectedItem();
        if (selectedTarget == null) {
            return;
        }
        // And finally check that we can request the overploted model
        updateAvailableObservables();
    }//GEN-LAST:event_targetComboBoxActionPerformed

    private void residualsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_residualsCheckBoxActionPerformed
        updateAvailableObservables();
    }//GEN-LAST:event_residualsCheckBoxActionPerformed

    private void addModelCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModelCheckBoxActionPerformed
        updateAvailableObservables();
    }//GEN-LAST:event_addModelCheckBoxActionPerformed

    private void radialComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radialComboBoxActionPerformed
        if (radialComboBox.getSelectedIndex() >= 0 && radialComboBox.isEnabled()) {
            lastObservable = radialComboBox.getSelectedItem().toString();
            updateAvailableObservables();
        }
    }//GEN-LAST:event_radialComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addModelCheckBox;
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JPanel fillerPanel1;
    private javax.swing.JFormattedTextField fovFormattedTextField;
    private javax.swing.JLabel fovLabel;
    private javax.swing.JButton helpButton1;
    private javax.swing.JButton helpButton2;
    private javax.swing.JButton helpButton3;
    private javax.swing.JLabel pixscalLabel;
    private javax.swing.JFormattedTextField pixscaleFormattedTextField;
    private javax.swing.JButton plotImageButton;
    private javax.swing.JFormattedTextField plotRadialAngleFormattedTextField1;
    private javax.swing.JButton plotRadialButton;
    private javax.swing.JButton plotUVMapButton;
    private javax.swing.JComboBox radialComboBox;
    private javax.swing.JCheckBox residualsCheckBox;
    private javax.swing.JComboBox targetComboBox;
    private javax.swing.JLabel targetLabel;
    // End of variables declaration//GEN-END:variables
}
