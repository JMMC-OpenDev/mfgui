/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.ResourcedAction;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.actions.LoadDataFilesAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Files;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesPanel extends javax.swing.JPanel {

    static Logger logger = LoggerFactory.getLogger(FilesPanel.class.getName());
    static Files current = null;
    SettingsViewerInterface settingsViewer = null;
    public static Action loadFilesAction;
    public static Action loadRemoteFilesAction;
    SettingsModel rootSettingsModel = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFileButton;
    private javax.swing.JButton addFileButton1;
    private javax.swing.JList fileList;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    /** Creates new form FilesPanel */
    public FilesPanel(SettingsViewerInterface viewer) {
        settingsViewer = viewer;
        loadFilesAction = ActionRegistrar.getInstance().get(LoadDataFilesAction.className, LoadDataFilesAction.actionName);
        loadRemoteFilesAction = new LoadRemoteDataFilesAction();
        initComponents();
        jButton1.setAction(new ShowHelpAction("_Load_Oifile_Bt_ou_Area"));
        addFileButton.setAction(loadFilesAction);
        addFileButton1.setAction(loadRemoteFilesAction);
    }

    public void show(Files f, SettingsModel s) {
        current = f;
        rootSettingsModel = s;
        logger.debug("Showing list of {} files", current.getFileCount());

        // update model because it could have been modified
        fileList.setModel(s.allFilesListModel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        addFileButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        addFileButton1 = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Oifile list"));
        setLayout(new java.awt.GridBagLayout());

        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(fileList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        addFileButton.setAction(loadFilesAction);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addFileButton, gridBagConstraints);

        jButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(jButton1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addFileButton1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void fileListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            if (fileList.getSelectedValue() == null) {
                loadFilesAction.actionPerformed(null);
            } else {
                rootSettingsModel.setSelectionPath(
                        new TreePath(new Object[]{
                            rootSettingsModel,
                            current,
                            fileList.getSelectedValue()}));
            }
        }
    } // </editor-fold>       

    protected class LoadRemoteDataFilesAction extends ResourcedAction {

        public final static String actionName = "loadRemoteDataFiles";

        public LoadRemoteDataFilesAction() {
            super(actionName);
        }

        public void actionPerformed(ActionEvent e) {
            MessagePane.showMessage("To import a remote oifits file using the VO:\n - visit http://jmmc.fr/oidb\n - press the 'register' button\n - choose 'broadcast' on the rigth data file.");
        }
    }
}
