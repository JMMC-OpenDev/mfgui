package fr.jmmc.mf.gui.models;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mf.gui.PlotPanel;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.models.Response;
import javax.swing.tree.DefaultMutableTreeNode;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mf.models.ResultFile;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.swing.JFrame;
import ptolemy.plot.plotml.PlotMLFrame;
import ptolemy.plot.plotml.PlotMLParser;
import ptolemy.plot.Plot;

public class ResultModel extends DefaultMutableTreeNode {
    public final static String className = "fr.jmmc.mf.gui.models.SettingsModel";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(className);
    private SettingsModel settingsModel;

    /*static Action plotBaselinesAction;
    static Action plotUVCoverageAction;
    static Action plotRadialVisAction;
    static Action plotRadialT3Action;
    static Action plotImageAction;
    */
    String htmlReport = null;
    private Result result;


    public ResultModel(SettingsModel settingsModel, Result result) {
        this.settingsModel = settingsModel;
        this.result=result;
        genReport(settingsModel);
        this.setUserObject(result);
        //genPlots(UtilsClass.getResultFiles(response));
    }
    
    public Result getResult() {
        return (Result) getUserObject();
    }

    /*
     public void genResultReport(SettingsModel settingsModel, Response response) {
        JFrame resultFrame = new JFrame();
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        resultFrame.getContentPane().add(p);
        genReport(settingsModel);
        JScrollPane sp = new JScrollPane(new JEditorPane("text/html", resultPanel.getReport()));
        p.add(sp);
//        addPlot(resultFrame, "--New fit result--");
        genPlots(settingsModel);
        
      */
 public String getHtmlReport() {
        logger.entering("" + this.getClass(), "getHtmlReport");
        return htmlReport;
    }

    public void genPlots(SettingsModel s) {
        logger.entering("" + this.getClass(), "genPlots");

        settingsModel = s;
        /*
        plotBaselinesAction.actionPerformed(null);
        plotUVCoverageAction.actionPerformed(null);
        plotRadialVisAction.actionPerformed(null);
        plotRadialT3Action.actionPerformed(null);
        */
    }

    void genPlots(ResultFile[] resultFiles) {
        for (int i = 0; i < resultFiles.length; i++) {
            try {
                ResultFile r = resultFiles[i];
                ResultFile pdf = null;
                if (r.getHref().substring(1, 30).contains("png")) {
                    for (int j = 0; j < resultFiles.length; j++) {
                        ResultFile r2 = resultFiles[j];
                        String filenameWOExt = r2.getName().substring(0, r2.getName().lastIndexOf('.'));
                        if (r.getName().startsWith(filenameWOExt) && r2.getName().endsWith("pdf")) {
                            pdf = r2;
                        }
                    }
                    JFrame f = PlotPanel.buildFrameOf(r, pdf);
                    this.add(new DefaultMutableTreeNode(f,false));
                    //@todo add plots to treemodel viewer.addPlot(, r.getDescription());
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }


    private void genReport(SettingsModel s) {
        logger.entering("" + this.getClass(), "genReport");
        try {
            String xslPath = "fr/jmmc/mf/gui/resultToHtml.xsl";
            java.net.URL url = this.getClass().getClassLoader().getResource(xslPath);
            StringWriter sw= new StringWriter();
            result.marshal(sw);
            htmlReport = UtilsClass.xsl(sw.toString(), url, null);
        } catch (Exception exc) {
            htmlReport = "<html>Error during report generation.</html>";
            new FeedbackReport(null, true, exc);
        }
    }

   

    /** Plots using ptplot widgets */
    protected void ptplot(String plotName) {
        logger.entering("" + this.getClass(), "ptplot");

        String xmlStr = null;

        try {
            // Contruct xml document to plot
            java.net.URL url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/yogaToPlotML.xsl");
            xmlStr = UtilsClass.xsl(settingsModel.getLastXml(), url,
                    new String[]{"plotName", plotName});

            PlotMLParser plotMLParser;
            // Construct plot and parse xml
            Plot plot = new Plot();
            plotMLParser = new PlotMLParser(plot);
            plotMLParser.parse(null, xmlStr);

            // Show plot into frame
            PlotMLFrame plotMLFrame = new PlotMLFrame("Plotting " + plotName, plot);

            //@todo add new object to the tree viewer.addPlot(plotMLFrame, plotName);

            logger.finest("plot ready to be shown");
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
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

            if (plotName.equals("plotBaselines") || plotName.equals("plotUVCoverage") ||
                    plotName.startsWith("plotRadial")) {
                ptplot(plotName);
            } else {
                logger.severe("Code does not handle the following plot name : " + plotName);
            }
        }
    }
}
