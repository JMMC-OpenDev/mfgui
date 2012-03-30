/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.MessagePane;
import fr.jmmc.mf.ModelFitting;
import fr.jmmc.mf.gui.*;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

public class GetYogaVersionAction extends RegisteredAction {

    final static String className = GetYogaVersionAction.class.getName();
    /** Class logger */
    final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    String methodName = "getYogaVersion";
    MFGui mfgui;

    public GetYogaVersionAction(MFGui mfgui) {
        super(className, "getYogaVersion");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.fine("Requesting yoga \'" + methodName + "\' call");
        String v=null;
        try {
            v = UtilsClass.getOutputMsg(ModelFitting.execMethod(methodName, null));
        } catch (ExecutionException ex) {
            MessagePane.showErrorMessage("Can't get yoga version", ex);
            return;
        }
        fr.jmmc.jmcs.gui.component.StatusBar.show("Yoga version is \'" + v.trim() + "\'");
    }
}
