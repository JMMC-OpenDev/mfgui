package fr.jmmc.mf.gui.models;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mf.gui.FrameTreeNode;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.models.Residual;
import javax.swing.tree.DefaultMutableTreeNode;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mf.models.ResultFile;
import fr.jmmc.mf.models.Target;
import java.io.File;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JFrame;
import ptolemy.plot.plotml.PlotMLFrame;

/**
 * This treeNode brings one Result castor into the JTrees.
 */
public class ResultModel extends DefaultMutableTreeNode {

    public final static String className = "fr.jmmc.mf.gui.models.SettingsModel";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(className);
    private SettingsModel settingsModel;
    private String htmlReport = null;
    private String xmlResult = null;
    private Result result;
    private int index;

    public ResultModel(SettingsModel settingsModel, Result result, int index) {
        this.settingsModel = settingsModel;
        this.result = result;
        this.index = index;

        try {
            String xslPath = "fr/jmmc/mf/gui/resultToHtml.xsl";
            logger.fine("Start result section write into stringbuffer");
            StringWriter xmlResultSw = new StringWriter();
            UtilsClass.marshal(result, xmlResultSw);
            logger.fine("End result section write into stringbuffer");
            xmlResult = xmlResultSw.toString();
            logger.fine("Start html generation");
            htmlReport = UtilsClass.xsl(xmlResult, xslPath, null);
            logger.fine("End html generation");
            //genPlots(UtilsClass.getResultFiles(response));
            genPlots();
        } catch (Exception exc) {
            htmlReport = "<html>Error during report generation.</html>";
            new FeedbackReport(null, true, exc);
        }
        /*
        java.net.URL url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/yogaToVoTable.xsl");
        try {
        java.io.File file = java.io.File.createTempFile("model", "vot");
        file.deleteOnExit();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(UtilsClass.xsl(xmlResult, url, null));
        out.close();
        ModelFitting.startFitsViewer(file.getAbsolutePath());
        } catch (Exception exc) {
        new FeedbackReport(null, true, exc);
        }
         */
        this.setUserObject(result);
    }

    public Result getResult() {
        return (Result) getUserObject();
    }

    public String getHtmlReport() {
        logger.entering("" + this.getClass(), "getHtmlReport");
        return htmlReport;
    }

    public void genPlots() {
        logger.entering("" + this.getClass(), "genPlots");
        Hashtable<String, String> plotTobuild = new Hashtable();
        Target[] targets = settingsModel.getRootSettings().getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            if (target.getResiduals() != null) {
                Residual[] residuals = target.getResiduals().getResidual();
                for (int j = 0; j < residuals.length; j++) {
                    Residual residual = residuals[j];
                    plotTobuild.put(residual.getName(), residual.getName().toLowerCase());
                }
            }
        }

        // Plot baselines and uvcoverage
        String[] plotNames = new String[]{"plotBaselines", "plotUVCoverage"};//, "visamp", "visphi", "vis2", "t3amp", "t3phi"};
        for (int i = 0; i < plotNames.length; i++) {
            String plotName = plotNames[i];
            ptplot(plotName, false);
        }
        plotNames = plotTobuild.values().toArray(new String[0]);
        if (plotTobuild.size() == 0) {
            plotNames = new String[]{"visamp", "visphi", "vis2", "t3amp", "t3phi"};
        }
        for (int i = 0; i < plotNames.length; i++) {
            String plotName = plotNames[i];
            ptplot(plotName, false);
            ptplot(plotName, true);
        }
    }

    void genPlots(ResultFile[] resultFiles) {
        // try to locate the corresponding pdf of each png file
        for (int i = 0; i < resultFiles.length; i++) {
            try {
                ResultFile r = resultFiles[i];

                String b64file;
                File file = null;
                JFrame f = null;
                Vector<File> filesToExport = new Vector();
                if (r.getHref().substring(1, 30).contains("png")) {
                    for (int j = 0; j < resultFiles.length; j++) {
                        ResultFile r2 = resultFiles[j];
                        String filenameWOExt = r2.getName().substring(0, r2.getName().lastIndexOf('.'));
                        if (r.getName().startsWith(filenameWOExt + ".") && r2.getName().endsWith("pdf")) {
                            b64file = r2.getHref();
                            File pdfFile = UtilsClass.saveBASE64ToFile(b64file, "pdf");
                            filesToExport.add(pdfFile);
                        }
                    }
                    b64file = r.getHref();
                    File pngFile = UtilsClass.saveBASE64ToFile(b64file, "png");
                    filesToExport.add(pngFile);
                    f = UtilsClass.buildFrameFor(pngFile);
                    this.add(new FrameTreeNode(f, r.getName(), filesToExport.toArray(new File[0])));
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    /** Plots using ptplot widgets */
    protected void ptplot(String plotName, boolean residuals) {
        logger.entering("" + this.getClass(), "ptplot", plotName);
        String xmlStr = null;
        try {
            logger.fine("Start plot generation:" + plotName + "(residuals=" + residuals + ")");
            // Contruct xml document to plot
            String[] args = new String[]{"plotName", plotName};
            if (residuals) {
                args = new String[]{"plotName", plotName, "residuals", "" + residuals};
                plotName = plotName + " residuals";
            }
            xmlStr = UtilsClass.xsl(xmlResult, "fr/jmmc/mf/gui/yogaToPlotML.xsl",
                    args);

            // generate frame and tsv file
            PlotMLFrame plotMLFrame = UtilsClass.getPlotMLFrame(xmlStr, plotName);
            logger.fine("End plot generation:" + plotName);
            logger.fine("Start tsv generation:" + plotName);
            File tsv = UtilsClass.getPlotMLTSVFile(xmlStr);
            logger.fine("End tsv generation:" + plotName);

            // add on frameTreeNode as child
            this.add(new FrameTreeNode(plotMLFrame, plotName, tsv));
        } catch (Exception exc) {
            new FeedbackReport(new Exception("Plot generation failed for " + plotName, exc));
        }
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return "Fit Result " + index;
    }
}
