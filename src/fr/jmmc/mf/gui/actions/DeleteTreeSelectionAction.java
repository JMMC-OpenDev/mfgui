/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTreeSelectionAction extends RegisteredAction implements TreeSelectionListener, ChangeListener{

    private final static String className = DeleteTreeSelectionAction.class.getName();
    /** Class logger */
    static Logger logger = LoggerFactory.getLogger(className);

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
            logger.warn("dropped treeSelectionEvent from {} ", e.getSource());
        }
    }
}
