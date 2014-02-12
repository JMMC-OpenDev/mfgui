/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowLitproSettingsFileAction extends RegisteredAction implements TreeModelListener, TreeSelectionListener, ChangeListener{

    public final static String className = ShowLitproSettingsFileAction.class.getName();
    public final static String actionName = "showLitproSettingsFile";
    /** Class logger */
    static Logger logger = LoggerFactory.getLogger(className);

    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public ShowLitproSettingsFileAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui=mfgui;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        // Get yorick code
        final StringBuffer yorickSettings = new StringBuffer(settingsModel.toLITproDesc());
        
        // Copy it into the clipboard
        StringSelection ss = new StringSelection(yorickSettings.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        
        // and display into one message pane
        yorickSettings.append("---\nPrevious content has been copied into your clipboard");
        MessagePane.showMessage(yorickSettings.toString(), "LITpro settings file");
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
        this.setEnabled(settingsModel.isValid());
        if(!settingsModelListener.contains(settingsModel)){
            settingsModel.addTreeSelectionListener(this);
            settingsModel.addTreeModelListener(this);
        }
    }

    private void checkSettings(){
        this.setEnabled(settingsModel.isValid());
    }

    /** Listen to the settings tree selection changes
     *
     * @param e TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        setEnabled(false);
        if (e.getSource() instanceof SettingsModel){
            settingsModel = (SettingsModel)e.getSource();
            checkSettings();
        }else{
            logger.warn("dropped treeSelectionEvent from {}", e.getSource());
        }
    }

    public void treeNodesChanged(TreeModelEvent e) {
        checkSettings();
    }

    public void treeNodesInserted(TreeModelEvent e) {
        checkSettings();
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        checkSettings();
    }

    public void treeStructureChanged(TreeModelEvent e) {
        checkSettings();
    }
}
