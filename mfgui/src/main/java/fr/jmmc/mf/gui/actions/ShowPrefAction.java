/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import fr.jmmc.mf.gui.PreferencesView;
import java.awt.event.ActionEvent;

/**
 * Display the (centered) preference window.
 * @author mella
 */
public class ShowPrefAction extends RegisteredAction {

    /** Class name */
    final static String className = ShowPrefAction.class.getName();
    /** Preferences view */
    PreferencesView preferencesView;

    /**
     * Create show preference action
     */
    public ShowPrefAction() {
        super(className, "showPreferences");
        flagAsPreferenceAction();
        preferencesView = new PreferencesView();
        WindowUtils.centerOnMainScreen(preferencesView);
    }

    public void actionPerformed(ActionEvent e) {
        preferencesView.setVisible(true);
    }
}
