/*
 * JMMC
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

import java.lang.reflect.*;

/**
 *
 * @author  mella
 */
public class ModelPanel extends javax.swing.JPanel
        implements TreeSelectionListener, TreeModelListener, ChangeListener{
    
    protected JTree     tree;
    ModelModel          treeModel;
    private TreePath    currentSelectedPath = null; // indicates jtree selected item
    protected Logger    _logger             =
        Logger.getLogger("jmmc.mf.gui.ModelPanel");
    protected Model     supportedModels     = null;
    protected Hashtable modelsList;
    
    // Element for paramsPanel
    JTable table;   
    ParametersTableModel tableModel;
    
    /** Creates new form ModelPanel */
    public ModelPanel(ModelModel treeModel) {
        initComponents();
        try{
            // MANAGE LEFT PART OF MODEL (model tree) 
            // init combo content with server list of models
            supportedModels                     = ServerImpl.get_mdl_list();
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
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);            
            
            JScrollPane scrollPane = new JScrollPane(tree);
            modelPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
            
            // register to Selection to be able to add a model  into the hierachical
            // structure and to present fresh view of the selected model and its
            // parameters.
            tree.addTreeSelectionListener(this);
            treeModel.addTreeModelListener(this);            
            
            // MANAGE RIGTH PART OF MODEL (parameters table)
            // Place centered Table for parameters
            table          = new JTable();
            tableModel = new ParametersTableModel(); 
            table.setModel(tableModel);
            scrollPane     = new javax.swing.JScrollPane();
            scrollPane.setViewportView(table);
            paramsPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
            modelSplitPane.setRightComponent(paramsPanel);
            
            // Adjust recursive checkBox            
            showChildrenParametersCheckBox.addChangeListener(this);
            showChildrenParametersCheckBox.setModel(PreferencedButtonModel.getInstance(
                    Preferences.getInstance(), "show.recursive.parameters"));
            showChildrenParametersCheckBox.setAction(new MFAction(
                    "showChildrenParameters"));
                        
            // next call make table show the rootModel
            display((Model) treeModel.getRoot());            
            
            
        } catch (Exception e) {
            new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }
    }
    
    /** returns the selected model or null */
    public Model getCurrentSelectedModel() {
        MCSLogger.trace();
        
        if (currentSelectedPath == null) {
            _logger.fine("No model is currently selected ");
            return null;
        }
        
        _logger.fine("Current selected path is :" + currentSelectedPath);
        
        return (Model) currentSelectedPath.getLastPathComponent();
    }
    
    
    /** Used to store into the given list all models and its components*/
    private void registerModels(Model parent, List l) {
        l.add(parent);
        
        Model[] models = parent.getModel();
        
        for (int i = 0; i < models.length; i++) {
            registerModels(models[i], l);
        }
    }
    
    /** 
     * This method is called on model tree selection changes and adjust state
     * of buttons
     */
    public void valueChanged(TreeSelectionEvent evt) {
        MCSLogger.trace();               
        
        // Get all nodes whose selection status has changed
        TreePath[] paths               = evt.getPaths();
        
        boolean    setAddButtonEnabled = false;
        boolean    setDelButtonEnabled = false;
        currentSelectedPath            = null;
        
        // Iterate through all affected nodes
        for (int i = 0; i < paths.length; i++) {
            _logger.fine("Changed occured on:" + paths[i]);
            if (evt.isAddedPath(i)) {
                _logger.fine("Selected:" + paths[i]);
                Model m = (Model) paths[i].getLastPathComponent();
                // This node has been selected            
                currentSelectedPath = paths[i];
                // Tell addButton to be activated only if selected model is of composite type
                if (m.getType().equals(ModelModel.COMPOSITE_MODEL_TYPE)) {                             
                        setAddButtonEnabled = true;                                          
                }
                setDelButtonEnabled = true;
            } else {
                // This node has been deselected
                _logger.fine("Deselected:" + paths[i]);
                setAddButtonEnabled = true;
            }
        }
        // if nothing selected, accept add actions
        if (paths.length==0){
            setAddButtonEnabled = true;
            _logger.fine("Nothing seems selected");
        }
        
        // update GUI
        addButton.setEnabled(setAddButtonEnabled);
        delButton.setEnabled(setDelButtonEnabled);        
        display(getCurrentSelectedModel());
    }
    
    /** This method is called on model tree changes */
    public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {
        jmmc.mcs.log.MCSLogger.trace();
    }
    
    /** This method is called on model tree changes */
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {
        jmmc.mcs.log.MCSLogger.trace();
    }
    
    /** This method is called on model tree changes */
    public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {
        jmmc.mcs.log.MCSLogger.trace();
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
    
    /** 
     * This method make the panel showing information about given model.
     */
    public void display(Model currentModel, boolean showDescendant) {
        MCSLogger.trace();        
        
        tableModel.setModel(currentModel, showDescendant);
        showChildrenParametersCheckBox.setVisible(false);
        // change model description, show recursive checkbox if necessary    
        // set columns width
        if (currentModel == null) {
            modelNameTextField.setText("");
            modelNameTextField.setEditable(false);
            modelTypeTextField.setText("");                        
        }else{                    
            modelNameTextField.setEditable(true);
            modelNameTextField.setText(currentModel.getName());
            modelTypeTextField.setText("" + currentModel.getType());
            if (currentModel.getModelCount() != 0) {
                showChildrenParametersCheckBox.setVisible(true);
            }
            McsClass.initColumnSizes(table,300);
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
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        modelSplitPane = new javax.swing.JSplitPane();
        modelPanel = new javax.swing.JPanel();
        modelModifierPanel = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        addButton = new javax.swing.JButton();
        delButton = new javax.swing.JButton();
        paramsPanel = new javax.swing.JPanel();
        descPanel = new javax.swing.JPanel();
        modelNameLabel = new javax.swing.JLabel();
        modelNameTextField = new javax.swing.JTextField();
        modelTypeLabel = new javax.swing.JLabel();
        modelTypeTextField = new javax.swing.JTextField();
        tablePanel = new javax.swing.JPanel();
        showChildrenParametersCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        modelPanel.setLayout(new java.awt.BorderLayout());

        modelModifierPanel.setLayout(new javax.swing.BoxLayout(modelModifierPanel, javax.swing.BoxLayout.X_AXIS));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        modelModifierPanel.add(jComboBox1);

        addButton.setText("+");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        modelModifierPanel.add(addButton);

        delButton.setText("-");
        delButton.setEnabled(false);
        delButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delButtonActionPerformed(evt);
            }
        });

        modelModifierPanel.add(delButton);

        modelPanel.add(modelModifierPanel, java.awt.BorderLayout.SOUTH);

        modelSplitPane.setLeftComponent(modelPanel);

        paramsPanel.setLayout(new java.awt.BorderLayout());

        descPanel.setLayout(new javax.swing.BoxLayout(descPanel, javax.swing.BoxLayout.X_AXIS));

        descPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Model"));
        modelNameLabel.setText("Name:");
        descPanel.add(modelNameLabel);

        modelNameTextField.setEditable(false);
        modelNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelNameTextFieldActionPerformed(evt);
            }
        });

        descPanel.add(modelNameTextField);

        modelTypeLabel.setText("Type:");
        descPanel.add(modelTypeLabel);

        modelTypeTextField.setEditable(false);
        descPanel.add(modelTypeTextField);

        paramsPanel.add(descPanel, java.awt.BorderLayout.NORTH);

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        paramsPanel.add(tablePanel, java.awt.BorderLayout.CENTER);

        showChildrenParametersCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        showChildrenParametersCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        paramsPanel.add(showChildrenParametersCheckBox, java.awt.BorderLayout.SOUTH);

        modelSplitPane.setRightComponent(paramsPanel);

        add(modelSplitPane, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    private void modelNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelNameTextFieldActionPerformed
        MCSLogger.trace();        
        // Change model name
        Model m = getCurrentSelectedModel();
        
        if (m!=null){
            m.setName(modelNameTextField.getText());
            // Update tree
            tree.repaint();
        }
    }//GEN-LAST:event_modelNameTextFieldActionPerformed
    
    private void delButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delButtonActionPerformed
        MCSLogger.trace();
        treeModel.removeModel(getCurrentSelectedModel());
        fix();
    }//GEN-LAST:event_delButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        MCSLogger.trace();
        
        String modelType = (String) jComboBox1.getSelectedItem();
        String modelName=modelType;
        Model  model     = (Model) modelsList.get(modelType);
        Model  m         = model.getClone();
        Model  selectedModel = getCurrentSelectedModel();
        
        // Accept to add only if selected model is not a composite model
        // or is the new root
        // V0.0 does  not support hierachical level > 1                        
        if( (modelType.equals(ModelModel.COMPOSITE_MODEL_TYPE) )
            && (treeModel.getRoot() != null)){          
             _logger.warning("Can't add one composite to one other if it isn't the new rootnode");             
             return;
        }else{
            // Add the node to the selected model or to the root
            // Prepare the name
            if (selectedModel != null) {            
                // getCurrentSelectedModel().getModelCount() must be called before addModel call
                // addModel makes getCurrentSelectedModel() returns null
                modelName = modelType + selectedModel.getModelCount();
                treeModel.addModel(selectedModel, m);            
            }        
            else {                
                treeModel.addModel(m);                
            }
        }
        
        m.setName(modelName);
        m.setType(modelType);
        fix();
    }//GEN-LAST:event_addButtonActionPerformed
    
    void fix(){
        MCSLogger.trace();
        try{
            // get root model
            Model rootModel = (Model) treeModel.getRoot();
            
            if (rootModel==null){
                _logger.warning("Can't fix an empty model");
                // @todo maybe add one user level error 
                return;
            }
            
            // Send clear model command
            ServerImpl.clear_mdl();
            
            // get list of models
            List  models    = new ArrayList();
            registerModels(rootModel,models);
            
            // send addmodel command for each model
            Object[] array = models.toArray();
            for (int i = 0; i < array.length; i++) {
                Model m = (Model) array[i];
                ServerImpl.add_mdl(m.getName(),m.getType());
            }
            
            // and send associated parameters
            for (int i = 0; i < array.length; i++) {
                Model m = (Model) array[i];
                Parameter[] params = m.getParameter();
                for (int j=0; j<params.length;j++){
                    Parameter p = params[j];
                    ServerImpl.set_mdl_param(m.getName(),p.getName(), p.getValue());
                }
            }
            
        } catch (Exception e) {
            new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }
    }
    
    void showModel(){
        MCSLogger.trace();
        String       xml;
        try {
            xml  = ServerImpl.get_mdl_desc();
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
    }
    
    /**
     * Implementation of a table model that is based on a given Model.
     */
    class ParametersTableModel extends  AbstractTableModel{
        protected Model currentModel=null;
        protected boolean recursive;
        protected Parameter[] parameters;
        // Store model of corresponding parameter in parameters array
        protected Model[] modelOfParameters; 
        
        
        // Init columns titles and types
        protected final String[] columnNames = new String[]{
            "Name","Units","Value","MinValue","MaxValue","Scale",
                "HasFixedValue","HasMinValue","HasMaxValue"};        

        public ParametersTableModel(){
            // next static line should be replaced by a preference listener            
            recursive=true;               
        }
        
        /**
         * tell table model to represent the parameters of the given model. 
         */
        public void setModel(Model modelToPresent, boolean recursive){
            currentModel = modelToPresent;
            this.recursive=recursive;
            parameters = new Parameter[]{};
            if(currentModel!=null){
                // get list , create array and init array with content list
                Vector params = new Vector();
                Vector models = new Vector();
                addParamsFor(currentModel,params, models, recursive);
                parameters = new Parameter[params.size()];
                modelOfParameters = new Model[params.size()];
                for (int i=0; i<parameters.length; i++){
                    parameters[i] = (Parameter) params.elementAt(i); 
                    modelOfParameters[i]=(Model) models.elementAt(i);
                }
            }
            // notify observers
            fireTableDataChanged();
        }                
        
        protected void addParamsFor(Model model, Vector paramContainer, Vector modelContainer, boolean recursive) {
            MCSLogger.trace();
            
            // First append model parameters
            Parameter[] params     = model.getParameter();
            int         nbOfParams = params.length;
            
            // Create with initial data
            for (int i = 0; i < nbOfParams; i++) {
                Parameter p   = params[i];
                paramContainer.add(p);
                modelContainer.add(model);
            }
            
            if (recursive) {
                Model[] models = model.getModel();                
                for (int i = 0; i < models.length; i++) {
                    addParamsFor(models[i], paramContainer, modelContainer, true);
                }
            }
        }        
        
        // Next parts makes respond to the full TableModel interface        
        public Class 	getColumnClass(int columnIndex){
            Object o = getValueAt(0, columnIndex);            
            if (o == null) {
                return Object.class;
            } else {
                return o.getClass();
            }
        }
        
        public int 	getColumnCount(){
            return columnNames.length;
        }
        
        public String 	getColumnName(int columnIndex){
            return columnNames[columnIndex];            
        }
        
        public int 	getRowCount(){ 
            if (parameters!=null){
                return parameters.length;
            }
            return 0;
        }
        
        public Object 	getValueAt(int rowIndex, int columnIndex){
            Parameter p = parameters[rowIndex];  
            // @todo ask quality software responsible to validate following code
            try{      
            String methodName="get"+columnNames[columnIndex];
            Method m = Parameter.class.getMethod(methodName,new Class[0]);            
            // Name is handled apart to supply model name
            if (columnNames[columnIndex].equals("Name")){
                if(recursive){
                    Model model = modelOfParameters[rowIndex];
                    return model.getName()+"."+m.invoke(p,new Object[0]);                    
                }        
            }
//            return m.invoke(p,new Object[]{});            
            return m.invoke(p,new Object[0]);            
            }catch(Exception e){
                _logger.warning(""+e); 
                return "Error";
            }
        }
        
        public boolean 	isCellEditable(int rowIndex, int columnIndex){
            Parameter p = parameters[rowIndex];            
            if(columnIndex>1){
                return p.getEditable();
            }else{
                return false;
            }
        }
                        
        public void setValueAt(Object aValue, int rowIndex, int columnIndex){
            Parameter p = parameters[rowIndex];
            Model m = modelOfParameters[rowIndex];
            
            try {                
                //@todo implement result interpretation instead of example code                               
                if(aValue instanceof Long){
                    String xml=ServerImpl.set_mdl_param(m.getName(),p.getName(), ((Long)aValue).longValue());
                }else{
                     _logger.fine("parameter "+p.getName()+"@"
                         +m.getName()+" old:"+p.getValue() 
                         +" new:"+aValue+"("+aValue.getClass()+")");
                }                
                
            } catch (Exception e) {
                 new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
            }
            
            
        }        
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton delButton;
    private javax.swing.JPanel descPanel;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel modelModifierPanel;
    private javax.swing.JLabel modelNameLabel;
    private javax.swing.JTextField modelNameTextField;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JSplitPane modelSplitPane;
    private javax.swing.JLabel modelTypeLabel;
    private javax.swing.JTextField modelTypeTextField;
    private javax.swing.JPanel paramsPanel;
    private javax.swing.JCheckBox showChildrenParametersCheckBox;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
    
}
