package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import java.awt.event.ActionEvent;

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
            String v;
            v = UtilsClass.getOutputMsg(ModelFitting.execMethod(methodName, null));
            fr.jmmc.jmcs.gui.StatusBar.show("Yoga version is \'" + v.trim() + "\'");        
    }
}
