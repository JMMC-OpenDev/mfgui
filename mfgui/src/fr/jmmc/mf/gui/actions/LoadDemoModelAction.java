package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.JOptionPane;

public class LoadDemoModelAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.LoadDemoModelAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    String demoURL_ = "";
    MFGui mfgui;

    public LoadDemoModelAction(String id, String demoURL, String demoDesc, MFGui mfgui) {
        super(className, id, demoDesc);
        this.mfgui = mfgui;
        this.putValue(Action.NAME, demoDesc);
        demoURL_ = demoURL;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        try {
            mfgui.addSettings(new SettingsModel(new URL(demoURL_)));
        } catch (UnknownHostException ex) {
            String msg = "Network seems down. Can\'t contact host " + ex.getMessage();
            JOptionPane.showMessageDialog(null, msg, "Error ", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, ex.getMessage(), ex);
            fr.jmmc.mcs.gui.StatusBar.show("Error recovering demo setting");
            return;
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
        fr.jmmc.mcs.gui.StatusBar.show("New model ready for modifications");
    }
}
