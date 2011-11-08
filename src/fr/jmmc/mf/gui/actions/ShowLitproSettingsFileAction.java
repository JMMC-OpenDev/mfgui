/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.FeedbackReport;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class ShowLitproSettingsFileAction extends RegisteredAction implements TreeModelListener, TreeSelectionListener, ChangeListener{

    public final static String className = "fr.jmmc.mf.gui.actions.ShowLitproSettingsFileAction";
    public final static String actionName = "showLitproSettingsFile";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);

    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public ShowLitproSettingsFileAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui=mfgui;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try{
            JTextArea msg = new JTextArea(settingsModel.toLITproDesc(),40,60);
            JScrollPane pane = new JScrollPane(msg);
            JOptionPane.showMessageDialog(null, pane, "LITpro settings file", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex){
            FeedbackReport.openDialog(ex);
        }
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
            logger.warning("dropped treeSelectionEvent from "+e.getSource());
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
