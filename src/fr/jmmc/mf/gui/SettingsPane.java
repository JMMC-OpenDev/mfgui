package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.actions.RunFitAction;
import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mcs.util.ActionRegistrar;
import fr.jmmc.mf.models.*;

import java.awt.BorderLayout;
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
    public Action getModelListAction = new GetModelListAction();
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
    PlotPanel plotPanel;
    FramePanel framePanel;
    JTree settingsTree;

    // adjusted to true after user modification
    protected boolean modified;
    protected FrameList frameList;

    /** Creates new form SettingsPane */
    public SettingsPane(SettingsModel settingsModel) {
        init(settingsModel);
        showSettingElement(rootSettingsModel.getRootSettings());
    }

    /** Creates new form SettingsPane */
    public SettingsPane() {
        init(new SettingsModel());
    }

    private void init(SettingsModel settingsModel) {
        frameList = new FrameList(this);
        // instanciate actions
        runFitAction = new RunFitAction(this);

        // Because settingsTree has rootSettingsModel as tree model,
        // we need to init rootSettingsModel before entering initComponents
        rootSettingsModel = settingsModel;

        initComponents();

        runFitAction.setConstraints(ITMaxCheckBox.getModel(), ITMaxTextField.getDocument());

        settingsTree = new JTree();
        settingsTree.setMinimumSize(new Dimension(200, 200));
        settingsTree.setPreferredSize(new Dimension(200, 200));
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
        resultPanel = new ResultPanel(this);
        plotPanel = new PlotPanel(this);
        targetPanel = new TargetPanel(this, plotPanel);
        framePanel = new FramePanel(this);

        ActionRegistrar actionRegistrar = ActionRegistrar.getInstance();
        saveSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.actions.SaveModelAction", "saveModel");
        closeSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.actions.CloseModelAction", "closeModel");

        // to permit modifier panel changes,
        // register as treeSelectionListener
        settingsTree.setModel(rootSettingsModel);
        settingsTree.addTreeSelectionListener(this);
        settingsTree.setCellRenderer(new MyCellRenderer());
        // to be notified of settingsModel changes
        // register as treeModelListener
        rootSettingsModel.addTreeModelListener(this);
        // finaly ask to show top level element
        showSettingElement(rootSettingsModel.getRootSettings());

        runFitButton.setAction(runFitAction);

        listScrollPane.setViewportView(frameList);
    }

    /**
     * DOCUMENT ME!
     */
    public void expandSettingsTree() {
        // Next line does not work because node doesn't respond to treeNode interface
        //McsClass.expandAll(settingsTree,true);
        //expandAll(settingsTree, true);
    }
    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.

    public void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    /**
     * Responds to tree selection events.
     */
    public void valueChanged(TreeSelectionEvent e) {
        logger.entering("" + this.getClass(), "valueChanged");

        Object o = e.getPath().getLastPathComponent();
        if (e.getNewLeadSelectionPath() != null) {
            showSettingElement(o);
        }
        checkValidSettings();
    }

    public void showElement(Object o) {
        logger.entering("" + this.getClass(), "showElement");

        if (o == null) {
            return;
        }

        logger.finest("object to show is :" + o);
        modifierPanel.removeAll();

        if (o instanceof JFrame) {
            framePanel.show(rootSettingsModel, (JFrame) o);
            modifierPanel.add(framePanel);
            settingsTree.clearSelection();
        //frameList.clearSelection();
        } else if (o instanceof PlotPanel) {
            plotPanel.show(rootSettingsModel);
            modifierPanel.add(plotPanel);
            settingsTree.clearSelection();
            frameList.clearSelection();
        } else {
            modifierPanel.add(new JLabel("missing modifier panel for '" + o.getClass() +
                    "' objects"));
        }
        modifierPanel.revalidate();
        modifierPanel.repaint();
        // check one more time that GUI view is up to date
        checkValidSettings();

    }

    /**
     *Call this method if you want to show something particular in the right
     * side of the SettingsPane.
     *
     * @param o the object you want to be displayed.
     */
    public void showSettingElement(Object o) {
        logger.entering("" + this.getClass(), "showSettingElement");

        if (o == null) {
            return;
        }

        logger.finest("object to show is :" + o);
        modifierPanel.removeAll();

        if (o instanceof Settings) {
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
            filePanel.show((File) o);
            modifierPanel.add(filePanel);
        } else if (o instanceof FileLink) {
            FileLink link = (FileLink) o;
            filePanel.show((File) link.getFileRef());
            modifierPanel.add(filePanel);
        } else if (o instanceof Parameters) {
            parametersPanel.show((Parameters) o);
            modifierPanel.add(parametersPanel);
        } else if (o instanceof Result) {
            resultPanel.show((Result) o, rootSettingsModel);
            modifierPanel.add(resultPanel);
        } else {
            modifierPanel.add(new JLabel("missing modifier panel for '" + o.getClass() +
                    "' objects"));
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
    //showSettingElement(rootSettingsModel.getRoot());
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {
        treeChanged(e);
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {
        treeChanged(e);
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
        treeChanged(e);
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
        showPlotButton.setEnabled(validSettingsModel);
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
        jPanel3 = new javax.swing.JPanel();
        showPlotButton = new javax.swing.JButton();
        listScrollPane = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        modifierPanel = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jSplitPane2.setBorder(null);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings tree"));
        jPanel2.setMinimumSize(new java.awt.Dimension(31, 168));
        jPanel2.setPreferredSize(new java.awt.Dimension(91, 168));
        jPanel2.setLayout(new java.awt.GridBagLayout());
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

        jLabel1.setText("Use max iterations number");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        controlPanel.add(jPanel1, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Plot list"));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        showPlotButton.setText("New plot...");
        showPlotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPlotButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(showPlotButton, gridBagConstraints);

        listScrollPane.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(listScrollPane, gridBagConstraints);

        jButton1.setText("Detach/Attach");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1, new java.awt.GridBagConstraints());

        jButton2.setText("Remove");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        controlPanel.add(jPanel3, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        controlPanel.add(jPanel5, gridBagConstraints);

        jSplitPane2.setBottomComponent(controlPanel);

        jSplitPane1.setLeftComponent(jSplitPane2);

        modifierPanel.setLayout(new javax.swing.BoxLayout(modifierPanel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setRightComponent(modifierPanel);

        add(jSplitPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void showPlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPlotButtonActionPerformed
        showElement(plotPanel);
}//GEN-LAST:event_showPlotButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        framePanel.toggleFrame();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        removeSelectedPlots();
    }//GEN-LAST:event_jButton2ActionPerformed

    public void addPlot(JFrame frame, String title) {
        frameList.add(frame, title);
    }

    /** remove the frame from the list and show main setting element */
    public void removeSelectedPlots() {
        frameList.removeSelectedPlots();
        showSettingElement(rootSettingsModel.getRootSettings());
    }

    public void genResultReport(SettingsModel settingsModel, Response response) {
        JFrame resultFrame = new JFrame();
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        resultFrame.getContentPane().add(p);
        resultPanel.genReport(settingsModel);
        JScrollPane sp = new JScrollPane(new JEditorPane("text/html", resultPanel.getReport()));
        p.add(sp);
        addPlot(resultFrame, "--New fit result--");
        resultPanel.genPlots(settingsModel);
        resultPanel.genPlots(UtilsClass.getResultFiles(response));
        showElement(resultFrame);
    }

    protected class GetModelListAction extends fr.jmmc.mcs.util.MCSAction {

        String methodName = "getModelList";

        public GetModelListAction() {
            super("getModelList");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.fine("Requesting yoga '" + methodName + "' call");

            try {
                Response r = ModelFitting.execMethod(methodName, null);
                // Search model into return result
                Model newModel = UtilsClass.getModel(r);
                // Indicates to the rootSettingsModel list of availables models
                rootSettingsModel.setSupportedModels(newModel.getModel());
            } catch (Exception exc) {
                new FeedbackReport(exc);

            }
        }
    }

    // Cell renderer used by the settings tree
    // it make red faulty nodes and place help tooltips
    protected class MyCellRenderer extends DefaultTreeCellRenderer {
        //getTreeCellRendererComponent

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

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

            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ITMaxCheckBox;
    private javax.swing.JFormattedTextField ITMaxTextField;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JScrollPane listScrollPane;
    private javax.swing.JPanel modifierPanel;
    private javax.swing.JButton runFitButton;
    private javax.swing.JScrollPane settingsTreeScrollPane;
    private javax.swing.JButton showPlotButton;
    // End of variables declaration//GEN-END:variables
}
