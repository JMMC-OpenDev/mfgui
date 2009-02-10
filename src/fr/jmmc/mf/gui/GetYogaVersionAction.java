package fr.jmmc.mf.gui;

import fr.jmmc.mcs.util.RegisteredAction;
import java.awt.event.ActionEvent;

class GetYogaVersionAction extends RegisteredAction {

    String methodName = "getYogaVersion";
    MFGui outer;

    public GetYogaVersionAction(MFGui outer) {
        super(outer.className_, "getYogaVersion");
        this.outer = outer;
    }

    public void actionPerformed(ActionEvent e) {
        outer.logger.fine("Requesting yoga \'" + methodName + "\' call");
        try {
            String v;
            v = UtilsClass.getOutputMsg(ModelFitting.execMethod(methodName, null));
            outer.setStatus("Yoga version is \'" + v.trim() + "\'");
        } catch (Exception ex) {
            outer.logger.warning(ex.getClass().getName() + " " + ex.getMessage());
            outer.setStatus("Can\'t get Yoga version");
        }
    }
}
