/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.LITpro;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.*;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.action.MCSAction;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Settings;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunFitAction extends MCSAction {
    
    /** Main logger */
    static Logger logger = LoggerFactory.getLogger(RunFitAction.class.getName());
    
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
        
        
        Response r;
        try {
            r = LITpro.execMethod(methodName, tmpFile, args);
        } catch (IllegalStateException ise) {
            MessagePane.showErrorMessage("Can't perform operation for " + methodName,ise);
            return;
        } catch (ExecutionException ex) {
            MessagePane.showErrorMessage("Can't perform operation for " + methodName,ex);
            return;
        }
        
        Settings newModel = UtilsClass.getSettings(r);
        StatusBar.show("Fitting response received, creating result node...");
        if (newModel == null) {
            logger.warn("no settings present in result message");
            if (UtilsClass.getErrorMsg(r).length() == 0) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Sorry a problem occured on server side without error message.\n");
                sb.append("No result has been returned\n");
                sb.append("Please send this bug report (with email)");
                sb.append("and you will be contacted if the data are needed to repeat the problem");                                
                logger.warn(settingsModel.toLITproDesc());
                logger.warn(Preferences.getInstance().dumpCurrentProperties());
                throw new IllegalStateException(sb.toString());
            }
            //setCursor(null);
            return;
        }
        settingsModel.updateWithNewSettings(r);
        StatusBar.show("GUI updated with fitting results");

    //setCursor(null);
    }
}
