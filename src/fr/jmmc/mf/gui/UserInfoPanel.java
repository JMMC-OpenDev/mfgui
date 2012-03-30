/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.mf.models.Settings;


/**
 * Edit and render the user info element of the settings file.
 */
public class UserInfoPanel extends javax.swing.JPanel
{
    final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            UserInfoPanel.class.getName());
    Settings current = null;
    SettingsViewerInterface settingsViewer = null;
   
    /** Creates new form UserInfoPanel */
    public UserInfoPanel(SettingsViewerInterface viewer)
    {
        settingsViewer = viewer;
        initComponents();

        // Set online help
        jButton1.setAction(new ShowHelpAction(("BEGtt_PersonalNotebook_Bt")));
    }

    public void show(Settings s)
    {        
        current = s;

        if (s.getUserInfo() != null)
        {
            userInfoTextArea.setText(s.getUserInfo());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        userInfoTextArea = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Personal notebook:"));
        setLayout(new java.awt.GridBagLayout());

        jButton1.setText("jButton1");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(jButton1, gridBagConstraints);

        userInfoTextArea.setBackground(new java.awt.Color(255, 255, 153));
        userInfoTextArea.setColumns(20);
        userInfoTextArea.setFont(new java.awt.Font("Monospaced", 0, 10));
        userInfoTextArea.setRows(5);
        userInfoTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                userInfoTextAreaCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(userInfoTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void userInfoTextAreaCaretUpdate(javax.swing.event.CaretEvent evt)
    {//GEN-FIRST:event_userInfoTextAreaCaretUpdate
      //logger.entering(""+this.getClass(), "userInfoTextAreaCaretUpdate");
        current.setUserInfo(userInfoTextArea.getText());
    }//GEN-LAST:event_userInfoTextAreaCaretUpdate

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea userInfoTextArea;
    // End of variables declaration//GEN-END:variables
}
