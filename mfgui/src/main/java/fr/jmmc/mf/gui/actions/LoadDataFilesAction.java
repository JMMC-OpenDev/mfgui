/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.FileChooser;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.data.MimeType;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.nom.tam.fits.FitsException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadDataFilesAction extends RegisteredAction implements TreeSelectionListener, ChangeListener {

    public final static String className = LoadDataFilesAction.class.getName();
    public final static String actionName = "loadDataFiles";
    static final Logger logger = LoggerFactory.getLogger(className);
    java.io.File lastDir = null;
    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public LoadDataFilesAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        java.io.File[] files = FileChooser.showOpenFilesChooser(NAME, lastDir, MimeType.OIFITS);

        if (files == null) {
            return;
        }

        if (settingsModel == null) {
            try {
                settingsModel = new SettingsModel();
            } catch (ExecutionException ex) {
                return;
            }
            mfgui.addSettings(settingsModel);
        }

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
        lastDir = files[0].getParentFile();
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
            logger.warn("dropped treeSelectionEvent from {}", e.getSource());
        }
    }
}
