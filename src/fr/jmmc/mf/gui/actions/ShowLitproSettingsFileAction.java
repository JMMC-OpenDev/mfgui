package fr.jmmc.mf.gui.actions;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class ShowLitproSettingsFileAction extends RegisteredAction implements TreeSelectionListener, ChangeListener{

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
            JTextArea msg = new JTextArea(settingsModel.toLITproDesc(),20,80);
            JScrollPane pane = new JScrollPane(msg);
            JOptionPane.showMessageDialog(null, pane, "Here comes the fresh associated LITpro desc file", JOptionPane.INFORMATION_MESSAGE);
        }catch(Exception ex){
            new FeedbackReport(ex);
        }
    }

    /** Listen to the settings pane selection changes
     *
     * @param e ChangeEvent
     */
    public void stateChanged(ChangeEvent e) {
        settingsModel=mfgui.getSelectedSettings();
        this.setEnabled(settingsModel.isValid());
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
            this.setEnabled(settingsModel.isValid());
        }else{
            logger.warning("dropped treeSelectionEvent from "+e.getSource());
        }
    }
}
