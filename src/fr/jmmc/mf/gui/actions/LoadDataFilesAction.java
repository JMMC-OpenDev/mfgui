package fr.jmmc.mf.gui.actions;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class LoadDataFilesAction extends RegisteredAction implements TreeSelectionListener, ChangeListener{
    public final static String className = "fr.jmmc.mf.gui.actions.LoadDataFileAction";
    public final static String actionName = "loadDataFiles";
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    String lastDir = System.getProperty("user.home");
    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public LoadDataFilesAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui=mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering(className, "actionPerformed");
            try
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);

                // Set in previous load directory
                if (lastDir != null)
                {
                    fileChooser.setCurrentDirectory(new java.io.File(lastDir));
                }

                // Open file chooser
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    java.io.File[] files = fileChooser.getSelectedFiles();

                    for (int i = 0; i < files.length; i++)
                    {
                        settingsModel.addFile(files[i]);
                    }

                    lastDir = files[0].getParent();
                }
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }

        settingsModel.toggleSelectedFrames();
    }

    /** Listen to the settings pane selection changes
     *
     * @param e ChangeEvent
     */
    public void stateChanged(ChangeEvent e) {
        settingsModel=mfgui.getSelectedSettings();
        if(!settingsModelListener.contains(settingsModel)){
            settingsModel.addTreeSelectionListener(this);
        }        
    }

    /** Listen to the settings tree selection changes
     *
     * @param e TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        this.setEnabled(false);
        if (e.getSource() instanceof SettingsModel){
            settingsModel = (SettingsModel)e.getSource();            
        }else{
            logger.warning("dropped treeSelectionEvent from "+e.getSource());
        }
    }
}
