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
import java.util.HashSet;

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
        startValue = Preferences.getInstance().getPreferenceAsInt("user.fov");
        initComponents();
        // Set default value for angle
        plotRadialAngleFormattedTextField1.setValue(0);
        // Fix #714 (still requires a preference setup?)
        pixscaleFormattedTextField.setText("0.1");
        pixscaleFormattedTextField1.setText("1");

        // build help button
        //helpButton1.setAction(new ShowHelpAction(("ENDtt_PlotImage_Bt")));
        helpButton1.setAction(new ShowHelpAction(("ENDtt_PlotImage_Bt")));
        helpButton2.setAction(new ShowHelpAction(("ENDtt_PlotUVmap_Bt")));
        helpButton3.setAction(new ShowHelpAction(("ENDtt_PlotRadial_Bt")));
        helpButton4.setAction(new ShowHelpAction(("ENDtt_PlotSniffer_Bt")));
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
        Object[] targets = s.getTargetListModel().toArray();
        targetComboBox.removeAllItems();
        for (int i = 0; i < targets.length; i++) {
            Object object = targets[i];
            targetComboBox.addItem(object);
        }
        // User can act only if settings is valid with a minimum of one target
        boolean hasOneTarget = targetComboBox.getItemCount() != 0;
        boolean valid = hasOneTarget && s.isValid();
        plotImageButton.setEnabled(valid);
        plotSnifferMapButton.setEnabled(valid);
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

        sameBoxCheckBox = new javax.swing.JCheckBox();
        plotImageButton = new javax.swing.JButton();
        targetLabel = new javax.swing.JLabel();
        targetComboBox = new javax.swing.JComboBox();
        plotUVMapButton = new javax.swing.JButton();
        plotSnifferMapButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();
        helpButton2 = new javax.swing.JButton();
        helpButton3 = new javax.swing.JButton();
        plotRadialButton = new javax.swing.JButton();
        radialComboBox = new javax.swing.JComboBox();
        helpButton4 = new javax.swing.JButton();
        plotRadialAngleFormattedTextField1 = new javax.swing.JFormattedTextField();
        residualsCheckBox = new javax.swing.JCheckBox();
        addModelCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        xminFormattedTextField = new javax.swing.JFormattedTextField();
        yminFormattedTextField = new javax.swing.JFormattedTextField();
        xmaxFormattedTextField = new javax.swing.JFormattedTextField();
        ymaxFormattedTextField = new javax.swing.JFormattedTextField();
        pixscaleFormattedTextField = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        xminFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        yminFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        xmaxFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        ymaxFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        pixscaleFormattedTextField1 = new javax.swing.JFormattedTextField();

        sameBoxCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sameBoxCheckBoxActionPerformed(evt);
            }
        });

        setBorder(javax.swing.BorderFactory.createTitledBorder("Plot model panel"));
        setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(targetComboBox, gridBagConstraints);

        plotUVMapButton.setText("Plot UV Map");
        plotUVMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotUVMapButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(plotUVMapButton, gridBagConstraints);

        plotSnifferMapButton.setText("Plot sniffer map");
        plotSnifferMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotSnifferMapButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(plotSnifferMapButton, gridBagConstraints);

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(helpButton1, gridBagConstraints);

        helpButton2.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
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

        helpButton4.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(helpButton4, gridBagConstraints);

        plotRadialAngleFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("ymin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText("xmax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("ymax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel5.setText("pixscale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel6.setText("xmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel6, gridBagConstraints);

        xminFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xminFormattedTextField.setText("-"+startValue);
        xminFormattedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xminFormattedTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(xminFormattedTextField, gridBagConstraints);

        yminFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        yminFormattedTextField.setText("-"+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(yminFormattedTextField, gridBagConstraints);

        xmaxFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xmaxFormattedTextField.setText(""+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(xmaxFormattedTextField, gridBagConstraints);

        ymaxFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        ymaxFormattedTextField.setText(""+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(ymaxFormattedTextField, gridBagConstraints);

        pixscaleFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        pixscaleFormattedTextField.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(pixscaleFormattedTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("xmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel7, gridBagConstraints);

        xminFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xminFormattedTextField1.setText("-"+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(xminFormattedTextField1, gridBagConstraints);

        jLabel8.setText("ymin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel8, gridBagConstraints);

        yminFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        yminFormattedTextField1.setText("-"+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(yminFormattedTextField1, gridBagConstraints);

        jLabel9.setText("xmax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel9, gridBagConstraints);

        xmaxFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xmaxFormattedTextField1.setText(""+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(xmaxFormattedTextField1, gridBagConstraints);

        jLabel10.setText("ymax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel10, gridBagConstraints);

        ymaxFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        ymaxFormattedTextField1.setText(""+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(ymaxFormattedTextField1, gridBagConstraints);

        jLabel11.setText("pixscale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel11, gridBagConstraints);

        pixscaleFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        pixscaleFormattedTextField1.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(pixscaleFormattedTextField1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void plotImageButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_plotImageButtonActionPerformed
    {//GEN-HEADEREND:event_plotImageButtonActionPerformed
        Target targetToPlot = (Target) targetComboBox.getSelectedItem();
        plotPanel.plotModelImage(targetToPlot, xminFormattedTextField.getText(), xmaxFormattedTextField.getText(), yminFormattedTextField.getText(), ymaxFormattedTextField.getText(), pixscaleFormattedTextField.getText());
    }//GEN-LAST:event_plotImageButtonActionPerformed

    private void plotUVMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotUVMapButtonActionPerformed
        Target targetToPlot = (Target) targetComboBox.getSelectedItem();
        plotPanel.plotModelUVMap(targetToPlot);
}//GEN-LAST:event_plotUVMapButtonActionPerformed

    private void plotSnifferMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotSnifferMapButtonActionPerformed
        plotPanel.plotModelSnifferMap((Target) targetComboBox.getSelectedItem(),
                xminFormattedTextField1.getText(),
                xmaxFormattedTextField1.getText(),
                yminFormattedTextField1.getText(),
                ymaxFormattedTextField1.getText(),
                pixscaleFormattedTextField1.getText());
}//GEN-LAST:event_plotSnifferMapButtonActionPerformed

    private void plotRadialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotRadialButtonActionPerformed
        String observableType = radialComboBox.getSelectedItem().toString();
        plotPanel.plotModelRadial((Target) targetComboBox.getSelectedItem(),
                observableType, residualsCheckBox.isSelected(),
                addModelCheckBox.isSelected() && addModelCheckBox.isEnabled(),
                plotRadialAngleFormattedTextField1.getText());
}//GEN-LAST:event_plotRadialButtonActionPerformed

    private void updateImageBounds() {
        String v = xminFormattedTextField.getText().replace("-", "");
        xminFormattedTextField.setText("-" + v);
        xmaxFormattedTextField.setText(v);
        yminFormattedTextField.setText("-" + v);
        ymaxFormattedTextField.setText(v);
    }

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

    private void sameBoxCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sameBoxCheckBoxActionPerformed
        final boolean sameBounds = sameBoxCheckBox.isSelected();
        yminFormattedTextField.setEnabled(sameBounds);
        ymaxFormattedTextField.setEnabled(sameBounds);
        xmaxFormattedTextField.setEnabled(sameBounds);
        if (sameBounds) {
            updateImageBounds();
        }
    }//GEN-LAST:event_sameBoxCheckBoxActionPerformed

    private void xminFormattedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xminFormattedTextFieldActionPerformed
        if (sameBoxCheckBox.isSelected()) {
            updateImageBounds();
        }
    }//GEN-LAST:event_xminFormattedTextFieldActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addModelCheckBox;
    private javax.swing.JButton helpButton1;
    private javax.swing.JButton helpButton2;
    private javax.swing.JButton helpButton3;
    private javax.swing.JButton helpButton4;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JFormattedTextField pixscaleFormattedTextField;
    private javax.swing.JFormattedTextField pixscaleFormattedTextField1;
    private javax.swing.JButton plotImageButton;
    private javax.swing.JFormattedTextField plotRadialAngleFormattedTextField1;
    private javax.swing.JButton plotRadialButton;
    private javax.swing.JButton plotSnifferMapButton;
    private javax.swing.JButton plotUVMapButton;
    private javax.swing.JComboBox radialComboBox;
    private javax.swing.JCheckBox residualsCheckBox;
    private javax.swing.JCheckBox sameBoxCheckBox;
    private javax.swing.JComboBox targetComboBox;
    private javax.swing.JLabel targetLabel;
    private javax.swing.JFormattedTextField xmaxFormattedTextField;
    private javax.swing.JFormattedTextField xmaxFormattedTextField1;
    private javax.swing.JFormattedTextField xminFormattedTextField;
    private javax.swing.JFormattedTextField xminFormattedTextField1;
    private javax.swing.JFormattedTextField ymaxFormattedTextField;
    private javax.swing.JFormattedTextField ymaxFormattedTextField1;
    private javax.swing.JFormattedTextField yminFormattedTextField;
    private javax.swing.JFormattedTextField yminFormattedTextField1;
    // End of variables declaration//GEN-END:variables
}
