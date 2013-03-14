/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.models;

import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.jmcs.util.XslTransform;
import fr.jmmc.mf.gui.FrameTreeNode;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.models.Residual;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mf.models.ResultFile;
import fr.jmmc.mf.models.Target;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ptolemy.plot.plotml.PlotMLFrame;

/**
 * This treeNode brings one Result castor into the JTrees.
 */
public class ResultModel extends DefaultMutableTreeNode {

    public final static String className = ResultModel.class.getName();
    /** Class logger */
    static final Logger logger = LoggerFactory.getLogger(className);
    private SettingsModel settingsModel;
    private String htmlReport = null;
    private String xmlResult = null;
    private Result result = null;

    public ResultModel(SettingsModel settingsModel, Result result, boolean testDataBeforeShowing) {
        this.settingsModel = settingsModel;
        this.result = result;

        String xslPath = "fr/jmmc/mf/gui/resultToHtml.xsl";

        // use content or href to get the result element
        if (result.getHref() == null) {
            logger.debug("Start result section write into stringbuffer");
            StringWriter xmlResultSw = new StringWriter(16384); // 16K buffer
            UtilsClass.marshal(result, xmlResultSw);
            logger.debug("End result section write into stringbuffer");
            xmlResult = xmlResultSw.toString();
            logger.debug("Start html generation");
            htmlReport = XslTransform.transform(xmlResult, xslPath);
            logger.debug("End html generation");
        } else {
            xmlResult = "<result>"
                    + UtilsClass.saveBASE64ToString(result.getHref())
                    + "</result>";
            logger.debug("Start html generation");
            htmlReport = XslTransform.transform(xmlResult, xslPath);
            logger.debug("End html generation");
        }

        genPlots(testDataBeforeShowing);
        this.setUserObject(result);        
    }

    public Result getResult() {
        return (Result) getUserObject();
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void genPlots(boolean testDataBeforeShowing) {
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
            ptplot(plotName, false, false);
        }
        plotNames = plotTobuild.values().toArray(new String[0]);
        if (plotTobuild.isEmpty()) {
            plotNames = new String[]{"visamp", "visphi", "vis2", "t3amp", "t3phi"};
        }
        for (int i = 0; i < plotNames.length; i++) {
            String plotName = plotNames[i];
            ptplot(plotName, false, testDataBeforeShowing);
            ptplot(plotName, true, testDataBeforeShowing);
        }
    }

    void genPlots(String plotTitle, String plotDescription, ResultFile[] resultFiles) {
        // try to locate the corresponding pdf of each png file
        for (int i = 0; i < resultFiles.length; i++) {
            ResultFile r = resultFiles[i];

            String b64file;
            JFrame f = null;
            ArrayList<File> filesToExport = new ArrayList<File>();
            ArrayList<String> filenamesToExport = new ArrayList<String>();
            if (r.getHref().substring(1, 30).contains("png")) {
                for (int j = 0; j < resultFiles.length; j++) {
                    ResultFile r2 = resultFiles[j];
                    String filenameWOExt = r2.getName().substring(0, r2.getName().lastIndexOf('.'));
                    if (r.getName().startsWith(filenameWOExt + ".") && r2.getName().endsWith("pdf")) {
                        b64file = r2.getHref();
                        File pdfFile = UtilsClass.saveBASE64ToFile(b64file, "pdf");
                        filesToExport.add(pdfFile);
                        filenamesToExport.add(r.getName());
                    }
                }
                b64file = r.getHref();
                File pngFile = UtilsClass.saveBASE64ToFile(b64file, "png");
                filesToExport.add(pngFile);
                f = UtilsClass.buildFrameFor(plotTitle, plotDescription, new File[]{pngFile}, new String[]{r.getName()});
                this.add(new FrameTreeNode(f, filesToExport.toArray(new File[0]), filenamesToExport.toArray(new String[0])));
            }
        }
    }

    /** Plots using ptplot widgets */
    protected void ptplot(String plotName, boolean residuals, boolean testDataBeforeShowing) {
        String xmlStr = null;

        logger.debug("Start plot generation:{}(residuals={})", plotName, residuals);
        // Construct xml document to plot
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("plotName", plotName);
        if (residuals) {
            // TODO test if we can use residuals directly
            args.put("residuals", Boolean.toString(residuals));
            plotName = plotName + " residuals";
        }

        logger.debug("start xslt from yoga xml to ptolemy plot (args={})", args);
        xmlStr = XslTransform.transform(xmlResult, "fr/jmmc/mf/gui/yogaToPlotML.xsl", args);
        logger.debug("end xslt from yoga xml to ptolemy plot");

        // this test is perform during load of results element in settings file that may or not have all kind of dataset
        if (testDataBeforeShowing && !xmlStr.substring(0, Math.min(xmlStr.length(), 500)).contains("dataset")) {
            logger.debug("No dataset to display");
            return;
        }
        // generate frame and tsv file
        PlotMLFrame plotMLFrame = UtilsClass.getPlotMLFrame(xmlStr, plotName);

        if (!residuals && plotName.toLowerCase().contains("phi")) {
            UtilsClass.fixPlotAxesForPhases(plotMLFrame.plot);
        } else {
            UtilsClass.fixPlotAxesForAmp(plotMLFrame.plot);
        }

        logger.debug("End plot generation");
        logger.debug("Start tsv generation:{}", plotName);
        File tsv = UtilsClass.getPlotMLTSVFile(xmlStr);
        logger.debug("End tsv generation");

        // add on frameTreeNode as child
        this.add(new FrameTreeNode(plotMLFrame, tsv));
    }

    public String toString() {
        String s = result.getLabel();
        if (StringUtils.isEmpty(s)){
            return "Fit result (missing timedate)";
        }
        return s;
    }
}
