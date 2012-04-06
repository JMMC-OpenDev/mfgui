/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import com.jidesoft.swing.CheckBoxList;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.mf.ModelFitting;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Residual;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.ResultFile;
import fr.jmmc.oitools.model.OIFitsFile;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PlotPanel extends javax.swing.JPanel implements ListSelectionListener {

    final static String className = PlotPanel.class.getName();
    /** Class logger */
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    /** settings model reference */
    private SettingsModel settingsModel = null;
    private PlotModelPanel plotModelPanel = null;
    private PlotChi2Panel plotChi2Panel = null;
    private SettingsViewerInterface viewer = null;
    private ListModel targets = null;
    private boolean showChi2AndModelPanels;
    private String lastObservable = null;
    private CheckBoxList targetList = null;

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

        // Use jide CheckboxList widget for target list
        targetList = new CheckBoxList();
        targetListScrollPane.setViewportView(targetList);
        //targetList.addListSelectionListener(this);
        targetList.getCheckBoxListSelectionModel().addListSelectionListener(this);


        // Set online help
        jButton3.setAction(new ShowHelpAction(("ENDtt_CommonPlots_Bt")));

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
    private String getGroupValue(Target target) {
        return Integer.toString(settingsModel.getTargetListModel().indexOf(target) + 1);
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
            plot("getModelResidualsPlot", args, observableType
                    + " weighted residuals of targets " + getGroupValue(targetsToPlot));
        } else {
            if (overplotModel) {
                String args = observableType + " " + getGroupValue(targetsToPlot)
                        + " " + angle;
                plot("getModelRadialPlot", args, "Model " + observableType
                        + " of targets " + getGroupValue(targetsToPlot) + " " + angle + "°");
            } else {
                String args = observableType + " " + getGroupValue(targetsToPlot);
                plot("getModelRadialPlot", args, "Model " + observableType
                        + " of targets " + getGroupValue(targetsToPlot));
            }
        }
    }

    private void plotBaselinesButton(Object[] targetsToPlot) {
        String args = getGroupValue(targetsToPlot);
        plot("getBaselinesPlot", args, "Baselines of targets "
                + getGroupValue(targetsToPlot));
    }

    private void plotUVCoverage(Object[] targetsToPlot) {
        String args = getGroupValue(targetsToPlot);
        plot("getUVCoveragePlot", args, "UV coverage of targets "
                + getGroupValue(targetsToPlot));
    }

    public void plotModelSnifferMap(Target targetToPlot, String xmin, String xmax,
            String ymin, String ymax, String pixscale) {
        String args = getGroupValue(targetToPlot) + " " + xmin
                + " " + xmax
                + " " + ymin
                + " " + ymax
                + " " + pixscale;
        plot("getModelSnifferMap", args, "Sniffer Map of " + targetToPlot.getIdent());
    }

    void plotModelUVMap(Target targetToPlot) {
        String args = getGroupValue(targetToPlot);
        plot("getModelUVMap", args, "UV map of " + targetToPlot.getIdent());
    }

    void plotModelImage(Target targetToPlot, String xmin, String xmax,
            String ymin, String ymax, String pixscale) {
        String args = getGroupValue(targetToPlot) + " " + xmin
                + " " + xmax
                + " " + ymin
                + " " + ymax
                + " " + pixscale;
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
        } catch (ExecutionException ex) {
            MessagePane.showErrorMessage("Can't plot data", ex);
            return;
        }
        ResultFile[] resultFiles = UtilsClass.getResultFiles(response);
        if (resultFiles.length == 0) {
            throw new IllegalStateException("No data returned (this problem is probably data related)");
        }

        String b64file;
        File file = null;
        JFrame f = null;
        ArrayList<File> filesToExport = new ArrayList();
        ArrayList<String> filenamesToExport = new ArrayList();
        ArrayList<File> filesToDisplay = new ArrayList<File>();
        ArrayList<String> filenamesToDisplay = new ArrayList<String>();
        
        for (int i = 0; i < resultFiles.length; i++) {
            ResultFile r = resultFiles[i];
            b64file = r.getHref();
            file = UtilsClass.saveBASE64ToFile(b64file, r.getName().substring(r.getName().indexOf(".")));
            filesToExport.add(file);
            filenamesToExport.add(r.getName());
            if (r.getName().endsWith("png")) {
                filesToDisplay.add(file);
                if(r.getDescription()!=null && r.getDescription().length()>0){
                    filenamesToDisplay.add(r.getDescription());
                }else{
                    filenamesToDisplay.add(r.getName());
                }
            }
        }
        f = UtilsClass.buildFrameFor(filesToDisplay.toArray(new File[0]),filenamesToDisplay.toArray(new String[0]));
        
        if (f != null) {
            FrameTreeNode ftn = new FrameTreeNode(f, title, filesToExport.toArray(new File[0]), filenamesToExport.toArray(new String[0]));
            settingsModel.addPlot(ftn);
        }

        StatusBar.show(methodName + " process finished");
        /**
        if (false) {
        fr.jmmc.mcs.ImageViewer v = new fr.jmmc.mcs.ImageViewer("replace by result/blah");
        v = null;
        v.setTitle(title);
        v.setSize(400, 400);
        }
         */
    }

    /**
     * Return the list of valid targets to plot
     * @return one array with valid targets
     */
    private Object[] getTargetsToPlot() {
        Object[] inputTargets;
        
        
        // Display all targets in the target list with at list one selection
        targets = settingsModel.getTargetListModel();
        if (targets.getSize()!= targetList.getModel().getSize()){
            targetList.setModel(targets);
            logger.severe("Trying to work on a non consistent model!!");
        }
        
        
        if (useAllTargetsCheckBox.isSelected()) {
            inputTargets = settingsModel.getRootSettings().getTargets().getTarget();
        } else {
            inputTargets = targetList.getCheckBoxListSelectedValues();
        }

        ArrayList<Target> l = new ArrayList();
        for (int i = 0; i < inputTargets.length; i++) {
            Target t = (Target) inputTargets[i];
            if (t.isValid()) {
                l.add(t);
            }
        }

        return l.toArray();
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
        plotBaselinesButton = new javax.swing.JButton();
        plotRadialButton = new javax.swing.JButton();
        plotUvCoverageButton = new javax.swing.JButton();
        useAllTargetsCheckBox = new javax.swing.JCheckBox();
        radialComboBox = new javax.swing.JComboBox();
        plotRadialAngleFormattedTextField1 = new javax.swing.JFormattedTextField();
        jButton3 = new javax.swing.JButton();
        addModelCheckBox = new javax.swing.JCheckBox();
        residualsCheckBox = new javax.swing.JCheckBox();
        targetListScrollPane = new javax.swing.JScrollPane();
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(chi2Panel, gridBagConstraints);

        commonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Common plots panel"));
        commonPanel.setLayout(new java.awt.GridBagLayout());

        plotBaselinesButton.setText("Plot baselines");
        plotBaselinesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plotBaselinesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        commonPanel.add(plotBaselinesButton, gridBagConstraints);

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
        radialComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radialComboBoxActionPerformed(evt);
            }
        });
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

        jButton3.setText("jButton3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        commonPanel.add(jButton3, gridBagConstraints);

        addModelCheckBox.setSelected(true);
        addModelCheckBox.setText("Overplot model with cut angle");
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        commonPanel.add(targetListScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
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
        updateAvailableObservables();
    }//GEN-LAST:event_residualsCheckBoxActionPerformed

    private void addModelCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModelCheckBoxActionPerformed
        boolean flag = addModelCheckBox.isSelected();
        plotRadialAngleFormattedTextField1.setEnabled(flag);
    }//GEN-LAST:event_addModelCheckBoxActionPerformed

    private void plotBaselinesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotBaselinesButtonActionPerformed
        plotBaselinesButton(getTargetsToPlot());
    }//GEN-LAST:event_plotBaselinesButtonActionPerformed

    private void plotUvCoverageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotUvCoverageButtonActionPerformed
        plotUVCoverage(getTargetsToPlot());
    }//GEN-LAST:event_plotUvCoverageButtonActionPerformed

    private void plotRadialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotRadialButtonActionPerformed
        String observableType = radialComboBox.getSelectedItem().toString();

        plotModelRadial(getTargetsToPlot(),
                observableType, residualsCheckBox.isSelected(),
                addModelCheckBox.isSelected() && addModelCheckBox.isEnabled(),
                plotRadialAngleFormattedTextField1.getText());
    }//GEN-LAST:event_plotRadialButtonActionPerformed

    private void useAllTargetsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAllTargetsCheckBoxActionPerformed
        targetList.setEnabled(!useAllTargetsCheckBox.isSelected());
        updateAvailableObservables();
    }//GEN-LAST:event_useAllTargetsCheckBoxActionPerformed

    private void radialComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radialComboBoxActionPerformed
        if (radialComboBox.getSelectedIndex() >= 0 && radialComboBox.isEnabled()) {
            lastObservable = radialComboBox.getSelectedItem().toString();
            updateAvailableObservables();
        }
    }//GEN-LAST:event_radialComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addModelCheckBox;
    private javax.swing.JPanel blankPanel;
    private javax.swing.JPanel chi2Panel;
    private javax.swing.JPanel commonPanel;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JButton plotBaselinesButton;
    private javax.swing.JFormattedTextField plotRadialAngleFormattedTextField1;
    private javax.swing.JButton plotRadialButton;
    private javax.swing.JButton plotUvCoverageButton;
    private javax.swing.JComboBox radialComboBox;
    private javax.swing.JCheckBox residualsCheckBox;
    private javax.swing.JScrollPane targetListScrollPane;
    private javax.swing.JCheckBox useAllTargetsCheckBox;
    // End of variables declaration//GEN-END:variables

    // Thrown by a targetList change
    public void valueChanged(ListSelectionEvent e) {
        updateAvailableObservables();        
    }

    // todo: remove duplicated code with same method shared between PlotPanel and PlotModelPanel
    private void updateAvailableObservables() {
        
        // Perform update only if widgets are visible
        if (!isVisible())
        {
            return;
        }
        
        // disable widget to flag automatic action into radialComboBoxActionPerformed
        radialComboBox.setEnabled(false);
        // Check if any target is selected
        Object[] targetsToPlot = getTargetsToPlot();
        boolean somethingToPlot = targetsToPlot.length >= 1;
        plotBaselinesButton.setEnabled(somethingToPlot);
        plotRadialButton.setEnabled(somethingToPlot);
        plotUvCoverageButton.setEnabled(somethingToPlot);

        // check that there is at least one target to plot or return
        radialComboBox.removeAllItems();
        Object[] selectedTargets = getTargetsToPlot();
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
}
