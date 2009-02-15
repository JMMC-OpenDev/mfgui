package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import java.awt.event.ActionEvent;

public class GetYogaVersionAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.GetYogaVersionAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    String methodName = "getYogaVersion";
    MFGui mfgui;

    public GetYogaVersionAction(MFGui mfgui) {
        super(className, "getYogaVersion");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.fine("Requesting yoga \'" + methodName + "\' call");
        try {
            String v;
            v = UtilsClass.getOutputMsg(ModelFitting.execMethod(methodName, null));
            fr.jmmc.mcs.gui.StatusBar.show("Yoga version is \'" + v.trim() + "\'");
        } catch (Exception ex) {
            logger.warning(ex.getClass().getName() + " " + ex.getMessage());
            fr.jmmc.mcs.gui.StatusBar.show("Can\'t get Yoga version");
        }
    }
}