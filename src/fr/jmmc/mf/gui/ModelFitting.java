/*
JMMC
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.util.*;
import java.util.logging.*;

import javax.swing.JButton;
import javax.swing.JOptionPane;


/**
 *
 * @author mella
 */
public class ModelFitting extends fr.jmmc.mcs.gui.App{
    final static String rcsId = "$Id: ModelFitting.java,v 1.18 2008-10-06 15:06:55 mella Exp $";
    static Logger logger = Logger.getLogger("fr.jmmc.mf.gui.ModelFitting");
    static Preferences myPreferences;
    static ModelFitting instance_;
    static MFGui gui=null;
    
    public ModelFitting(String[] args) {
        super(args);
        instance_ = this;                
    }

    static String getVersion() {        
        return rcsId;
    }

    protected void init(String args[]){
        // Set default resource for application
        fr.jmmc.mcs.util.Resources.setResourceName("fr/jmmc/mf/gui/Resources");
        myPreferences = Preferences.getInstance();
     
        // Set the default locale to custom locale
        Locale locale = Locale.US;
        Locale.setDefault(locale);
        logger.info("Setting locale to:" + locale);

        gui = new MFGui(args); 
    }
    
    protected void execute(){
        gui.setVisible(true);
    }

    protected static void exit() {
        logger.info("Thank you for using this software!");        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ModelFitting mf = new ModelFitting(args);
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
            System.exit(1);
        }
    }

    /** This is the main wrappers method to execute yoga actions
     *  @param methodName name of method to wrap
     *  @param xmlFile file to give as argument of the method or null if
     *         no one is requested
     */
    public String execMethod(String methodName, java.io.File xmlFile)
        throws Exception {
        if (myPreferences.getPreferenceAsBoolean("yoga.remote.use")) {
            return doPost(methodName, xmlFile);
        } else {
            return doExec(methodName, xmlFile);
        }
    }

    private String doExec(String methodName, java.io.File xmlFile)
        throws Exception {
        String result = "";
        String yogaProgram = myPreferences.getPreference("yoga.local.home") +
            myPreferences.getPreference("yoga.local.progname");
        String filename = null;

        if (xmlFile != null) {
            filename = xmlFile.getAbsolutePath();
        }

        // Run main application waiting for end of cat process
        fr.jmmc.mcs.util.ProcessHandler ph;

        if (xmlFile == null) {
            ph = new fr.jmmc.mcs.util.ProcessHandler(new String[] {
                        yogaProgram, methodName
                    });
            logger.fine("Making call using yoga script:" + yogaProgram + " " +
                methodName);
        } else {
            ph = new fr.jmmc.mcs.util.ProcessHandler(new String[] {
                        yogaProgram, methodName, filename
                    });
            logger.fine("Making call using yoga script:" + yogaProgram + " " +
                methodName + " " + filename);
        }

        YogaExec pm = new YogaExec();
        ph.setProcessManager(pm);
        ph.start();
        ph.waitFor();
        result = pm.getContent();

        return result;
    }

    public static String doPost(String methodName, java.io.File xmlFile)
        throws Exception {
        String result = "";
        String targetURL = myPreferences.getPreference("yoga.remote.url");
        PostMethod myPost = new PostMethod(targetURL);
        Part[] parts;

        if (xmlFile == null) {
            parts = new Part[] { new StringPart("method", methodName) };
        } else {
            parts = new Part[] {
                    new StringPart("method", methodName),
                    new FilePart("userfile", xmlFile.getName(), xmlFile)
                };
        }

        try {
            myPost.setRequestEntity(new MultipartRequestEntity(parts,
                    myPost.getParams()));

            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams()
                  .setConnectionTimeout(5000);

            int status = client.executeMethod(myPost);

            if (status == HttpStatus.SC_OK) {
                result = myPost.getResponseBodyAsString();
            } else {
                logger.fine("Post for '" + methodName + "' failed");
                throw new Exception("Requets failed, response=" +
                    HttpStatus.getStatusText(status));
            }

            myPost.releaseConnection();
        } catch (Exception e) {
            myPost.releaseConnection();
            throw e;
        }

        return result;
    }

    public static void startFitsViewer(String filename) {
        logger.fine("request fits viewer to show '" + filename + "'");

        uk.ac.starlink.topcat.ControlWindow topcat = uk.ac.starlink.topcat.ControlWindow.getInstance();

        try {
            topcat.addTable(topcat.getTableFactory().makeStarTable(filename),
                filename, true);
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }

    protected class YogaExec implements fr.jmmc.mcs.util.ProcessManager {
        StringBuffer sb;

        public YogaExec() {
            sb = new StringBuffer();
        }

        public void processStarted() {
            logger.entering("" + this.getClass(), "processStarted");
        }

        public void processStoped() {
            logger.entering("" + this.getClass(), "processStoped");
        }

        public void processTerminated(int returnedValue) {
            logger.entering("" + this.getClass(), "processTerminated");
        }

        public void errorOccured(Exception exception) {
            logger.entering("" + this.getClass(), "errorOccured");
            exception.printStackTrace();
        }

        public void outputOccured(String line) {
            logger.entering("" + this.getClass(), "outputOccured");
            sb.append(line);
            logger.finest("occured line:" + line);
        }

        public String getContent() {
            return sb.toString();
        }
    }
}
