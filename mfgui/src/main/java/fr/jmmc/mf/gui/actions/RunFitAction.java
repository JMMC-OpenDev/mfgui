/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.ResourcedAction;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.task.Task;
import fr.jmmc.jmcs.gui.task.TaskSwingWorker;
import fr.jmmc.mf.LITpro;
import fr.jmmc.mf.gui.Preferences;
import fr.jmmc.mf.gui.SettingsViewerInterface;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Message;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.ResponseItem;
import fr.jmmc.mf.models.Settings;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunFitAction extends ResourcedAction {

    /** Main logger */
    static Logger logger = LoggerFactory.getLogger(RunFitAction.class.getName());

    String methodName = "runFit";
    ButtonModel iTMaxButtonModel = null;
    Document iTMaxDocument = null;
    ButtonModel skipPlotDocument = null;
    SettingsViewerInterface settingsViewer = null;
    TaskSwingWorker<Response> worker = null;
    Task task = new Task("task_RunFitAction");
    String initialName = null;

    public RunFitAction(SettingsViewerInterface settingsViewer) {
        super("runFit");
        this.settingsViewer = settingsViewer;
        worker = new RunFitActionWorker(this);
        initialName = (String) this.getValue(Action.NAME);
    }

    public void setConstraints(ButtonModel iTMaxButtonModel, Document iTMaxDocument, ButtonModel skipPlotDocument) {
        this.iTMaxButtonModel = iTMaxButtonModel;
        this.iTMaxDocument = iTMaxDocument;
        this.skipPlotDocument = skipPlotDocument;
    }

    public boolean shouldSkipPlots() {
        // this dummy test could be replaced by a more sophisticated rule eg: 
        // max data threshold ...
        // could also be a preference...
        return skipPlotDocument.isSelected();
    }

    public void actionPerformed(ActionEvent e) {
        final SwingWorker.StateValue state = worker.getState();
        if (state == SwingWorker.StateValue.PENDING) {
            worker.executeTask();
        } else if (state == SwingWorker.StateValue.DONE) {
            worker = new RunFitActionWorker(this);
            worker.executeTask();
        } else {
            // interrup if running because network connection can have long duration
            // TODO handle cancel signal in callee code for data integrity and memory leak
            // TODO check behaviour of remote service
            // good news, the 'context' is  insulated through a tmp file
            // killer option would be to set the LITpro web service asynchronous and interruptible.
            worker.cancel(true);
        }
        updateUI();
    }

    /**
     * Helper function to update UI related elements.
     * The action name is set depending on the associated worker state to update
     * text of associated swing elements.
     * TODO update menu entry
     * update mouse icon ?
     */
    private void updateUI() {
        final SwingWorker.StateValue state = worker.getState();

        System.out.println("update for state = " + state );
        //setCursor(null);
        if (state == SwingWorker.StateValue.PENDING) {
            putValue(Action.NAME, "Cancel");
        } else if (state == SwingWorker.StateValue.DONE) {
            putValue(Action.NAME, initialName);
        } else {
            putValue(Action.NAME, initialName);
            //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }    

    class RunFitActionWorker extends TaskSwingWorker<Response> {

        RunFitAction parentAction = null;

        public RunFitActionWorker(RunFitAction parent) {
            super(task);
            parentAction = parent;
        }

        @Override
        public Response computeInBackground() {
            if (false) {
                logger.info("skip remote call for testing purpose. Just do a pause");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(RunFitAction.class.getName()).log(Level.SEVERE, null, ex);
            }
                updateUI();
                Message m = new Message();
                m.setContent("test");
                m.setType("ERROR");
                ResponseItem ri = new ResponseItem();
                ri.setMessage(m);                
                Response r = new Response();
                r.addResponseItem(ri);
                return r;
            }

            String args = "";
            if (shouldSkipPlots()) {
                args = "-s ";
            }

            if (iTMaxButtonModel.isSelected() && (iTMaxDocument.getLength() > 0)) {
                try {
                    args = args + "itmax=" + iTMaxDocument.getText(0, iTMaxDocument.getLength());
                } catch (BadLocationException ble) {
                    throw new IllegalStateException("Can't read ITMax document", ble);
                }
            }
            SettingsModel settingsModel = settingsViewer.getSettingsModel();

            File tmpFile = settingsModel.getTempFile(false);
            StatusBar.show("Running fitting process");

            Response r;
            try {
                r = LITpro.execMethod(methodName, tmpFile, args);
            } catch (IllegalStateException ise) {
                MessagePane.showErrorMessage("Can't perform operation for " + methodName, ise);
                return null;
            } catch (ExecutionException ex) {
                MessagePane.showErrorMessage("Can't perform operation for " + methodName, ex);
                return null;
            }

            return r;

        }

        @Override
        public void refreshUI(Response r) {

            SettingsModel settingsModel = settingsViewer.getSettingsModel();
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
                    updateUI();
                    throw new IllegalStateException(sb.toString());
                } else {
                    // TODO display error message ;
                }

                return;
            }
            // TODO display error message here : move code from LITpro.execMethod
            settingsModel.updateWithNewSettings(r);
            StatusBar.show("GUI updated with fitting results");
            updateUI();
        }
    }
}
