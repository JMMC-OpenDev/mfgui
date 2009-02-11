package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

public class LoadModelAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.LoadModelAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    public String lastDir = System.getProperty("user.home");
    MFGui mfgui;

    public LoadModelAction(MFGui mfgui) {
        super(className, "loadModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        try {
            JFileChooser fileChooser = new JFileChooser();
            // Set in previous load directory
            if (lastDir != null) {
                fileChooser.setCurrentDirectory(new File(lastDir));
            }
            // Open file chooser
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                lastDir = file.getParent();
                mfgui.addSettings(new SettingsModel(file));
            }
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }
}
