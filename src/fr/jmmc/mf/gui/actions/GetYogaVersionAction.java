/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.mf.LITpro;
import fr.jmmc.mf.gui.*;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

public class GetYogaVersionAction extends RegisteredAction {

    final static String className = GetYogaVersionAction.class.getName();
    final static String methodName = "getYogaVersion";
    MFGui mfgui;

    public GetYogaVersionAction(MFGui mfgui) {
        super(className, methodName);
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        String v=null;
        try {
            v = UtilsClass.getOutputMsg(LITpro.execMethod(methodName, null));
        } catch (ExecutionException ex) {
            MessagePane.showErrorMessage("Can't get yoga version", ex);
            return;
        }
        fr.jmmc.jmcs.gui.component.StatusBar.show("Yoga version is \'" + v.trim() + "\'");
    }
}
