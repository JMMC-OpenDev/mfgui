/*
JMMC
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.App;
import fr.jmmc.mcs.gui.MessagePane;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.gui.SwingSettings;
import fr.jmmc.mcs.interop.SampCapability;
import fr.jmmc.mcs.interop.SampMessageHandler;

import fr.jmmc.mcs.util.Http;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Target;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import java.util.logging.*;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;
import uk.ac.starlink.table.TableFormatException;

/**
 *
 * @author mella
 */
public class ModelFitting extends fr.jmmc.mcs.gui.App {

    final static String rcsId = "$Id: ModelFitting.java,v 1.41 2011-04-07 14:07:27 mella Exp $";
    final static String className = ModelFitting.class.getName();
    final static Logger logger = Logger.getLogger(className);
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
        // init swing application for science
        SwingSettings.setup();

        ModelFitting mf = new ModelFitting(args);
    }

    /** This is the main wrappers method to execute yoga actions.
     *  The job is delegated to a local program or distant web service
     *  according yoga.remote.use preference.
     *  @param methodName name of method to wrap
     *  @param xmlFile file to give as argument of the method or null if
     *         no one is requested
     */
    public static Response execMethod(String methodName, java.io.File xmlFile) {
        return execMethod(methodName, xmlFile, "");
    }

    /** This is the main wrappers method to execute yoga actions
     *  The job is delegated to a local program or distant web service
     *  according yoga.remote.use preference.
     *  @param methodName name of method to wrap
     *  @param xmlFile file to give as argument of the method or null if
     *         no one is requested
     */
    public static Response execMethod(String methodName, java.io.File xmlFile, String methodArg) {
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
            MessagePane.showErrorMessage(errors);
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
     */
    private static String doExec(String methodName, java.io.File xmlFile, String methodArg) {
        try {
            String result = "";
            String yogaProgram = myPreferences.getPreference("yoga.local.home") + myPreferences.getPreference("yoga.local.progname");
            String filename = null;
            // Run main application waiting for end of cat process
            fr.jmmc.mcs.util.ProcessHandler ph;
            if (xmlFile != null) {
                filename = xmlFile.getAbsolutePath();
            }
            if (xmlFile == null) {
                ph = new fr.jmmc.mcs.util.ProcessHandler(new String[]{yogaProgram, methodName, methodArg});
                logger.fine("Making call using yoga script:" + yogaProgram + " " + methodName + " " + methodArg);
            } else {
                ph = new fr.jmmc.mcs.util.ProcessHandler(new String[]{yogaProgram, methodName, filename, methodArg});
                logger.fine("Making call using yoga script:" + yogaProgram + " " + methodName + " " + filename + " " + methodArg);
            }
            YogaExec pm = new YogaExec();
            ph.setProcessManager(pm);
            ph.start();
            ph.waitFor();
            result = pm.getContent();
            logger.finest("exec result=\n" + result);
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException("Can't execute LITpro local service", ex);
        } catch (InterruptedException ex) {
            throw new IllegalStateException("LITpro local service have been interrupted", ex);
        }
    }

    private static String doExec(String methodName, java.io.File xmlFile)
            throws Exception {
        return doExec(methodName, xmlFile, "");
    }

    public static String doPost(String methodName, java.io.File xmlFile, String methodArg) {
        String result = "";

        // Build parts of request
        Part[] parts;
        if (methodArg != null && methodArg.length() == 0) {
            methodArg = null;
        }
        try {
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
        } catch (FileNotFoundException fnfe) {
            throw new IllegalStateException("Can't query LITpro remote webservice", fnfe);
        }

        // Try to perform post operation
        String targetURL = myPreferences.getPreference("yoga.remote.url");

        final PostMethod myPost = new PostMethod(targetURL);
        try {
            myPost.setRequestEntity(new MultipartRequestEntity(parts, myPost.getParams()));

            if (client_ == null) {
                client_ = Http.getHttpClient();
            }

            int status = client_.executeMethod(myPost);
            if (status == HttpStatus.SC_OK) {
                final Reader reader = new InputStreamReader(myPost.getResponseBodyAsStream(), myPost.getResponseCharSet());

                // TODO : define initialSize correctly (get http content length ?) :
                final StringWriter sw = new StringWriter(65535);
                char cbuf[] = new char[1024];
                int len = reader.read(cbuf);
                while (len > 0) {
                    sw.write(cbuf, 0, len);
                    len = reader.read(cbuf);
                }
                result = sw.toString();

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Post for '" + methodName + " " + methodArg + "' ok");
                }

            } else {

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Post for '" + methodName + " " + methodArg + "' failed");
                }

                throw new IllegalStateException(
                        "Can't query LITpro remote webservice\nurl: '"
                        + targetURL
                        + "'\nHTTP status:"
                        + HttpStatus.getStatusText(status));
            }

        } catch (HttpException ex) {
            throw new IllegalStateException("Can't query LITpro remote webservice\nurl: '"
                    + targetURL + "'", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't query LITpro remote webservice\nurl: '"
                    + targetURL + "'", ex);
        } finally {
            // Release the connection.
            myPost.releaseConnection();
        }

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("post result=\n" + result);
        }
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
        } catch (TableFormatException ex) {
            throw new IllegalStateException("Can't make topcat display file " + filename, ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't make topcat display file " + filename, ex);
        }

    }

    private void declareInteroperability() {
        // Add handler to load one new setting given oifits and model description

        new SampMessageHandler(SampCapability.LITPRO_START_SETTING) {

            /**
             * Implements message processing
             *
             * @param senderId public ID of sender client
             * @param message message with MType this handler is subscribed to
             * @throws SampException if any error occured while message processing
             */
            protected void processMessage(final String senderId, final Message message) throws SampException {

                final String xmlModel = (String) message.getParam("model");
                final String filename = (String) message.getParam("filename");

                if (filename == null) {
                    throw new SampException("Missing parameter 'filename'");
                }
                if (xmlModel == null) {
                    throw new SampException("Missing parameter 'model'");
                }

                StatusBar.show("Samp message received : building new model");

                final SettingsModel sm = new SettingsModel();
                sm.getRootSettings().setUserInfo("Settings file built from incomming request of external VO application");

                // Try to read file on disk as one oifits file
                sm.addFile(new File(filename));

                // Try to build one new model object from given string
                final StringReader sr = new StringReader(xmlModel);
                Model modelContainer = null;
                modelContainer = (Model) UtilsClass.unmarshal(Model.class, sr);

                final fr.jmmc.mf.models.File f = sm.getRootSettings().getFiles().getFile(0);
                final String targetIdent = f.getOitarget(0).getTarget();
                final Target target = sm.addTarget(targetIdent);

                for (Model model : modelContainer.getModel()) {
                    target.addModel(model);
                }

                // Refresh GUI :
                SwingUtilities.invokeLater(new Runnable() {

                    /**
                     * Synchronized by EDT
                     */
                    public void run() {
                        // bring this application to front :
                        App.showFrameToFront();

                        gui.addSettings(sm);
                    }
                });
            }
        };
    }

    protected static class YogaExec implements fr.jmmc.mcs.util.ProcessManager {

        StringBuffer sb;

        public YogaExec() {
            sb = new StringBuffer();
        }

        public void processStarted() {
            logger.entering(this.getClass().getName(), "processStarted");
        }

        public void processStoped() {
            logger.entering(this.getClass().getName(), "processStoped");
        }

        public void processTerminated(int returnedValue) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "processTerminated");
            }
        }

        public void errorOccured(Exception e) {
            logger.log(Level.SEVERE, this.getClass().getName() + " errorOccured", e);
        }

        public void outputOccured(String line) {
            sb.append(line);
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("occured line:" + line);
            }
        }

        public String getContent() {
            return sb.toString();
        }
    }
}
