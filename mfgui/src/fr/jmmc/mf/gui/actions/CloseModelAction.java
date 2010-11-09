package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import java.awt.event.ActionEvent;

public class CloseModelAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.CloseModelAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    MFGui mfgui;

    public CloseModelAction(MFGui mfgui) {
        super(className, "closeModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        mfgui.closeSettings();
    }
}
