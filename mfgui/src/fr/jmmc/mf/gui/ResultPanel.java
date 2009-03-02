package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.models.ResultModel;

public class ResultPanel extends javax.swing.JPanel {
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ResultPanel");
    ResultModel current;
    SettingsViewerInterface viewer = null;
    SettingsModel settingsModel = null;
 
    /** Creates new form ResultPanel */
    public ResultPanel(SettingsViewerInterface viewer) {
        this.viewer = viewer;
        initComponents();
    }

    public void show(ResultModel r, SettingsModel s) {
        logger.entering("" + this.getClass(), "show");
        current = r;
        settingsModel = s;
        resultEditorPane.setContentType("text/html");
        resultEditorPane.setText(r.getHtmlReport());
        resultEditorPane.setCaretPosition(0);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultEditorPane = new javax.swing.JEditorPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        resultEditorPane.setEditable(false);
        jScrollPane2.setViewportView(resultEditorPane);

        jPanel1.add(jScrollPane2);

        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JEditorPane resultEditorPane;
    // End of variables declaration//GEN-END:variables
}
