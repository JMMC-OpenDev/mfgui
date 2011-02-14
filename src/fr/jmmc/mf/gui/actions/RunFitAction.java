package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.util.MCSAction;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Settings;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.UnknownHostException;
import java.util.logging.Level;
import javax.swing.ButtonModel;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class RunFitAction extends MCSAction {

    /** Main logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.actions.RunFitAction");
    String methodName = "runFit";
    ButtonModel iTMaxButtonModel = null;
    Document iTMaxDocument = null;
    SettingsViewerInterface settingsViewer = null;

    public RunFitAction(SettingsViewerInterface settingsViewer) {
        super("runFit");
        this.settingsViewer = settingsViewer;
    }

    public void setConstraints(ButtonModel iTMaxButtonModel, Document iTMaxDocument) {
        this.iTMaxButtonModel = iTMaxButtonModel;
        this.iTMaxDocument = iTMaxDocument;
    }

    public void actionPerformed(ActionEvent e) {
        String args = "";
        if (iTMaxButtonModel.isSelected() && (iTMaxDocument.getLength() > 0)) {
            try {
                args = "itmax=" + iTMaxDocument.getText(0, iTMaxDocument.getLength());
            } catch (BadLocationException ble) {
                throw new IllegalStateException("Can't read ITMax document", ble);
            }
        }
        SettingsModel settingsModel = settingsViewer.getSettingsModel();                

        //@todo remettre le curseur setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        File tmpFile = settingsModel.getTempFile(false);
        StatusBar.show("Running fitting process");
        Response r = ModelFitting.execMethod(methodName, tmpFile, args);
        Settings newModel = UtilsClass.getSettings(r);
        StatusBar.show("Fitting response received, creating result node...");
        if (newModel == null) {
            logger.warning("no settings present in result message");
            if (UtilsClass.getErrorMsg(r).length() == 0) {
                throw new IllegalStateException(
                        "Sorry a problem occured on server side without error message.\n"
                        +"No result has been returned\n"+
                        "Please send this bug report (with email)"
                        +" and you will be contacted if the data are needed to repeat the problem");
            }
            //setCursor(null);
            return;
        }
        settingsModel.updateWithNewSettings(r);
        StatusBar.show("GUI updated with fitting results");

    //setCursor(null);
    }
}
