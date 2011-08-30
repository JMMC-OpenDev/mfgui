package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.gui.ShowHelpAction;
import fr.jmmc.mf.gui.actions.SaveFileAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JButton;

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
        attachDetachButton.setAction(MFGui.attachDetachFrameAction);
        // build help button
        helpButton1.setAction(new ShowHelpAction(("BEG_ResultPlots_MT")));
    }

    public void show(FrameTreeNode frameTreeNode,SettingsModel s) {
        settingsModel = s;
        frame = frameTreeNode.getFrame();
        // Take care to add this framePanel to the list one and only one time
        if (!Arrays.asList(frame.getWindowListeners()).contains(this)) {
            frame.addWindowListener(this);
        }

        titleLabel.setText(frameTreeNode.getTitle());

        // Update buttonsPanel content
        buttonsPanel.removeAll();
        buttonsPanel.add(attachDetachButton);
        File[] files = frameTreeNode.getFilesToExport();        
        for (int i = 0; i < files.length; i++) {
            JButton button = new JButton(new SaveFileAction(files[i]));
            buttonsPanel.add(button);            
        }
        buttonsPanel.add(helpButton1);

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
        java.awt.GridBagConstraints gridBagConstraints;

        buttonsPanel = new javax.swing.JPanel();
        attachDetachButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        blankPanel = new javax.swing.JPanel();
        fillerPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Frame panel"));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        buttonsPanel.setLayout(new javax.swing.BoxLayout(buttonsPanel, javax.swing.BoxLayout.LINE_AXIS));

        attachDetachButton.setAlignmentX(0.5F);
        buttonsPanel.add(attachDetachButton);

        helpButton1.setText("jButton1");
        buttonsPanel.add(helpButton1);

        add(buttonsPanel);

        titlePanel.setAlignmentX(1.0F);
        titlePanel.setMaximumSize(new java.awt.Dimension(32767, 20));
        titlePanel.setPreferredSize(new java.awt.Dimension(10, 20));
        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.LINE_AXIS));

        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("jLabel1");
        titleLabel.setAlignmentX(12.0F);
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        titlePanel.add(titleLabel);

        add(titlePanel);

        blankPanel.setLayout(new javax.swing.BoxLayout(blankPanel, javax.swing.BoxLayout.LINE_AXIS));
        add(blankPanel);
        add(fillerPanel);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton attachDetachButton;
    private javax.swing.JPanel blankPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JButton helpButton1;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
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
