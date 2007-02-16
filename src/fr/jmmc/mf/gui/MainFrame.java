/*
 * MainFrame.java
 *
 * Created on 5 octobre 2006, 09:31
 */

package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ReportDialog;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Files;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Oitarget;
import fr.jmmc.mf.models.Parameters;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mcs.util.*;

import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.Component;
import java.awt.Color;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.lang.reflect.*;
/**
 * Main class that group general elements.
 * It implements TreeSelectionListener interface to show right modifiers Panels.
 *
 * @author  mella
 */
public class MainFrame extends javax.swing.JFrame implements TreeSelectionListener, TreeModelListener, SettingsViewerInterface {
    
    /** Model reference */
    static public SettingsModel rootSettingsModel=null;
    
    static Logger logger = Logger.getLogger("fr.jmmc.mf.gui.MainFrame");
    
    // List of viewer panel used to display sub components
    static TargetsPanel targetsPanel;    
    static FilesPanel filesPanel;
    static UserInfoPanel userInfoPanel;    
    static FitterPanel fitterPanel;
    static SettingsPanel settingsPanel;
    static TargetPanel targetPanel;    
    static FilePanel filePanel;
    static ModelPanel modelPanel;
    static ParametersPanel parametersPanel;
    static ResultPanel resultPanel;
    static StatusBar statusBar;
    
    // Application actions
    public static Action runFitAction;
    public static Action getYogaVersionAction;
    public static Action getModelListAction;
    
    // Preference and general actions
    public static Action quitAction;
    public static Action showPrefAction;
    public static Action savePrefAction;
    public static Action restorePrefAction;
    public static Action showRevisionAction;
    public static Action showHelpAction;

    // Model actions
    public static Action newModelAction;
    public static Action loadModelAction;
    public static Action saveModelAction;
    
    static Preferences myPreferences = Preferences.getInstance();
    
    // adjusted to true after user modification
    // toggled again to false after saving action
    protected boolean modified;       
   
    private static PlasticListener plasticServer_;

    private static MainFrame instance=null;
    
    
    /** Creates new form MainFrame */
    public MainFrame() {
        instance=this;
        // instanciate actions
        runFitAction = new RunFitAction();
        getYogaVersionAction = new GetYogaVersionAction();
        getModelListAction = new GetModelListAction();
        quitAction = new QuitAction();
        savePrefAction = new SavePrefAction();
        restorePrefAction = new RestorePrefAction();
        showPrefAction = new ShowPrefAction();
        showRevisionAction = new ShowRevisionAction();
        showHelpAction = new ShowHelpAction();
        newModelAction     = new NewModelAction();
        loadModelAction     = new LoadModelAction();
        saveModelAction     = new SaveModelAction();                        
        
        // Because settingsTree has rootSettingsModel as tree model,
        // we need to init rootSettingsModel before entering initComponents
        rootSettingsModel = new SettingsModel();
        
        initComponents();
        ToolTipManager.sharedInstance().registerComponent(settingsTree);
        
        targetsPanel = new TargetsPanel(this);
        filesPanel = new FilesPanel(this);
        fitterPanel =new FitterPanel(this);
        userInfoPanel = new UserInfoPanel(this);
        settingsPanel = new SettingsPanel(filesPanel,
                targetsPanel, fitterPanel, userInfoPanel);
        targetPanel = new TargetPanel(this);
        filePanel = new FilePanel();
        modelPanel = new ModelPanel();
        parametersPanel = new ParametersPanel(this);
        resultPanel = new ResultPanel(this);
        
    
        // To permit modifier panel changes,
        // Register myself as treeselectionListener
        settingsTree.addTreeSelectionListener(this);
        
        // next part is done using netbeans design area
        //settingsTree.setCellRenderer(new MyCellRenderer());        
     
        // Place engine Panel
        enginePanel.add(new EnginePanel());                                
        
        /* Plastic transmitter. */
        plasticServer_ = new PlasticListener();
        
        initMenuBar();        
        statusBar = new StatusBar();
        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
        
        setStatus("Application started");
        setTitle("ModelFitting V"+Resources.getResource("mf.version"));     
             
        showSettingElement(rootSettingsModel.getRootSettings());            
    }
    
    public static SettingsModel getSettingsModel(){
        return rootSettingsModel;
    }
    
    public static MainFrame getInstance(){
        return instance;
    }
    
    public static PlasticListener getPlasticServer(){
        return plasticServer_;
    }
    
    /**
     * In a multi document, this should return the selected pane associated
     * model or null
     */
    
    public SettingsModel getSelectedSettingsModel(){
        return rootSettingsModel;        
    }
    
    public static void expandSettingsTree(){        
        // Next line does not work because node doesn't respond to treeNode interface
        //McsClass.expandAll(settingsTree,true);                    
    }
    
    public void setStatus(String text){
        statusBar.show(text);
    }
    
    /** This method is called from within the constructor to
     * initialize the form's menu bar.
     */
    public void initMenuBar() {
        logger.entering(""+this.getClass(), "initMenuBar");
        
        JMenuBar jMenuBar        = new JMenuBar();
        this.setJMenuBar(jMenuBar);
        
        // First level menus

        // Add File
        JMenu fileMenu        = new JMenu();
        fileMenu.setText("File");
        jMenuBar.add(fileMenu);
        
        // Add Edit
        JMenu editMenu        = new JMenu();
        editMenu.setText("Edit");
        jMenuBar.add(editMenu);

        // Add Interop
        JMenu interopMenu        = new JMenu();
        interopMenu.setText("Interop");
        jMenuBar.add(interopMenu);                
        
        // Add Help
        JMenu helpMenu        = new JMenu();
        helpMenu.setText("Help");
        jMenuBar.add(helpMenu);
       
        // Second level menus                
        
        // Add File->NewModel
        JMenuItem menuItem = new JMenuItem();
        Action    action   = newModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);        
        
        // Add File->LoadModel
        menuItem = new JMenuItem();
        action   = loadModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);
        
        // Add File->SaveModel
        menuItem     = new JMenuItem();
        action       = saveModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);
        fileMenu.add(new JSeparator());
        
        // Add File->quit
        menuItem     = new JMenuItem();        
        menuItem.setAction(quitAction);
        fileMenu.add(menuItem);                      
        
        // Add Edit->ShowPrefs
        menuItem     = new JMenuItem();        
        menuItem.setAction(showPrefAction);
        editMenu.add(menuItem);
        
        // Add Edit->SavePrefs
        menuItem     = new JMenuItem();
        menuItem.setAction(savePrefAction);
        editMenu.add(menuItem);
        
        // Add Edit->RestorePrefs
        menuItem     = new JMenuItem();        
        menuItem.setAction(restorePrefAction);
        editMenu.add(menuItem);        
        editMenu.add(new JSeparator());

        // Add Help->ShowRevision
        menuItem     = new JMenuItem();
        menuItem.setAction(showRevisionAction);
        helpMenu.add(menuItem);
        
        // Add Help->ShowHelp
        menuItem     = new JMenuItem();
        menuItem.setAction(showHelpAction);
        helpMenu.add(menuItem);
        
        helpMenu.add(new JSeparator());
        
        // Add Help->ShowTooltips
        menuItem     = new JCheckBoxMenuItem("Tooltips");
        menuItem.setModel(fr.jmmc.mcs.util.PreferencedButtonModel.getInstance(myPreferences, "help.tooltips.show"));
        helpMenu.add(menuItem);
        

        // Fill Interop menu
        try {
            interopMenu.add( plasticServer_.getRegisterAction( true ) );
            interopMenu.add( plasticServer_.getRegisterAction( false ) );
            interopMenu.add( plasticServer_.getHubStartAction( true ) );
            interopMenu.add( plasticServer_.getHubStartAction( false ) );
            interopMenu.add( new HubWatchAction( plasticServer_ ) );
            interopMenu.addSeparator();
            //    interopMenu.add( tableTransmitter_.getBroadcastAction() );
//            interopMenu.add( tableTransmitter_.createSendMenu() );
            interopMenu.addSeparator();
            //          interopMenu.add( interophelpAct );
        } catch ( SecurityException e ) {
            interopMenu.setEnabled( false );
            logger.warning( "Security manager denies use of PLASTIC" );
        }
        
        
        
    }
    
    public static void closeTab(java.awt.Component c){
        // not static logger.entering(""+this.getClass(), "closeTab");
        tabbedPane.remove(c);
    }
    
    /**
     * Responds to tree selection events.
     */
    public void valueChanged(TreeSelectionEvent e){
        logger.entering(""+this.getClass(), "valueChanged");
        Object o = e.getPath().getLastPathComponent();
        showSettingElement(o);
        
    }

    
    
    public  void showSettingElement(Object o){
        logger.entering(""+this.getClass(), "selectModifierPanel");
        if(o==null){
            return;
        }
        
        if (getSettingsModel().isModified()){
            tabbedPane.setTitleAt(0,"Settings *");
        }else{
            tabbedPane.setTitleAt(0,"Settings");
        }

        
        modifierPanel.removeAll();
        if ( o instanceof Settings ){
            //settingsPanel.show((Settings)o);
            settingsPanel.show(rootSettingsModel.getRootSettings(), getSelectedSettingsModel());
            modifierPanel.add(settingsPanel);            
        }else if ( o instanceof Targets){
            targetsPanel.show((Targets)o);
            modifierPanel.add(targetsPanel);
        }else if ( o instanceof Files){
            filesPanel.show((Files)o, getSelectedSettingsModel());
            modifierPanel.add(filesPanel);
        }else if ( o instanceof Target){
            targetPanel.show((Target)o, rootSettingsModel.getRootSettings());
            modifierPanel.add(targetPanel);
        }else if ( o instanceof Model){
            modelPanel.show((Model)o, getSelectedSettingsModel());
            modifierPanel.add(modelPanel);
        }else if ( o instanceof File){
            filePanel.show((File)o);
            modifierPanel.add(filePanel);
        }else if ( o instanceof FileLink){
            FileLink link = (FileLink)o; 
            filePanel.show((File)link.getFileRef());
            modifierPanel.add(filePanel);
        }else if ( o instanceof Parameters){
            parametersPanel.show((Parameters)o);
            modifierPanel.add(parametersPanel);
        }else if ( o instanceof Result){
            resultPanel.show((Result)o);
            modifierPanel.add(resultPanel);
        }else{            
            modifierPanel.add(new JLabel("missing modifier panel for '"+o.getClass()+"' objects"));
            if ( o instanceof Files){
                Files f = (Files)o;
                logger.fine("Selected files contains "+f.getFileCount());                
            }else if ( o instanceof Targets){
                Targets t = (Targets)o;
                logger.fine("Selected targets contains "+t.getTargetCount());                
            }
        }   
        modifierPanel.revalidate();
        modifierPanel.repaint();                
    }
    /**
     * Responds to tree structure changes
     */
    public void treeStructureChanged(javax.swing.event.TreeModelEvent e){        
        logger.entering(""+this.getClass(), "treeStructureChanged");
        showSettingElement(rootSettingsModel.getRoot());        
    }
    
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent e){
         
    }
    public void treeNodesInserted(javax.swing.event.TreeModelEvent e){
        
    }
    public void treeNodesChanged(javax.swing.event.TreeModelEvent e){
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        tabbedPane = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        treeAndSettingsSplitPane = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        settingsTree = new javax.swing.JTree();
        modifierPanel = new javax.swing.JPanel();
        enginePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.X_AXIS));

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(200, 22));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(200, 363));
        settingsTree.setCellRenderer(new MyCellRenderer());
        settingsTree.setEditable(true);
        settingsTree.setModel(rootSettingsModel);
        jScrollPane2.setViewportView(settingsTree);

        treeAndSettingsSplitPane.setLeftComponent(jScrollPane2);

        modifierPanel.setLayout(new javax.swing.BoxLayout(modifierPanel, javax.swing.BoxLayout.X_AXIS));

        treeAndSettingsSplitPane.setRightComponent(modifierPanel);

        jSplitPane1.setTopComponent(treeAndSettingsSplitPane);

        jSplitPane1.setBottomComponent(enginePanel);

        mainPanel.add(jSplitPane1);

        tabbedPane.addTab("Settings", mainPanel);

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 626, 592);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        quitAction.actionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        quitAction.actionPerformed(null);
    }//GEN-LAST:event_formWindowClosed
    
    
    protected class ShowPrefAction extends fr.jmmc.mcs.util.MCSAction {
        /** Preferences view */
        PreferencesView preferencesView;
        public ShowPrefAction(){
            super("showPreferences");
            preferencesView = new PreferencesView();
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                preferencesView.setVisible(true);
                logger.fine("Showing preferences");
            } catch (Exception exc) {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }
    
    
    // @todo try to move it into the mcs preferences area
    protected class SavePrefAction extends fr.jmmc.mcs.util.MCSAction {
        public SavePrefAction(){
            super("savePreferences");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                myPreferences.saveToFile();
                //@todo move next line into Preferences 
                logger.fine("Saving preferences");
            } catch (Exception exc) {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }
    
    protected class RestorePrefAction extends fr.jmmc.mcs.util.MCSAction {
        public RestorePrefAction(){
            super("restorePreferences");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                myPreferences.resetToDefaultPreferences();
                //@todo move next line into Preferences 
                logger.fine("Restoring preferences");
            } catch (Exception exc) {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }          
    /** Display another tab with revision informations */
    protected class ShowRevisionAction extends fr.jmmc.mcs.util.MCSAction {
        public ShowRevisionAction(){
            super("showRevision");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {            
            logger.fine("Requesting revision display");                                    
            try {
                URL url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/Releases.html");                                                                     
                TabbedPanel rp = new TabbedPanel("");
                rp.setPage(url);
                tabbedPane.addTab("Revision", rp);                
                
            } catch (Exception exc) {
                  new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);   
            }
        }
    }
    
    /** Display another tab with help informations */
    protected class ShowHelpAction extends fr.jmmc.mcs.util.MCSAction {
        public ShowHelpAction(){
            super("showHelp");
        }        
        public void actionPerformed(java.awt.event.ActionEvent e) {            
            logger.fine("Requesting Help display");
            try {
                URL url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/Help.html");                                                                     
                TabbedPanel rp = new TabbedPanel("");
                rp.setPage(url);
                tabbedPane.addTab("Help", rp);                                
            } catch (Exception exc) {
                  new ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);   
            }
        }
    }
    
    protected class RunFitAction extends fr.jmmc.mcs.util.MCSAction {
        String methodName="runFit";
        
        public RunFitAction(){
            super("runFit");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {            
            logger.fine("Requesting yoga '"+methodName+"' call");
            
            String result="";                                                
            try {
                // Create temp file.
                java.io.File targetFile = java.io.File.createTempFile("tmpSettings", ".xml");
                // Delete temp file when program exits.
                targetFile.deleteOnExit();
                rootSettingsModel.saveSettingsFile(targetFile, true);
                 if(myPreferences.getPreferenceAsBoolean("yoga.remote.use")){
                    result=doPost(methodName, targetFile);
                }else{                
                    result=doExec(methodName, targetFile);
                }        
                setStatus("Fitting process finished");
            } catch (Exception ex) {
                logger.warning(ex.getClass().getName() + " "+ ex.getMessage());
                ex.printStackTrace();
                result="Error:"+ex.getMessage();
                setStatus("Error during fitting process");
            }
            
            TabbedPanel rp = new TabbedPanel(result);
            tabbedPane.addTab("Fit Result", rp);    
            int i1 = result.indexOf("START_XML_RESULT");
            int i2 = result.indexOf("END_XML_RESULT");
            String xml = result.substring(i1+"START_XML_RESULT".length(),i2);
            
            try{
                java.io.StringReader reader   = new java.io.StringReader(xml);
                Settings      newModel = (Settings) Settings.unmarshal(reader);
                Settings prevModel = rootSettingsModel.getRootSettings();
                Model m;
                
                // add href from previous files
                File[] newFiles = newModel.getFiles().getFile();
                File[] prevFiles = prevModel.getFiles().getFile();
                
                // no check is done to assert that every newFile have been updated
                for (int i = 0; i < newFiles.length; i++) {
                    File newFile = newFiles[i];
                    String newId  = newFile.getId();
                    for (int j = 0; j < prevFiles.length; j++) {
                        File prevFile = prevFiles[j];
                        String prevId  = prevFile.getId();
                        if(prevId.equals(newId)){
                            newFile.setHref(prevFile.getHref());
                            Oitarget[] oitargets = prevFile.getOitarget();    
                            for (int k = 0; k < oitargets.length; k++) {
                                newFile.addOitarget(oitargets[i]);
                            }
                        }
                    }    
                }                
                rootSettingsModel.setRootSettings(newModel);
                showSettingElement(rootSettingsModel.getRootSettings());
                logger.info("Settings created");
            }catch(Exception ex){
                logger.warning(ex.getClass().getName() + " "+ ex.getMessage());
            }
            
            
        }
    }
    

    protected class GetYogaVersionAction extends fr.jmmc.mcs.util.MCSAction {
        String methodName="getYogaVersion";
        public GetYogaVersionAction(){
            super("getYogaVersion");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {             
            logger.fine("Requesting yoga '"+methodName+"' call");
            String result="";
            try {
                
                if(myPreferences.getPreferenceAsBoolean("yoga.remote.use")){
                    result=doPost(methodName);
                }else{                
                    result=doExec(methodName);
                }         
                setStatus("Yoga version is '"+result.trim()+"'");
                
            } catch (Exception ex) {
                logger.warning(ex.getClass().getName() + " "+ ex.getMessage());                               
                setStatus("Can't get Yoga version");
            }                                                           
        }
    }
    
    protected class YogaExec implements fr.jmmc.mcs.util.ProcessManager{
        StringBuffer sb;
        
        public YogaExec(){
            sb = new StringBuffer();
        }
        public void processStarted(){
            logger.entering(""+this.getClass(), "processStarted");
        }
        public void processStoped(){
            logger.entering(""+this.getClass(), "processStoped");
        }
        public void processTerminated(int returnedValue){
            logger.entering(""+this.getClass(), "processTerminated");
        }
        public void errorOccured(Exception exception){
            logger.entering(""+this.getClass(), "errorOccured");
            exception.printStackTrace();
        }
        public void outputOccured(String line){
            logger.entering(""+this.getClass(), "outputOccured");
            sb.append(line);
            logger.finest("occured line:"+line);
        }
        
        public String getContent(){
            return sb.toString();
        }
        
    }
    // call yoga.sh with method Name
    public String doExec(String methodName) throws Exception{            
            String yogaProgram=myPreferences.getPreference("yoga.local.home")+"/bin/yoga.sh";
            logger.fine("Making call using yoga script:"+yogaProgram+" "+methodName);
            
            String result="";
            // Run main application waiting for end of cat process
            fr.jmmc.mcs.util.ProcessHandler ph = new fr.jmmc.mcs.util.ProcessHandler(new String[]{yogaProgram, methodName});
            YogaExec pm = new YogaExec();
            ph.setProcessManager(pm);
            ph.start();
            ph.waitFor();
            result=pm.getContent();
            
            return result;
        }       
    
    public String doPost(String methodName)throws Exception{
            String result="";
            String targetURL = myPreferences.getPreference("yoga.remote.url");
            PostMethod myPost = new PostMethod(targetURL);
            Part[] parts =
            {
                new StringPart("method", methodName)
            };
            
            try{
                myPost.setRequestEntity( new MultipartRequestEntity(parts, myPost.getParams()));
                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                int status = client.executeMethod(myPost);
                if (status == HttpStatus.SC_OK) {
                    result=myPost.getResponseBodyAsString();
                } else {
                    logger.fine("Post for '"+methodName+"' failed");            
                    throw new Exception("Requets failed, response=" + HttpStatus.getStatusText(status));                    
                }                
                myPost.releaseConnection();             
            }catch(Exception e){
                myPost.releaseConnection();
                throw e;
            }
            return result;
        }                
    
     // call yoga.sh with method Name
    public String doExec(String methodName, java.io.File targetFile) throws Exception{            
            String yogaProgram=myPreferences.getPreference("yoga.local.home")+"/bin/yoga.sh";
            String filename=targetFile.getAbsolutePath();
            logger.fine("Making call using yoga script:"+yogaProgram+" "+methodName+" "+filename);
            
            String result="";
            // Run main application waiting for end of cat process
            fr.jmmc.mcs.util.ProcessHandler ph = new fr.jmmc.mcs.util.ProcessHandler(new String[]{yogaProgram, methodName, filename});
            YogaExec pm = new YogaExec();
            ph.setProcessManager(pm);
            ph.start();
            ph.waitFor();
            result=pm.getContent();            
            return result;
        }
    
        public String doPost(String methodName, java.io.File targetFile)throws Exception{                                        
            String result="";
            String targetURL = myPreferences.getPreference("yoga.remote.url");
            PostMethod myPost = new PostMethod(targetURL);
            Part[] parts =
            {
                new StringPart("method", methodName),
                new FilePart("userfile", targetFile.getName(), targetFile)                                         
            };
            
            try{
                myPost.setRequestEntity( new MultipartRequestEntity(parts, myPost.getParams()));
                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                int status = client.executeMethod(myPost);
                if (status == HttpStatus.SC_OK) {
                    result=myPost.getResponseBodyAsString();
                } else {
                    logger.fine("Post for '"+methodName+"' failed");            
                    throw new Exception("Requets failed, response=" + HttpStatus.getStatusText(status));                    
                }                
                myPost.releaseConnection();             
            }catch(Exception e){
                myPost.releaseConnection();
                throw e;
            }
            return result;
        }                
    
    
    protected class GetModelListAction extends fr.jmmc.mcs.util.MCSAction {
        String methodName="getModelList";
        public GetModelListAction(){
            super("getModelList");
        }                
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.fine("Requesting yoga '"+methodName+"' call");            
            String result="";                
            try{
                if(myPreferences.getPreferenceAsBoolean("yoga.remote.use")){
                    result=doPost(methodName);
                }else{                
                    result=doExec(methodName);
                }                
                // create a result panel
                TabbedPanel rp = new TabbedPanel(result);
                tabbedPane.addTab("Available Models", rp);
                
                // Indicates to the rootSettingsModel list of availables models
                java.io.StringReader reader   = new java.io.StringReader(result);
                Model newModel = (Model) Model.unmarshal(reader);
                rootSettingsModel.setSupportedModels(newModel.getModel());
                
            } catch (Exception ex) {
                logger.warning(ex.getClass().getName() + " " + ex.getMessage());                
                setStatus("Can't get available models");
            }         
        }
    }
    
    protected class QuitAction extends MCSAction
    {
        public QuitAction()
        {
            super("quit");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.info("Bye!");

            // here will come a loop over every open settings            
            ModifyAndSaveObject[] objs;
            objs = new ModifyAndSaveObject[] {rootSettingsModel};
            // next line should quit if no modification occurs
            // or user answer yes to save every modifications 
            UtilsClass.checkUserModificationAndQuit(objs);
        }
    }
    
    
        /**
     * Action which displays a window giving some information about 
     * the state of the PLASTIC hub.
     */
    private class HubWatchAction extends MCSAction {
        private final uk.ac.starlink.plastic.HubManager hubManager_;
        private JFrame hubWindow_;

        HubWatchAction( uk.ac.starlink.plastic.HubManager hubManager ) {
            /*super( "Show Registered Applications", null,
                   "Display applications registered with the PLASTIC hub" );
             **/
            super("hubWatch");
            hubManager_ = hubManager;
        }
        
        public void actionPerformed( java.awt.event.ActionEvent evt ) {
            if ( hubWindow_ == null ) {
                hubWindow_ = new uk.ac.starlink.plastic.PlasticListWindow( hubManager_
                        .getApplicationListModel() );
                hubWindow_.setTitle( "PLASTIC apps" );
                //AuxWindow.positionAfter( ControlWindow.this, hubWindow_ );
                hubWindow_.pack();
            }
            hubWindow_.setVisible( true );
        }
    }
    
    protected class NewModelAction extends fr.jmmc.mcs.util.MCSAction {
        public String lastDir = System.getProperty("user.dir");
        
        public NewModelAction() {
            super("newModel");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering(""+this.getClass(), "actionPerformed");
            
            // We only have one document so: ask to save previously lost...
            
            if ( ! UtilsClass.askToSaveUserModification(rootSettingsModel)){                
                 return;
            }
            
            rootSettingsModel = new SettingsModel();
            settingsTree.setModel(rootSettingsModel);
            settingsTree.removeTreeSelectionListener(MainFrame.this);
            // To permit modifier panel changes,
            // Register myself as treeselectionListener
            settingsTree.addTreeSelectionListener(MainFrame.this);
                    
            rootSettingsModel.fireUpdate();                                
            showSettingElement(rootSettingsModel.getRootSettings());            
            setStatus("New model ready for modifications");
        }
    }
    
    protected class LoadModelAction extends fr.jmmc.mcs.util.MCSAction {
        public String lastDir = System.getProperty("user.dir");
        
        public LoadModelAction() {
            super("loadModel");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering(""+this.getClass(), "actionPerformed");
            
            try{
                JFileChooser fileChooser = new JFileChooser();
                
                // Set in previous load directory
                if (lastDir != null) {
                    fileChooser.setCurrentDirectory(new java.io.File(lastDir));
                }
                
                // Open file chooser
                int returnVal = fileChooser.showOpenDialog(null);
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fileChooser.getSelectedFile();
                    lastDir = file.getParent();
                    rootSettingsModel.loadSettingsFile(file);                    
                }
                
            }catch(Exception exc){
                new ReportDialog(null, true, exc).setVisible(true);
            }
        }
    }
    
    protected class SaveModelAction extends fr.jmmc.mcs.util.MCSAction {
        public String lastDir = System.getProperty("user.dir");
        
        public SaveModelAction() {
            super("saveModel");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering(""+this.getClass(), "actionPerformed");
            
            JFileChooser fileChooser = new JFileChooser();
            
            // Set in previous save directory
            if (lastDir != null) {
                fileChooser.setCurrentDirectory(new java.io.File(lastDir));
            }
            
            try {
                // Open filechooser
                int returnVal = fileChooser.showSaveDialog(null);
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fileChooser.getSelectedFile();
                    lastDir = file.getParent();                    
                    // Ask to overwrite
                    if (file.exists()) {
                        String message = "File '" + file.getName() +
                                "' already exists\nDo you want to overwrite this file?";
                        
                        // Modal dialog with yes/no button
                        int answer = JOptionPane.showConfirmDialog(null, message);
                        
                        if (answer == JOptionPane.YES_OPTION) {
                            rootSettingsModel.saveSettingsFile(file, false);
                        }
                    } else {
                        rootSettingsModel.saveSettingsFile(file,false);
                    }
                }
            } catch (Exception exc) {
                ReportDialog dialog = new ReportDialog(null, true, exc);
                dialog.setVisible(true);
                // if (dialog.returnedValue="Report")
            }
        }
    }
    

    
    // Cell renderer used by the settings tree 
    // it make red faulty nodes and place help tooltips
    protected class MyCellRenderer extends DefaultTreeCellRenderer{
        
        //getTreeCellRendererComponent
        public Component getTreeCellRendererComponent(JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
                        
            super.getTreeCellRendererComponent(
                tree, value, sel,expanded, leaf, row,
                hasFocus);            
            
            setToolTipText("TBD");
            // Next part try to get valide state of serialized xml objects
           try{                
                String methodName="validate";                 
                Class[] c  = new Class[0] ;
                Method m = value.getClass().getMethod(methodName,c);
                Object[] o = new Object [0];
                m.invoke(value,o);
                //logger.fine("method invoked using reflexion");                
            }catch(Exception e){
                setForeground(Color.red);
                if (e.getCause()!=null){
                    setToolTipText(e.getCause().getMessage());
                }else{
                    setToolTipText(e.getMessage());    
                }
            }            
            return this;
        }
    }
           
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel enginePanel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel modifierPanel;
    private static javax.swing.JTree settingsTree;
    private static javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JSplitPane treeAndSettingsSplitPane;
    // End of variables declaration//GEN-END:variables
    
}
