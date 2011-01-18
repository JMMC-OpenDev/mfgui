package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.MimeType;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class SaveSettingsAction extends RegisteredAction {
    private final static String className="fr.jmmc.mf.gui.actions.SaveSettingsAction";
    /** Class logger */
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);

    private String lastDir = System.getProperty("user.home");
    MFGui mfgui;
    protected  static Preferences myPreferences = Preferences.getInstance();


    public SaveSettingsAction(MFGui mfgui,String actionName) {
        super(className, actionName);
        this.mfgui = mfgui;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        boolean saveResults = myPreferences.getPreferenceAsBoolean("save.results");
        try {
            // @todo replace next coede part by enable and disabled event asociated to actions...
            SettingsModel settingsModel=mfgui.getSelectedSettings();
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
            String res="";
            if (saveResults){
                res=" with results";
            }
            fileChooser.setDialogTitle("Save " + settingsModel.getAssociatedFilename() + res + "?");
            // Open filechooser
            int returnVal = fileChooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                if (file.exists()) {
                    String message = "File \'" + file.getName() + "\' already exists\nDo you want to overwrite this file?";
                    int answer = JOptionPane.showConfirmDialog(null, message);
                    if (answer != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            } else {
                return;
            }
            lastDir = file.getParent();
            // Fix user associated file and save it with result
            settingsModel.setAssociatedFile(file);
            settingsModel.saveSettingsFile(file, saveResults);
            /* ask to update title */
            mfgui.getSelectedSettings();
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }
}
