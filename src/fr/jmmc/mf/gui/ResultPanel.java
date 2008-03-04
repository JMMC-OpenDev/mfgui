/*
 * ResultPanel.java
 *
 * Created on 27 février 2008, 14:11
 */

package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ReportDialog;
import fr.jmmc.mf.models.Result;
import java.io.StringWriter;
import ptolemy.plot.*;
import ptolemy.plot.plotml.*;
import javax.swing.*;

/**
 *
 * @author  mella
 */
public class ResultPanel extends javax.swing.JPanel {
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ResultPanel");

    /* actions  (definitions are at end of file) */
    static Action plotBaselinesAction;
    static Action plotUVCoverageAction;
    static Action plotRadialAction;
    static Action plotImageAction;

    //ParametersTableModel parametersTableModel;
    Result current;
    SettingsViewerInterface settingsViewer = null;
    SettingsModel settingsModel = null;

    /** Creates new form ResultPanel */
    public ResultPanel(SettingsViewerInterface viewer) {
         /* actions must be inited before component init */
        plotBaselinesAction = new PlotAction("plotBaselines");
        plotUVCoverageAction = new PlotAction("plotUVCoverage");
        plotRadialAction = new PlotAction("plotRadial");
        initComponents();
    }
     public void show(Result r, SettingsModel s) {
        current = r;
        settingsModel = s;

        try {
            StringWriter sw = new StringWriter();
            r.marshal(sw);
            //String xmlContent = s.getLastXml();
            String xmlContent = sw.toString();
            // Do simple xslt transform result
            logger.fine("Get url for xslt");

            java.net.URL url = this.getClass().getClassLoader()
                                   .getResource("fr/jmmc/mf/gui/resultToHtml.xsl");
            logger.fine("Start of transformation with :" + url);

            String htmlStr = UtilsClass.xsl(xmlContent, url, null);
            logger.fine("End of transformation");
            resultEditorPane.setContentType("text/html");
            resultEditorPane.setText(htmlStr);    
        } catch (Exception exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultEditorPane = new javax.swing.JEditorPane();
        jPanel2 = new javax.swing.JPanel();
        plotBaselinesButton = new javax.swing.JButton();
        plotUVCoverageButton = new javax.swing.JButton();
        plotRadialButton = new javax.swing.JButton();
        plotTabbedPane = new javax.swing.JTabbedPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane2.setViewportView(resultEditorPane);

        jPanel1.add(jScrollPane2);

        add(jPanel1);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        plotBaselinesButton.setAction(plotBaselinesAction);
        jPanel2.add(plotBaselinesButton);

        plotUVCoverageButton.setAction(plotUVCoverageAction);
        jPanel2.add(plotUVCoverageButton);

        plotRadialButton.setAction(plotRadialAction);
        jPanel2.add(plotRadialButton);

        add(jPanel2);
        add(plotTabbedPane);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton plotBaselinesButton;
    private javax.swing.JButton plotRadialButton;
    private javax.swing.JTabbedPane plotTabbedPane;
    private javax.swing.JButton plotUVCoverageButton;
    private javax.swing.JEditorPane resultEditorPane;
    // End of variables declaration//GEN-END:variables
    
     /** Plots using ptplot widgets */
    protected void ptplot(String plotName) {
        logger.entering("" + this.getClass(), "ptplot");

        try {
            // Contruct xml document to plot
            java.net.URL url = this.getClass().getClassLoader()
                                   .getResource("fr/jmmc/mf/gui/yogaToPlotML.xsl");
            String xmlStr = UtilsClass.xsl(settingsModel.getLastXml(), url,
                    new String[] { "plotName", plotName });

            // Construct plot and parse xml
            Plot plot = new Plot();
            PlotMLParser plotMLParser = new PlotMLParser(plot);
            plotMLParser.parse(null, xmlStr);

            // Show plot into frame
            PlotMLFrame plotMLFrame = new PlotMLFrame("Plotting " + plotName,
                    plot);            
            plotMLFrame.setVisible(true);
            //plotMLFrame.setSize(new java.awt.Dimension(400, 400));
            plotMLFrame.validate();
                                  
            Plot plot2 = new Plot();
        plotMLParser = new PlotMLParser(plot2);
            plotMLParser.parse(null, xmlStr);

            //plot2.read(xmlStr);
            addPlotPanel(plotName,plot2);
            
            logger.finest("plot ready to be shown");
        } catch (Exception exc) {
            new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
    }
    
    protected void addPlotPanel(String title,JPanel pp)
    {
        plotTabbedPane.add(title,pp);
        this.validate();
    }

    protected void plotImage(String plotName) {
        logger.entering("" + this.getClass(), "plotImage");

        fr.jmmc.mcs.ImageCanvas c = new fr.jmmc.mcs.ImageCanvas();
        //c.fitsInit("/home/users/mella/img.fits");        
        c.xmlInit("<table></table>");

        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.getContentPane().add(c);
        frame.setTitle(plotName);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    protected class PlotAction extends fr.jmmc.mcs.util.MCSAction {
        String plotName;

        /* plotName must correcspond to a resource action and one xslt template */
        public PlotAction(String plotName) {
            super(plotName);
            this.plotName = plotName;
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering("" + this.getClass(), "actionPerformed");

            if (plotName.equals("plotBaselines") ||
                    plotName.equals("plotUVCoverage") ||
                    plotName.equals("plotRadial")) {
                ptplot(plotName);
            } else {
                plotImage(plotName);
            }
        }
    }
}