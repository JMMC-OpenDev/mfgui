/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.models;

import fr.jmmc.jmcs.service.XslTransform;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.models.Result;
import java.io.StringWriter;
import javax.swing.tree.DefaultMutableTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        this.setUserObject(result);        
    }

    public Result getResult() {
        return (Result) getUserObject();
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public String getXmlResult() {
        return xmlResult;
    }
    
    public String toString() {
        String s = result.getLabel();
        if (StringUtils.isEmpty(s)) {
            return "Fit result (missing timedate)";
        }
        return s;
    }
}
