/*
 * ModelPanel.java
 *
 * Created on 27 février 2008, 08:57
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ShowHelpAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.models.Model;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;

public class ModelPanel extends javax.swing.JPanel {

    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            ModelPanel.class.getName());
    ParametersTableModel parametersTableModel;
    Model current;
    SettingsModel settingsModel;
    Vector<MouseListener> mouseListeners = new Vector();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTable parametersTable;
    private javax.swing.JComboBox typeComboBox;
    // End of variables declaration//GEN-END:variables

    /** Creates new form ModelPanel */
    public ModelPanel() {
        // first create our parameters table model
        parametersTableModel = new ParametersTableModel();
        initComponents();
        // set help buttons
        helpButton1.setAction(new ShowHelpAction("_BEG_ParametersPanel"));
    }

    public void show(Model m, SettingsModel s) {
        settingsModel = s;

        // select corresponding element with current=null to ignore event
        current = null;
        typeComboBox.setSelectedItem(s.getSupportedModel(m.getType()));

        current = m;
        nameTextField.setText(m.getName());


        parametersTableModel.setModel(s, m, false);
        jPanel2.add(parametersTable.getTableHeader(), BorderLayout.NORTH);
        UtilsClass.initColumnSizes(parametersTable, 330);

        if (!mouseListeners.contains(parametersTableModel)) {
            parametersTable.addMouseListener(parametersTableModel);
            mouseListeners.add(parametersTableModel);
        }
        descTextArea.setText(m.getDesc());
        descTextArea.setCaretPosition(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel2 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        helpButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        parametersTable = new fr.jmmc.mcs.gui.NumericJTable();
        jPanel3 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Model panel:"));
        setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(jLabel2, gridBagConstraints);

        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(nameTextField, gridBagConstraints);

        jLabel1.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(jLabel1, gridBagConstraints);

        typeComboBox.setModel(SettingsModel.getSupportedModelsModel());
        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(typeComboBox, gridBagConstraints);

        jSplitPane1.setBorder(null);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setEnabled(false);

        descTextArea.setColumns(20);
        descTextArea.setEditable(false);
        descTextArea.setRows(5);
        jScrollPane2.setViewportView(descTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(jScrollPane2, gridBagConstraints);

        jSplitPane1.setTopComponent(jPanel6);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        helpButton1.setText("jButton1");
        helpButton1.setAlignmentX(1.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel4.add(helpButton1, gridBagConstraints);

        jPanel2.setPreferredSize(new java.awt.Dimension(50, 50));
        jPanel2.setLayout(new java.awt.BorderLayout());

        parametersTable.setModel(parametersTableModel);
        parametersTable.setToolTipText("Use right click to manage parameters");
        parametersTable.setAlignmentX(0.0F);
        jPanel2.add(parametersTable, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel3, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_nameTextFieldActionPerformed
        logger.entering("" + this.getClass(), "nameTextFieldActionPerformed");
        settingsModel.setModelName(current, nameTextField.getText());
    }//GEN-LAST:event_nameTextFieldActionPerformed

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboBoxActionPerformed
        if (current == null) {
            return;
        }
        if (evt.getSource() != typeComboBox) {
            return;
        }
        if (!typeComboBox.isShowing()) {
            return;
        }

        // Construct a new copy
        Model selectedModel = (Model) typeComboBox.getSelectedItem();
        if(selectedModel == null){
            return;
        }
        Model m;
        // Clone selected Model (this code could have been moved into model???
        StringWriter writer = new StringWriter();
        try {
            UtilsClass.marshal(selectedModel, writer);
        } catch (IOException ex) {
            // GM : no io error sould occurs one stringwritters, does it ?
            throw new IllegalStateException("Can't perform copy in memory",ex);
        }
        StringReader reader = new StringReader(writer.toString());
        m = (Model) UtilsClass.unmarshal(Model.class, reader);
        settingsModel.replaceModel(current, m);          
    }//GEN-LAST:event_typeComboBoxActionPerformed

}
