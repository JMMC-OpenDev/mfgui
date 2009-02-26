package fr.jmmc.mf.gui.actions;

import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class ShowLitproSettingsFileAction extends RegisteredAction implements TreeSelectionListener, ChangeListener{

    private final static String className = "fr.jmmc.mf.gui.actions.ShowLitproSettingsFileAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);

    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public ShowLitproSettingsFileAction(MFGui mfgui) {
        super(className, "show");
        this.mfgui=mfgui;
        // action should be enabled in the future
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        settingsModel.addLITproSettings();
        System.out.println("We should remove the selection on :"+settingsModel);
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
        if (e.getSource() instanceof SettingsModel){
            System.out.println("Selection has changed on jtree for:"+e.getSource());
            settingsModel = (SettingsModel)e.getSource();
            // @todo test what is the selection and accept or not the remove action
            this.setEnabled(settingsModel.isValid());
        }else{
            logger.warning("dropped treeSelectionEvent from "+e.getSource());
        }
    }


}