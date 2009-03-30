package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ShowHelpAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Target;

/**
 *
 * @author mella
 */
public class PlotModelPanel extends javax.swing.JPanel
{

    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.PlotModelPanel");
    public SettingsModel settingsModel = null;
    private PlotPanel plotPanel;

    /** Creates new form PlotPanel */
    public PlotModelPanel(PlotPanel plotPanel)
    {
        this.plotPanel=plotPanel;
        initComponents();
        // build help button
        helpButton1.setAction(new ShowHelpAction(("BEG_PlotSniffer_Bt")));
        helpButton2.setAction(new ShowHelpAction(("BEG_Plots_PlotUVmap_Bt")));
        helpButton3.setAction(new ShowHelpAction(("END_Plots_PlotSniffer_Bt")));
    }

    public void show(SettingsModel s, Target t)
    {
        targetComboBox.setVisible(false);
        targetLabel.setVisible(false);
        show(s);
        if (t != null)
        {
            targetComboBox.setSelectedItem(t);
        }
    }

    public void show(SettingsModel s)
    {
        settingsModel = s;
        Object[] targets = s.getTargetListModel().toArray();
        targetComboBox.removeAllItems();
        for (int i = 0; i < targets.length; i++) {
            Object object = targets[i];
            targetComboBox.addItem(object);
        }
        boolean hasOneTarget = targetComboBox.getItemCount()!=0;
        plotImageButton.setEnabled(hasOneTarget);
        plotSnifferMapButton.setEnabled(hasOneTarget);
        plotUVMapButton.setEnabled(hasOneTarget);
        plotRadialButton.setEnabled(hasOneTarget);
    }
  
    private void plotModelUVMap(Target targetToPlot)
    {
        int groupValue = settingsModel.getTargetListModel().indexOf(targetToPlot) +
                1;
        String args = "" + groupValue;
        plotPanel.plot("getModelUVMap", args, "UV map of "+targetToPlot.getIdent());
    }

    private void plotModelRadial(Target targetToPlot)
    {
        int groupValue = settingsModel.getTargetListModel().indexOf(targetToPlot) +
                1;
        String args = radialComboBox.getSelectedItem()+" " + groupValue;
        plotPanel.plot("getModelRadialPlot", args, "Model " + radialComboBox.getSelectedItem()+" of "+targetToPlot.getIdent());
    }

    private void plotModelImage(Target targetToPlot)
    {
        int groupValue = settingsModel.getTargetListModel().indexOf(targetToPlot) +
                1;
        String args = "" + groupValue + " " + xminFormattedTextField.getText() + " " + xmaxFormattedTextField.getText() + " " + yminFormattedTextField.getText()  + " " + ymaxFormattedTextField.getText() + " " + pixscaleFormattedTextField.getText();
        plotPanel.plot("getModelImage", args, "Model Image of "+targetToPlot.getIdent());
    }

    private void plotModelSnifferMap(Target targetToPlot)
    {
        int groupValue = settingsModel.getTargetListModel().indexOf(targetToPlot) +
                1;
        String args = "" + groupValue + " " + xminFormattedTextField1.getText() + " " + xmaxFormattedTextField1.getText() + " " + yminFormattedTextField1.getText()  + " " + ymaxFormattedTextField1.getText() + " " + pixscaleFormattedTextField1.getText();
        plotPanel.plot("getModelSnifferMap", args, "Sniffer Map of "+targetToPlot.getIdent());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        plotImageButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        targetLabel = new javax.swing.JLabel();
        targetComboBox = new javax.swing.JComboBox();
        xminFormattedTextField = new javax.swing.JFormattedTextField();
        yminFormattedTextField = new javax.swing.JFormattedTextField();
        xmaxFormattedTextField = new javax.swing.JFormattedTextField();
        ymaxFormattedTextField = new javax.swing.JFormattedTextField();
        pixscaleFormattedTextField = new javax.swing.JFormattedTextField();
        plotUVMapButton = new javax.swing.JButton();
        plotSnifferMapButton = new javax.swing.JButton();
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
        helpButton1 = new javax.swing.JButton();
        helpButton2 = new javax.swing.JButton();
        helpButton3 = new javax.swing.JButton();
        plotRadialButton = new javax.swing.JButton();
        radialComboBox = new javax.swing.JComboBox();

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(plotImageButton, gridBagConstraints);

        jLabel2.setText("ymin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("xmax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        add(jLabel3, gridBagConstraints);

        jLabel4.setText("ymax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        add(jLabel4, gridBagConstraints);

        jLabel5.setText("pixscale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        add(jLabel5, gridBagConstraints);

        jLabel6.setText("xmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        add(jLabel6, gridBagConstraints);

        targetLabel.setText("Target:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        add(targetLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(targetComboBox, gridBagConstraints);

        xminFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xminFormattedTextField.setText("-50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xminFormattedTextField, gridBagConstraints);

        yminFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        yminFormattedTextField.setText("-50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(yminFormattedTextField, gridBagConstraints);

        xmaxFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xmaxFormattedTextField.setText("50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xmaxFormattedTextField, gridBagConstraints);

        ymaxFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        ymaxFormattedTextField.setText("50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(ymaxFormattedTextField, gridBagConstraints);

        pixscaleFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        pixscaleFormattedTextField.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pixscaleFormattedTextField, gridBagConstraints);

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

        jLabel7.setText("xmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        add(jLabel7, gridBagConstraints);

        xminFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xminFormattedTextField1.setText("-50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xminFormattedTextField1, gridBagConstraints);

        jLabel8.setText("ymin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        add(jLabel8, gridBagConstraints);

        yminFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        yminFormattedTextField1.setText("-50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(yminFormattedTextField1, gridBagConstraints);

        jLabel9.setText("xmax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        add(jLabel9, gridBagConstraints);

        xmaxFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        xmaxFormattedTextField1.setText("50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(xmaxFormattedTextField1, gridBagConstraints);

        jLabel10.setText("ymax");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;
        add(jLabel10, gridBagConstraints);

        ymaxFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        ymaxFormattedTextField1.setText("50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(ymaxFormattedTextField1, gridBagConstraints);

        jLabel11.setText("pixscale");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        add(jLabel11, gridBagConstraints);

        pixscaleFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        pixscaleFormattedTextField1.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pixscaleFormattedTextField1, gridBagConstraints);

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(helpButton1, gridBagConstraints);

        helpButton2.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(helpButton2, gridBagConstraints);

        helpButton3.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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

        radialComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "VIS2", "VISamp", "VISphi" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(radialComboBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void plotImageButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_plotImageButtonActionPerformed
    {//GEN-HEADEREND:event_plotImageButtonActionPerformed
        plotModelImage((Target) targetComboBox.getSelectedItem());
    }//GEN-LAST:event_plotImageButtonActionPerformed

    private void plotUVMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotUVMapButtonActionPerformed
        plotModelUVMap((Target) targetComboBox.getSelectedItem());
}//GEN-LAST:event_plotUVMapButtonActionPerformed

    private void plotSnifferMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotSnifferMapButtonActionPerformed
        plotModelSnifferMap((Target) targetComboBox.getSelectedItem());
}//GEN-LAST:event_plotSnifferMapButtonActionPerformed

    private void plotRadialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotRadialButtonActionPerformed
        plotModelRadial((Target) targetComboBox.getSelectedItem());
}//GEN-LAST:event_plotRadialButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton helpButton1;
    private javax.swing.JButton helpButton2;
    private javax.swing.JButton helpButton3;
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
    private javax.swing.JFormattedTextField pixscaleFormattedTextField;
    private javax.swing.JFormattedTextField pixscaleFormattedTextField1;
    private javax.swing.JButton plotImageButton;
    private javax.swing.JButton plotRadialButton;
    private javax.swing.JButton plotSnifferMapButton;
    private javax.swing.JButton plotUVMapButton;
    private javax.swing.JComboBox radialComboBox;
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
