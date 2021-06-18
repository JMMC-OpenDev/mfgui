/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewModelAction extends RegisteredAction {

    private final static String className = NewModelAction.class.getName();
    /** Class logger */
    static final Logger logger = LoggerFactory.getLogger(className);
    public String lastDir = System.getProperty("user.home");
    MFGui mfgui;

    public NewModelAction(MFGui mfgui) {
        super(className, "newModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            mfgui.addSettings(new SettingsModel());
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Can't build a new model", ex);
        } catch (ExecutionException ex) {
            MessagePane.showErrorMessage("Can't build a new model", ex);
            return;
        }
        logger.info("New settings created and loaded into the GUI");
        fr.jmmc.jmcs.gui.component.StatusBar.show("New model ready for modifications! Please load oifits files, select target to fit and build your model.");
    }
}
