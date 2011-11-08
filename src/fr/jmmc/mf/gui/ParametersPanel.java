/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.ShowHelpAction;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Parameters;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class ParametersPanel extends javax.swing.JPanel implements Observer{
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ModelPanel");
    Parameters current;
    SettingsModel settingsModel;
    DefaultListModel parameterListModel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTree jTree1;
    private javax.swing.JTable sharedParametersTable;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
    private ParametersTableModel sharedTableModel;
    private Vector<SettingsModel> knownSettingsModels = new Vector();

    /** Creates new form ParametersPanel */
    public ParametersPanel(SettingsViewerInterface viewer) {
        initComponents();
        parameterListModel = new DefaultListModel();
        sharedTableModel = new ParametersTableModel();
        sharedParametersTable.setModel(sharedTableModel);

        // build help button
        helpButton1.setAction(new ShowHelpAction(("ENDtt_SharedParameters")));
    }

    public void show(SettingsModel s, Parameters p) {
        settingsModel = s;
        current = p;
        parameterListModel.clear();
        Parameter[] params = s.getSharedParameters();
        sharedTableModel.setModel(s, params, false);
        tablePanel.add(sharedParametersTable.getTableHeader(), BorderLayout.NORTH);

        // set one click edition on following table and show all decimals in numerical values        
        ((DefaultCellEditor)sharedParametersTable.getDefaultEditor(String.class)).setClickCountToStart(1);
        sharedParametersTable.setDefaultEditor(Double.class, (DefaultCellEditor)sharedParametersTable.getDefaultEditor(String.class));
        sharedParametersTable.setDefaultRenderer(Double.class, sharedParametersTable.getDefaultRenderer(String.class));


        // we want to listen model change events
        if (!knownSettingsModels.contains(settingsModel)) {
            settingsModel.addObserver(this);
            knownSettingsModels.add(settingsModel);
        }

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Here comes the list of models that get one or more shared parameters");
        for (int spIndex = 0; spIndex < params.length; spIndex++) {
            Parameter sharedParameter = params[spIndex];
            DefaultMutableTreeNode sharedParameterNode = new DefaultMutableTreeNode(sharedParameter);
            top.add(sharedParameterNode);
            Target[] targets = s.getRootSettings().getTargets().getTarget();
            for (int i = 0; i < targets.length; i++) {
                Target target = targets[i];
                Model[] models = target.getModel();
                for (int j = 0; j < models.length; j++) {
                    Model model = models[j];
                    ParameterLink[] paramLinks = model.getParameterLink();
                    for (int k = 0; k < paramLinks.length; k++) {
                        ParameterLink paramLink = paramLinks[k];
                        if (paramLink.getParameterRef() == sharedParameter) {
                            DefaultMutableTreeNode parameterNode = new DefaultMutableTreeNode(""+target+"/"+model+"/"+paramLink.getType());
                            sharedParameterNode.add(parameterNode);
                        }
                    }
                }
            }
        }

        if (params.length==0){
            top = new DefaultMutableTreeNode("There is no shared parameter yet");
        }

        jTree1 = new JTree(top);
        UtilsClass.expandAll(jTree1, true);
        jScrollPane1.setViewportView(jTree1);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jSeparator1 = new javax.swing.JSeparator();
        tablePanel = new javax.swing.JPanel();
        sharedParametersTable = new fr.jmmc.jmcs.gui.NumericJTable();
        helpButton1 = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Parameter list"));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Shared parameters:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

        jScrollPane1.setViewportView(jTree1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.5;
        add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        add(jSeparator1, gridBagConstraints);

        tablePanel.setLayout(new java.awt.BorderLayout());

        sharedParametersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablePanel.add(sharedParametersTable, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(tablePanel, gridBagConstraints);

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(helpButton1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    public void update(Observable o, Object arg) {        
        if(isShowing()){
            show(settingsModel, current);
        }
    }
}
