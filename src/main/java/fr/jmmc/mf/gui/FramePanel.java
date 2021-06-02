/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.component.FileChooser;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.network.interop.SampCapabilityAction;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.mf.LITpro;
import fr.jmmc.mf.gui.interop.SendFitsImageAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JFrame;
import ptolemy.plot.plotml.PlotMLFrame;

public class FramePanel extends javax.swing.JPanel implements WindowListener {

    /** settings model reference */
    private static SettingsModel settingsModel = null;
    SettingsViewerInterface viewer = null;
    JFrame frame;
    Container contentPane;
    File lastDir = null;
    /** File stack */
    File[] files;
    /** File names stack */
    String[] filenames;

    /** Creates new form PlotPanel */
    public FramePanel(SettingsViewerInterface viewer) {
        this.viewer = viewer;
        initComponents();
        attachDetachButton.setAction(MFGui.attachDetachFrameAction);
        // build help button
        helpButton1.setAction(new ShowHelpAction(("BEG_ResultPlots_MT")));
        sampButton.setAction(LITpro.sendFitsImageAction);        
        sampButton.setActionCommand(SampCapabilityAction.BROADCAST_MENU_LABEL);        
        
    }

    public void show(FrameTreeNode frameTreeNode, SettingsModel s) {
        settingsModel = s;
        frame = frameTreeNode.getFrame();
        // Take care to add this framePanel to the list one and only one time
        if (!Arrays.asList(frame.getWindowListeners()).contains(this)) {
            frame.addWindowListener(this);
        }

        titleLabel.setText(frameTreeNode.getFrame().getTitle());

        files = frameTreeNode.getFilesToExport();
        filenames = frameTreeNode.getFilenamesToExport();

        updateFileCombo();
        
        infoLabel.setVisible(false);
        storeInfoButton.setVisible(false);

        if ( frameTreeNode.hasResponse()){
            String info = UtilsClass.getOutputMsg(frameTreeNode.getResponse());
            if (info.length() > 1) {                
                infoLabel.setText("<html>"+info+"</html>");
                infoLabel.setVisible(true);
                storeInfoButton.setVisible(true);
            }            
        }       
        

        contentPane = frame.getContentPane();
        blankPanel.removeAll();
        if (!frame.isVisible()) {
            //throw new UnsupportedOperationException("Not supported yet.");
            frame.setVisible(false);

            blankPanel.add(frame.getContentPane());
        } else {
            frame.setContentPane(contentPane);
            frame.setVisible(true);
        }
        
        resetZoomButton.setVisible(this.frame instanceof PlotMLFrame);
        
        blankPanel.revalidate();
        repaint();  
        
        // this swing refresh line fixed the refresh issue we stille may remove some code just before ??
        SwingUtils.invokeLaterEDT(new Runnable() {
            @Override
            public void run() {App.showFrameToFront();}
                    
        });
        
    }

    public void toggleFrame() {
        if (frame.isVisible()) {
            //throw new UnsupportedOperationException("Not supported yet.");
            frame.setVisible(false);
            blankPanel.removeAll();
            blankPanel.add(frame.getContentPane());
        } else {
            frame.setContentPane(contentPane);
            frame.setVisible(true);
        }

        //refresh the gui
        validate();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        attachDetachButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();
        filenamesComboBox = new javax.swing.JComboBox();
        exportButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        blankPanel = new javax.swing.JPanel();
        resetZoomButton = new javax.swing.JButton();
        sampButton = new javax.swing.JButton();
        storeInfoButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        userinfoPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Frame panel"));
        setLayout(new java.awt.GridBagLayout());

        attachDetachButton.setAlignmentX(0.5F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(attachDetachButton, gridBagConstraints);

        helpButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        add(helpButton1, gridBagConstraints);

        filenamesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(filenamesComboBox, gridBagConstraints);

        exportButton.setText("Save as ...");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(exportButton, gridBagConstraints);

        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("jLabel1");
        titleLabel.setAlignmentX(12.0F);
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(titleLabel, gridBagConstraints);

        blankPanel.setLayout(new javax.swing.BoxLayout(blankPanel, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(blankPanel, gridBagConstraints);

        resetZoomButton.setText("Reset zoom");
        resetZoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetZoomButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        add(resetZoomButton, gridBagConstraints);

        sampButton.setText("replacedbySampAction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(sampButton, gridBagConstraints);

        storeInfoButton.setText("Store info below in notebook");
        storeInfoButton.setActionCommand("Store info below in personal notebook");
        storeInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                storeInfoButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        add(storeInfoButton, gridBagConstraints);

        infoLabel.setText("infoLabel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(infoLabel, gridBagConstraints);

        userinfoPanel.setLayout(new javax.swing.BoxLayout(userinfoPanel, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(userinfoPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed

        int fileIndex = filenamesComboBox.getSelectedIndex();
        String fileName = filenames[fileIndex];
        File fileToSave = files[fileIndex];

        // Open filechooser to get file to save        
        File newFile = FileChooser.showSaveFileChooser("Export as " + fileName + "?", lastDir, null, fileName);
        if (newFile == null) {
            return;
        }
        try {
            FileUtils.copy(fileToSave, newFile);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("Can't export data into " + newFile.getAbsolutePath(), ex);
        } catch (IOException ex) {
            MessagePane.showErrorMessage("Can't export data into " + newFile.getAbsolutePath(), ex);
            return;
        }
        lastDir = newFile.getParentFile();
        filenames[fileIndex] = newFile.getName();
        updateFileCombo();
    }//GEN-LAST:event_exportButtonActionPerformed

    private void resetZoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetZoomButtonActionPerformed
        
        if( this.frame instanceof PlotMLFrame ) 
        {
              ((PlotMLFrame)this.frame).plot.resetAxes();
        }
        
    }//GEN-LAST:event_resetZoomButtonActionPerformed

    private void storeInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_storeInfoButtonActionPerformed
        // string massa(g/cr)e
        viewer.getSettingsPane().getUserInfoPanel().append(
                settingsModel.getRootSettings().getUserInfo() + 
                "--------------------------------------------------------------------------------" + 
                infoLabel.getText().replaceAll("<br>", "\n").replaceAll("\\<.*?\\>", "").replaceAll("\n\n", "\n"));       
        
        storeInfoButton.setVisible(false);        
        infoLabel.setVisible(false);        
        userinfoPanel.add(viewer.getSettingsPane().getUserInfoPanel());
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_storeInfoButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton attachDetachButton;
    private javax.swing.JPanel blankPanel;
    private javax.swing.JButton exportButton;
    private javax.swing.JComboBox filenamesComboBox;
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton resetZoomButton;
    private javax.swing.JButton sampButton;
    private javax.swing.JButton storeInfoButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel userinfoPanel;
    // End of variables declaration//GEN-END:variables

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        if (frame == e.getSource()) {
            toggleFrame();
        }
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Update the filenames combobox with file names found in given array.
     */
    private void updateFileCombo() {
        filenamesComboBox.removeAllItems();
        sampButton.setVisible(false);
        for (int i = 0; i < filenames.length; i++) {            
            filenamesComboBox.addItem(filenames[i]);
            if (filenames[i].endsWith(".fits")){
                SendFitsImageAction.setFitsFileToSend(files[i],filenames[i]);
                sampButton.setVisible(true);
            }
        }
    }
}
