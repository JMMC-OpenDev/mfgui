package fr.jmmc.mf.gui.models;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mf.gui.PlotPanel;
import fr.jmmc.mf.gui.UtilsClass;
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
    private String htmlReport = null;
    private String xmlResult = null;
    private Result result;


    public ResultModel(SettingsModel settingsModel, Result result) {
        this.settingsModel = settingsModel;
        this.result=result;

        try {
            String xslPath = "fr/jmmc/mf/gui/resultToHtml.xsl";
            java.net.URL url = this.getClass().getClassLoader().getResource(xslPath);
            StringWriter xmlResultSw= new StringWriter();
            result.marshal(xmlResultSw);
            xmlResult=xmlResultSw.toString();
            htmlReport = UtilsClass.xsl(xmlResult, url, null);
            genPlots();
        } catch (Exception exc) {
            htmlReport = "<html>Error during report generation.</html>";
            new FeedbackReport(null, true, exc);
        }

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

    public void genPlots() {
        logger.entering("" + this.getClass(), "genPlots");
        String [] plotNames = new String[]{"plotBaselines","plotUVCoverage","plotRadial"};
        for (int i = 0; i < plotNames.length; i++) {
            String plotName = plotNames[i];
            ptplot(plotName);
        }
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
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    /** Plots using ptplot widgets */
    protected void ptplot(final String plotName) {
        logger.entering("" + this.getClass(), "ptplot");
        String xmlStr = null;
        try {
            // Contruct xml document to plot
            java.net.URL url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/yogaToPlotML.xsl");
            xmlStr = UtilsClass.xsl(xmlResult, url,
                    new String[]{"plotName", plotName});
            PlotMLParser plotMLParser;
            // Construct plot and parse xml
            Plot plot = new Plot();
            plotMLParser = new PlotMLParser(plot);
            plotMLParser.parse(null, xmlStr);
            // Show plot into frame
            PlotMLFrame plotMLFrame = new PlotMLFrame("Plotting " + plotName, plot){
                public String toString(){
                    return plotName;
                }
            };
            this.add(new DefaultMutableTreeNode(plotMLFrame));

            url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/yogaToVoTable.xsl");
            System.out.println(UtilsClass.xsl(xmlResult, url, null));

        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }
}
