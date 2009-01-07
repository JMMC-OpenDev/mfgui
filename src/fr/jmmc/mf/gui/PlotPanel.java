/*
 * PlotPanel.java
 *
 * Created on 29 oct. 2008, 08:16:23
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.ResultFile;
import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.StringReader;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author mella
 */
public class PlotPanel extends javax.swing.JPanel
{
    /** Class logger */
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.PlotPanel");
    /** settings model reference */
    private static SettingsModel settingsModel = null;
    
    private PlotModelPanel plotModelPanel = null;
    private PlotChi2Panel plotChi2Panel = null;

    /** Creates new form PlotPanel */
    public PlotPanel()
    {
        initComponents();
        plotModelPanel = new PlotModelPanel();
        plotChi2Panel = new PlotChi2Panel();
        add(plotModelPanel);
        add(plotChi2Panel);
        add(blankPanel);
    }

    public void show(SettingsModel s)
    {
        settingsModel=s;
        plotModelPanel.show(s);
        plotChi2Panel.show(s);
    }

    /**
     * Call plot build routine and draw the new plot.
     *
     * @param methodName the method's name.
     * @param methodArgs the method's arguments.
     * @param title the plot title.
     */
    static public void plot(String methodName, String methodArgs, String title)
    {
        logger.fine("Requesting yoga '" + methodName + "' call");

        String result = "";

        try {
            result = ModelFitting.instance_.execMethod(methodName,
                    settingsModel.getTempFile(false), methodArgs);
            StatusBar.show(methodName + " process finished");
        } catch (Exception ex) {
            JTextArea textArea = new JTextArea(20, 80);
            textArea.setText(result);

            JScrollPane scrollPane = new JScrollPane(textArea);
            textArea.setEditable(false);
            javax.swing.JOptionPane.showMessageDialog(null, scrollPane, "Error ",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            logger.severe("No xml returned by server side");
            logger.log(Level.WARNING, ex.getMessage(), ex);
            StatusBar.show("Error during process of " + methodName);
            return;
        }

        // try to marshal into resultFile or pass to imageviewer
        try {
            StringReader reader = new StringReader(result);
            ResultFile r = ResultFile.unmarshal(reader);
            String b64file = r.getHref();
            File f = UtilsClass.saveBASE64ToFile(b64file);
            Image image = ImageIO.read(f);
            // Use a label to display the image
            JFrame frame = new JFrame();
            JLabel label = new JLabel(new ImageIcon(image));
            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Cant read png, so try with imageViewer", e);
            fr.jmmc.mcs.ImageViewer v = new fr.jmmc.mcs.ImageViewer(result);
            v.setTitle(title);
            v.setSize(400, 400);
            v.setVisible(true);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        blankPanel = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        add(blankPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blankPanel;
    // End of variables declaration//GEN-END:variables
}
