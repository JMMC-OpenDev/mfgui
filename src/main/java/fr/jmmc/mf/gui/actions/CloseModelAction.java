/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import java.awt.event.ActionEvent;

public class CloseModelAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.CloseModelAction";
    MFGui mfgui;

    public CloseModelAction(MFGui mfgui) {
        super(className, "closeModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        mfgui.closeSettings();
    }
}
