/*
 * ResultPanel.java
 *
 * Created on 13 novembre 2006, 17:10
 */
package jmmc.mf.gui;


/**
 *
 * @author  mella
 */
public class ResultPanel extends javax.swing.JPanel {
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField dateTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JEditorPane resultPane;

    /** Creates new form ResultPanel */
    public ResultPanel(String result) {
        initComponents();
        resultPane.setText(result);
        dateTextField.setText("" + new java.util.Date());
    }

    public void setPage(String url) throws Exception {
        resultPane.setPage(url);
    }

    public void setPage(java.net.URL url) throws Exception {
        resultPane.setPage(url);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        dateTextField = new javax.swing.JTextField();
        closeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1,
                javax.swing.BoxLayout.X_AXIS));

        dateTextField.setEditable(false);
        dateTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        jPanel1.add(dateTextField);

        closeButton.setText("Close this tab");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    closeButtonActionPerformed(evt);
                }
            });

        jPanel1.add(closeButton);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        resultPane.setEditable(false);
        jScrollPane1.setViewportView(resultPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_closeButtonActionPerformed
        MainFrame.closeTab(this);
    } //GEN-LAST:event_closeButtonActionPerformed

    // End of variables declaration//GEN-END:variables
}
