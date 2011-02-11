package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.MessagePane;
import fr.jmmc.mcs.util.MimeType;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

public class SaveSettingsAction extends RegisteredAction {

    static final String className = SaveSettingsAction.class.getName();
    /** Class logger */
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    private String lastDir = System.getProperty("user.home");
    MFGui mfgui;
    protected static Preferences myPreferences = Preferences.getInstance();

    public SaveSettingsAction(MFGui mfgui, String actionName) {
        super(className, actionName);
        this.mfgui = mfgui;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        boolean saveResults = myPreferences.getPreferenceAsBoolean("save.results");
        // @todo replace next coede part by enable and disabled event asociated to actions...
        SettingsModel settingsModel = mfgui.getSelectedSettings();
        if (settingsModel == null) {
            return;
        }
        File file = new File(settingsModel.getAssociatedFilename());
        // Open a filechooser in previous save directory
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(MimeType.LITPRO_SETTINGS.getFileFilter());
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        fileChooser.setSelectedFile(file);
        String res = "";
        if (saveResults) {
            res = " with results";
        }
        fileChooser.setDialogTitle("Save " + settingsModel.getAssociatedFilename() + res + "?");
        // Open filechooser
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        file = fileChooser.getSelectedFile();
        if (file.exists()) {
            if (!MessagePane.showConfirmFileOverwrite(file.getAbsolutePath())) {
                return;
            }
        }
        lastDir = file.getParent();
        // Fix user associated file and save it with result
        settingsModel.setAssociatedFile(file);
        settingsModel.saveSettingsFile(file, saveResults);
        /* ask to update title */
        mfgui.getSelectedSettings();
    }
}
