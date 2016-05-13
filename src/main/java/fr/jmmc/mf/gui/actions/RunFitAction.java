/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.ResourcedAction;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.task.Task;
import fr.jmmc.jmcs.gui.task.TaskSwingWorker;
import fr.jmmc.jmcs.gui.task.TaskSwingWorkerExecutor;
import fr.jmmc.mf.LITpro;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Message;
import fr.jmmc.mf.models.Response;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunFitAction extends ResourcedAction {

    final SettingsModel settingsModel;

    /** Main logger */
    static final Logger logger = LoggerFactory.getLogger(RunFitAction.class.getName());

    static final String methodName = "runFit";

    ButtonModel iTMaxButtonModel = null;
    Document iTMaxDocument = null;
    ButtonModel skipPlotDocument = null;

    String initialActionName = null;
    //boolean running = false;
    private Task task;

    public RunFitAction(SettingsModel settingsModel) {
        super("runFit");
        this.settingsModel = settingsModel;
        initialActionName = (String) this.getValue(Action.NAME);
        task = new Task("runFit_" + settingsModel.hashCode());
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

        if (settingsModel.isRunning()) {
            // cancel job
            TaskSwingWorkerExecutor.cancelTask(getTask());
            settingsModel.setRunning(false);
        } else {
            // display non writable panel and launch new job
            settingsModel.selectInTree(settingsModel.getRootSettings().getResults());

            StringBuffer args = new StringBuffer();
            if (shouldSkipPlots()) {
                args.append("-s ");
            }

            if (iTMaxButtonModel.isSelected() && (iTMaxDocument.getLength() > 0)) {
                try {
                    args.append("itmax=").append(iTMaxDocument.getText(0, iTMaxDocument.getLength()));
                } catch (BadLocationException ble) {
                    throw new IllegalStateException("Can't read ITMax document", ble);
                }
            }

            // change model state to lock it and extract its snapshot
            settingsModel.setRunning(true);
            File tmpFile = settingsModel.getTempFile(false);
            StatusBar.show("Running fitting process of" + settingsModel.getAssociatedFilename());

            new RunFitActionWorker(getTask(), tmpFile, args.toString(), settingsModel).executeTask();
        }
    }

    public Task getTask() {
        return task;
    }

    public String getInitialActionName() {
        return initialActionName;
    }

    static class RunFitActionWorker extends TaskSwingWorker<Response> {

        final java.io.File xmlFile;
        final String methodArg;
        final SettingsModel parent;

        public RunFitActionWorker(Task task, java.io.File xmlFile, String methodArg, SettingsModel sm) {
            super(task);
            this.xmlFile = xmlFile;
            this.methodArg = methodArg;
            this.parent = sm; // only for callback
        }

        @Override
        public Response computeInBackground() {

            if (false) {
                logger.info("skip remote call for testing purpose. Just loop for pause");
                try {
                    for (int i = 0; i < 200; i++) {
                        Thread.sleep(100);
                        System.out.print(" " + i);
                        System.out.flush();
                    }                    
                } catch (InterruptedException ex) {
                    logger.info("interruped during loop", ex);
                }
                Message m = new Message();
                m.setContent("test");
                m.setType("ERROR");
                Response r = new Response();
                r.addMessage(m);
                return r;
            }

            try {
                return LITpro.execMethod(methodName, xmlFile, methodArg);
            } catch (IOException ex) {
                // should only come from http io execption 
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void refreshUI(Response r) {
            // action finished, we can change state and update model just after.
            this.parent.setRunning(false);
            this.parent.updateWithNewSettings(r);
            StatusBar.show("GUI updated with fitting results");
        }

        @Override
        public void handleException(ExecutionException ee) {
            // notify that process is finished
            this.parent.setRunning(false);

            // filter some exceptions to avoid feedback report
            if (filter(ee)) {
                MessagePane.showErrorMessage("Please check your network setup", ee);
            } else {
                super.handleException(ee);
            }

            StatusBar.show("Error occured during fitting process");
        }

        public boolean filter(Exception e) {
            Throwable c = e.getCause();
            return c != null
                    && (c instanceof UnknownHostException
                    || c instanceof ConnectTimeoutException);
        }
    }
}
