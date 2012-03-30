/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.gui.models.ResultModel;

public class ResultPanel extends javax.swing.JPanel {
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ResultPanel");
    ResultModel current;
    SettingsViewerInterface viewer = null;
    SettingsModel settingsModel = null;
    private UserInfoPanel userInfoPanel = null;
 
    /** Creates new form ResultPanel */
    public ResultPanel(SettingsViewerInterface viewer) {
        this.viewer = viewer;
        userInfoPanel = new UserInfoPanel(viewer);
        initComponents();
        jPanel2.add(userInfoPanel);
        

        // build help button
        helpButton1.setAction(new ShowHelpAction(("BEG_Results")));
    }

    public void show(ResultModel r, SettingsModel s) {
        current = r;
        settingsModel = s;
        resultEditorPane.setContentType("text/html");
        resultEditorPane.setText(r.getHtmlReport());
        resultEditorPane.setCaretPosition(0);
        userInfoPanel.show(s.getRootSettings());
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel4 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        helpButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultEditorPane = new javax.swing.JEditorPane();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Result panel:"));
        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setAlignmentX(1.0F);
        jPanel2.setMaximumSize(new java.awt.Dimension(100, 150));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 150));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 170));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanel3.add(filler1);

        helpButton1.setText("jButton1");
        helpButton1.setAlignmentX(1.0F);
        helpButton1.setAlignmentY(0.0F);
        jPanel3.add(helpButton1);

        jPanel2.add(jPanel3);

        jSplitPane1.setTopComponent(jPanel2);

        jScrollPane1.setAlignmentX(1.0F);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        resultEditorPane.setEditable(false);
        jScrollPane2.setViewportView(resultEditorPane);

        jPanel1.add(jScrollPane2);

        jScrollPane1.setViewportView(jPanel1);

        jSplitPane1.setBottomComponent(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton helpButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JEditorPane resultEditorPane;
    // End of variables declaration//GEN-END:variables
}
