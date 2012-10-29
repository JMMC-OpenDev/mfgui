/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.PreferencesView;
import java.awt.event.ActionEvent;

public class ShowPrefAction extends RegisteredAction {

    final static String className = ShowPrefAction.class.getName();
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
    }
}
