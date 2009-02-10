/*
 * ModelPanel.java
 *
 * Created on 27 février 2008, 08:57
 */
package fr.jmmc.mf.gui;


import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;



import javax.swing.*;


public class ModelPanel extends javax.swing.JPanel
{
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ModelPanel");
    ParametersTableModel parametersTableModel;
    Model current;
    SettingsModel settingsModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPopupMenu parameterPopupMenu;
    private javax.swing.JTable parametersTable;
    private javax.swing.JComboBox typeComboBox;
    // End of variables declaration//GEN-END:variables

    /** Creates new form ModelPanel */
    public ModelPanel()
    {
        // first create our parameters table model
        parametersTableModel = new ParametersTableModel();
        initComponents();
    }

    public void show(Model m, SettingsModel s)
    {
        current           = m;
        settingsModel     = s;
        nameTextField.setText(m.getName());
        typeComboBox.setSelectedItem(m);
        parametersTableModel.setModel(m, false);
        descTextArea.setText(m.getDesc());
        descTextArea.setCaretPosition(0);
    }


    public void refresh()
    {
        show(current, settingsModel);
    }

    public void shareParameter(Parameter myParam)
    {
        logger.entering("" + this.getClass(), "shareParameter");

        // Shared parameters must have one id
        if (myParam.getId() == null)
        {
            myParam.setId(myParam.getName() + Integer.toHexString(myParam.hashCode()));
        }

        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(myParam);
        newLink.setType(myParam.getType());
        current.addParameterLink(newLink);

        Parameter[] params = current.getParameter();

        for (int i = 0; i < params.length; i++)
        {
            if (params[i] == myParam)
            {
                current.removeParameter(i);
            }
        }

        //menuRequested(app);
        settingsModel.getRootSettings().getParameters().addParameter(myParam);
        settingsModel.fireUpdate();
        refresh();
    }

    public void linkParameter(Parameter myParam, Parameter sharedParameter)
    {
        logger.entering("" + this.getClass(), "linkParameter");

        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(sharedParameter);
        current.addParameterLink(newLink);

        Parameter[] params = current.getParameter();

        for (int i = 0; i < params.length; i++)
        {
            if (params[i] == myParam)
            {
                current.removeParameter(i);
            }
        }

        settingsModel.fireUpdate();
        refresh();
    }

    private void checkPopupMenu(java.awt.event.MouseEvent evt)
    {
        if (evt.isPopupTrigger())
        {
            logger.finest("Menu required");
            parameterPopupMenu.removeAll();

            // Check if pointed row is positive
            int rowIdx = parametersTable.rowAtPoint(evt.getPoint());

            if (rowIdx == -1)
            {
                return;
            }

            // Show info
            // @todo I would prefere to get the under mouse parameter selected before that menu appears
            // to handle under the mouse param instead of the
            final Parameter p        = current.getParameter(rowIdx);
            JMenuItem       menuItem = new JMenuItem("Manage parameter " + p.getName() +
                    " of type " + p.getType());
            menuItem.setEnabled(false);
            parameterPopupMenu.add(menuItem);
            parameterPopupMenu.add(new JSeparator());

            // Show share parameter entry
            menuItem = new JMenuItem("Share this parameter");
            menuItem.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        shareParameter(p);
                    }
                });
            parameterPopupMenu.add(menuItem);

            // Associate with shared parameter
            JMenu shareMenu = new JMenu("Link to ");
            parameterPopupMenu.add(shareMenu);

            Parameter[] sharedParams = settingsModel.getRootSettings().getParameters().getParameter();

            for (int i = 0; i < sharedParams.length; i++)
            {
                final Parameter sp = sharedParams[i];
                menuItem = new JMenuItem(sp.getName());
                menuItem.addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                            linkParameter(p, sp);
                        }
                    });
                /* we previously shared parameters with same types
                   if (!p.getType().equals(sp.getType())) {
                       menuItem.setEnabled(false);
                   }
                 */
                shareMenu.add(menuItem);
            }

            parameterPopupMenu.validate();
            parameterPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        else
        {
            logger.finest("No menu required");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        parameterPopupMenu = new javax.swing.JPopupMenu();
        jLabel2 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        parametersTable = new javax.swing.JTable();

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

        typeComboBox.setModel(SettingsModel.supportedModelsModel);
        typeComboBox.setEnabled(false);
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
        jPanel4.setLayout(new java.awt.BorderLayout());

        parametersTable.setModel(parametersTableModel);
        parametersTable.setToolTipText("Use right click to manage parameters");
        parametersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parametersTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                parametersTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                parametersTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(parametersTable);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

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
        current.setName(nameTextField.getText());
    }//GEN-LAST:event_nameTextFieldActionPerformed

    private void parametersTableMouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_parametersTableMouseClicked
        logger.entering("" + this.getClass(), "parametersTableMouseClicked");
        checkPopupMenu(evt);
    }//GEN-LAST:event_parametersTableMouseClicked

    private void parametersTableMousePressed(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_parametersTableMousePressed
        logger.entering("" + this.getClass(), "parametersTableMousePressed");
        checkPopupMenu(evt);
    }//GEN-LAST:event_parametersTableMousePressed

    private void parametersTableMouseReleased(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_parametersTableMouseReleased
        logger.entering("" + this.getClass(), "parametersTableMouseReleased");
        checkPopupMenu(evt);
    }//GEN-LAST:event_parametersTableMouseReleased

}
