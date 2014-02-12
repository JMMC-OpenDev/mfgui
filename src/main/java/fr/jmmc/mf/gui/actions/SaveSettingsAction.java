/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.FileChooser;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.data.MimeType;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.io.File;

public class SaveSettingsAction extends RegisteredAction {

    static final String className = SaveSettingsAction.class.getName();
    private File lastDir = null;
    MFGui mfgui;

    public SaveSettingsAction(MFGui mfgui, String actionName) {
        super(className, actionName);
        this.mfgui = mfgui;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        // @todo replace next coede part by enable and disabled event asociated to actions...
        SettingsModel settingsModel = mfgui.getSelectedSettings();
        if (settingsModel == null) {
            return;
        }
                
        File file =  FileChooser.showSaveFileChooser(NAME, lastDir, MimeType.LITPRO_SETTINGS, settingsModel.getAssociatedFilename());                
        if(file==null){
            return;
        }
        // Store directory for next time
        lastDir = file.getParentFile();       
        
        // Save model on disk
        settingsModel.saveSettingsFile(file);
        
        // Fix user associated file and save it with result
        settingsModel.setAssociatedFile(file, true);
        
        /* Ask to update title */
        mfgui.getSelectedSettings();
        StatusBar.show("Settings stored into '" + file.getName() + "'");
    }
}
