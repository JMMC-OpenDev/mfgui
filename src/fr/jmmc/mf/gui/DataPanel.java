/*
 * DataPanel.java
 *
 * Created on 6 septembre 2006, 11:08
 */

package jmmc.mf.gui;

import javax.swing.event.ListSelectionListener;
import jmmc.mcs.log.MCSLogger;
import jmmc.mcs.gui.ReportDialog;

import jmmc.mf.svr.*;

import java.io.*;

import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
/**
 *
 * @author  mella
 */
public class DataPanel extends javax.swing.JPanel {
  
    String lastDir = null;
    Logger _logger = MCSLogger.getLogger();
    DataTableModel dataTableModel;
    
    /** Creates new form DataPanel */
    public DataPanel() {
        //init table model         
        initComponents(); 
        clearData();
        // associate our selectionListener
        dataTable.getSelectionModel().addListSelectionListener(new DataSelectionListener());
    }
    
    /** add one new line into the table and send command to the server */ 
    public void addData(File file){
        MCSLogger.trace();        
        try {
            ServerImpl.addobsdata(file.getCanonicalPath());                      
            // next line should be replaced by the computation of previous command result
            dataTableModel.addRow(new Object[]{file.getCanonicalPath()});                            
        } catch (Exception e) {            
           new ReportDialog( new javax.swing.JFrame(), true, e).setVisible(true);           
        }                         
    }
    /** clear the table and send a command to the server */
    public void clearData(){
        MCSLogger.trace();
        try {
            dataTableModel = new DataTableModel(new Object [][] {},
                new String [] {"File name"});             
            dataTable.setModel(dataTableModel);    
            ServerImpl.clearobsdata();
        } catch (Exception e) {            
           new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        } 
    }    
    
    /** remove selected lines from the table and resend new file list to the server */
    public void removeSelectedData(){
        MCSLogger.trace();
        try {
            // Remove selected data and sync with the server
            
            ListSelectionModel lsm = dataTable.getSelectionModel();
            while ( lsm.getMinSelectionIndex() != -1 ){
                int selectedRow = lsm.getMinSelectionIndex();
                dataTableModel.removeRow(selectedRow);
            }
            
            // To be synchronized with the server, we have to
            // clear and send again the user's file list
            ServerImpl.clearobsdata();
            for( int row = 0; row < dataTableModel.getRowCount(); row++){
                ServerImpl.addobsdata((String)dataTableModel.getValueAt(row, 0));
            }
        } catch (Exception e) {
            new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel6 = new javax.swing.JPanel();
        addDataButton = new javax.swing.JButton();
        delDataButton = new javax.swing.JButton();
        clearDataButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        addDataButton.setText("Add File...");
        addDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDataButtonActionPerformed(evt);
            }
        });

        jPanel6.add(addDataButton);

        delDataButton.setText("Delete File");
        delDataButton.setEnabled(false);
        delDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delDataButtonActionPerformed(evt);
            }
        });

        jPanel6.add(delDataButton);

        clearDataButton.setText("Clear");
        clearDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearDataButtonActionPerformed(evt);
            }
        });

        jPanel6.add(clearDataButton);

        add(jPanel6, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(22, 70));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(453, 100));
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File Name", "Table Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(dataTable);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents

    private void delDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delDataButtonActionPerformed
        removeSelectedData();
    }//GEN-LAST:event_delDataButtonActionPerformed

    private void clearDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearDataButtonActionPerformed
        clearData();
    }//GEN-LAST:event_clearDataButtonActionPerformed

    private void addDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDataButtonActionPerformed

        JFileChooser fileChooser = new JFileChooser();
        // Set in previous load directory
        if (lastDir != null)
        {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }

        // Open file chooser
        int returnVal = fileChooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            lastDir = file.getParent();
            addData(file);
        }
    }//GEN-LAST:event_addDataButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDataButton;
    private javax.swing.JButton clearDataButton;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton delDataButton;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    /** table model of data table */
    class DataTableModel extends DefaultTableModel{
        public DataTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }
        
        /** Make return false by default */
        public boolean isCellEditable(int row, int column){
            return false;
        }
    }

    /**
     * Inner class use to get user selection
     */
    class DataSelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()){
                return;
            }
            
            boolean flag;
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
                flag=false;
            } else {
                int selectedRow = lsm.getMinSelectionIndex();
                flag=true;
            }
            delDataButton.setEnabled(flag);
        }
        
    }

}
