package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.JOptionPane;

public class LoadRemoteModelAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.LoadRemoteModelAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    public String lastURL = "";
    MFGui mfgui;

    public LoadRemoteModelAction(MFGui mfgui) {
        super(className, "loadRemoteModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        try {
            String s = (String) JOptionPane.showInputDialog("Enter URL of remote setting:", lastURL);
            if ((s != null) && (s.length() > 5)) {
                lastURL = s;
                URL url = new URL(s);
                mfgui.addSettings(new SettingsModel(url));
            }
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }
}
