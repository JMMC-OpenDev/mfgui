package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Results;

public class ResultsPanel extends javax.swing.JPanel {
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ResultsPanel");
    Results current;
    SettingsViewerInterface viewer = null;
    SettingsModel settingsModel = null;
 
    /** Creates new form ResultPanel */
    public ResultsPanel(SettingsViewerInterface viewer) {
        this.viewer = viewer;
        initComponents();
    }

    public void show(Results r, SettingsModel s) {
        logger.entering("" + this.getClass(), "show");
        current = r;
        settingsModel = s;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createTitledBorder("Results Panel"));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
