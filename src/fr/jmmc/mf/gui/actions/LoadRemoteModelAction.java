package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

public class LoadRemoteModelAction extends RegisteredAction {

    final static String className = LoadRemoteModelAction.class.getName();   
    final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    public String lastURL = "";
    MFGui mfgui;

    public LoadRemoteModelAction(MFGui mfgui) {
        super(className, "loadRemoteModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        String url = (String) JOptionPane.showInputDialog("Enter URL of remote setting:", lastURL);
        mfgui.addSettings(new SettingsModel(url));
        lastURL = url;
    }
}
