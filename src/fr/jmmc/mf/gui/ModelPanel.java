/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ModelPanel extends javax.swing.JPanel implements ListSelectionListener{
    ParametersTableModel parametersTableModel;
    Model current;
    SettingsModel settingsModel;
    Vector<MouseListener> mouseListeners = new Vector();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addParamButton;
    private javax.swing.JList availableModelList;
    private javax.swing.JScrollPane availableModelScrollPane;
    private javax.swing.JLabel codePrototypeLabel;
    private javax.swing.JPanel customCodePanel;
    private javax.swing.JScrollPane customCodeScrollPane;
    private javax.swing.JTextArea customCodeTextArea;
    private javax.swing.JTextField customTypeTextField;
    private javax.swing.JButton delParamButton;
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTable parametersTable;
    // End of variables declaration//GEN-END:variables

    /** Creates new form ModelPanel */
    public ModelPanel() {
        // first create our parameters table model
        parametersTableModel = new ParametersTableModel();
        initComponents();        
        // set help buttons
        helpButton1.setAction(new ShowHelpAction("_BEG_ParametersPanel"));
        parametersTable.getSelectionModel().addListSelectionListener(this);
    }

    public void show(Model m, SettingsModel s) {
        settingsModel = s;

        // select corresponding element with current=null to ignore event
        current = m;
        
        // handle some widget config if custom model or not
        boolean isCustomModel = !StringUtils.isEmpty(m.getCode());                
        customTypeTextField.setVisible(isCustomModel);
        customCodeTextArea.setVisible(isCustomModel);
        customCodeScrollPane.setVisible(isCustomModel);
        customCodePanel.setVisible(isCustomModel);
        addParamButton.setVisible(isCustomModel);
        delParamButton.setVisible(isCustomModel);
        codePrototypeLabel.setVisible(isCustomModel);
        
        availableModelList.setVisible(!isCustomModel);
        availableModelScrollPane.setVisible(!isCustomModel);        
        
        descTextArea.setEditable(isCustomModel);
        parametersTableModel.setEditable(isCustomModel);     
        
        if(!isCustomModel){
            availableModelList.setSelectedValue(s.getSupportedModel(m.getType()),true);    
            availableModelList.setModel(settingsModel.getSupportedModels());
        }else{
            StringBuffer sb = new StringBuffer("");
            sb.append("tf= ").append(current.getType()).append("(ufreq, vfreq,");
            Parameter[] params = current.getParameter();
            for (Parameter param : params) {
                sb.append(param.getType()).append(", ");
            }
            sb.setLength(sb.length()-2);
            sb.append(")");
            codePrototypeLabel.setText(sb.toString());
            
            // Fill custom type textfield
            customTypeTextField.setText(m.getType());
            
            // Fill code
            customCodeTextArea.setText(m.getCode());
            
        }
                
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        availableModelScrollPane = new javax.swing.JScrollPane();
        availableModelList = new javax.swing.JList();
        customTypeTextField = new javax.swing.JTextField();
        customCodePanel = new javax.swing.JPanel();
        customCodeScrollPane = new javax.swing.JScrollPane();
        customCodeTextArea = new javax.swing.JTextArea();
        codePrototypeLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        parametersTable = new fr.jmmc.jmcs.gui.component.NumericJTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        addParamButton = new javax.swing.JButton();
        delParamButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Model panel:"));
        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(null);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(110, 50));

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(254, 50));

        descTextArea.setEditable(false);
        descTextArea.setColumns(20);
        descTextArea.setRows(5);
        descTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                descTextAreaCaretUpdate(evt);
            }
        });
        jScrollPane2.setViewportView(descTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane2, gridBagConstraints);

        jLabel2.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        jPanel5.add(jLabel2, gridBagConstraints);

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
        jPanel5.add(nameTextField, gridBagConstraints);

        jLabel1.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel5.add(jLabel1, gridBagConstraints);

        availableModelList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        availableModelScrollPane.setViewportView(availableModelList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(availableModelScrollPane, gridBagConstraints);

        customTypeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customTypeTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(customTypeTextField, gridBagConstraints);

        customCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Code"));
        customCodePanel.setLayout(new java.awt.GridBagLayout());

        customCodeTextArea.setColumns(20);
        customCodeTextArea.setRows(5);
        customCodeTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                customCodeTextAreaCaretUpdate(evt);
            }
        });
        customCodeScrollPane.setViewportView(customCodeTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        customCodePanel.add(customCodeScrollPane, gridBagConstraints);

        codePrototypeLabel.setText("replaced by prototype");
        customCodePanel.add(codePrototypeLabel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(customCodePanel, gridBagConstraints);

        jSplitPane1.setTopComponent(jPanel5);
        jPanel5.getAccessibleContext().setAccessibleDescription("");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        jPanel4.setLayout(new java.awt.GridBagLayout());

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

        jPanel1.setLayout(new java.awt.GridBagLayout());

        addParamButton.setText("+");
        addParamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addParamButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(addParamButton, gridBagConstraints);

        delParamButton.setText("-");
        delParamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delParamButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(delParamButton, gridBagConstraints);

        helpButton1.setText("jButton1");
        helpButton1.setAlignmentX(1.0F);
        jPanel1.add(helpButton1, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(jPanel1, gridBagConstraints);

        jSplitPane1.setBottomComponent(jPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_nameTextFieldActionPerformed
        settingsModel.setModelName(current, nameTextField.getText());
    }//GEN-LAST:event_nameTextFieldActionPerformed

    private void addParamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addParamButtonActionPerformed
        Parameter p = new Parameter();
        p.setValue(0.0d);
        p.setHasFixedValue(false);        
        current.addParameter(p);
        // TODO : check if there is a better refresh method ?
        show(current, settingsModel);
    }//GEN-LAST:event_addParamButtonActionPerformed

    private void delParamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delParamButtonActionPerformed
        current.removeParameter(current.getParameter(parametersTable.getSelectedRow()));
        show(current, settingsModel);
    }//GEN-LAST:event_delParamButtonActionPerformed

    private void customCodeTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_customCodeTextAreaCaretUpdate
        current.setCode(customCodeTextArea.getText());
    }//GEN-LAST:event_customCodeTextAreaCaretUpdate

    private void descTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_descTextAreaCaretUpdate
        current.setDesc(descTextArea.getText());
    }//GEN-LAST:event_descTextAreaCaretUpdate

    private void customTypeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customTypeTextFieldActionPerformed
        current.setType(customTypeTextField.getText());
    }//GEN-LAST:event_customTypeTextFieldActionPerformed

    public void valueChanged(ListSelectionEvent e) {
        boolean hasSelection = parametersTable.getSelectedRow()>=0;
        delParamButton.setEnabled(hasSelection);
    }
}