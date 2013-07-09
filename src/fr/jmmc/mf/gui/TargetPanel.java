/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import com.jidesoft.swing.CheckBoxList;
import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.jmcs.service.BrowserLauncher;
import fr.jmmc.mf.ModelUtils;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.*;
import fr.jmmc.oitools.model.OIFitsFile;
import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Display GUI for Target elements */
public class TargetPanel extends javax.swing.JPanel implements ListSelectionListener {

    final static String className = TargetPanel.class.getName();
    /** Class logger */
    final static Logger logger = LoggerFactory.getLogger(className);
    /** Main model object of this GUI controller */
    Target current = null;
    ListModel targetFiles;
    boolean listenToFileSelection;
    DefaultListModel models = new DefaultListModel();
    private CheckBoxList fileList = null;
    SettingsViewerInterface settingsViewer;
    public Settings rootSettings = null;
    public SettingsModel rootSettingsModel = null;
    private PlotModelPanel plotModelImagePanel;
    private PlotChi2Panel plotChi2Panel;
    private ParametersTableModel parametersTableModel;
    private Vector<MouseListener> mouseListeners = new Vector();
    private Hashtable<String, JCheckBox> moduleNameToCheckBox = new Hashtable();

    /** Creates new form TargetPanel */
    public TargetPanel(SettingsViewerInterface viewer, PlotPanel plotPanel) {
        settingsViewer = viewer;
        parametersTableModel = new ParametersTableModel();
        initComponents();
        postInit(plotPanel);
    }
    
    private void postInit(PlotPanel plotPanel){
        listenToFileSelection = false;

        modelList.setModel(models);

        plotModelImagePanel = new PlotModelPanel(plotPanel);
        subplotPanel.add(plotModelImagePanel);

        plotChi2Panel = new PlotChi2Panel(plotPanel);
        subplotPanel.add(plotChi2Panel);

        // link residual module name with its widget
        String[] moduleNames = new String[]{"VISamp", "VISphi", "VIS2", "T3amp", "T3phi"};
        JCheckBox[] modulesCheckBoxes = new JCheckBox[]{visAmpCheckBox,
            visPhiCheckBox, vis2CheckBox, t3ampCheckBox, t3phiCheckBox};
        for (int i = 0; i < moduleNames.length; i++) {
            moduleNameToCheckBox.put(moduleNames[i], modulesCheckBoxes[i]);
        }

        // build help button
        addModelHelpButton.setAction(new ShowHelpAction(("BEG_AddModel_Bt")));
        fitterSetupHelpButton.setAction(new ShowHelpAction(("END_FitterSetup_TargetPanel")));
        availableModelList.addListSelectionListener(this);                        
    }

    /**
     * Display graphical view of given target.
     *
     * @param t the target to show
     * @param settingsModel its associated settingsModel
     */
    public void show(Target t, SettingsModel settingsModel) {
        current = t;
        this.rootSettingsModel = settingsModel;
        this.rootSettings = settingsModel.getRootSettings();

        parametersTableModel.setModel(rootSettingsModel, current, true);
        if (!mouseListeners.contains(parametersTableModel)) {
            parametersTable.addMouseListener(parametersTableModel);
            mouseListeners.add(parametersTableModel);
        }

        jPanel5.add(parametersTable.getTableHeader(), BorderLayout.NORTH);

        //// Select current ident
        identComboBox.setSelectedItem(t.getIdent());

        listenToFileSelection = false;
        // a new empty chekbox list is created each time, because the selection can't be reset
        // TODO fix this very nasty leaking code
        fileList = new CheckBoxList();
        fileListScrollPane.setViewportView(fileList);
        fileList.getCheckBoxListSelectionModel().addListSelectionListener(this);
        
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked(evt);
            }
        });
      
        targetFiles = settingsViewer.getSettingsModel().getFileListModelForOiTarget(t.getIdent());

        fileList.setModel(targetFiles);
        
        if (targetFiles != null) {
            fileList.clearSelection();
            // define selected files reading fileLinks of current target            
            for (int i = 0; i < targetFiles.getSize(); i++) {
                Object file = targetFiles.getElementAt(i);
                FileLink[] links = current.getFileLink();
                for (int j = 0; j < links.length; j++) {
                    FileLink fileLink = links[j];
                    if (fileLink.getFileRef() == file) {
                        fileList.addCheckBoxListSelectedIndex(i);                        
                    }
                }
            }
        } else {
            // should not append except if user delete some files??
            logger.warn("Can't find list of files");
        }
        listenToFileSelection = true;

        availableModelList.setModel(rootSettingsModel.getSupportedModels());
        
        updateModels();

        fixFitterSetup();

        plotModelImagePanel.show(settingsModel, current);
        plotChi2Panel.show(settingsModel);                
    }

    private void updateModels() {
        // Set model list
        models.clear();
        for (int i = 0; i < current.getModelCount(); i++) {
            models.addElement(current.getModel(i));
        }
        parametersTableModel.setModel(rootSettingsModel, current, true);
        UtilsClass.initColumnSizes(parametersTable, 330);
    }
    
    private void updateModels(Model modelToSelect) {
        updateModels();
        modelList.setSelectedValue(modelToSelect, true);
    }

    /* receive event on file list */
    public void valueChanged(ListSelectionEvent e) {
        
        if ( e.getValueIsAdjusting()) {
            return;
        }
        if(listenToFileSelection && e.getSource()==fileList){
            fileListValueChanged(e);
        }
        //TODO add support for model selection
        if(e.getSource()==availableModelList){
            boolean hasSelection = availableModelList.getSelectedIndex()>=0;
            addModelButton.setEnabled(hasSelection);
        }
        
    }
    
    public void fileListValueChanged(ListSelectionEvent e) {
        // We will try to detect differences between selection and target filelinks
        // for all possible file
        for (int i = 0; i < targetFiles.getSize(); i++) {
            Object file = targetFiles.getElementAt(i);
            boolean isInTarget = false;
            FileLink[] targetLinks = current.getFileLink();
            for (int j = 0; j < targetLinks.length; j++) {
                FileLink targetLink = targetLinks[j];
                if (targetLink.getFileRef() == file) {
                    isInTarget = true;
                }
            }

            boolean isInSelection = false;
            Object[] selectedOnes = fileList.getCheckBoxListSelectedValues();
            for (int j = 0; j < selectedOnes.length; j++) {
                Object selectedOne = selectedOnes[j];
                if (selectedOne == file) {
                    isInSelection = true;
                }
            }

            if (isInSelection && (!isInTarget)) {
                rootSettingsModel.addFile(current, (File) file);
            }

            if ((!isInSelection) && isInTarget) {
                rootSettingsModel.removeFile(current, (File) file);
            }

        }

        fixFitterSetup();
    }

    private void fixFitterSetup() {
        /* disable and unselect every fitter entries
         * and enable them if one or more files have relevant data
         */
        boolean initValue = false;
        visAmpCheckBox.setSelected(initValue);
        visPhiCheckBox.setSelected(initValue);
        vis2CheckBox.setSelected(initValue);
        t3ampCheckBox.setSelected(initValue);
        t3phiCheckBox.setSelected(initValue);
        vis2CheckBox.setEnabled(false);
        visAmpCheckBox.setEnabled(false);
        visPhiCheckBox.setEnabled(false);
        t3ampCheckBox.setEnabled(false);
        t3phiCheckBox.setEnabled(false);

        Residuals residuals = current.getResiduals();

        Object[] selectedFiles = fileList.getCheckBoxListSelectedValues();
        for (int j = 0; j
                < selectedFiles.length; j++) {
            File selectedFile = (File) selectedFiles[j];
            OIFitsFile oifile = SettingsModel.getOIFitsFromFile(selectedFile);
            if (oifile.hasOiVis2()) {
                vis2CheckBox.setEnabled(true);
                if (residuals == null) {
                    vis2CheckBox.setSelected(true);
                }
            }

            if (oifile.hasOiVis()) {
                visAmpCheckBox.setEnabled(true);
                visPhiCheckBox.setEnabled(true);
                if (residuals == null) {
                    visAmpCheckBox.setSelected(true);
                    visPhiCheckBox.setSelected(true);
                }
            }

            if (oifile.hasOiT3()) {
                t3ampCheckBox.setEnabled(true);
                t3phiCheckBox.setEnabled(true);
                if (residuals == null) {
                    t3ampCheckBox.setSelected(true);
                    t3phiCheckBox.setSelected(true);
                }
            }
        }

        // Set normalizeCheckBox
        normalizeCheckBox.setSelected(current.getNormalize());

        // and search their values if residuals exist
        if (residuals != null) {
            for (String key : moduleNameToCheckBox.keySet()) {
                JCheckBox cb = moduleNameToCheckBox.get(key);
                Residual[] res = residuals.getResidual();
                for (int i = 0; i < res.length; i++) {
                    Residual residual = res[i];
                    if (residual.getName().equals(key)) {
                        if (cb.isEnabled()) {
                            cb.setSelected(true);
                        } else {
                            // the residual must be updated according available files's observables
                            residuals.removeResidual(residual);
                        }
                    }
                }
            }
        }
        // updateResidual content into model
        targetFitterParamActionPerformed(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel5 = new javax.swing.JPanel();
        parametersTable = new fr.jmmc.jmcs.gui.component.NumericJTable();
        jLabel1 = new javax.swing.JLabel();
        identComboBox = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        normalizeCheckBox = new javax.swing.JCheckBox();
        t3phiCheckBox = new javax.swing.JCheckBox();
        t3ampCheckBox = new javax.swing.JCheckBox();
        visPhiCheckBox = new javax.swing.JCheckBox();
        vis2CheckBox = new javax.swing.JCheckBox();
        visAmpCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        fitterSetupHelpButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        modelList = new javax.swing.JList();
        addModelButton = new javax.swing.JButton();
        removeModelButton = new javax.swing.JButton();
        addModelHelpButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        availableModelList = new javax.swing.JList();
        addMyModelButton = new javax.swing.JButton();
        polarCheckBox = new javax.swing.JCheckBox();
        stretchedCheckBox = new javax.swing.JCheckBox();
        visitUmRepositoryButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        fileListScrollPane = new javax.swing.JScrollPane();
        subplotPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Target panel"));
        setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        jPanel5.setLayout(new java.awt.BorderLayout());

        parametersTable.setModel(parametersTableModel);
        jPanel5.add(parametersTable, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel5, gridBagConstraints);

        jLabel1.setText("Ident:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel1, gridBagConstraints);

        identComboBox.setModel(settingsViewer.getSettingsModel().oiTargets);
        identComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(identComboBox, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Fitter setup"));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        normalizeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetFitterParamActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel6.add(normalizeCheckBox, gridBagConstraints);

        t3phiCheckBox.setText("T3phi");
        t3phiCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetFitterParamActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(t3phiCheckBox, gridBagConstraints);

        t3ampCheckBox.setText("T3amp");
        t3ampCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetFitterParamActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(t3ampCheckBox, gridBagConstraints);

        visPhiCheckBox.setText("VISphi");
        visPhiCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetFitterParamActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(visPhiCheckBox, gridBagConstraints);

        vis2CheckBox.setText("VIS2");
        vis2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetFitterParamActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(vis2CheckBox, gridBagConstraints);

        visAmpCheckBox.setText("VISamp");
        visAmpCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetFitterParamActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(visAmpCheckBox, gridBagConstraints);

        jLabel2.setText("Select data to fit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        jPanel6.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Normalize total flux");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel6.add(jLabel3, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        fitterSetupHelpButton.setText("jButton2");
        fitterSetupHelpButton.setAlignmentX(1.0F);
        fitterSetupHelpButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fitterSetupHelpButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(fitterSetupHelpButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel6, gridBagConstraints);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Model list"));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(22, 82));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(260, 82));

        modelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                modelListMouseClicked(evt);
            }
        });
        modelList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                modelListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(modelList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane2, gridBagConstraints);

        addModelButton.setText("+");
        addModelButton.setEnabled(false);
        addModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        jPanel4.add(addModelButton, gridBagConstraints);

        removeModelButton.setText("-");
        removeModelButton.setEnabled(false);
        removeModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel4.add(removeModelButton, gridBagConstraints);

        addModelHelpButton.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(addModelHelpButton, gridBagConstraints);

        availableModelList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        availableModelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                availableModelListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(availableModelList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jScrollPane1, gridBagConstraints);

        addMyModelButton.setText("Create user model...");
        addMyModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMyModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        jPanel4.add(addMyModelButton, gridBagConstraints);

        polarCheckBox.setText("Polar");
        polarCheckBox.setEnabled(false);
        polarCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                polarCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        jPanel4.add(polarCheckBox, gridBagConstraints);

        stretchedCheckBox.setText("Stretched");
        stretchedCheckBox.setEnabled(false);
        stretchedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stretchedCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        jPanel4.add(stretchedCheckBox, gridBagConstraints);

        visitUmRepositoryButton.setText("Visit web repos...");
        visitUmRepositoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitUmRepositoryButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(visitUmRepositoryButton, gridBagConstraints);

        jPanel7.add(jPanel4);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected file list"));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanel2.add(fileListScrollPane);

        jPanel7.add(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel7, gridBagConstraints);

        subplotPanel.setLayout(new javax.swing.BoxLayout(subplotPanel, javax.swing.BoxLayout.Y_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(subplotPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void modelListMouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_modelListMouseClicked
        if (evt.getClickCount() == 2)
        {
            rootSettingsModel.setSelectionPath(
                    new TreePath(new Object[]{
                rootSettingsModel,
                rootSettingsModel.getRootSettings().getTargets(),
                current,
                modelList.getSelectedValue() } ));
        }
    }//GEN-LAST:event_modelListMouseClicked
    
    private void fileListMouseClicked(java.awt.event.MouseEvent evt)
    {                                      
        if (evt.getClickCount() == 2)
        {
            rootSettingsModel.setSelectionPath(
                    new TreePath(new Object[]{
                rootSettingsModel,
                rootSettingsModel.getRootSettings().getTargets(),
                current,
                fileList.getCheckBoxListSelectedValue() } ));
        }

    }                                     

    private void removeModelButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_removeModelButtonActionPerformed
        Object[] values = modelList.getSelectedValues();
        for (int i = 0; i < values.length; i++) {
            Object object = values[i];
            rootSettingsModel.removeModel(current, (Model)object);
            updateModels();
        }
        removeModelButton.setEnabled(false);
        plotModelImagePanel.show(rootSettingsModel, current);
        plotChi2Panel.show(rootSettingsModel);
    }//GEN-LAST:event_removeModelButtonActionPerformed

private void modelListValueChanged(javax.swing.event.ListSelectionEvent evt)
    {//GEN-FIRST:event_modelListValueChanged
        boolean hasSelection = modelList.getSelectedIndex() != -1;
        removeModelButton.setEnabled(hasSelection);

        //sync checkbox with selection
        polarCheckBox.setSelected(hasSelection && getSelectedModel().getPolar());
        stretchedCheckBox.setSelected(hasSelection && getSelectedModel().getStretched());

        // accept some modification for positional model only
        hasSelection = hasSelection && getSelectedModel() != null && ModelUtils.hasPosition(getSelectedModel());
        polarCheckBox.setEnabled(hasSelection);
        stretchedCheckBox.setEnabled(hasSelection);

    }//GEN-LAST:event_modelListValueChanged

            private void targetFitterParamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetFitterParamActionPerformed

                rootSettingsModel.setNormalize(current, normalizeCheckBox.isSelected());

                String visAmp = null;
                String visPhi = null;
                String vis2 = null;
                String t3Amp = null;
                String t3Phi = null;

                if (visAmpCheckBox.isSelected()) {
                    visAmp = "default";
                }
                if (visPhiCheckBox.isSelected()) {
                    visPhi = "default";
                }
                if (vis2CheckBox.isSelected()) {
                    vis2 = "default";
                }
                if (t3phiCheckBox.isSelected()) {
                    t3Phi = "default";
                }
                if (t3ampCheckBox.isSelected()) {
                    t3Amp = "default";
                }
                rootSettingsModel.setResiduals(current, visAmp, visPhi, vis2, t3Amp, t3Phi);

                // refresh modelpanel to get updated list of residual plots
                plotModelImagePanel.show(rootSettingsModel, current);
}//GEN-LAST:event_targetFitterParamActionPerformed

    private void addMyModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMyModelButtonActionPerformed
        rootSettingsModel.addUserModel();
    }//GEN-LAST:event_addMyModelButtonActionPerformed

    private void addModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModelButtonActionPerformed
        // Construct a new copy
        Model selectedModel = (Model) availableModelList.getSelectedValue();
        // Check that usermodel is in the settings file        
        rootSettingsModel.addUserModel(selectedModel);        
        
        // create and add new instance
        Model m = (Model) UtilsClass.clone(selectedModel);
        m.setCode(null);
        rootSettingsModel.addModel(current, m);
        updateModels(m);
        plotModelImagePanel.show(rootSettingsModel, current);
        plotChi2Panel.show(rootSettingsModel);
    }//GEN-LAST:event_addModelButtonActionPerformed

    private void polarCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_polarCheckBoxActionPerformed
        if (ModelUtils.setPolarModel(getSelectedModel(), polarCheckBox.isSelected())) {
            updateModels(getSelectedModel());
        }
    }//GEN-LAST:event_polarCheckBoxActionPerformed

    private void stretchedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stretchedCheckBoxActionPerformed
        if (ModelUtils.setStretchedModel(getSelectedModel(), stretchedCheckBox.isSelected())) {
            updateModels(getSelectedModel());
        }
    }//GEN-LAST:event_stretchedCheckBoxActionPerformed

    private void availableModelListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_availableModelListMouseClicked
        if (evt.getClickCount() == 2) {
            addModelButtonActionPerformed(null);
        }

    }//GEN-LAST:event_availableModelListMouseClicked

    private void visitUmRepositoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitUmRepositoryButtonActionPerformed
        ModelUtils.visitUsermodelsRepository();
    }//GEN-LAST:event_visitUmRepositoryButtonActionPerformed

    private Model getSelectedModel() {
        if (modelList.getSelectedIndex() < 0) {
            return null;
        }
        return (Model) models.getElementAt(modelList.getSelectedIndex());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addModelButton;
    private javax.swing.JButton addModelHelpButton;
    private javax.swing.JButton addMyModelButton;
    private javax.swing.JList availableModelList;
    private javax.swing.JScrollPane fileListScrollPane;
    private javax.swing.JButton fitterSetupHelpButton;
    private javax.swing.JComboBox identComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList modelList;
    private javax.swing.JCheckBox normalizeCheckBox;
    private javax.swing.JTable parametersTable;
    private javax.swing.JCheckBox polarCheckBox;
    private javax.swing.JButton removeModelButton;
    private javax.swing.JCheckBox stretchedCheckBox;
    private javax.swing.JPanel subplotPanel;
    private javax.swing.JCheckBox t3ampCheckBox;
    private javax.swing.JCheckBox t3phiCheckBox;
    private javax.swing.JCheckBox vis2CheckBox;
    private javax.swing.JCheckBox visAmpCheckBox;
    private javax.swing.JCheckBox visPhiCheckBox;
    private javax.swing.JButton visitUmRepositoryButton;
    // End of variables declaration//GEN-END:variables
}
