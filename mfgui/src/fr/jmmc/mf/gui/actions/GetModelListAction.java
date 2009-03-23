package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.MCSAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Response;
import java.awt.event.ActionEvent;

public class GetModelListAction extends MCSAction {

    private final static String className = "fr.jmmc.mf.gui.actions.GetModelListAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    String methodName = "getModelList";
    SettingsModel settingsModel;

    public GetModelListAction(SettingsModel settingsModel) {
        super("getModelList");
        this.settingsModel = settingsModel;
    }

    public void actionPerformed(ActionEvent e) {
        logger.fine("Requesting yoga \'" + methodName + "\' call");
        try {
            Response r = ModelFitting.execMethod(methodName, null);
            // Search model into return result
            Model newModel = UtilsClass.getModel(r);
            // Indicates to the rootSettingsModel list of availables models
            settingsModel.setSupportedModels(newModel.getModel());
        } catch (Exception exc) {
            new FeedbackReport(exc);
        }
    }
}
