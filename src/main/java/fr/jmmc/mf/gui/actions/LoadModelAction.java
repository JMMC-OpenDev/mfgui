/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.data.MimeType;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.FileChooser;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.nom.tam.fits.FitsException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadModelAction extends RegisteredAction {

    /** Class name. This name is used to register to the ActionRegistrar */
    public final static String className = LoadModelAction.class.getName();
    /** Action name. This name is used to register to the ActionRegistrar */
    public final static String actionName = "loadModel";
    /** Class logger */
    static final Logger logger = LoggerFactory.getLogger(
            className);
    MFGui mfgui;

    public LoadModelAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui = mfgui;
        // register as default open action and remove text to keep small icons in the toolbar
        flagAsOpenAction();
        putValue(LoadModelAction.NAME, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Retrieve file given by App startup or open a filechooser
        final File file;

        // If the action was automatically triggered from App launch
        if (e.getSource() == ActionRegistrar.getInstance()) {
            // Open given file
            file = new File(e.getActionCommand());
        } else // User clicked the menu item
        {
            file = FileChooser.showOpenFileChooser("Load settings", null, MimeType.LITPRO_SETTINGS, null);
        }

        // Finally try to load file
        if (file != null) {
            try {
                logger.info("Loading '{}' setting file", file.getName());
                mfgui.addSettings(new SettingsModel(file));
            } catch (IOException ex) {
                MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
            } catch (ExecutionException ex) {
                MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
            } catch (FitsException ex) {
                MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
            }
        }

    }
}
