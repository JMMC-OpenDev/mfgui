/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Parameter;
import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotChi2Panel extends javax.swing.JPanel implements Observer {

    /** Class logger */
    final static Logger logger = LoggerFactory.getLogger(PlotChi2Panel.class.getName());
    public SettingsModel settingsModel = null;
    private PlotPanel plotPanel;
    private ParametersTableModel param1TableModel;
    private Parameter oldXParam = null;
    private Parameter oldYParam = null;
    private Vector<SettingsModel> knownSettingsModels = new Vector();
    private int startValue = 0;
    /** this variable is true during init method and false else */
    private boolean isIniting;

    /** Creates new form PlotPanel */
    public PlotChi2Panel(PlotPanel plotPanel) {
        this.plotPanel = plotPanel;
        param1TableModel = new ParametersTableModel();
        startValue = Preferences.getInstance().getPreferenceAsInt("user.fov");
        initComponents();
        // build help button
        helpButton1.setAction(new ShowHelpAction(("END_Plots_PlotChi2_Bt")));
        tablePanel.add(jTable1.getTableHeader(), BorderLayout.NORTH);
    }

    public void show(SettingsModel s) {
        settingsModel = s;

        isIniting = true;

        Object[] params;

        params = s.getParameterListModel().toArray();

        // we want to listen model change events
        if (!knownSettingsModels.contains(settingsModel)) {
            settingsModel.addObserver(this);
            knownSettingsModels.add(settingsModel);
        }

        // Store old references
        Parameter tmpX = oldXParam;
        Parameter tmpY = oldYParam;
        // Detect if oldParam are kept
        boolean xParamChanged = false;
        boolean yParamChanged = false;

        // Populates combo with given params & Call old user entries back
        xComboBox.removeAllItems();
        yComboBox.removeAllItems();
        logger.debug("Searching {} parameters to use in chi2 slice", params.length);
        for (int i = 0; i < params.length; i++) {
            Parameter p = (Parameter) params[i];
            if (!p.getHasFixedValue()) {
                logger.debug("{} added to the comboboxes", p.getName());
                xComboBox.addItem(p);
                yComboBox.addItem(p);
                if (tmpX != null && tmpX.getName().equals(p.getName())) {
                    xComboBox.setSelectedItem(p);
                    xParamChanged = true;
                }
                if (tmpY != null && tmpY.getName().equals(p.getName())) {
                    yComboBox.setSelectedItem(p);
                    yParamChanged = true;
                }
            }
        }

        // update table with
        updateTable();

        isIniting = false;
        // fix bounds for new parameters
        if (xParamChanged) {
            xComboBoxActionPerformed(null);
        }
        if (yParamChanged) {
            yComboBoxActionPerformed(null);
        }
    }

    private void plotChi2(boolean log, boolean reduced) {
        String titleLabel = "Slice";

        // Build command line arguments according to the widget states                
        StringBuilder args = new StringBuilder();

        // If check box for probing is selected
        if (runFitCheckBox.isSelected()) {
            // add option in  command line
            args.append("-m 'probe' ");
            // change plot title
            titleLabel = "Probe";
        }

        StringBuilder type = new StringBuilder();
        args.append("-o '");
        if (reduced) {
            type.append("Reduced ");
            args.append("reduced_chi2=1,");
        }
        if (log) {
            type.append("log(Chi2)");
            args.append("log_chi2=1");
        } else {
            type.append("Chi2");
            args.append("log_chi2=0");
        }
        args.append("' ");
        final String xParamName = ((Parameter) xComboBox.getSelectedItem()).getName();
        args.append(xParamName).append(" ");
        args.append(xminFormattedTextField.getText()).append(" ");
        args.append(xmaxFormattedTextField.getText()).append(" ");
        args.append(xSamplingFormattedTextField.getText());

        if (jRadioButton1D.isSelected()) {
            plotPanel.plot("getChi2Map", args.toString(), "1D " + type + " " + titleLabel + " on " + xParamName);
        } else {
            final String yParamName = ((Parameter) yComboBox.getSelectedItem()).getName();
            args.append(" '").append(yParamName).append("' ").append(yminFormattedTextField.getText()).append(" ");
            args.append(ymaxFormattedTextField.getText()).append(" ").append(ySamplingFormattedTextField.getText());
            plotPanel.plot("getChi2Map", args.toString(),
                    "2D " + type + " " + titleLabel + " on " + xParamName + " and " + yParamName);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        xminFormattedTextField = new javax.swing.JFormattedTextField();
        xmaxFormattedTextField = new javax.swing.JFormattedTextField();
        xSamplingFormattedTextField = new javax.swing.JFormattedTextField();
        plotChi2Button = new javax.swing.JButton();
        ySamplingFormattedTextField = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        yminFormattedTextField = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ymaxFormattedTextField = new javax.swing.JFormattedTextField();
        xComboBox = new javax.swing.JComboBox();
        yComboBox = new javax.swing.JComboBox();
        tablePanel = new javax.swing.JPanel();
        jTable1 = new fr.jmmc.jmcs.gui.component.NumericJTable();
        jPanel1 = new javax.swing.JPanel();
        helpButton1 = new javax.swing.JButton();
        jRadioButton1D = new javax.swing.JRadioButton();
        jRadioButton2D = new javax.swing.JRadioButton();
        logChi2CheckBox = new javax.swing.JCheckBox();
        reducedChi2CheckBox = new javax.swing.JCheckBox();
        runFitCheckBox = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Cuts in the chi2 space panel"));
        setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("max");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        add(jLabel3, gridBagConstraints);

        jLabel5.setText("#samples");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        add(jLabel5, gridBagConstraints);

        jLabel6.setText("min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        add(jLabel6, gridBagConstraints);

        xminFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        xminFormattedTextField.setText("-"+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xminFormattedTextField, gridBagConstraints);

        xmaxFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        xmaxFormattedTextField.setText(""+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xmaxFormattedTextField, gridBagConstraints);

        xSamplingFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xSamplingFormattedTextField.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xSamplingFormattedTextField, gridBagConstraints);

        plotChi2Button.setText("Plot Chi2");
        plotChi2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotChi2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        add(plotChi2Button, gridBagConstraints);

        ySamplingFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        ySamplingFormattedTextField.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(ySamplingFormattedTextField, gridBagConstraints);

        jLabel7.setText("#samples");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 2;
        add(jLabel7, gridBagConstraints);

        yminFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        yminFormattedTextField.setText("-"+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(yminFormattedTextField, gridBagConstraints);

        jLabel8.setText("max");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        add(jLabel8, gridBagConstraints);

        jLabel10.setText("min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        add(jLabel10, gridBagConstraints);

        ymaxFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        ymaxFormattedTextField.setText(""+startValue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(ymaxFormattedTextField, gridBagConstraints);

        xComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        add(xComboBox, gridBagConstraints);

        yComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        add(yComboBox, gridBagConstraints);

        tablePanel.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(param1TableModel);
        tablePanel.add(jTable1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(tablePanel, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(helpButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(jPanel1, gridBagConstraints);

        buttonGroup1.add(jRadioButton1D);
        jRadioButton1D.setText("1D");
        jRadioButton1D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1DActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        add(jRadioButton1D, gridBagConstraints);

        buttonGroup1.add(jRadioButton2D);
        jRadioButton2D.setSelected(true);
        jRadioButton2D.setText("2D");
        jRadioButton2D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1DActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        add(jRadioButton2D, gridBagConstraints);

        logChi2CheckBox.setSelected(true);
        logChi2CheckBox.setText("log");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(logChi2CheckBox, gridBagConstraints);

        reducedChi2CheckBox.setSelected(true);
        reducedChi2CheckBox.setText("reduced");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        add(reducedChi2CheckBox, gridBagConstraints);

        runFitCheckBox.setText("with fit");
        runFitCheckBox.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        add(runFitCheckBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void plotChi2ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_plotChi2ButtonActionPerformed
    {//GEN-HEADEREND:event_plotChi2ButtonActionPerformed
        plotChi2(logChi2CheckBox.isSelected(), reducedChi2CheckBox.isSelected());
}//GEN-LAST:event_plotChi2ButtonActionPerformed

    private void updateTable() {
        boolean hasParam = (xComboBox.getSelectedItem() != null) && (yComboBox.getSelectedItem() != null);
        boolean enabled = hasParam && settingsModel.isValid();
        plotChi2Button.setEnabled(enabled);
        if (hasParam) {
            Parameter[] parameters = new Parameter[2];
            parameters[0] = (Parameter) xComboBox.getSelectedItem();
            parameters[1] = (Parameter) yComboBox.getSelectedItem();
            param1TableModel.setModel(settingsModel, parameters, true);
        } else {
            logger.debug("No parameter to use for chi2map");
        }
        UtilsClass.initColumnSizes(jTable1, 400);
    }

    private void yComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yComboBoxActionPerformed
        /* Do not update during init method */
        if (!isIniting) {
            Parameter yParam = (Parameter) yComboBox.getSelectedItem();
            if (yParam == null) {
                return;
            }
            updateTable();
            oldYParam = yParam;
            if (yParam.hasMinValue()) {
                yminFormattedTextField.setText(Double.toString(yParam.getMinValue()));
            }
            if (yParam.hasMaxValue()) {
                ymaxFormattedTextField.setText(Double.toString(yParam.getMaxValue()));
            }
        }
}//GEN-LAST:event_yComboBoxActionPerformed

    private void xComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xComboBoxActionPerformed
        /* Do not update during init method */
        if (!isIniting) {
            Parameter xParam = (Parameter) xComboBox.getSelectedItem();

            if (xParam == null) {
                return;
            }
            updateTable();
            oldXParam = xParam;
            if (xParam.hasMinValue()) {
                xminFormattedTextField.setText(Double.toString(xParam.getMinValue()));
            }
            if (xParam.hasMaxValue()) {
                xmaxFormattedTextField.setText(Double.toString(xParam.getMaxValue()));
            }
        }
    }//GEN-LAST:event_xComboBoxActionPerformed

    private void jRadioButton1DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1DActionPerformed
        boolean flag = jRadioButton2D.isSelected();
        yComboBox.setEnabled(flag);
        ySamplingFormattedTextField.setEnabled(flag);
        ymaxFormattedTextField.setEnabled(flag);
        yminFormattedTextField.setEnabled(flag);
        jLabel10.setEnabled(flag);
        jLabel8.setEnabled(flag);
        jLabel7.setEnabled(flag);
    }//GEN-LAST:event_jRadioButton1DActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1D;
    private javax.swing.JRadioButton jRadioButton2D;
    private javax.swing.JTable jTable1;
    private javax.swing.JCheckBox logChi2CheckBox;
    private javax.swing.JButton plotChi2Button;
    private javax.swing.JCheckBox reducedChi2CheckBox;
    private javax.swing.JCheckBox runFitCheckBox;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JComboBox xComboBox;
    private javax.swing.JFormattedTextField xSamplingFormattedTextField;
    private javax.swing.JFormattedTextField xmaxFormattedTextField;
    private javax.swing.JFormattedTextField xminFormattedTextField;
    private javax.swing.JComboBox yComboBox;
    private javax.swing.JFormattedTextField ySamplingFormattedTextField;
    private javax.swing.JFormattedTextField ymaxFormattedTextField;
    private javax.swing.JFormattedTextField yminFormattedTextField;
    // End of variables declaration//GEN-END:variables

    public void update(Observable o, Object arg) {
        show(settingsModel);
    }
}
