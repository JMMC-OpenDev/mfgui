/*
 * JMMC
 */
package fr.jmmc.mf.gui;


import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Settings;

/**
 * This Panel displays the main elements of a settings.
 */
public class SettingsPanel extends javax.swing.JPanel
{
    /** Class logger */
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            SettingsPanel.class.getName());
   
    private FilesPanel filesPanel = null;
    private TargetsPanel targetsPanel = null;
    private FitterPanel fitterPanel = null;
    private UserInfoPanel userInfoPanel = null;
    private Settings current = null;

    /** Creates new SettingslPanel */
    public SettingsPanel(FilesPanel fp, TargetsPanel tp, FitterPanel fitp, UserInfoPanel uip)
    {
        filesPanel        = fp;
        targetsPanel      = tp;
        fitterPanel       = fitp;
        userInfoPanel     = uip;
        initComponents();
    }

    /**
     * Show the given settings
     * @param s the settings to show
     * @param sm the assocaited settings model
     */
    public void show(Settings s, SettingsModel sm)
    {        
        current = s;

        // @todo check why next lines are required
        // !(( why component disapears???  does their parent change or???
        jSplitPane2.setTopComponent(filesPanel);

        if (current.getFiles().getFileCount() >= 1)
        {
            jSplitPane2.setBottomComponent(targetsPanel);
            jSplitPane3.setTopComponent(fitterPanel);
        }
        else
        {
            logger.finest("Current settings has no file");
            jSplitPane2.setBottomComponent(null);
            jSplitPane3.setTopComponent(null);
        }

        jSplitPane3.setBottomComponent(userInfoPanel);

        // Set File list
        filesPanel.show(current.getFiles(), sm);
        // Set Target List
        targetsPanel.show(current.getTargets(), sm);
        // Set fitter
        fitterPanel.show(current);
        // Set userInfo
        userInfoPanel.show(current);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Settings panel"));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jSplitPane1.setBorder(null);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.2);

        jSplitPane2.setBorder(null);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.2);
        jSplitPane1.setLeftComponent(jSplitPane2);

        jSplitPane3.setBorder(null);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setResizeWeight(0.1);
        jSplitPane1.setRightComponent(jSplitPane3);

        add(jSplitPane1);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    // End of variables declaration//GEN-END:variables


}
