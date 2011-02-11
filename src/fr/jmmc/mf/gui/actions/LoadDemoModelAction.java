package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class LoadDemoModelAction extends RegisteredAction {

    final static String className = "fr.jmmc.mf.gui.actions.LoadDemoModelAction";
    /** Class logger */
    final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    String demoURL_ = "";
    MFGui mfgui;

    public LoadDemoModelAction(String id, String demoURL, String demoDesc, MFGui mfgui) {
        super(className, id, demoDesc);
        this.mfgui = mfgui;
        this.putValue(Action.NAME, demoDesc);
        demoURL_ = demoURL;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        mfgui.addSettings(new SettingsModel(demoURL_));
    }
}
