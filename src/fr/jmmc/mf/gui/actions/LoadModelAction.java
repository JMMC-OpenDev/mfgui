package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import fr.jmmc.mcs.util.MimeType;

public class LoadModelAction extends RegisteredAction {

    /** Class name. This name is used to register to the ActionRegistrar */
    public final static String className = LoadModelAction.class.getName();
    /** Action name. This name is used to register to the ActionRegistrar */
    public final static String actionName = "loadModel";
    /** Class logger */
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    public String lastDir = System.getProperty("user.home");
    MFGui mfgui;

    public LoadModelAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        //try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(MimeType.LITPRO_SETTINGS.getFileFilter());
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
    }
}
