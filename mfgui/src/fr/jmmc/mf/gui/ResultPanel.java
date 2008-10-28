/*
 * ResultPanel.java
 *
 * Created on 27 février 2008, 14:11
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mf.models.Result;

import ptolemy.plot.*;

import ptolemy.plot.plotml.*;

import java.io.StringWriter;

import javax.swing.*;


/**
 *
 * @author  mella
 */
public class ResultPanel extends javax.swing.JPanel
{
    /**
     * DOCUMENT ME!
     */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ResultPanel");

    /* actions  (definitions are at end of file) */
    /**
     * DOCUMENT ME!
     */
    static Action plotBaselinesAction;

    /**
     * DOCUMENT ME!
     */
    static Action plotUVCoverageAction;

    /**
     * DOCUMENT ME!
     */
    static Action plotRadialVisAction;

    /**
     * DOCUMENT ME!
     */
    static Action plotRadialT3Action;

    /**
     * DOCUMENT ME!
     */
    static Action plotImageAction;

    //ParametersTableModel parametersTableModel;
    /**
     * DOCUMENT ME!
     */
    Result                  current;

    /**
     * DOCUMENT ME!
     */
    SettingsViewerInterface settingsViewer = null;

    /**
     * DOCUMENT ME!
     */
    SettingsModel settingsModel = null;

    // Variables declaration - do not modify                     
    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel      jPanel1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel2;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel3;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JScrollPane jScrollPane2;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTabbedPane jTabbedPane1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton plotBaselinesButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton plotRadialT3Button;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton plotRadialVisButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTabbedPane plotTabbedPane;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton plotUVCoverageButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JEditorPane resultEditorPane;

    /** Creates new form ResultPanel */
    public ResultPanel(SettingsViewerInterface viewer)
    {
        /* actions must be inited before component init */
        plotBaselinesAction      = new PlotAction("plotBaselines");
        plotUVCoverageAction     = new PlotAction("plotUVCoverage");
        plotRadialVisAction      = new PlotAction("plotRadialVIS");
        plotRadialT3Action       = new PlotAction("plotRadialT3");
        initComponents();
    }

    /**
     * DOCUMENT ME!
     *
     * @param r DOCUMENT ME!
     * @param s DOCUMENT ME!
     */
    public void show(Result r, SettingsModel s)
    {
        current           = r;
        settingsModel     = s;

        try
        {
            String xslPath = "fr/jmmc/mf/gui/resultToHtml.xsl";            
            java.net.URL url = this.getClass().getClassLoader().getResource(xslPath);                        
            String htmlStr = UtilsClass.xsl(s.getLastXml(), url, null);
            resultEditorPane.setContentType("text/html");
            resultEditorPane.setText(htmlStr);
        }
        catch (Exception exc)
        {
            new FeedbackReport(null, true, exc);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jTabbedPane1             = new javax.swing.JTabbedPane();
        jPanel1                  = new javax.swing.JPanel();
        jScrollPane2             = new javax.swing.JScrollPane();
        resultEditorPane         = new javax.swing.JEditorPane();
        jPanel3                  = new javax.swing.JPanel();
        jPanel2                  = new javax.swing.JPanel();
        plotBaselinesButton      = new javax.swing.JButton();
        plotUVCoverageButton     = new javax.swing.JButton();
        plotRadialVisButton      = new javax.swing.JButton();
        plotRadialT3Button       = new javax.swing.JButton();
        plotTabbedPane           = new javax.swing.JTabbedPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        resultEditorPane.setEditable(false);
        jScrollPane2.setViewportView(resultEditorPane);

        jPanel1.add(jScrollPane2);

        jTabbedPane1.addTab("Text report", jPanel1);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        plotBaselinesButton.setAction(plotBaselinesAction);
        jPanel2.add(plotBaselinesButton);

        plotUVCoverageButton.setAction(plotUVCoverageAction);
        jPanel2.add(plotUVCoverageButton);

        plotRadialVisButton.setAction(plotRadialVisAction);
        jPanel2.add(plotRadialVisButton);

        plotRadialT3Button.setAction(plotRadialT3Action);
        jPanel2.add(plotRadialT3Button);

        jPanel3.add(jPanel2);
        jPanel3.add(plotTabbedPane);

        jTabbedPane1.addTab("Plots", jPanel3);

        add(jTabbedPane1);
    } // </editor-fold>//GEN-END:initComponents

    // End of variables declaration                   

    /** Plots using ptplot widgets */
    protected void ptplot(String plotName)
    {
        logger.entering("" + this.getClass(), "ptplot");

        String xmlStr = null;

        try
        {
            // Contruct xml document to plot
            java.net.URL url = this.getClass().getClassLoader()
                                   .getResource("fr/jmmc/mf/gui/yogaToPlotML.xsl");
            xmlStr = UtilsClass.xsl(settingsModel.getLastXml(), url,
                    new String[] { "plotName", plotName });

            PlotMLParser plotMLParser;

            // Do not display the plot in another window
            // @TODO give access to save export print menus
            if (false)
            {
                // Construct plot and parse xml
                Plot plot = new Plot();
                plotMLParser = new PlotMLParser(plot);
                plotMLParser.parse(null, xmlStr);

                // Show plot into frame
                PlotMLFrame plotMLFrame = new PlotMLFrame("Plotting " + plotName, plot);
                plotMLFrame.setVisible(true);
                //plotMLFrame.setSize(new java.awt.Dimension(400, 400));
                plotMLFrame.validate();
            }
            else
            {
                // a second plot must be done else it stole display into previous frame
                Plot plot2 = new Plot();
                plotMLParser = new PlotMLParser(plot2);
                plotMLParser.parse(null, xmlStr);
                //plot2.read(xmlStr);
                addPlotPanel(plotName, plot2);
            }

            logger.finest("plot ready to be shown");
        }
        catch (Exception exc)
        {
            new FeedbackReport(null, true, exc);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param title DOCUMENT ME!
     * @param pp DOCUMENT ME!
     */
    protected void addPlotPanel(String title, JPanel pp)
    {
        plotTabbedPane.add(title, pp);
        plotTabbedPane.setSelectedComponent(pp);
        this.validate();
    }

    /**
     * DOCUMENT ME!
     *
     * @param plotName DOCUMENT ME!
     */
    protected void plotImage(String plotName)
    {
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

    protected class PlotAction extends fr.jmmc.mcs.util.MCSAction
    {
        String plotName;

        /* plotName must correcspond to a resource action and one xslt template */
        public PlotAction(String plotName)
        {
            super(plotName);
            this.plotName = plotName;
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.entering("" + this.getClass(), "actionPerformed");

            if (plotName.equals("plotBaselines") || plotName.equals("plotUVCoverage") ||
                    plotName.startsWith("plotRadial"))
            {
                ptplot(plotName);
            }
            else
            {
                plotImage(plotName);
            }
        }
    }
}
