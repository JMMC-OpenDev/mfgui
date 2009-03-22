package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.actions.GetModelListAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.actions.RunFitAction;

import fr.jmmc.mcs.util.ActionRegistrar;
import fr.jmmc.mf.gui.models.ResultModel;
import fr.jmmc.mf.models.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import java.lang.reflect.*;


import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 */
public class SettingsPane extends javax.swing.JPanel implements TreeSelectionListener,
        TreeModelListener, SettingsViewerInterface {

    /** Main logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.SettingsPane");

    // Application actions
    public static RunFitAction runFitAction;
    public Action getModelListAction;
    public Action saveSettingsAction;
    public Action closeSettingsAction;
    /** Model reference */
    private SettingsModel rootSettingsModel = null;

    // List of viewer panel used to display sub components
    TargetsPanel targetsPanel;
    FilesPanel filesPanel;
    UserInfoPanel userInfoPanel;
    FitterPanel fitterPanel;
    SettingsPanel settingsPanel;
    TargetPanel targetPanel;
    FilePanel filePanel;
    ModelPanel modelPanel;
    ParametersPanel parametersPanel;
    ResultPanel resultPanel;
    ResultsPanel resultsPanel;
    PlotPanel plotPanel;
    FramePanel framePanel;
    JTree settingsTree;

    // adjusted to true after user modification
    protected boolean modified;

    /** Creates new form SettingsPane */
    public SettingsPane(SettingsModel settingsModel) {
        init(settingsModel);
        showElement(rootSettingsModel.getRootSettings());
    }

    /** Creates new form SettingsPane */
    public SettingsPane() {
        init(new SettingsModel());
    }

    private void init(SettingsModel settingsModel) {
        // instanciate actions
        runFitAction = new RunFitAction(this);
        getModelListAction = new GetModelListAction(settingsModel);

        // Because settingsTree has rootSettingsModel as tree model,
        // we need to init rootSettingsModel before entering initComponents
        rootSettingsModel = settingsModel;

        initComponents();

        runFitAction.setConstraints(ITMaxCheckBox.getModel(), ITMaxTextField.getDocument());

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
        parametersPanel = new ParametersPanel(this);
        resultsPanel = new ResultsPanel(this);
        resultPanel = new ResultPanel(this);
        plotPanel = new PlotPanel(this);
        targetPanel = new TargetPanel(this, plotPanel);
        framePanel = new FramePanel(this);

        ActionRegistrar actionRegistrar = ActionRegistrar.getInstance();
        saveSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.actions.SaveModelAction", "saveModel");
        closeSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.actions.CloseModelAction", "closeModel");


        settingsTree.setModel(rootSettingsModel);
        settingsTree.setSelectionModel(rootSettingsModel);
        // register as treeSelectionListener to permit modifier panel changes
        settingsTree.addTreeSelectionListener(this);
        settingsTree.setCellRenderer(new MyCellRenderer());
        // to be notified of settingsModel changes
        // register as treeModelListener
        rootSettingsModel.addTreeModelListener(this);
        // finaly ask to show top level element
        showElement(rootSettingsModel.getRootSettings());

        // help to fix userobject that will be given to be shown by the Mutable TreeNode
        settingsModel.setPlotPanel(plotPanel);
        runFitButton.setAction(runFitAction);
    }

    /**
     * Responds to tree selection events and try to show the element
     * in the right panel; 
     */
    public void valueChanged(TreeSelectionEvent e) {
        logger.entering("" + this.getClass(), "valueChanged",e);
        
        Object o = e.getPath().getLastPathComponent();
        if (e.getNewLeadSelectionPath() != null) {
            showElement(o);            
        }
        checkValidSettings();
    }

    private void showElement(Object o) {
        logger.entering("" + this.getClass(), "showElement",o);

        if (o == null) {
            return;
        }

        logger.finest("object to show is :" + o);
        modifierPanel.removeAll();

        if (o instanceof ResultModel) {
            // ResultModel must not be dereferenced as it is a DefaultMutableNode
            resultPanel.show((ResultModel)o, rootSettingsModel);
            modifierPanel.add(resultPanel);
        } else if (o instanceof FrameTreeNode) {
            // ResultModel must not be dereferenced as it is a DefaultMutableNode
            framePanel.show((FrameTreeNode)o, rootSettingsModel);
            modifierPanel.add(framePanel);
        } else if (o instanceof DefaultMutableTreeNode) {
            // dereference object if it isa contained by a mutableTreeNode
            showElement(((DefaultMutableTreeNode)o).getUserObject());
        } else if (o instanceof PlotPanel) {
            plotPanel.show(rootSettingsModel);
            modifierPanel.add(plotPanel);
        } else if (o instanceof Settings) {
            settingsPanel.show(rootSettingsModel.getRootSettings(), rootSettingsModel);
            modifierPanel.add(settingsPanel);
        } else if (o instanceof Targets) {
            targetsPanel.show((Targets) o, rootSettingsModel);
            modifierPanel.add(targetsPanel);
        } else if (o instanceof Files) {
            filesPanel.show((Files) o, rootSettingsModel);
            modifierPanel.add(filesPanel);
        } else if (o instanceof Target) {
            targetPanel.show((Target) o, rootSettingsModel);
            modifierPanel.add(targetPanel);
        } else if (o instanceof Model) {
            modelPanel.show((Model) o, rootSettingsModel);
            modifierPanel.add(modelPanel);
        } else if (o instanceof File) {
            filePanel.show((File) o, rootSettingsModel);
            modifierPanel.add(filePanel);
        } else if (o instanceof FileLink) {
            FileLink link = (FileLink) o;
            filePanel.show((File) link.getFileRef(), rootSettingsModel);
            modifierPanel.add(filePanel);
        } else if (o instanceof Parameters) {
            parametersPanel.show(rootSettingsModel,(Parameters) o);
            modifierPanel.add(parametersPanel);
        } else if (o instanceof Results) {
            resultsPanel.show((Results)o, rootSettingsModel);
            modifierPanel.add(resultsPanel);
        } else {
            modifierPanel.add(new JLabel("missing modifier panel for '" + o.getClass() +
                    "' objects "+o));
            new Throwable("missing modifier panel for '" + o.getClass()+"'="+o).printStackTrace();
        }
        modifierPanel.revalidate();
        modifierPanel.repaint();
        // check one more time that GUI view is up to date
        checkValidSettings();
    }

    /**
     * Responds to tree structure changes
     */
    public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {
        logger.entering("" + this.getClass(), "treeStructureChanged");
        treeChanged(e);
    //showElement(rootSettingsModel.getRoot());
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
        if (path!=null){
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
        logger.fine("Listening for settingsModel tree event" + e);
        if (e.getSource() instanceof SettingsModel) {
            if (rootSettingsModel.isModified()) {
                //tabbedPane.setTitleAt(0, "Settings *");
                logger.fine("tree changed and setting model modified");
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
        boolean validSettingsModel = rootSettingsModel.isValid();
        runFitAction.setEnabled(validSettingsModel);
        saveSettingsAction.setEnabled(validSettingsModel);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SettingsModel getSettingsModel() {
        return rootSettingsModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        settingsTreeScrollPane = new javax.swing.JScrollPane();
        controlPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        runFitButton = new javax.swing.JButton();
        ITMaxCheckBox = new javax.swing.JCheckBox();
        ITMaxTextField = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        modifierPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        modifierPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(null);

        jSplitPane2.setBorder(null);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setMinimumSize(new java.awt.Dimension(275, 234));
        jSplitPane2.setPreferredSize(new java.awt.Dimension(260, 234));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings tree"));
        jPanel2.setMinimumSize(new java.awt.Dimension(31, 168));
        jPanel2.setPreferredSize(new java.awt.Dimension(91, 168));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        settingsTreeScrollPane.setMinimumSize(new java.awt.Dimension(150, 23));
        settingsTreeScrollPane.setPreferredSize(new java.awt.Dimension(150, 4));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(settingsTreeScrollPane, gridBagConstraints);

        jSplitPane2.setTopComponent(jPanel2);

        controlPanel.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        runFitButton.setMinimumSize(new java.awt.Dimension(175, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(runFitButton, gridBagConstraints);

        ITMaxCheckBox.setToolTipText("Define a maximum number of iterations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(ITMaxCheckBox, gridBagConstraints);

        ITMaxTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(ITMaxTextField, gridBagConstraints);

        jLabel1.setText("Use max iterations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        controlPanel.add(jPanel1, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        controlPanel.add(jPanel5, gridBagConstraints);

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
                try {
                    String methodName = "validate";
                    Class[] c = new Class[0];
                    Method m = value.getClass().getMethod(methodName, c);
                    Object[] o = new Object[0];
                    m.invoke(value, o);

                //logger.fine("method invoked using reflexion");
                } catch (Exception e) {
                    setForeground(Color.red);

                    if (e.getCause() != null) {
                        setToolTipText(e.getCause().getMessage());
                    } else {
                        setToolTipText(e.getMessage());
                    }
                }
            }

            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ITMaxCheckBox;
    private javax.swing.JFormattedTextField ITMaxTextField;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel modifierPanel;
    private javax.swing.JPanel modifierPanel2;
    private javax.swing.JButton runFitButton;
    private javax.swing.JScrollPane settingsTreeScrollPane;
    // End of variables declaration//GEN-END:variables
}
