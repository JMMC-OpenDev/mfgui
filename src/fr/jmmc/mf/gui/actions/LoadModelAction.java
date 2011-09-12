/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.mf.gui.*;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import fr.jmmc.jmcs.util.MimeType;

public class LoadModelAction extends RegisteredAction {

    /** Class name. This name is used to register to the ActionRegistrar */
    public final static String className = LoadModelAction.class.getName();
    /** Action name. This name is used to register to the ActionRegistrar */
    public final static String actionName = "loadModel";
    /** Class logger */
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    public String lastDir = System.getProperty("user.home");
    MFGui mfgui;

    public LoadModelAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui = mfgui;
        // register as default open action and remove text to keep small icons in the toolbar
        flagAsOpenAction();
        putValue(LoadModelAction.NAME, null);
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering(className, "actionPerformed");

        // Retrieve file given by App startup or open a filechooser 
        File file = null;

        // If the action was automatically triggered from App launch
        if (e.getSource() == ActionRegistrar.getInstance()) {
            // Open given file
            file = new File(e.getActionCommand());
        } else // User clicked the menu item
        {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(MimeType.LITPRO_SETTINGS.getFileFilter());
            // Set in previous load directory
            if (lastDir != null) {
                fileChooser.setCurrentDirectory(new File(lastDir));
            }
            // Open file chooser
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }
        }

        if (file != null) {
            lastDir = file.getParent();
            mfgui.addSettings(new SettingsModel(file));
            logger.info("Loading '" + file.getName() + "' setting file");
        }
    }
}
