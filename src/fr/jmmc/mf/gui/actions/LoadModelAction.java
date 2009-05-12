package fr.jmmc.mf.gui.actions;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class LoadModelAction extends RegisteredAction {
    /** Class name. This name is used to register to the ActionRegistrar */
    public final static String classPath = "fr.jmmc.mf.gui.actions.LoadModelAction";
    /** Action name. This name is used to register to the ActionRegistrar */
    public final static String actionName = "loadModel";

    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            classPath);
    public String lastDir = System.getProperty("user.home");
    MFGui mfgui;

    public LoadModelAction(MFGui mfgui) {
        super(classPath, actionName);
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering("" + this.getClass(), "actionPerformed");
        //try {
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
            try {
                mfgui.addSettings(new SettingsModel(file));
            } catch (Exception ex) {
                String msg = "Can't read '"+file.getName()+"' file. Please load one modelfitting settings file.\n\n(" +
                        ex.getMessage() + ")";
                JOptionPane.showMessageDialog(null, msg, "Error ", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.WARNING, "Can't load settings with selected file", ex);
            }
        }
    }
}