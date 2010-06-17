package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.ShowHelpAction;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.ResultFile;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlotPanel extends javax.swing.JPanel implements ListSelectionListener {

  /** Class logger */
  private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
          "fr.jmmc.mf.gui.PlotPanel");
  /** settings model reference */
  private SettingsModel settingsModel = null;
  private PlotModelPanel plotModelPanel = null;
  private PlotChi2Panel plotChi2Panel = null;
  private SettingsViewerInterface viewer = null;
  private ListModel targets = null;
  private boolean showChi2AndModelPanels;

  /** Creates new form PlotPanel */
  public PlotPanel(SettingsViewerInterface viewer, boolean showChi2AndModelPanels) {
    this.viewer = viewer;
    this.showChi2AndModelPanels = showChi2AndModelPanels;
    settingsModel = viewer.getSettingsModel();
    initComponents();
    if (showChi2AndModelPanels) {
      plotModelPanel = new PlotModelPanel(this);
      plotChi2Panel = new PlotChi2Panel(this);
      modelPanel.add(plotModelPanel);
      chi2Panel.add(plotChi2Panel);
    }

    // Change widget for target list
    targetList = new fr.jmmc.mcs.gui.CheckBoxJList();
    jScrollPane1.setViewportView(targetList);
    targetList.addListSelectionListener(this);

    // Set online help
    jButton1.setAction(new ShowHelpAction(("END_Plots_PlotChi2_Bt")));
    jButton2.setAction(new ShowHelpAction(("END_Plots_PlotChi2_Bt")));
    jButton3.setAction(new ShowHelpAction(("END_Plots_PlotChi2_Bt")));

    // Set default value for angle
    plotRadialAngleFormattedTextField1.setValue(0);
    // Set the default values of radial plots
    // TODO filter one target selection change
    radialComboBox.setModel(new javax.swing.DefaultComboBoxModel(
            new String[]{"VIS2", "VISamp", "VISphi", "T3amp", "T3phi"}));

  }

  public void show(SettingsModel s) {
    settingsModel = s;

    if (showChi2AndModelPanels) {
      // update sub panels
      plotModelPanel.show(s);
      plotChi2Panel.show(s);
    }

    // Display all targets in the target list with at list one selection
    targets = s.getTargetListModel();
    targetList.setModel(targets);
    if (targetList.getSelectedIndex() < 0) {
      targetList.setSelectedIndex(0);
    }

    // fix state according selection
    valueChanged(null);
  }

  /** Return the syntax used by yorick code to describe a list of targets */
  private int getGroupValue(Target target) {
    return settingsModel.getTargetListModel().indexOf(target) + 1;
  }

  private String getGroupValue(Object[] targets) {
    String s = "[";
    for (Object target : targets) {
      s += getGroupValue((Target) target) + ",";
    }
    return s.substring(0, s.lastIndexOf(',')) + "]";
  }

  public void plotModelRadial(Target targetToPlot, String observableType,
          boolean residuals, boolean overplotModel, String angle) {
    plotModelRadial(new Object[]{targetToPlot}, observableType,
            residuals, overplotModel, angle);
  }

  public void plotModelRadial(Object[] targetsToPlot, String observableType,
          boolean residuals, boolean overplotModel, String angle) {
    if (residuals) {
      String args = observableType + " " + getGroupValue(targetsToPlot);
      plot("getModelResidualsPlot", args, observableType +
              " residuals of targets " + getGroupValue(targetsToPlot));
    } else {
      if (overplotModel) {
        String args = observableType + " " + getGroupValue(targetsToPlot) +
                " " + angle;
        plot("getModelRadialPlot", args, "Model " + observableType +
                " of targets " + getGroupValue(targetsToPlot) + " " + angle + "°");
      } else {
        String args = observableType + " " + getGroupValue(targetsToPlot);
        plot("getModelRadialPlot", args, "Model " + observableType +
                " of targets " + getGroupValue(targetsToPlot));
      }
    }
  }

  private void plotBaselineButton(Object[] targetsToPlot) {
    String args = getGroupValue(targetsToPlot);
    plot("getBaselinePlot", args, "Baselines of targets " +
            getGroupValue(targetsToPlot));
  }

  private void plotUVCoverage(Object[] targetsToPlot) {
    String args = getGroupValue(targetsToPlot);
    plot("getUVCoveragePlot", args, "UV coverage of targets " +
            getGroupValue(targetsToPlot));
  }

  public void plotModelSnifferMap(Target targetToPlot, String xmin, String xmax,
          String ymin, String ymax, String pixscale) {
    String args = "" + getGroupValue(targetToPlot) + " " + xmin +
            " " + xmax +
            " " + ymin +
            " " + ymax +
            " " + pixscale;
    plot("getModelSnifferMap", args, "Sniffer Map of " + targetToPlot.getIdent());
  }

  void plotModelUVMap(Target targetToPlot) {
    String args = "" + getGroupValue(targetToPlot);
    plot("getModelUVMap", args, "UV map of " + targetToPlot.getIdent());
  }

  void plotModelImage(Target targetToPlot, String xmin, String xmax,
          String ymin, String ymax, String pixscale) {
    String args = "" + getGroupValue(targetToPlot) + " " + xmin +
            " " + xmax +
            " " + ymin +
            " " + ymax +
            " " + pixscale;
    plot("getModelImage", args, "Model Image of " + targetToPlot.getIdent());
  }

  /**
   * Call plot build routine and draw the new plot.
   *
   * @param methodName the method's name.
   * @param methodArgs the method's arguments.
   * @param title the plot title.
   */
  public void plot(String methodName, String methodArgs, String title) {
    logger.fine("Requesting yoga '" + methodName + "' call");

    Response response = null;
    try {
      response = ModelFitting.execMethod(methodName,
              settingsModel.getTempFile(false), methodArgs);
      ResultFile[] resultFiles = UtilsClass.getResultFiles(response);
      if (resultFiles.length == 0) {
        logger.log(Level.WARNING, "No result data interpreted");
        return;
      }

      String b64file;
      File file = null;
      JFrame f = null;
      Vector<File> filesToExport = new Vector();
      for (int i = 0; i < resultFiles.length; i++) {
        ResultFile r = resultFiles[i];
        b64file = r.getHref();
        file = UtilsClass.saveBASE64ToFile(b64file, r.getName().substring(r.getName().indexOf(".")));
        filesToExport.add(file);
        if (r.getName().endsWith("png")) {
          f = UtilsClass.buildFrameFor(file);
        }
      }
      if (f != null) {
        FrameTreeNode ftn = new FrameTreeNode(f, title, filesToExport.toArray(new File[0]));
        settingsModel.addPlot(ftn);
      }

      StatusBar.show(methodName + " process finished");
    } catch (java.net.UnknownHostException ex) {
      String msg = "Network seems down. Can't contact host " + ex.getMessage();
      javax.swing.JOptionPane.showMessageDialog(null, msg, "Error ",
              javax.swing.JOptionPane.ERROR_MESSAGE);
      logger.log(Level.WARNING, ex.getMessage(), ex);
      StatusBar.show("Error during process of " + methodName);
      return;
    } catch (Exception ex) {
      new FeedbackReport(ex);
    }

    if (false) {
      try {
        fr.jmmc.mcs.ImageViewer v = new fr.jmmc.mcs.ImageViewer("replace by result/blah");
        v = null;
        v.setTitle(title);
        v.setSize(400, 400);

        //@todo recode next part viewer.addPlot(v, title);
      } catch (Exception ex) {
        logger.log(Level.WARNING, "Cant read with imageViewer", ex);
        String msg = "Error processing result";
        javax.swing.JOptionPane.showMessageDialog(null, msg, "Error ",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        StatusBar.show("Error during process of " + methodName);
        return;
      }
    }
  }

  private Object[] getTargetsToPlot() {
    if (useAllTargetsCheckBox.isSelected()) {
      return settingsModel.getRootSettings().getTargets().getTarget();
    }
    return targetList.getSelectedValues();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    modelPanel = new javax.swing.JPanel();
    chi2Panel = new javax.swing.JPanel();
    commonPanel = new javax.swing.JPanel();
    plotBaselineButton = new javax.swing.JButton();
    plotRadialButton = new javax.swing.JButton();
    plotUvCoverageButton = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    targetList = new javax.swing.JList();
    useAllTargetsCheckBox = new javax.swing.JCheckBox();
    radialComboBox = new javax.swing.JComboBox();
    plotRadialAngleFormattedTextField1 = new javax.swing.JFormattedTextField();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    jButton3 = new javax.swing.JButton();
    addModelCheckBox = new javax.swing.JCheckBox();
    residualsCheckBox = new javax.swing.JCheckBox();
    blankPanel = new javax.swing.JPanel();

    setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    setLayout(new java.awt.GridBagLayout());

    modelPanel.setLayout(new javax.swing.BoxLayout(modelPanel, javax.swing.BoxLayout.LINE_AXIS));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    add(modelPanel, gridBagConstraints);

    chi2Panel.setLayout(new javax.swing.BoxLayout(chi2Panel, javax.swing.BoxLayout.LINE_AXIS));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    add(chi2Panel, gridBagConstraints);

    commonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Common plots panel"));
    commonPanel.setLayout(new java.awt.GridBagLayout());

    plotBaselineButton.setText("Plot baselines");
    plotBaselineButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        plotBaselineButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    commonPanel.add(plotBaselineButton, gridBagConstraints);

    plotRadialButton.setText("Plot Radial");
    plotRadialButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        plotRadialButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    commonPanel.add(plotRadialButton, gridBagConstraints);

    plotUvCoverageButton.setText("Plot UV-Coverage");
    plotUvCoverageButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        plotUvCoverageButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    commonPanel.add(plotUvCoverageButton, gridBagConstraints);

    jScrollPane1.setPreferredSize(new java.awt.Dimension(48, 80));

    targetList.setModel(new javax.swing.AbstractListModel() {
      String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
      public int getSize() { return strings.length; }
      public Object getElementAt(int i) { return strings[i]; }
    });
    targetList.setMaximumSize(new java.awt.Dimension(45, 150));
    targetList.setMinimumSize(new java.awt.Dimension(45, 50));
    targetList.setPreferredSize(new java.awt.Dimension(45, 50));
    jScrollPane1.setViewportView(targetList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    commonPanel.add(jScrollPane1, gridBagConstraints);

    useAllTargetsCheckBox.setText("Use all targets");
    useAllTargetsCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        useAllTargetsCheckBoxActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 5;
    commonPanel.add(useAllTargetsCheckBox, gridBagConstraints);

    radialComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "VIS2" }));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    commonPanel.add(radialComboBox, gridBagConstraints);

    plotRadialAngleFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
    plotRadialAngleFormattedTextField1.setText("0");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 5;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    commonPanel.add(plotRadialAngleFormattedTextField1, gridBagConstraints);

    jButton1.setText("jButton1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    commonPanel.add(jButton1, gridBagConstraints);

    jButton2.setText("jButton2");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    commonPanel.add(jButton2, gridBagConstraints);

    jButton3.setText("jButton3");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    commonPanel.add(jButton3, gridBagConstraints);

    addModelCheckBox.setSelected(true);
    addModelCheckBox.setText("Add model with cut angle");
    addModelCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addModelCheckBoxActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 4;
    commonPanel.add(addModelCheckBox, gridBagConstraints);

    residualsCheckBox.setText("Residuals");
    residualsCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        residualsCheckBoxActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    commonPanel.add(residualsCheckBox, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    add(commonPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(blankPanel, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents

    private void residualsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_residualsCheckBoxActionPerformed
      boolean flag = residualsCheckBox.isSelected();
      addModelCheckBox.setEnabled(!flag);
      plotRadialAngleFormattedTextField1.setEnabled(!flag);
    }//GEN-LAST:event_residualsCheckBoxActionPerformed

    private void addModelCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModelCheckBoxActionPerformed
      boolean flag = addModelCheckBox.isSelected();
      plotRadialAngleFormattedTextField1.setEnabled(flag);
    }//GEN-LAST:event_addModelCheckBoxActionPerformed

    private void plotBaselineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotBaselineButtonActionPerformed
      plotBaselineButton(getTargetsToPlot());
    }//GEN-LAST:event_plotBaselineButtonActionPerformed

    private void plotUvCoverageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotUvCoverageButtonActionPerformed
      plotUVCoverage(getTargetsToPlot());
    }//GEN-LAST:event_plotUvCoverageButtonActionPerformed

    private void plotRadialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotRadialButtonActionPerformed
      String observableType = radialComboBox.getSelectedItem().toString();

      plotModelRadial(getTargetsToPlot(),
              observableType, residualsCheckBox.isSelected(),
              addModelCheckBox.isSelected(),
              plotRadialAngleFormattedTextField1.getText());
    }//GEN-LAST:event_plotRadialButtonActionPerformed

    private void useAllTargetsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllTargetsCheckBoxActionPerformed
      // simulate a list selection change to update button states
      valueChanged(null);
    }//GEN-LAST:event_useAllTargetsCheckBoxActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox addModelCheckBox;
  private javax.swing.JPanel blankPanel;
  private javax.swing.JPanel chi2Panel;
  private javax.swing.JPanel commonPanel;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JButton jButton3;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPanel modelPanel;
  private javax.swing.JButton plotBaselineButton;
  private javax.swing.JFormattedTextField plotRadialAngleFormattedTextField1;
  private javax.swing.JButton plotRadialButton;
  private javax.swing.JButton plotUvCoverageButton;
  private javax.swing.JComboBox radialComboBox;
  private javax.swing.JCheckBox residualsCheckBox;
  private javax.swing.JList targetList;
  private javax.swing.JCheckBox useAllTargetsCheckBox;
  // End of variables declaration//GEN-END:variables

  public void valueChanged(ListSelectionEvent e) {
    boolean someSelection = targetList.getSelectedIndex() >= 0;
    boolean oneOrMoreTarget = targetList.getModel().getSize() > 0;
    boolean flag = someSelection ||
            (oneOrMoreTarget && useAllTargetsCheckBox.isSelected());
    plotBaselineButton.setEnabled(flag);
    plotRadialButton.setEnabled(flag);
    plotUvCoverageButton.setEnabled(flag);
  }
}
