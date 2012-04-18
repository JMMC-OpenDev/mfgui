/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.component.MessagePane;

import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.SwingSettings;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.network.Http;
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampMessageHandler;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.Preferences;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Target;
import fr.nom.tam.fits.FitsException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import org.apache.commons.httpclient.HttpClient;

import java.util.logging.*;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.SampException;
import org.ivoa.util.runner.LocalLauncher;

/**
 *
 * @author mella
 */
public class ModelFitting extends fr.jmmc.jmcs.App {    

    final static String rcsId = "$Id: ModelFitting.java,v 1.41 2011-04-07 14:07:27 mella Exp $";
    final static String className = ModelFitting.class.getName();
    final static Logger logger = Logger.getLogger(className);
    static Preferences myPreferences;
    static MFGui gui = null;
    static HttpClient client_ = null;
    /** Title of message pane shown after reception of message/error from the remote service */
    protected static final String LITPRO_SERVER_MESSAGE_TITLE = "LITpro server message";

    /**
     * Creates a new ModelFitting object.
     *
     * @param args command-line options.
     */
    public ModelFitting(final String[] args) {
        super(args);
    }

    /**
     * Initialize application objects
     * @param args ignored arguments
     *
     * @throws RuntimeException if the AppLauncher initialisation failed
     */
    @Override
    protected void init(final String[] args) {
        // Set default resource for application
        fr.jmmc.jmcs.util.ResourceUtils.setResourceName("fr/jmmc/mf/gui/Resources");
        
        myPreferences = Preferences.getInstance();

        // Initialize job runner:
        LocalLauncher.startUp();
        
        // Using invokeAndWait to be in sync with this thread :
        // note: invokeAndWaitEDT throws an IllegalStateException if any exception occurs
        SwingUtils.invokeAndWaitEDT(new Runnable() {

            /**
             * Initializes the swing components with their actions in EDT
             */
            @Override
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
    }

    /**
     * Execute application body = make the application frame visible
     */
    @Override
    protected void execute() {
        SwingUtils.invokeLaterEDT(new Runnable() {

            /**
             * Show the application frame using EDT
             */
            @Override
            public void run() {
                logger.fine("ModelFitting.ready : handler called.");

                getFrame().setVisible(true);
            }
        });
    }

    /**
     * Hook to handle operations before closing application.
     *
     * @return should return true if the application can exit, false otherwise
     * to cancel exit.
     */
    @Override
    protected boolean finish() {
        return gui.finish();
    }

    /**
     * Hook to handle operations when exiting application.
     * @see App#exit(int)
     */
    @Override
    public void cleanup() {

        logger.info("Thank you for using this software!");

        // Stop job runner:
        LocalLauncher.shutdown();
        
        super.cleanup();
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
     *  @throws ExecutionException thrown if local execution fails
     *  @throws IllegalStateException if one unexpected (bad) things occurs 
     */
    public static Response execMethod(String methodName, java.io.File xmlFile) throws ExecutionException {
        return execMethod(methodName, xmlFile, "");
    }

    /** This is the main wrappers method to execute yoga actions
     *  The job is delegated to a local program or distant web service
     *  according yoga.remote.use preference.
     *  @param methodName name of method to wrap
     *  @param xmlFile file to give as argument of the method or null if
     *         no one is requested
     *  @throws ExecutionException if the local execution fails
     *  @throws IllegalStateException if one unexpected (bad) things occurs 
     */
    public static Response execMethod(String methodName, java.io.File xmlFile, String methodArg) throws ExecutionException {
        String xmlResult = null;
        if (myPreferences.getPreferenceAsBoolean("yoga.remote.use")) {
            try {
                xmlResult = doPost(methodName, xmlFile, methodArg);
            } catch (IOException ex) {
                throw new ExecutionException(ex);
            }
        } else {
            xmlResult = doExec(methodName, xmlFile, methodArg);
        }                
        
        if (logger.isLoggable(Level.FINE)){
            logger.fine(xmlResult);
        }
        
        Response r = (Response) UtilsClass.unmarshal(Response.class, xmlResult);
        String errors = UtilsClass.getErrorMsg(r);
        if (errors.length() > 1) {
            MessagePane.showErrorMessage(errors, LITPRO_SERVER_MESSAGE_TITLE);            
            logger.warning("Error occurs after following call to LITpro server : "+methodName+" "+methodArg);
        }
        String info = UtilsClass.getOutputMsg(r);
        if (info.length() > 1) {
            MessagePane.showMessage(info, LITPRO_SERVER_MESSAGE_TITLE);
        }
        return r;
    }

    /**
     * Execute given command on local machine.
     *
     * @param methodName name of method to call
     * @param xmlFile setting file
     * @param methodArg optional arguments
     *
     * @return the output of the process
     * @throws ExecutionException thrown if process launching fails
     * @throws IllegalStateException if one unexpected (bad) things occurs 
     */
    private static String doExec(String methodName, java.io.File xmlFile, String methodArg) throws ExecutionException {
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
            throw new ExecutionException("Can't execute LITpro local service", ex);
        } catch (InterruptedException ex) {
            throw new ExecutionException("LITpro local service have been interrupted", ex);
        }
    }

    /**
     * Execute given command on local machine.
     *
     * @param methodName name of method to call
     * @param xmlFile setting file
     *
     * @return the output of the process
     * @throws ExecutionException thrown if process launching fails
     * @throws IllegalStateException if one unexpected (bad) things occurs 
     */
    private static String doExec(String methodName, java.io.File xmlFile)
            throws ExecutionException {
        return doExec(methodName, xmlFile, "");
    }

    public static String doPost(String methodName, java.io.File xmlFile, String methodArg) throws IOException {
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
       * Create SAMP Message handlers
       */
      @Override
      protected void declareInteroperability() {
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

                SettingsModel sm = null;
                try {
                    sm = new SettingsModel();
                    sm.getRootSettings().setUserInfo("Settings file built from incomming request of external VO application");
                
                    // Try to read file on disk as one oifits file
                    sm.addFile(new File(filename));
                } catch (IOException ex) {
                    MessagePane.showErrorMessage("Could not load file from samp message : " + filename, ex);
                } catch (ExecutionException ex) {
                    MessagePane.showErrorMessage("Could not load file from samp message : " + filename, ex);
                } catch (FitsException ex) {
                    MessagePane.showErrorMessage("Could not load file from samp message : " + filename, ex);
                }

                // Try to build one new model object from given string
                Model modelContainer = (Model) UtilsClass.unmarshal(Model.class, xmlModel);

                final fr.jmmc.mf.models.File f = sm.getRootSettings().getFiles().getFile(0);
                final String targetIdent = f.getOitarget(0).getTarget();
                final Target target = sm.addTarget(targetIdent);

                for (Model model : modelContainer.getModel()) {
                    target.addModel(model);
                }

                final SettingsModel fsm = sm;
                // Refresh GUI :
                SwingUtils.invokeLaterEDT(new Runnable() {

                    /**
                     * Synchronized by EDT
                     */
                    public void run() {
                        // bring this application to front :
                        App.showFrameToFront();

                        gui.addSettings(fsm);
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
