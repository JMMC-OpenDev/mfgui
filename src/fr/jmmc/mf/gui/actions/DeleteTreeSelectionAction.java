package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class DeleteTreeSelectionAction extends RegisteredAction implements TreeSelectionListener, ChangeListener{

    private final static String className = "fr.jmmc.mf.gui.actions.DeleteTreeSelectionAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);

    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public DeleteTreeSelectionAction(MFGui mfgui) {
        super(className, "deleteTreeSelection");
        this.mfgui=mfgui;
        // action should be enabled in the future
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        settingsModel.removeTreeSelection();
    }

    /** Listen to the settings pane selection changes
     *
     * @param e ChangeEvent
     */
    public void stateChanged(ChangeEvent e) {
        settingsModel=mfgui.getSelectedSettings();
        if(settingsModel==null){
            return;
        }
        if(!settingsModelListener.contains(settingsModel)){
            settingsModel.addTreeSelectionListener(this);
        }        
    }

    /** Listen to the settings tree selection changes
     *
     * @param e TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        setEnabled(false);
        if (e.getSource() instanceof SettingsModel){
            settingsModel = (SettingsModel)e.getSource();
            this.setEnabled(settingsModel.isSelectionRemovable());
        }else{
            logger.warning("dropped treeSelectionEvent from "+e.getSource());
        }
    }
}
