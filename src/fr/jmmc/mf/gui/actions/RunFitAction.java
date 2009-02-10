package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.util.MCSAction;
import fr.jmmc.mf.models.Oitarget;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Settings;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.UnknownHostException;
import java.util.logging.Level;
import javax.swing.ButtonModel;
import javax.swing.JOptionPane;
import javax.swing.text.Document;

public class RunFitAction extends MCSAction {
    /** Main logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.actions.RunFitAction");
    String methodName = "runFit";
    ButtonModel iTMaxButtonModel = null;
    Document iTMaxDocument = null;
    SettingsViewerInterface settingsViewer=null;

    public RunFitAction(SettingsViewerInterface settingsViewer) {
        super("runFit");
        this.settingsViewer =settingsViewer;
    }

    public void setConstraints(ButtonModel iTMaxButtonModel, Document iTMaxDocument) {
        this.iTMaxButtonModel = iTMaxButtonModel;
        this.iTMaxDocument = iTMaxDocument;
    }

    public void actionPerformed(ActionEvent e) {
        //@todo add support of ITMax optionnal parameter

        String args="";
        if (iTMaxButtonModel.isSelected() && (iTMaxDocument.getLength() > 0)) {
            try {
                args = "itmax=" + iTMaxDocument.getText(0, iTMaxDocument.getLength());
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Can't read ITMax document",ex);
            }
        }

        SettingsModel settingsModel = settingsViewer.getSettingsModel();
        logger.fine("Requesting yoga \'" + methodName + "\' call");
        StatusBar.show("Running fitting process");
        //@todo remettre le curseur setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            File tmpFile = settingsModel.getTempFile(false);
            Response r = ModelFitting.execMethod(methodName, tmpFile, args);
            StatusBar.show("Fitting process finished");
            Settings newModel = UtilsClass.getSettings(r);
            if (newModel == null) {
                logger.warning("no settings present in result message");
                if (UtilsClass.getErrorMsg(r).length() == 0) {
                    new FeedbackReport(new Exception("Sorry a problem occured on server side without error message.\nNo result has been returned\n Please send this bug report and you will be contacted if the data are needed to repeat the problem\nBest regards,"));
                }
                //setCursor(null);
                return;
            }
            Settings prevModel = settingsModel.getRootSettings();
            // add href from previous files
            fr.jmmc.mf.models.File[] newFiles = newModel.getFiles().getFile();
            fr.jmmc.mf.models.File[] prevFiles = prevModel.getFiles().getFile();
            // no check is done to assert that every newFile have been updated
            for (int i = 0; i < newFiles.length; i++) {
                fr.jmmc.mf.models.File newFile = newFiles[i];
                String newId = newFile.getId();
                for (int j = 0; j < prevFiles.length; j++) {
                    fr.jmmc.mf.models.File prevFile = prevFiles[j];
                    String prevId = prevFile.getId();
                    if (prevId.equals(newId)) {
                        newFile.setHref(prevFile.getHref());
                        Oitarget[] oitargets = prevFile.getOitarget();
                        for (int k = 0; k < oitargets.length; k++) {
                            newFile.addOitarget(oitargets[k]);
                        }
                    }
                }
            }
            settingsModel.setRootSettings(newModel);
            settingsModel.setLastXml(ModelFitting.getLastXmlResult());
            logger.info("Settings created");
            settingsViewer.genResultReport(settingsModel,r);
        } catch (UnknownHostException ex) {
            String msg = "Network seems down. Can\'t contact host " + ex.getMessage();
            JOptionPane.showMessageDialog(null, msg, "Error ", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, ex.getMessage(), ex);
            StatusBar.show("Error during process of " + methodName);
            //setCursor(null);
            return;
        } catch (Exception ex) {
            logger.warning(ex.getClass().getName() + " " + ex.getMessage());
            StatusBar.show("Error during fitting process");
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error ", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, ex.getMessage(), ex);
            //setCursor(null);
            return;
        }
        //setCursor(null);
    }
}
