/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.actions.RunFitAction;
import fr.jmmc.mf.gui.models.ResultModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Files;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameters;
import fr.jmmc.mf.models.Results;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;
import fr.jmmc.mf.models.Usercode;
import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class SettingsPane extends javax.swing.JPanel implements TreeSelectionListener,
        TreeModelListener, SettingsViewerInterface, Observer {

    /** Model reference */
    final private SettingsModel settingsModel;

    final static String className = SettingsPane.class.getName();

    final static MsgPanel msgPanel = new MsgPanel();

    /** Main logger */
    final static Logger logger = LoggerFactory.getLogger(className);
    // Application actions
    
    Action saveSettingsAction;
    Action closeSettingsAction;
    
    // List of viewer panel used to display sub components
    TargetsPanel targetsPanel;
    FilesPanel filesPanel;
    UserInfoPanel userInfoPanel;
    FitterPanel fitterPanel;
    SettingsPanel settingsPanel;
    TargetPanel targetPanel;
    FilePanel filePanel;
    ModelPanel modelPanel;
    UsercodePanel usercodePanel;
    ParametersPanel parametersPanel;
    ResultPanel resultPanel;
    ResultsPanel resultsPanel;
    PlotPanel plotPanel;
    FramePanel framePanel;
    JTree settingsTree;
    // adjusted to true after user modification
    protected boolean modified;

    /** Creates new form SettingsPane */
    public SettingsPane() throws ExecutionException {
        this(new SettingsModel());
    }

    /** Creates new form SettingsPane */
    public SettingsPane(SettingsModel settingsModel) {
        // Because settingsTree has rootSettingsModel as tree model,
        // we need to init rootSettingsModel before entering initComponents
        this.settingsModel = settingsModel;

        init();
        showElement(settingsModel.getRootSettings());
    }

    private void init() {
        // instanciate actions        
        initComponents();

        settingsTree = new JTree();
        settingsTreeScrollPane.getViewport().add(settingsTree);

        ToolTipManager.sharedInstance().registerComponent(settingsTree);

        targetsPanel = new TargetsPanel(this);
        filesPanel = new FilesPanel(this);
        fitterPanel = new FitterPanel(this);
        userInfoPanel = new UserInfoPanel(this);
        settingsPanel = new SettingsPanel(filesPanel, targetsPanel, fitterPanel, userInfoPanel);
        filePanel = new FilePanel();
        modelPanel = new ModelPanel();
        usercodePanel = new UsercodePanel(this);
        parametersPanel = new ParametersPanel(this);
        resultsPanel = new ResultsPanel(this);
        resultPanel = new ResultPanel(this);
        plotPanel = new PlotPanel(this, true);
        targetPanel = new TargetPanel(this, plotPanel);
        framePanel = new FramePanel(this);

        ActionRegistrar actionRegistrar = ActionRegistrar.getInstance();
        saveSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.actions.SaveSettingsAction", "saveSettings");
        closeSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.actions.CloseModelAction", "closeModel");
        
        skipPlotResultsButton.setAction(actionRegistrar.getPreferenceAction());

        settingsTree.setModel(settingsModel);
        settingsTree.setSelectionModel(settingsModel);
        // register as treeSelectionListener to permit modifier panel changes
        settingsTree.addTreeSelectionListener(this);
        settingsTree.setCellRenderer(new MyCellRenderer());
        // to be notified of settingsModel changes
        // register as treeModelListener and observer
        settingsModel.addTreeModelListener(this);
        settingsModel.addObserver(this);
        // finaly ask to show top level element
        showElement(settingsModel.getRootSettings());

        // help to fix userobject that will be given to be shown by the Mutable TreeNode
        settingsModel.setPlotPanel(plotPanel);        

        // build help button
        helpButton1.setAction(new ShowHelpAction(("BEG_RunFit_Bt")));        
    }

    /**
     * Responds to tree selection events and try to show the element
     * in the right panel; 
     */
    public void valueChanged(TreeSelectionEvent e) {
        Object o = e.getPath().getLastPathComponent();
        if (e.getNewLeadSelectionPath() != null) {
            showElement(o);
        }
        checkValidSettings();
    }

    public void update(Observable o, Object arg) {
        checkValidSettings();
    }
    
    private void showElement(Object o) {

        if (o == null) {
            return;
        }

        logger.trace("object to show is : {}",o);
        modifierPanel.removeAll();

        // always update runfit subpanel
        updateRunFitPanel();

        
        
        if (o instanceof DefaultMutableTreeNode && !(o instanceof ResultModel || o instanceof FrameTreeNode)) {
            // dereference most objects if they are mutableTreeNode
            showElement(((DefaultMutableTreeNode) o).getUserObject());
        } else if (stopModifierPanels(o)) {
            // prevent the display of some modifiers
            // set a fixed message for locked setting cases
            msgPanel.setMessage("<html>Run fit in progress!\nThis part of the setting can't be modified.\n You may abort the running job using the <em>Cancel</em> button</html>");
            modifierPanel.add(msgPanel);
        } else if (o instanceof ResultModel) {
            // ResultModel must not be dereferenced as it is a DefaultMutableNode
            resultPanel.show((ResultModel) o, settingsModel);
            modifierPanel.add(resultPanel);
        } else if (o instanceof FrameTreeNode) {
            // ResultModel must not be dereferenced as it is a DefaultMutableNode
            framePanel.show((FrameTreeNode) o, settingsModel);
            modifierPanel.add(framePanel);
        } else if (o instanceof PlotPanel) {
            plotPanel.show(settingsModel);
            modifierPanel.add(plotPanel);
        } else if (o instanceof Settings) {
            settingsPanel.show(settingsModel.getRootSettings(), settingsModel);
            modifierPanel.add(settingsPanel);
        } else if (o instanceof Targets) {
            targetsPanel.show((Targets) o, settingsModel);
            modifierPanel.add(targetsPanel);
        } else if (o instanceof Files) {
            filesPanel.show((Files) o, settingsModel);
            modifierPanel.add(filesPanel);
        } else if (o instanceof Target) {
            targetPanel.show((Target) o, settingsModel);
            modifierPanel.add(targetPanel);
        } else if (o instanceof Model) {
            modelPanel.show((Model) o, settingsModel);
            modifierPanel.add(modelPanel);
        } else if (o instanceof File) {
            filePanel.show((File) o, settingsModel);
            modifierPanel.add(filePanel);
        } else if (o instanceof FileLink) {
            FileLink link = (FileLink) o;
            filePanel.show((File) link.getFileRef(), settingsModel);
            modifierPanel.add(filePanel);
        } else if (o instanceof Usercode) {
            usercodePanel.show(settingsModel, (Usercode) o);
            modifierPanel.add(usercodePanel);
        } else if (o instanceof Parameters) {
            parametersPanel.show(settingsModel, (Parameters) o);
            modifierPanel.add(parametersPanel);
        } else if (o instanceof Results) {
            resultsPanel.show((Results) o, settingsModel);
            modifierPanel.add(resultsPanel);
        } else {
            msgPanel.setMessage("Missing modifier panel for '" + o.getClass()
                    + "' objects " + o  );
            modifierPanel.add(msgPanel);
            logger.error("missing modifier panel for {}", o, new Throwable());
        }        
        modifierPanel.revalidate();
        modifierPanel.repaint();
        // check one more time that GUI view is up to date
        checkValidSettings();
    }

    private boolean stopModifierPanels(Object o) {
        boolean acceptPanelDuringLock = o instanceof PlotPanel
                || o instanceof File
                || o instanceof Files
                || o instanceof FileLink
                || o instanceof FrameTreeNode
                || o instanceof Parameters
                || o instanceof PlotPanel
                || o instanceof Results
                || o instanceof ResultModel;

        return settingsModel.isLocked() && !acceptPanelDuringLock;
    }

    /**
     * Responds to tree structure changes
     */
    public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {
        treeChanged(e);
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {
        treeChanged(e);
    }

    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
        // Traverse parent
        TreePath path = parent.getParentPath();
        if (path != null) {
            expandAll(tree, path, expand);
        }

    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {
        treeChanged(e);
        expandAll(settingsTree, e.getTreePath(), true);
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
        treeChanged(e);
        expandAll(settingsTree, e.getTreePath(), true);
    }

    private void treeChanged(javax.swing.event.TreeModelEvent e) {
        logger.debug("Listening for settingsModel tree event {}", e);
        if (e.getSource() instanceof SettingsModel) {
            if (settingsModel.isModified()) {
                //tabbedPane.setTitleAt(0, "Settings *");
                logger.debug("tree changed and setting model modified");
            } else {
                // tabbedPane.setTitleAt(0, "Settings");
            }
        }
        checkValidSettings();
        //expandSettingsTree();
    }

    /**
     * Call this method after any modification to be sure to present a valid
     * controller state. If the settings Model is not valid some GUI parts
     * will not be enabled.
     */
    protected void checkValidSettings() {
        // get valid state (that also updates runFitAction state)
        boolean validSettingsModel = settingsModel.isValid();
        
        int nbMeasurements = settingsModel.getNbMeasurements();
        int prefLimit = Preferences.getInstance().getPreferenceAsInt(Preferences.USER_UVPOINT_LIMITFORPLOT);
        boolean limitReached = nbMeasurements > prefLimit;
        if(limitReached){
            skipPlotResultsLabel.setText(""+nbMeasurements+" UV points > "+prefLimit+" : skip plots");
        }
        skipPlotResultsButton.setVisible(limitReached);
        skipPlotResultsLabel.setVisible(limitReached);
        skipPlotResultsCheckBox.setSelected(limitReached);
        
        saveSettingsAction.setEnabled(validSettingsModel);
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    public SettingsPane getSettingsPane() {
        return this;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        skipPlotResultsCheckBox = new javax.swing.JCheckBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        settingsTreeScrollPane = new javax.swing.JScrollPane();
        controlPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        skipPlotResultsLabel = new javax.swing.JLabel();
        skipPlotResultsButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        runFitButton = new javax.swing.JButton();
        ITMaxCheckBox = new javax.swing.JCheckBox();
        ITMaxTextField = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        helpButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        modifierPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        modifierPanel = new javax.swing.JPanel();

        skipPlotResultsCheckBox.setForeground(java.awt.Color.red);

        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(null);

        jSplitPane2.setBorder(null);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setMinimumSize(new java.awt.Dimension(220, 234));
        jSplitPane2.setName(""); // NOI18N
        jSplitPane2.setPreferredSize(new java.awt.Dimension(275, 234));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings tree"));
        jPanel2.setMinimumSize(new java.awt.Dimension(31, 168));
        jPanel2.setPreferredSize(new java.awt.Dimension(91, 168));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        settingsTreeScrollPane.setForeground(new java.awt.Color(234, 90, 90));
        settingsTreeScrollPane.setMinimumSize(new java.awt.Dimension(110, 23));
        settingsTreeScrollPane.setPreferredSize(new java.awt.Dimension(110, 4));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(settingsTreeScrollPane, gridBagConstraints);

        jSplitPane2.setTopComponent(jPanel2);

        controlPanel.setMinimumSize(new java.awt.Dimension(275, 100));
        controlPanel.setPreferredSize(new java.awt.Dimension(275, 100));
        controlPanel.setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        skipPlotResultsLabel.setForeground(java.awt.Color.red);
        skipPlotResultsLabel.setText("replaced in code");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel5.add(skipPlotResultsLabel, gridBagConstraints);

        skipPlotResultsButton.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        jPanel5.add(skipPlotResultsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        controlPanel.add(jPanel5, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(232, 57));
        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(runFitButton, gridBagConstraints);

        ITMaxCheckBox.setToolTipText("Define a maximum number of iterations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(ITMaxCheckBox, gridBagConstraints);

        ITMaxTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(ITMaxTextField, gridBagConstraints);

        jLabel1.setText("Use max iterations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel1, gridBagConstraints);

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        jPanel1.add(helpButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        controlPanel.add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        controlPanel.add(jPanel3, gridBagConstraints);

        jSplitPane2.setBottomComponent(controlPanel);

        jSplitPane1.setLeftComponent(jSplitPane2);

        modifierPanel2.setLayout(new javax.swing.BoxLayout(modifierPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBorder(null);

        modifierPanel.setLayout(new javax.swing.BoxLayout(modifierPanel, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane1.setViewportView(modifierPanel);

        modifierPanel2.add(jScrollPane1);

        jSplitPane1.setRightComponent(modifierPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void updateRunFitPanel() {        
        RunFitAction runFitAction = settingsModel.getRunFitAction();
        runFitAction.setConstraints(ITMaxCheckBox.getModel(), ITMaxTextField.getDocument(), skipPlotResultsCheckBox.getModel());
        runFitButton.setAction(runFitAction);
    }

    // Cell renderer used by the settings tree
    // it make red faulty nodes and place help tooltips
    protected class MyCellRenderer extends DefaultTreeCellRenderer {
        //getTreeCellRendererComponent

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            setToolTipText(null);
            if (value.getClass().getName().startsWith("fr.jmmc.mf.models")) {
                //setToolTipText("TBD");
                // Next part try to get valide state of serialized xml objects
                Exception e = null;
                try {
                    String methodName = "validate";
                    Class[] c = new Class[0];
                    Method m = value.getClass().getMethod(methodName, c);
                    Object[] o = new Object[0];
                    m.invoke(value, o);

                } catch (NoSuchMethodException ex) {
                    e = ex;
                } catch (SecurityException ex) {
                    e = ex;
                } catch (IllegalAccessException ex) {
                    e = ex;
                } catch (IllegalArgumentException ex) {
                    e = ex;
                } catch (InvocationTargetException ex) {
                    e = ex;
                }
                if (e != null) {
                    setForeground(Color.red);
                    if (e.getCause() != null) {
                        setToolTipText(e.getCause().getMessage());
                    } else {
                        setToolTipText(e.getMessage());
                    }
                }
            }
            // TODO test else case
            return this;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ITMaxCheckBox;
    private javax.swing.JFormattedTextField ITMaxTextField;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel modifierPanel;
    private javax.swing.JPanel modifierPanel2;
    private javax.swing.JButton runFitButton;
    private javax.swing.JScrollPane settingsTreeScrollPane;
    private javax.swing.JButton skipPlotResultsButton;
    private javax.swing.JCheckBox skipPlotResultsCheckBox;
    private javax.swing.JLabel skipPlotResultsLabel;
    // End of variables declaration//GEN-END:variables
}
