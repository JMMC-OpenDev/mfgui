package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.Parameters;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.*;

public class ParametersPanel extends javax.swing.JPanel implements Observer{
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ModelPanel");
    Parameters current;
    SettingsModel settingsModel;
    DefaultListModel parameterListModel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable sharedParametersTable;
    // End of variables declaration//GEN-END:variables
    private ParametersTableModel sharedTableModel;
    private Vector<SettingsModel> knownSettingsModels = new Vector();

    /** Creates new form ParametersPanel */
    public ParametersPanel(SettingsViewerInterface viewer) {
        initComponents();
        parameterListModel = new DefaultListModel();
        sharedTableModel = new ParametersTableModel();
        sharedParametersTable.setModel(sharedTableModel);
    }

    public void show(SettingsModel s, Parameters p) {
        settingsModel = s;
        current = p;
        parameterListModel.clear();
        Parameter[] params = s.getSharedParameters();
        sharedTableModel.setModel(s, params, false);
        // we want to listen model change events
        if (!knownSettingsModels.contains(settingsModel)) {
            settingsModel.addObserver(this);
            knownSettingsModels.add(settingsModel);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        sharedParametersTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Parameter list"));
        setLayout(new java.awt.GridBagLayout());

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
        jScrollPane2.setViewportView(sharedParametersTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(jScrollPane2, gridBagConstraints);

        jLabel1.setText("Shared parameters:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    public void update(Observable o, Object arg) {        
        if(isShowing()){
            show(settingsModel, current);
        }
    }
}
