/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.util.MimeType;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.nom.tam.fits.FitsException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class LoadDataFilesAction extends RegisteredAction implements TreeSelectionListener, ChangeListener {

    public final static String className = LoadDataFilesAction.class.getName();
    public final static String actionName = "loadDataFiles";
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    String lastDir = System.getProperty("user.home");
    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public LoadDataFilesAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering(className, "actionPerformed");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(MimeType.OIFITS.getFileFilter());
        // Set in previous load directory
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new java.io.File(lastDir));
        }

        // Open file chooser
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.io.File[] files = fileChooser.getSelectedFiles();
            for (int i = 0; i < files.length; i++) {
                java.io.File file = files[i];
                try {
                    settingsModel.addFile(file);
                } catch (IOException ex) {
                    MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
                } catch (FitsException ex) {
                    MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
                }
            }
            lastDir = files[0].getParent();
        }
    }

    /** Listen to the settings pane selection changes
     *
     * @param e ChangeEvent
     */
    public void stateChanged(ChangeEvent e) {
        settingsModel = mfgui.getSelectedSettings();
        if (settingsModel == null) {
            return;
        }
        if (!settingsModelListener.contains(settingsModel)) {
            settingsModel.addTreeSelectionListener(this);
        }
    }

    /** Listen to the settings tree selection changes
     *
     * @param e TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        if (e.getSource() instanceof SettingsModel) {
            settingsModel = (SettingsModel) e.getSource();
        } else {
            logger.warning("dropped treeSelectionEvent from " + e.getSource());
        }
    }
}
