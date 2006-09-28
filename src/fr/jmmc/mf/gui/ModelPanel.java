/*
 JMMC
 */
package jmmc.mf.gui;

import jmmc.mcs.log.MCSLogger;
import jmmc.mcs.gui.ReportDialog;
import jmmc.mcs.util.*;

import jmmc.mf.models.*;

import jmmc.mf.svr.*;

import java.awt.event.ActionEvent;

import java.io.StringReader;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;


/**
 *
 * @author  mella
 */
public class ModelPanel extends javax.swing.JPanel
        implements TreeSelectionListener, TreeModelListener, ChangeListener {
    protected JTree     tree;
    ModelModel          treeModel;
    private TreePath    currentSelectedPath = null; // indicates jtree selected item
    protected Logger    _logger             = MCSLogger.getLogger();
    protected Model     supportedModels     = null;
    protected Hashtable modelsList;
    
    // Element for paramsPanel
    JTable table;
    Vector columnNames;
    
    // Variables declaration - do not modify
    private javax.swing.JButton    addButton;
    private javax.swing.JButton    delButton;
    private javax.swing.JPanel     descPanel;
    private javax.swing.JButton    fixItButton;
    private javax.swing.JComboBox  jComboBox1;
    private javax.swing.JPanel     modelModifierPanel;
    private javax.swing.JLabel     modelNameLabel;
    private javax.swing.JTextField modelNameTextField;
    private javax.swing.JPanel     modelPanel;
    private javax.swing.JSplitPane modelSplitPane;
    private javax.swing.JLabel     modelTypeLabel;
    private javax.swing.JTextField modelTypeTextField;
    private javax.swing.JPanel     paramsPanel;
    private javax.swing.JCheckBox  showChildrenParametersCheckBox;
    private javax.swing.JButton    showModelButton;
    private javax.swing.JPanel     tablePanel;
    
    /** Creates new form ModelPanel */
    public ModelPanel(ModelModel treeModel) {
        initComponents();
        try{
            // init combo content with server list of models
            supportedModels                     = ServerImpl.getSupportedModels();
            modelsList                          = new Hashtable();
            Enumeration models                  = supportedModels.enumerateModel();
            Set         comboValues             = new TreeSet();
            while (models.hasMoreElements()) {
                Model m = (Model) models.nextElement();
                comboValues.add(m.getType());
                modelsList.put(m.getType(), m);
            }
            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(
                    comboValues.toArray()));
            
            this.treeModel     = treeModel;
            tree               = new JTree(treeModel);
            tree.setScrollsOnExpand(true);
            tree.setEditable(true);
            tree.getSelectionModel()
            .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            
            JScrollPane scrollPane = new JScrollPane(tree);
            modelPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
            
            // Place centered Table
            table          = new JTable();
            scrollPane     = new javax.swing.JScrollPane();
            scrollPane.setViewportView(table);
            paramsPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
            
            // Adjust recursive checkBox
            showChildrenParametersCheckBox.addChangeListener(this);
            showChildrenParametersCheckBox.setModel(PreferencedButtonModel.getInstance(
                    Preferences.getInstance(), "show.recursive.parameters"));
            showChildrenParametersCheckBox.setAction(new MFAction(
                    "showChildrenParameters"));
            
            // Init columns titles and types
            columnNames = new Vector();
            columnNames.add("Name");
            columnNames.add("Unit");
            columnNames.add("Value");
            columnNames.add("Fixed");
            columnNames.add("Min");
            columnNames.add("Max");
            columnNames.add("Desc");
            // next call to display(oneModel) will show it up
            display((Model) treeModel.getRoot());
            modelSplitPane.setRightComponent(paramsPanel);
            
            // register to Selection to be able to add a model  into the hierachical
            // structure and to present fresh view of the selected model and its
            // parameters.
            tree.addTreeSelectionListener(this);
            treeModel.addTreeModelListener(this);
            
        } catch (Exception e) {
            new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }
    }
    
    public Model getCurrentSelectedModel() {
        MCSLogger.trace();
        
        if (currentSelectedPath == null) {
            return null;
        }
        
        _logger.fine("Current selected path is :" + currentSelectedPath);
        
        return (Model) currentSelectedPath.getLastPathComponent();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        modelSplitPane                     = new javax.swing.JSplitPane();
        modelPanel                         = new javax.swing.JPanel();
        modelModifierPanel                 = new javax.swing.JPanel();
        jComboBox1                         = new javax.swing.JComboBox();
        addButton                          = new javax.swing.JButton();
        delButton                          = new javax.swing.JButton();
        fixItButton                        = new javax.swing.JButton();
        showModelButton                    = new javax.swing.JButton();
        paramsPanel                        = new javax.swing.JPanel();
        descPanel                          = new javax.swing.JPanel();
        modelNameLabel                     = new javax.swing.JLabel();
        modelNameTextField                 = new javax.swing.JTextField();
        modelTypeLabel                     = new javax.swing.JLabel();
        modelTypeTextField                 = new javax.swing.JTextField();
        tablePanel                         = new javax.swing.JPanel();
        showChildrenParametersCheckBox     = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridLayout(1, 0));

        modelPanel.setLayout(new java.awt.BorderLayout());

        modelModifierPanel.setLayout(new javax.swing.BoxLayout(
                modelModifierPanel, javax.swing.BoxLayout.X_AXIS));

        modelModifierPanel.add(jComboBox1);

        addButton.setText("+");
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    addButtonActionPerformed(evt);
                }
            });

        modelModifierPanel.add(addButton);

        delButton.setText("-");
        delButton.setEnabled(false);
        delButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    delButtonActionPerformed(evt);
                }
            });

        modelModifierPanel.add(delButton);

        fixItButton.setText("Fix");
        fixItButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    fixItButtonActionPerformed(evt);
                }
            });

        modelModifierPanel.add(fixItButton);

        showModelButton.setText("Refresh");
        showModelButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    showModelButtonActionPerformed(evt);
                }
            });

        modelModifierPanel.add(showModelButton);

        modelPanel.add(modelModifierPanel, java.awt.BorderLayout.SOUTH);

        modelSplitPane.setLeftComponent(modelPanel);

        paramsPanel.setLayout(new java.awt.BorderLayout());

        descPanel.setLayout(new javax.swing.BoxLayout(descPanel,
                javax.swing.BoxLayout.X_AXIS));

        descPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                "Model"));
        modelNameLabel.setText("Name:");
        descPanel.add(modelNameLabel);

        modelNameTextField.setEditable(false);
        modelNameTextField.setText("jTextField1");
        descPanel.add(modelNameTextField);

        modelTypeLabel.setText("Type:");
        descPanel.add(modelTypeLabel);

        modelTypeTextField.setEditable(false);
        modelTypeTextField.setText("jTextField1");
        descPanel.add(modelTypeTextField);

        paramsPanel.add(descPanel, java.awt.BorderLayout.NORTH);

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                "Parameters"));
        paramsPanel.add(tablePanel, java.awt.BorderLayout.CENTER);

        showChildrenParametersCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                0, 0, 0, 0));
        showChildrenParametersCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        paramsPanel.add(showChildrenParametersCheckBox,
            java.awt.BorderLayout.SOUTH);

        modelSplitPane.setRightComponent(paramsPanel);

        add(modelSplitPane);
    }

    // </editor-fold>//GEN-END:initComponents
    
    private void showModelButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_showModelButtonActionPerformed
        MCSLogger.trace();
        String       xml;
        try {
              xml  = ServerImpl.showmodel();
        } catch (Exception e) {          
          new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
            return;
        }
        
        StringReader reader = new StringReader(xml);
        try {
            Model newModel = Model.unmarshal(reader);
            treeModel.setRootModel(newModel);
        } catch (Exception e) {
            _logger.fine("Can't read model desc");
            _logger.fine(e.getMessage());
        }
    }//GEN-LAST:event_showModelButtonActionPerformed
    
    /** Used to store into the given list all models and its components*/
    private void registerModels(Model parent, List l) {
        l.add(parent);
        
        Model[] models = parent.getModel();
        
        for (int i = 0; i < models.length; i++) {
            registerModels(models[i], l);
        }
    }
    
    private void fixItButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_fixItButtonActionPerformed
        MCSLogger.trace();
        try{
        Model rootModel = (Model) treeModel.getRoot();
        List  models    = new ArrayList();
        
        // this should work in the future
        //registerModels(rootModel,models);
        
        // begin of temp part
        // but at begining we have to do
        Model[] ms = rootModel.getModel();
        
        for (int i = 0; i < ms.length; i++) {
            registerModels(ms[i], models);
        }
        
        ServerImpl.add_mdl(rootModel.getType());
        
        //  end of temp part
        Object[] array = models.toArray();
        
        for (int i = 0; i < array.length; i++) {
            Model m = (Model) array[i];
            ServerImpl.addmodel(m.getType(), true);
        }
        } catch (Exception e) {
            new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }
    }//GEN-LAST:event_fixItButtonActionPerformed
    
    private void delButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_delButtonActionPerformed
        MCSLogger.trace();
        treeModel.removeModel(getCurrentSelectedModel());
    }//GEN-LAST:event_delButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_addButtonActionPerformed
        MCSLogger.trace();
        
        String modelType = (String) jComboBox1.getSelectedItem();
        String modelName;
        Model  model     = (Model) modelsList.get(modelType);
        Model  m         = model.getClone();
        
        //m = new Model();
        // Add the node to the selected model or to the rootNode
        // And define model name
        if (getCurrentSelectedModel() != null) {
            // getCurrentSelectedModel().getModelCount() must be called before addModel call
            // addModel makes getCurrentSelectedModel() returns null
            modelName = modelType + getCurrentSelectedModel().getModelCount();
            treeModel.addModel(getCurrentSelectedModel(), m);
        } else {
            treeModel.addModel(m);
            modelName = modelType;
        }
        
        m.setName(modelName);
        m.setType(modelType);
    }//GEN-LAST:event_addButtonActionPerformed
    
    /** This method is called on model tree selection changes*/
    public void valueChanged(TreeSelectionEvent evt) {
        MCSLogger.trace();
        
        //_logger.fine(""+evt);
        
        // Get all nodes whose selection status has changed
        TreePath[] paths               = evt.getPaths();
        
        boolean    setAddButtonEnabled = false;
        boolean    setDelButtonEnabled = false;
        currentSelectedPath            = null;
        
        // Iterate through all affected nodes
        for (int i = 0; i < paths.length; i++) {
            _logger.fine("Changed occured on:" + paths[i]);
            if (evt.isAddedPath(i)) {
                Model m = (Model) paths[i].getLastPathComponent();
                // This node has been selected
                _logger.fine("Selected:" + paths[i]);
                currentSelectedPath = paths[i];
                
                if (m.getType().equals(ModelModel.COMPOSED_MODEL_TYPE)) {
                    setAddButtonEnabled = true;
                }
                setDelButtonEnabled = true;
            } else {
                // This node has been deselected
                _logger.fine("Deselected:" + paths[i]);
            }
        }
        
        addButton.setEnabled(setAddButtonEnabled);
        
        // Do not enable del for root component
        if (setDelButtonEnabled) {
            delButton.setEnabled(! getCurrentSelectedModel()
            .equals(treeModel.getRoot()));
            //expandAll(tree,currentSelectedPath.getParentPath(), true );
        }
        
        display(getCurrentSelectedModel());
    }
    
    /** This method is called on model tree changes */
    public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {
    }
    
    /** This method is called on model tree changes */
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {
    }
    
    /** This method is called on model tree changes */
    public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {
    }
    
    /** This method is called on model tree changes */
    public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
        jmmc.mcs.log.MCSLogger.trace();
        // Just refresh data
        display(getCurrentSelectedModel());
    }
    
    /** Respond to change events interface */
    public void stateChanged(ChangeEvent e) {
        jmmc.mcs.log.MCSLogger.trace();
        display(getCurrentSelectedModel());
    }
    
    public void display(Model m) {
        display(m, showChildrenParametersCheckBox.isSelected());
    }
    
    /** This method make the panel showing information about given model.
     */
    public void display(Model currentModel, boolean showDescendant) {
        MCSLogger.trace();
        
        // do nothing if given model is null
        if (currentModel == null) {
            return;
        }
        
        // change model description
        modelNameTextField.setText(currentModel.getName());
        modelTypeTextField.setText("" + currentModel.getType());
        
        // Build table model
        Vector content = new Vector();
        addParamsFor(currentModel, content, showDescendant);
        
        DefaultTableModel tableModel = new DefaultTableModel(content,
                columnNames) {
            // This method returns the Class object of the first
            // cell in specified column in the table model.
            // Unless this method is overridden, all values are
            // assumed to be the type Object.
            public Class getColumnClass(int columnIndex) {
                Object o = getValueAt(0, columnIndex);
                
                if (o == null) {
                    return Object.class;
                } else {
                    return o.getClass();
                }
            }
        };
        
        table.setModel(tableModel);
        
        if (currentModel.getModelCount() == 0) {
            showChildrenParametersCheckBox.setVisible(false);
        } else {
            showChildrenParametersCheckBox.setVisible(true);
        }
    }
    
    protected void addParamsFor(Model model, Vector container, boolean recursive) {
        MCSLogger.trace();
        
        // First append model parameters
        Parameter[] params     = model.getParameter();
        int         nbOfParams = params.length;
        
        // Create with initial data
        for (int i = 0; i < nbOfParams; i++) {
            Parameter p   = params[i];
            Vector    row = new Vector();
            
            // objects should be added into the row vectr following the column Name order
            if (recursive) {
                row.add(model.getName() + "." + p.getName());
            } else {
                row.add(p.getName());
            }
            
            row.add(p.getUnits());
            row.add("" + p.getValue());
            row.add(new Boolean(p.getFixed()));
            row.add("" + p.getMinValue());
            row.add("" + p.getMaxValue());
            row.add(p.getDesc());
            container.add(row);
        }
        
        if (recursive) {
            Model[] models = model.getModel();
            
            for (int i = 0; i < models.length; i++) {
                addParamsFor(models[i], container, true);
            }
        }
    }
    
    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public void expandAll() {
        MCSLogger.trace();
        
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
    
    // End of variables declaration
}
