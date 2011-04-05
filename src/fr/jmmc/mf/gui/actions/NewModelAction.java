package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;

public class NewModelAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.NewModelAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    public String lastDir = System.getProperty("user.home");
    MFGui mfgui;

    public NewModelAction(MFGui mfgui) {
        super(className, "newModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering(className, "actionPerformed");
        mfgui.addSettings(new SettingsModel());
        fr.jmmc.mcs.gui.StatusBar.show("New model ready for modifications");
    }
}
