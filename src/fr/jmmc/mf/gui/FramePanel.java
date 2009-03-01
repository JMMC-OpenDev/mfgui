package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import javax.swing.JFrame;

public class FramePanel extends javax.swing.JPanel implements WindowListener {

    /** Class logger */
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.PlotPanel");
    /** settings model reference */
    private static SettingsModel settingsModel = null;
    SettingsViewerInterface viewer = null;
    JFrame frame;
    Container contentPane;

    /** Creates new form PlotPanel */
    public FramePanel(SettingsViewerInterface viewer) {
        this.viewer = viewer;
        initComponents();
        jButton1.setAction(MFGui.attachDetachFrameAction);
    }

    public void show(SettingsModel s, JFrame f) {
        settingsModel = s;
        frame = f;
        // Take care to add this framePanel to the list one and only one time
        if (!Arrays.asList(f.getWindowListeners()).contains(this)) {
            f.addWindowListener(this);
        }

        contentPane = frame.getContentPane();
        blankPanel.removeAll();
        if (!frame.isVisible()) {
            //throw new UnsupportedOperationException("Not supported yet.");
            frame.setVisible(false);

            blankPanel.add(frame.getContentPane());
        } else {
            frame.setContentPane(contentPane);
            frame.setVisible(true);
        }
        blankPanel.repaint();
    }

    public void toggleFrame() {
        System.out.println("frame.isVisible() = " + frame.isVisible());
        if (frame.isVisible()) {
            //throw new UnsupportedOperationException("Not supported yet.");
            frame.setVisible(false);
            blankPanel.removeAll();
            blankPanel.add(frame.getContentPane());
        } else {
            frame.setContentPane(contentPane);
            frame.setVisible(true);
        }

        //refresh the gui
        validate();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        blankPanel = new javax.swing.JPanel();
        fillerPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Frame panel"));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jButton1.setAlignmentX(0.5F);
        add(jButton1);

        blankPanel.setLayout(new javax.swing.BoxLayout(blankPanel, javax.swing.BoxLayout.LINE_AXIS));
        add(blankPanel);
        add(fillerPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blankPanel;
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        if (frame == e.getSource()) {
            toggleFrame();
        }
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
