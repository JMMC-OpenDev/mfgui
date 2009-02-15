/*
 * Created on 3 novembre 2006, 15:48
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.models.ParametersTableModel;
import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import java.awt.BorderLayout;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;

/** Display GUI for Target elements */
public class TargetPanel extends javax.swing.JPanel implements ListSelectionListener
{
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.TargetPanel");

    /** Main model object of this GUI controller */
    Target current = null;
    ListModel targetFiles;
    boolean listenToFileSelection;
    ListSelectionModel selectedFiles = new DefaultListSelectionModel();
    DefaultListModel models = new DefaultListModel();
    SettingsViewerInterface settingsViewer;
    public Settings rootSettings = null;
    public SettingsModel rootSettingsModel = null;
    private PlotModelPanel plotModelImagePanel;
    private ParametersTableModel parametersTableModel;

    /** Creates new form TargetPanel */
    public TargetPanel(SettingsViewerInterface viewer, PlotPanel plotPanel)
    {
        settingsViewer = viewer;
        parametersTableModel = new ParametersTableModel();
        initComponents();
        
        listenToFileSelection = false;

        fileList.addListSelectionListener(this);
        fileList.setSelectionModel(selectedFiles);
        //fileList.setCellRenderer(new FileListCellRenderer());

        modelList.setModel(models);

        plotModelImagePanel = new PlotModelPanel(plotPanel);
        subplotPanel.add(plotModelImagePanel);
    }


    /** Call it after any internal modification  */
    public void refresh()
    {
        settingsViewer.getSettingsModel().fireUpdate();
        settingsViewer.showElement(current);
    }

    /**
     * Display graphical view of given target.
     *
     * @param t the target to show
     * @param settingsModel its associated settingsModel
     */
    public void show(Target t, SettingsModel settingsModel)
    {
        logger.entering("" + this.getClass(), "show");

        this.rootSettings          = settingsModel.getRootSettings();
        this.rootSettingsModel     = settingsModel;

        parametersTableModel.setModel(t,true);
        jPanel5.add(jTable1.getTableHeader(),BorderLayout.NORTH);

        listenToFileSelection      = false;
        current                    = t;
        //// Select current ident
        identComboBox.setSelectedItem(t.getIdent());

        //// Set file selection according target info
        selectedFiles.clearSelection();
        // select fileListModel corresponding to target ident
        targetFiles = settingsViewer.getSettingsModel().getFileListModelForOiTarget(t.getIdent());

        if (targetFiles != null)
        {
            fileList.setModel(targetFiles);

            // define selected files reading fileLinks
            Vector filesId = new Vector();
            File[] files   = rootSettings.getFiles().getFile();

            for (int i = 0; i < files.length; i++)
            {
                File file = files[i];
                filesId.addElement(file.getId());
            }

            FileLink[] links = current.getFileLink();

            for (int i = 0; i < links.length; i++)
            {
                FileLink link  = links[i];
                Object   idRef = link.getFileRef();
                selectedFiles.addSelectionInterval(i, i);

                if (idRef != null)
                {
                    logger.fine("Selecting file for ref=" + idRef);
                }
                else
                {
                    logger.warning("No idRef for link");
                }
            }
        }
        else
        {
            // should not append except if user delete some files??
            logger.warning("Can't find list of files");
        }

        listenToFileSelection = true;

        // Fill modelTypeComboBox model if empty
        if (modelTypeComboBox.getItemCount() < 1)
        {
            settingsViewer.getSettingsPane().getModelListAction.actionPerformed(null);
        }

        // Set model list
        models.clear();

        for (int i = 0; i < current.getModelCount(); i++)
        {
            models.addElement(current.getModel(i));
        }

        // Set normalizeCheckBox
        normalizeCheckBox.setSelected(current.getNormalize());        
        plotModelImagePanel.show(settingsModel,current);
    }

    public void valueChanged(ListSelectionEvent e)
    {
        logger.entering("" + this.getClass(), "valueChanged");

        if (! listenToFileSelection || e.getValueIsAdjusting())
        {
            return;
        }

        Object[] files = fileList.getSelectedValues();
        current.removeAllFileLink();

        for (int i = 0; i < files.length; i++)
        {
            File     file = (File) files[i];
            FileLink link = new FileLink();
            link.setFileRef(file);
            current.addFileLink(link);
        }

        // fire tree event to refresh
        settingsViewer.getSettingsModel().fireUpdate();        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        identComboBox = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new fr.jmmc.mcs.gui.CheckBoxJList();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        modelList = new javax.swing.JList();
        addModelButton = new javax.swing.JButton();
        modelTypeComboBox = new javax.swing.JComboBox();
        removeModelButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        normalizeCheckBox = new javax.swing.JCheckBox();
        subplotPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Target panel"));
        setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(parametersTableModel);
        jPanel5.add(jTable1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel5, gridBagConstraints);

        jLabel1.setText("Ident:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel1, gridBagConstraints);

        identComboBox.setModel(settingsViewer.getSettingsModel().oiTargets);
        identComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel3.add(identComboBox, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected file list"));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setMinimumSize(new java.awt.Dimension(22, 82));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(259, 82));

        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(fileList);

        jPanel2.add(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jPanel2, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Model list"));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(22, 122));

        modelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                modelListMouseClicked(evt);
            }
        });
        modelList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                modelListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(modelList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane2, gridBagConstraints);

        addModelButton.setText("Add model");
        addModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel4.add(addModelButton, gridBagConstraints);

        modelTypeComboBox.setModel(settingsViewer.getSettingsModel().supportedModelsModel);
        modelTypeComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                modelTypeComboBoxFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(modelTypeComboBox, gridBagConstraints);

        removeModelButton.setText("Remove");
        removeModelButton.setEnabled(false);
        removeModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeModelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        jPanel4.add(removeModelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jPanel4, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Fitter setup"));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        normalizeCheckBox.setText("Normalize total flux");
        normalizeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalizeCheckBoxActionPerformed(evt);
            }
        });
        jPanel6.add(normalizeCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jPanel6, gridBagConstraints);

        subplotPanel.setLayout(new javax.swing.BoxLayout(subplotPanel, javax.swing.BoxLayout.Y_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(subplotPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanel3.add(jPanel1, gridBagConstraints);

        jScrollPane3.setViewportView(jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane3, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
 
    private void modelTypeComboBoxFocusGained(java.awt.event.FocusEvent evt)
    {//GEN-FIRST:event_modelTypeComboBoxFocusGained
        logger.entering("" + this.getClass(), "modelTypeComboBoxFocusGained");
    }//GEN-LAST:event_modelTypeComboBoxFocusGained

    private void modelListMouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_modelListMouseClicked

        if (evt.getClickCount() == 2)
        {
            settingsViewer.showElement(modelList.getSelectedValue());
        }
    }//GEN-LAST:event_modelListMouseClicked

    private void fileListMouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_fileListMouseClicked
        if (evt.getClickCount() == 2)
        {
            settingsViewer.showElement(fileList.getSelectedValue());
        }
    }//GEN-LAST:event_fileListMouseClicked

    private void removeModelButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_removeModelButtonActionPerformed
        logger.entering("" + this.getClass(), "removeModelButtonActionPerformed");

        int[] indices = modelList.getSelectedIndices();

        for (int i = 0; i < indices.length; i++)
        {
            int indice = indices[i] - i;
            rootSettingsModel.removeModel(current, (Model)modelList.getSelectedValue());
        }
        removeModelButton.setEnabled(false);
        refresh();
    }//GEN-LAST:event_removeModelButtonActionPerformed

    private void modelListValueChanged(javax.swing.event.ListSelectionEvent evt)
    {//GEN-FIRST:event_modelListValueChanged

        if (modelList.getSelectedIndex() != -1)
        {
            removeModelButton.setEnabled(true);
        }
        else
        {
            removeModelButton.setEnabled(false);
        }
    }//GEN-LAST:event_modelListValueChanged

    private void addModelButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_addModelButtonActionPerformed
        logger.entering("" + this.getClass(), "addModelButtonActionPerformed");

        try
        {
            // Construct a new copy
            Model        selectedModel = (Model) modelTypeComboBox.getSelectedItem();
            Model        m;
            // Clone selected Model (this code could have been moved into model???
            StringWriter writer   = new StringWriter();
            selectedModel.marshal(writer);
            StringReader reader = new StringReader(writer.toString());
            m = Model.unmarshal(reader);

            rootSettingsModel.addModel(current, m);
            refresh();
        }
        catch (Exception e)
        {
            // this occurs when add button is pressed without selection
            logger.warning("No model selected");
        }
    }//GEN-LAST:event_addModelButtonActionPerformed
    
    private void normalizeCheckBoxActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_normalizeCheckBoxActionPerformed
        current.setNormalize(normalizeCheckBox.isSelected());
        // fire tree event to refresh
        settingsViewer.getSettingsModel().fireUpdate();
    }//GEN-LAST:event_normalizeCheckBoxActionPerformed

    class FileListCellRenderer extends JCheckBox implements ListCellRenderer
    {
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        public java.awt.Component getListCellRendererComponent(JList list, Object value, // value to display
            int index, // cell index
            boolean isSelected, // is the cell selected
            boolean cellHasFocus) // the list and the cell have the focus
        {
            setText(value.toString());
            setSelected(isSelected);
            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addModelButton;
    private javax.swing.JList fileList;
    private javax.swing.JComboBox identComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JList modelList;
    private javax.swing.JComboBox modelTypeComboBox;
    private javax.swing.JCheckBox normalizeCheckBox;
    private javax.swing.JButton removeModelButton;
    private javax.swing.JPanel subplotPanel;
    // End of variables declaration//GEN-END:variables
}
