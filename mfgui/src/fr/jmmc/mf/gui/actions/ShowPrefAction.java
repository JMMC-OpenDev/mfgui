package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import java.awt.event.ActionEvent;

public class ShowPrefAction extends RegisteredAction {

    final static String className = ShowPrefAction.class.getName();
    /** Class logger */
    final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    /** Preferences view */
    PreferencesView preferencesView;
    MFGui mfgui;

    public ShowPrefAction(MFGui mfgui) {
        super(className, "showPreferences");
        this.mfgui = mfgui;
        flagAsPreferenceAction();
        preferencesView = new PreferencesView();
    }

    public void actionPerformed(ActionEvent e) {
        preferencesView.setVisible(true);
        logger.fine("Showing preferences");
    }
}
