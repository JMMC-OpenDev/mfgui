/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

/**
 *
 * @author mellag
 */
public class MsgPanel extends javax.swing.JPanel {

    /** Creates new form MsgPanel */
    public MsgPanel() {
        initComponents();
        jEditorPane1.setContentType("text/html");
    }

    /**
     * Change message.
     * Use \n to break line.
     @param multiLineMsg message to display.
     */
    public void setMessage(String multiLineMsg) {
        jEditorPane1.setText(multiLineMsg);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPane1.setEditable(false);
        jEditorPane1.setFont(jEditorPane1.getFont().deriveFont(jEditorPane1.getFont().getSize()+17f));
        jEditorPane1.setOpaque(false);
        jScrollPane2.setViewportView(jEditorPane1);

        add(jScrollPane2);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}