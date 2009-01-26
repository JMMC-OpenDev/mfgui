package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.ResultFile;
import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PlotPanel extends javax.swing.JPanel
{
    /** Class logger */
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.PlotPanel");

    /** settings model reference */
    private static SettingsModel settingsModel = null;
    
    private PlotModelPanel plotModelPanel = null;
    private PlotChi2Panel plotChi2Panel = null;
    private SettingsViewerInterface viewer=null;

    /** Creates new form PlotPanel */
    public PlotPanel(SettingsViewerInterface viewer)
    {
        this.viewer=viewer;
        settingsModel=viewer.getSettingsModel();
        initComponents();
        plotModelPanel = new PlotModelPanel(this);
        plotChi2Panel = new PlotChi2Panel(this);
        add(plotModelPanel);
        add(plotChi2Panel);
        //add(blankPanel);
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
    public void plot(String methodName, String methodArgs, String title)
    {
        logger.fine("Requesting yoga '" + methodName + "' call");

        Response response=null;
        try {
            response = ModelFitting.instance_.execMethod(methodName,
                    settingsModel.getTempFile(false), methodArgs);
            StatusBar.show(methodName + " process finished");
           ResultFile[] resultFiles = UtilsClass.getResultFiles(response);
           if(resultFiles.length==0){
               logger.log(Level.WARNING, "No result data interpreted");
               return;
           }
            ResultFile pngResultFile=null;
            ResultFile pdfResultFile=null;
            for (int i = 0; i < resultFiles.length; i++) {
                ResultFile r = resultFiles[i];
                if (r.getName().endsWith("png")) {
                    pngResultFile=r;
                }else if (r.getName().endsWith("pdf")) {
                    pdfResultFile=r;
                }
            }
            viewer.addPlot(buildFrameOf(pngResultFile,pdfResultFile),title);

        } catch (java.net.UnknownHostException ex) {
            String msg="Network seems down. Can't contact host "+ex.getMessage();
            javax.swing.JOptionPane.showMessageDialog(null, msg, "Error ",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, ex.getMessage(), ex);
            StatusBar.show("Error during process of " + methodName);
            return;
        } catch (Exception ex) {
            new FeedbackReport(ex);
        }

        if(false){
                try {
                    fr.jmmc.mcs.ImageViewer v = new fr.jmmc.mcs.ImageViewer("replace by result/blah");
                    v=null;
                    v.setTitle(title);
                    v.setSize(400, 400);
                    viewer.addPlot(v, title);
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "Cant read with imageViewer", ex);
                    String msg = "Error processing result";
                    javax.swing.JOptionPane.showMessageDialog(null, msg, "Error ",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    StatusBar.show("Error during process of " + methodName);
                    return;
                }
        }
    }

    public static JFrame buildFrameOf(ResultFile pngResultFile, ResultFile pdfResultFile) throws IOException {
        String b64file;
        File f;
        JLabel label;
        JButton b;
        JFrame frame = new JFrame();
        JPanel p = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
        if (pngResultFile != null) {
            b64file = pngResultFile.getHref();
            f = UtilsClass.saveBASE64ToFile(b64file);
            Image image = ImageIO.read(f);
            // Use a label to display the image
            label = new JLabel(new ImageIcon(image));
            p.add(label, BorderLayout.CENTER);
            b = new JButton();
            b.setAction(new SaveFileAction(f,pngResultFile.getName()));
            buttonPanel.add(b);
        }
        if (pdfResultFile != null) {
            b64file = pdfResultFile.getHref();
            f = UtilsClass.saveBASE64ToFile(b64file);
            b = new JButton();
            b.setAction(new SaveFileAction(f,pdfResultFile.getName()));
            buttonPanel.add(b);
        }
        p.add(buttonPanel, BorderLayout.NORTH);
        frame.getContentPane().add(p);
        frame.pack();
        return frame;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        blankPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Plot panel"));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        add(blankPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel blankPanel;
    // End of variables declaration//GEN-END:variables
}
