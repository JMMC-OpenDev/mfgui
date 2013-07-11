/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.component.MessageContainer;
import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.jmcs.resource.image.ResourceImage;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.mf.ModelUtils;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import java.awt.BorderLayout;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class ModelPanel extends javax.swing.JPanel implements ListSelectionListener, TableModelListener {

    ParametersTableModel parametersTableModel;
    Model current;
    SettingsModel settingsModel;
    Vector<MouseListener> mouseListeners = new Vector();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addParamButton;
    private javax.swing.JList availableModelList;
    private javax.swing.JScrollPane availableModelScrollPane;
    private javax.swing.JTextArea closingTextArea;
    private javax.swing.JPanel customCodePanel;
    private javax.swing.JScrollPane customCodeScrollPane;
    private javax.swing.JTextArea customCodeTextArea;
    private javax.swing.JTextField customTypeTextField;
    private javax.swing.JButton delParamButton;
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JButton editAssociatedButton;
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private fr.jmmc.jmcs.gui.component.MessagePanel messagePanel1;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTable parametersTable;
    private javax.swing.JButton shareButton;
    private javax.swing.JTextField shortDescTextField;
    private javax.swing.JTextArea startingTextArea;
    private javax.swing.JButton validateButton;
    private javax.swing.JButton visitUmRepositoryButton;
    // End of variables declaration//GEN-END:variables
    private boolean initStep = false;
    private boolean isCustomModel = false;
    private MessageContainer inUseMessageContainer = new MessageContainer();
    private MessageContainer duplicatedMessageContainer = new MessageContainer();
    
    /** Creates new form ModelPanel */
    public ModelPanel() {
        // first create our parameters table model
        parametersTableModel = new ParametersTableModel();
        initComponents();
        // set help buttons
        helpButton1.setAction(new ShowHelpAction("_BEG_ParametersPanel"));
        parametersTable.getSelectionModel().addListSelectionListener(this);
        parametersTable.getModel().addTableModelListener(this);
    }

    public void show(Model m, SettingsModel s) {
        initStep = false;
        settingsModel = s;

        // select corresponding element with current=null to ignore event
        current = m;

        // handle some widget config if custom model or not
        isCustomModel = ModelUtils.isUserModel(m);
        final boolean isCustomInstance = !isCustomModel && ModelUtils.hasModelOfType(settingsModel.getUserCode().getModel(), current.getType());

        // test that we are not in use
        final boolean hasInstances = isCustomModel && ModelUtils.hasModelOfType(settingsModel.getRootSettings(), current.getType());
        
        if (hasInstances) {
            inUseMessageContainer.addWarningMessage("Model is currently in use, remove instance first to edit table of params. (WorkInProgress)");
        }else{
            inUseMessageContainer.clear();
        }

        // show only for custom models
        customTypeTextField.setVisible(isCustomModel);
        customCodeTextArea.setVisible(isCustomModel);
        customCodeScrollPane.setVisible(isCustomModel);
        customCodePanel.setVisible(isCustomModel);
        addParamButton.setVisible(!hasInstances && isCustomModel);
        delParamButton.setVisible(!hasInstances && isCustomModel);
        startingTextArea.setVisible(isCustomModel);
        shareButton.setVisible(isCustomModel);
        validateButton.setVisible(isCustomModel);
        moveDownButton.setVisible(isCustomModel);
        moveUpButton.setVisible(isCustomModel);
        descTextArea.setEditable(isCustomModel);
        shortDescTextField.setEditable(isCustomModel);

        customTypeTextField.setEditable(!hasInstances);
        
        if (isCustomModel) {
            addParamButton.setEnabled(!hasInstances);
            delParamButton.setEnabled(!hasInstances);
            moveDownButton.setEnabled(!hasInstances);
            moveUpButton.setEnabled(!hasInstances);
        }

        // show only for classical models
        nameTextField.setVisible(!isCustomModel);
        nameLabel.setVisible(!isCustomModel);
        availableModelList.setVisible(!isCustomModel);
        availableModelScrollPane.setVisible(!isCustomModel);

        // show only for instances of custom models
        editAssociatedButton.setVisible(isCustomInstance);

        if (!isCustomModel) {
            availableModelList.setModel(settingsModel.getSupportedModels());
            availableModelList.setSelectedValue(s.getSupportedModel(m.getType()), true);
        } else {
            updatePrototype();

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

        // Fill short and full description 
        final Model parentModel = settingsModel.getSupportedModel(current.getType());
        descTextArea.setText(parentModel.getDesc());
        descTextArea.setCaretPosition(0);
        shortDescTextField.setText(parentModel.getShortdesc());

        // init param related buttons
        valueChanged(null);

        // display messages if any
        updateMessagePanel();
        
        initStep = true;
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
        nameLabel = new javax.swing.JLabel();
        availableModelScrollPane = new javax.swing.JScrollPane();
        availableModelList = new javax.swing.JList();
        customTypeTextField = new javax.swing.JTextField();
        customCodePanel = new javax.swing.JPanel();
        customCodeScrollPane = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        startingTextArea = new javax.swing.JTextArea();
        customCodeTextArea = new javax.swing.JTextArea();
        closingTextArea = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        validateButton = new javax.swing.JButton();
        shareButton = new javax.swing.JButton();
        visitUmRepositoryButton = new javax.swing.JButton();
        editAssociatedButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        shortDescTextField = new javax.swing.JTextField();
        messagePanel1 = new fr.jmmc.jmcs.gui.component.MessagePanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        parametersTable = new fr.jmmc.jmcs.gui.component.NumericJTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        addParamButton = new javax.swing.JButton();
        delParamButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();

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
        descTextArea.setRows(4);
        descTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                descTextAreaCaretUpdate(evt);
            }
        });
        jScrollPane2.setViewportView(descTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane2, gridBagConstraints);

        jLabel2.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        jPanel5.add(jLabel2, gridBagConstraints);

        nameTextField.setEditable(false);
        nameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                nameTextFieldCaretUpdate(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(nameTextField, gridBagConstraints);

        nameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel5.add(nameLabel, gridBagConstraints);

        availableModelList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        availableModelScrollPane.setViewportView(availableModelList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(availableModelScrollPane, gridBagConstraints);

        customTypeTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                customTypeTextFieldCaretUpdate(evt);
            }
        });
        customTypeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                customTypeTextFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.1;
        jPanel5.add(customTypeTextField, gridBagConstraints);

        customCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Code"));
        customCodePanel.setLayout(new java.awt.GridBagLayout());

        jPanel6.setBackground(java.awt.Color.white);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        startingTextArea.setEditable(false);
        startingTextArea.setColumns(20);
        startingTextArea.setRows(1);
        startingTextArea.setText("replaced by prototype");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(startingTextArea, gridBagConstraints);

        customCodeTextArea.setColumns(20);
        customCodeTextArea.setRows(4);
        customCodeTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                customCodeTextAreaCaretUpdate(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanel6.add(customCodeTextArea, gridBagConstraints);

        closingTextArea.setColumns(20);
        closingTextArea.setRows(1);
        closingTextArea.setText("}");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel6.add(closingTextArea, gridBagConstraints);

        customCodeScrollPane.setViewportView(jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        customCodePanel.add(customCodeScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(customCodePanel, gridBagConstraints);

        validateButton.setText("Validate code");
        validateButton.setEnabled(false);
        jPanel7.add(validateButton);

        shareButton.setText("Share this model");
        shareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shareButtonActionPerformed(evt);
            }
        });
        jPanel7.add(shareButton);

        visitUmRepositoryButton.setText("Visit web repos...");
        visitUmRepositoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitUmRepositoryButtonActionPerformed(evt);
            }
        });
        jPanel7.add(visitUmRepositoryButton);

        editAssociatedButton.setText("Edit associated model");
        editAssociatedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editAssociatedButtonActionPerformed(evt);
            }
        });
        jPanel7.add(editAssociatedButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(jPanel7, gridBagConstraints);

        jLabel1.setText("Short description :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        jPanel5.add(jLabel1, gridBagConstraints);

        shortDescTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                shortDescTextFieldCaretUpdate(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(shortDescTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(messagePanel1, gridBagConstraints);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(addParamButton, gridBagConstraints);

        delParamButton.setText("-");
        delParamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delParamButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(delParamButton, gridBagConstraints);

        helpButton1.setText("Help");
        helpButton1.setAlignmentX(1.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(helpButton1, gridBagConstraints);

        moveUpButton.setIcon(ResourceImage.UP_ARROW.icon());
        moveUpButton.setEnabled(false);
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel1.add(moveUpButton, gridBagConstraints);

        moveDownButton.setIcon(ResourceImage.DOWN_ARROW.icon());
        moveDownButton.setEnabled(false);
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel1.add(moveDownButton, gridBagConstraints);

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

    private void addParamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addParamButtonActionPerformed
        Parameter p = new Parameter();
        p.setValue(0.0d);
        current.addParameter(p);
        p.setHasFixedValue(true);
        p.setHasFixedValue(false);

        refresh();
    }//GEN-LAST:event_addParamButtonActionPerformed

    private void delParamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delParamButtonActionPerformed
        current.removeParameter(current.getParameter(parametersTable.getSelectedRow()));
        refresh();
    }//GEN-LAST:event_delParamButtonActionPerformed

    private void customCodeTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_customCodeTextAreaCaretUpdate
        current.setCode(customCodeTextArea.getText());
    }//GEN-LAST:event_customCodeTextAreaCaretUpdate

    private void descTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_descTextAreaCaretUpdate
        current.setDesc(descTextArea.getText());
        updateMessagePanel();
    }//GEN-LAST:event_descTextAreaCaretUpdate

    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
        ModelUtils.moveParamDown(current, parametersTable.getSelectedRow());
        refresh();
    }//GEN-LAST:event_moveUpButtonActionPerformed

    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
        ModelUtils.moveParamUp(current, parametersTable.getSelectedRow());
        refresh();
    }//GEN-LAST:event_moveDownButtonActionPerformed

    private void nameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_nameTextFieldCaretUpdate
        current.setName(nameTextField.getText());
    }//GEN-LAST:event_nameTextFieldCaretUpdate

    private void customTypeTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_customTypeTextFieldCaretUpdate
        settingsModel.setModelType(current, customTypeTextField.getText(), isCustomModel);
        updatePrototype();
        updateMessagePanel();
    }//GEN-LAST:event_customTypeTextFieldCaretUpdate

    private void editAssociatedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAssociatedButtonActionPerformed
        Model customModel = ModelUtils.getModelOfType(settingsModel.getUserCode().getModel(), current.getType());
        settingsModel.selectUserModel(customModel);
    }//GEN-LAST:event_editAssociatedButtonActionPerformed

    private void shareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shareButtonActionPerformed
        try {
            ModelUtils.share(current);
        } catch (IOException ex) {
            FeedbackReport.openDialog(ex);
        }
    }//GEN-LAST:event_shareButtonActionPerformed

    private void shortDescTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_shortDescTextFieldCaretUpdate
        current.setShortdesc(shortDescTextField.getText());
        updateMessagePanel();
    }//GEN-LAST:event_shortDescTextFieldCaretUpdate

    private void customTypeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_customTypeTextFieldFocusLost
        updateMessagePanel();
    }//GEN-LAST:event_customTypeTextFieldFocusLost

    private void visitUmRepositoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitUmRepositoryButtonActionPerformed
        ModelUtils.visitUsermodelsRepository();
    }//GEN-LAST:event_visitUmRepositoryButtonActionPerformed

    public void valueChanged(ListSelectionEvent e) {
        final int selectedRow = parametersTable.getSelectedRow();
        final boolean hasSelection = selectedRow >= 0;
        delParamButton.setEnabled(hasSelection);
        moveUpButton.setEnabled(hasSelection && selectedRow != (current.getParameterCount() - 1));
        moveDownButton.setEnabled(hasSelection && selectedRow != 0);
    }

    private void refresh() {
        if (initStep) {
            // TODO : check if there is a better refresh method ?
            show(current, settingsModel);
        }
    }

    private void updatePrototype() {
        StringBuffer sb = new StringBuffer("");
        sb.append("func ").append(current.getType()).append("(ufreq, vfreq, wavelength, bandwidth, ");
        Parameter[] params = current.getParameter();
        for (Parameter param : params) {
            sb.append(param.getType()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append(") {");
        startingTextArea.setText(sb.toString());
    }
    
    public void tableChanged(TableModelEvent e) {
        updatePrototype();
    }

    private void updateMessagePanel() {            
        MessageContainer c = new MessageContainer();
                                
        if(isCustomModel){
            // complete if actual type is not uniq
            final boolean uniq = ModelUtils.isModelTypeUniq(settingsModel.getSupportedModels(), customTypeTextField.getText());
            if (!uniq) {
                c.addErrorMessage("Given type '"+customTypeTextField.getText()+"'is already defined.");            
            }
        }
        
        if(isCustomModel && StringUtils.isEmpty(current.getCode())){
            c.addErrorMessage("Code is missing");            
        }
        
        if(isCustomModel && StringUtils.isEmpty(current.getDesc())){
            c.addErrorMessage("Description is missing");            
        }
        
        if(isCustomModel && StringUtils.isEmpty(current.getShortdesc())){
            c.addErrorMessage("Short description is missing");            
        }

        // do not allow to share before everything is ok (except following tests)
        shareButton.setEnabled(!c.hasMessages());
        
        // add init message if any
        c.addMessages(inUseMessageContainer);
        
        messagePanel1.update(c);
    }
}