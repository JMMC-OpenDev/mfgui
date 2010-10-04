/*
JMMC
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.gui.ApplicationDataModel;
import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mcs.util.Http;
import fr.jmmc.mcs.util.MCSExceptionHandler;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Target;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import java.util.*;
import java.util.logging.*;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.Metadata;
import org.astrogrid.samp.SampMap;
import org.astrogrid.samp.client.AbstractMessageHandler;
import org.astrogrid.samp.client.ClientProfile;
import org.astrogrid.samp.client.DefaultClientProfile;
import org.astrogrid.samp.client.HubConnection;
import org.astrogrid.samp.client.HubConnector;

/**
 *
 * @author mella
 */
public class ModelFitting extends fr.jmmc.mcs.gui.App {

    final static String rcsId = "$Id: ModelFitting.java,v 1.34 2010-10-04 10:16:56 mella Exp $";
    final static Logger logger = Logger.getLogger("fr.jmmc.mf.gui.ModelFitting");
    static Preferences myPreferences;
    static MFGui gui = null;
    static HttpClient client_ = null;

    /**
     * Creates a new ModelFitting object.
     *
     * @param args DOCUMENT ME!
     */
    public ModelFitting(String[] args) {
        super(args);
    }

    protected void init(String[] args) {
        // Set default resource for application
        fr.jmmc.mcs.util.Resources.setResourceName("fr/jmmc/mf/gui/Resources");
        myPreferences = Preferences.getInstance();

        try {
            // Using invokeAndWait to be in sync with the main thread :
            SwingUtilities.invokeAndWait(new Runnable() {

                /**
                 * Initializes the swing components with their actions in EDT
                 */
                public void run() {
                    // there is a conflict if one option is given
                    // the app take it as argument!!##"!!
                    // gui = new MFGui(args);
                    // workaround make no more argument support
                    gui = new MFGui(new String[]{});

                    // define the application frame
                    // TODO should be refactored so that Gui uses the App.getFrame()
                    App.setFrame(gui);
                }
            });

        } catch (InterruptedException ie) {
            // propagate the exception :
            throw new RuntimeException("ModelFitting.init : interrupted", ie);
        } catch (InvocationTargetException ite) {
            // propagate the internal exception :
            throw new RuntimeException("ModelFitting.init : exception", ite.getCause());
        }

        // declare as one VO app
        declareInteroperability();
    }

    @Override
    protected void execute() {
        gui.setVisible(true);
    }

    @Override
    protected boolean finish() {
        return gui.finish();
    }

    protected static void exit() {
        logger.info("Thank you for using this software!");
    }

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale locale = Locale.US;
        Locale.setDefault(locale);
        System.setProperty("user.language", "us");
        logger.info("Setting locale to:" + locale);


        // Set the default timezone to GMT to handle properly the date in UTC :
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        // Change Swing defaults :
        changeSwingDefaults();

        // Install exception handlers :
        MCSExceptionHandler.installSwingHandler();

        ModelFitting mf = new ModelFitting(args);
    }

    /**
     * Change several default values for Swing rendering.
     */
    private static void changeSwingDefaults() {

        // Force Locale for Swing Components :
        JComponent.setDefaultLocale(Locale.US);

        // Let the tooltip stay longer (60s) :
        ToolTipManager.sharedInstance().setInitialDelay(250);
        ToolTipManager.sharedInstance().setDismissDelay(60000);
    }

    /** This is the main wrappers method to execute yoga actions.
     *  The job is delegated to a local program or distant web service
     *  according yoga.remote.use preference.
     *  @param methodName name of method to wrap
     *  @param xmlFile file to give as argument of the method or null if
     *         no one is requested
     */
    public static Response execMethod(String methodName, java.io.File xmlFile)
            throws Exception {
        return execMethod(methodName, xmlFile, "");
    }

    /** This is the main wrappers method to execute yoga actions
     *  The job is delegated to a local program or distant web service
     *  according yoga.remote.use preference.
     *  @param methodName name of method to wrap
     *  @param xmlFile file to give as argument of the method or null if
     *         no one is requested
     */
    public static Response execMethod(String methodName, java.io.File xmlFile, String methodArg)
            throws Exception {
        String xmlResult = null;
        if (myPreferences.getPreferenceAsBoolean("yoga.remote.use")) {
            xmlResult = doPost(methodName, xmlFile, methodArg);
        } else {
            xmlResult = doExec(methodName, xmlFile, methodArg);
        }
        java.io.StringReader reader = new java.io.StringReader(xmlResult);
        Response r = (Response) UtilsClass.unmarshal(Response.class, reader);
        String errors = UtilsClass.getErrorMsg(r);
        if (errors.length() > 1) {
            JTextArea t = new JTextArea(errors, 20, 80);
            JScrollPane sp = new JScrollPane(t);
            javax.swing.JOptionPane.showMessageDialog(null, sp, "Error ",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        String info = UtilsClass.getOutputMsg(r);
        if (info.length() > 1) {
            JTextArea t = new JTextArea(info, 20, 80);
            JScrollPane sp = new JScrollPane(t);
            javax.swing.JOptionPane.showMessageDialog(null, sp, "Info ",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
        return r;
    }

    /**
     * Execute given command on local machine.
     *
     * @param methodName name of method to call
     * @param xmlFile setting file
     * @param methodArg optionnal arguments
     *
     * @return the output of the process
     *
     * @throws Exception exception tbd
     */
    private static String doExec(String methodName, java.io.File xmlFile, String methodArg)
            throws Exception {
        String result = "";
        String yogaProgram = myPreferences.getPreference("yoga.local.home")
                + myPreferences.getPreference("yoga.local.progname");
        String filename = null;

        // Run main application waiting for end of cat process
        fr.jmmc.mcs.util.ProcessHandler ph;

        if (xmlFile != null) {
            filename = xmlFile.getAbsolutePath();
        }

        if (xmlFile == null) {
            ph = new fr.jmmc.mcs.util.ProcessHandler(new String[]{yogaProgram, methodName, methodArg});
            logger.fine("Making call using yoga script:" + yogaProgram + " " + methodName + " "
                    + methodArg);
        } else {
            ph = new fr.jmmc.mcs.util.ProcessHandler(new String[]{
                        yogaProgram, methodName, filename, methodArg
                    });
            logger.fine("Making call using yoga script:" + yogaProgram + " " + methodName + " "
                    + filename + " " + methodArg);
        }

        YogaExec pm = new YogaExec();
        ph.setProcessManager(pm);
        ph.start();
        ph.waitFor();
        result = pm.getContent();
        logger.finest("exec result=\n" + result);
        return result;
    }

    private static String doExec(String methodName, java.io.File xmlFile)
            throws Exception {
        return doExec(methodName, xmlFile, "");
    }

    public static String doPost(String methodName, java.io.File xmlFile, String methodArg)
            throws Exception {
        String result = "";
        String targetURL = myPreferences.getPreference("yoga.remote.url");
        PostMethod myPost = new PostMethod(targetURL);
        Part[] parts;
        if (methodArg != null && methodArg.length() == 0) {
            methodArg = null;
        }

        if ((xmlFile == null) && (methodArg == null)) {
            parts = new Part[]{new StringPart("method", methodName)};
        } else if ((xmlFile == null) && (methodArg != null)) {
            parts = new Part[]{
                        new StringPart("method", methodName), new StringPart("arg", methodArg)
                    };
        } else if ((xmlFile != null) && (methodArg == null)) {
            parts = new Part[]{
                        new StringPart("method", methodName),
                        new FilePart("userfile", xmlFile.getName(), xmlFile)
                    };
        } else {
            parts = new Part[]{
                        new StringPart("method", methodName),
                        new FilePart("userfile", xmlFile.getName(), xmlFile),
                        new StringPart("arg", methodArg)
                    };
        }

        try {
            myPost.setRequestEntity(new MultipartRequestEntity(parts, myPost.getParams()));

            if (client_ == null) {
                client_ = Http.getHttpClient();
            }
            // Since we can have long term exchanges
            client_.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

            int status = client_.executeMethod(myPost);

            if (status == HttpStatus.SC_OK) {
                StringBuffer sb = new StringBuffer();
                Reader reader = new InputStreamReader(
                        myPost.getResponseBodyAsStream(), myPost.getResponseCharSet());
                StringWriter sw = new StringWriter();
                char cbuf[] = new char[1024];
                int len = reader.read(cbuf);
                while (len > 0) {
                    sw.write(cbuf, 0, len);
                    len = reader.read(cbuf);
                }
                result = sw.toString();
                logger.fine("Post for '" + methodName + " " + methodArg + "' ok");
            } else {
                logger.fine("Post for '" + methodName + " " + methodArg + "' failed");
                throw new Exception("Requets failed, response=" + HttpStatus.getStatusText(status));
            }

            myPost.releaseConnection();
        } catch (Exception e) {
            myPost.releaseConnection();
            throw e;
        }

        logger.finest("post result=\n" + result);
        return result;
    }

    public static String doPost(String methodName, java.io.File xmlFile)
            throws Exception {
        return doPost(methodName, xmlFile, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     */
    public static void startFitsViewer(String filename) {
        logger.fine("request fits viewer to show '" + filename + "'");

        uk.ac.starlink.topcat.ControlWindow topcat = uk.ac.starlink.topcat.ControlWindow.getInstance();

        try {
            topcat.addTable(topcat.getTableFactory().makeStarTable(filename), filename, true);
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }

    private void declareInteroperability() {
        // Construct a connector
        ClientProfile profile = DefaultClientProfile.getProfile();
        HubConnector conn = new HubConnector(profile);

        // Configure it with metadata about this application
        Metadata meta = new Metadata();
        ApplicationDataModel adm = getSharedApplicationDataModel();
        meta.setName(adm.getProgramName());
        meta.setDescriptionText("Application that does stuff");
        conn.declareMetadata(meta);

        // Prepare to receive messages with specific MType(s)
        conn.addMessageHandler(new AbstractMessageHandler("stuff.do") {

            public Map processCall(HubConnection c, String senderId, Message msg) {
                // do stuff
                System.out.println("msg = " + msg);
                return null;
            }
        });
        // Prepare to receive messages with specific MType(s)
        conn.addMessageHandler(new AbstractMessageHandler("LITpro.runfit") {

            public Map processCall(HubConnection c, String senderId, Message msg) throws Exception {
                SampMap params = Message.asMessage(msg.getParams());
                String xmlModel = params.getString("model");
                String filename = params.getString("filename");

                if (filename == null) {
                    throw new Exception("Missing parameter 'filename'");
                }
                if (xmlModel == null) {
                    throw new Exception("Missing parameter 'model'");
                }

                fr.jmmc.mcs.gui.StatusBar.show("Samp message received : building new model");

                SettingsModel sm = new SettingsModel();
                sm.getRootSettings().setUserInfo("Settings file built from incomming request of external VO application");
                sm.addFile(new java.io.File(filename));
                StringReader sr = new StringReader(xmlModel);
                Model m = (Model) UtilsClass.unmarshal(Model.class, sr);
                fr.jmmc.mf.models.File f = sm.getRootSettings().getFiles().getFile(0);
                String targetIdent = f.getOitarget(0).getTarget();
                Target target = sm.addTarget(targetIdent);
                Model[] models = m.getModel();
                for (int i = 0; i < models.length; i++) {
                    Model model = models[i];
                    target.addModel(model);
                }
                gui.addSettings(sm);

                return null;
            }
        });

        // This step required even if no custom message handlers added.
        conn.declareSubscriptions(conn.computeSubscriptions());

        // Keep a look out for hubs if initial one shuts down
        conn.setAutoconnect(10);

        // Broadcast a message
        //conn.getConnection().notifyAll(new Message("stuff.event.doing"));

    }

    protected static class YogaExec implements fr.jmmc.mcs.util.ProcessManager {

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
