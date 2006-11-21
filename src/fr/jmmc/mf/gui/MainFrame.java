/*
 * MainFrame.java
 *
 * Created on 5 octobre 2006, 09:31
 */

package jmmc.mf.gui;

import jmmc.mf.models.*;

import jmmc.mf.svr.ServerImpl;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;


/**
 * Main class building general elements.
 * It implements TreeSelectionListener interface to show right modifiers Panels.
 *
 * @author  mella
 */
public class MainFrame extends javax.swing.JFrame implements TreeSelectionListener, TreeModelListener {
    
    /** Model reference */
    static public SettingsModel rootSettingsModel=null;
    
    static Logger logger = Logger.getLogger("jmmc.mf.gui.MainFrame");
    static SettingsPanel settingsPanel ;
    static TargetPanel targetPanel ;
    static ModelPanel modelPanel ;
    static FilePanel filePanel;
    
    public static Action runFitAction;
    
    protected String lastDir = null;
    
    /** Creates new form MainFrame */
    public MainFrame() {
        // Set default resource for application
        jmmc.mcs.util.Resources.setResourceName("jmmc/mf/gui/Resources");
    
        // instanciate runfit action
        runFitAction = new RunFitAction();
        
        // Because settingsTree has rootSettingsModel as tree model,
        // we need to init rootSettingsModel before entering initComponents
        rootSettingsModel = new SettingsModel();
        
        initComponents();
        
        settingsPanel = new SettingsPanel();
        targetPanel = new TargetPanel();
        modelPanel = new ModelPanel();
        filePanel = new FilePanel();
    
        // To permit modifier panel changes,
        // Register myself as treeselectionListener
        settingsTree.addTreeSelectionListener(this);
        
        // Place engine Panel
        enginePanel.add(new EnginePanel());                
            
        
        initMenuBar();        
    }
    
    public static SettingsModel getSettingsModel(){
        return rootSettingsModel;        
    }
    
    /** This method is called from within the constructor to
     * initialize the form's menu bar.
     */
    public void initMenuBar() {
        logger.entering(""+this.getClass(), "initMenuBar");
        
        JMenuBar jMenuBar        = new JMenuBar();
        this.setJMenuBar(jMenuBar);
        
        // Add File
        JMenu fileMenu        = new JMenu();
        fileMenu.setText("File");
        jMenuBar.add(fileMenu);
        
        // Add Edit
        JMenu editMenu        = new JMenu();
        editMenu.setText("Edit");
        jMenuBar.add(editMenu);
        
        // Add Edit->Preferences
        JMenu preferencesMenu = new JMenu();
        preferencesMenu.setText("Preferences");
        editMenu.add(preferencesMenu);
        
        // Add File->LoadModel
        JMenuItem menuItem = new JMenuItem();
        Action    action   = rootSettingsModel.loadModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);
        
        // Add File->SaveModel
        menuItem     = new JMenuItem();
        action       = rootSettingsModel.saveModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);
        
        // Add Edit->Preferences->ShowPrefs
        menuItem     = new JMenuItem();
        action       = new ShowPrefAction();
        menuItem.setAction(action);
        preferencesMenu.add(menuItem);
        
        // Add Edit->Preferences->SavePrefs
        menuItem     = new JMenuItem();
        action       = new SavePrefAction();
        menuItem.setAction(action);
        preferencesMenu.add(menuItem);
        
        // Add Edit->Preferences->RestorePrefs
        menuItem     = new JMenuItem();
        action       = new RestorePrefAction();
        menuItem.setAction(action);
        preferencesMenu.add(menuItem);
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
        selectModifierPanel(o);
    }

    private void selectModifierPanel(Object o){
        logger.entering(""+this.getClass(), "selectModifierPanel");
        modifierPanel.removeAll();
        if ( o instanceof Settings || 
             o instanceof Files ||
             o instanceof Targets ){
            //settingsPanel.show((Settings)o);
            settingsPanel.show(rootSettingsModel.getRootSettings());
            modifierPanel.add(settingsPanel);            
        }else if ( o instanceof Target){
            targetPanel.show((Target)o, rootSettingsModel.getRootSettings());
            modifierPanel.add(targetPanel);
        }else if ( o instanceof Model){
            modelPanel.show((Model)o);
            modifierPanel.add(modelPanel);
        }else if ( o instanceof File){
            filePanel.show((File)o);
            modifierPanel.add(filePanel);
        }else if ( o instanceof FileLink){
            FileLink link = (FileLink)o; 
            filePanel.show((File)link.getFileRef());
            modifierPanel.add(filePanel);
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
        selectModifierPanel(rootSettingsModel.getRoot());        
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
        statusPanel = new javax.swing.JPanel();
        jTextArea1 = new javax.swing.JTextArea();
        tabbedPane = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        settingsAndTreePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        settingsTree = new javax.swing.JTree();
        modifierPanel = new javax.swing.JPanel();
        enginePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("...TBD...V0.1");
        statusPanel.setLayout(new javax.swing.BoxLayout(statusPanel, javax.swing.BoxLayout.X_AXIS));

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(1);
        jTextArea1.setText("Application started");
        jTextArea1.setMinimumSize(new java.awt.Dimension(480, 15));
        jTextArea1.setPreferredSize(new java.awt.Dimension(480, 15));
        statusPanel.add(jTextArea1);

        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.X_AXIS));

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        settingsAndTreePanel.setLayout(new java.awt.BorderLayout());

        settingsTree.setExpandsSelectedPaths(false);
        settingsTree.setModel(rootSettingsModel);
        jScrollPane2.setViewportView(settingsTree);

        settingsAndTreePanel.add(jScrollPane2, java.awt.BorderLayout.WEST);

        modifierPanel.setLayout(new javax.swing.BoxLayout(modifierPanel, javax.swing.BoxLayout.X_AXIS));

        settingsAndTreePanel.add(modifierPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setTopComponent(settingsAndTreePanel);

        jSplitPane1.setRightComponent(enginePanel);

        mainPanel.add(jSplitPane1);

        tabbedPane.addTab("Settings", mainPanel);

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        setBounds(0, 0, 626, 592);
    }// </editor-fold>//GEN-END:initComponents
    
    
    protected class ShowPrefAction extends MFAction {
        /** Preferences view */
        PreferencesView preferencesView;
        public ShowPrefAction(){
            super("showPreferences");
            preferencesView = new PreferencesView();
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                preferencesView.setVisible(true);
            } catch (Exception exc) {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }
    
    
    // @todo try to move it into the mcs preferences area
    protected class SavePrefAction extends MFAction {
        public SavePrefAction(){
            super("savePreferences");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                Preferences.getInstance().saveToFile();
            } catch (Exception exc) {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }
    
    protected class RestorePrefAction extends MFAction {
        public RestorePrefAction(){
            super("restorePreferences");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                Preferences.getInstance().resetToDefaultPreferences();
            } catch (Exception exc) {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }
    
    protected class RunFitAction extends MFAction {
        public RunFitAction(){
            super("runFit");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {            
            String result="";            
            String targetURL = "http://jmmc.fr/~mella/LITpro/run.php";
            PostMethod filePost = new PostMethod(targetURL);
            
            try {
                // Create temp file.
                java.io.File targetFile = java.io.File.createTempFile("tmpSettings", ".xml");
                // Delete temp file when program exits.
                targetFile.deleteOnExit();
                
                rootSettingsModel.saveSettingsFile(targetFile);
                
                Part[] parts =
                {
                    new FilePart("userfile", targetFile.getName(), targetFile)
                };
                filePost.setRequestEntity( new MultipartRequestEntity(parts, filePost.getParams()));
                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                int status = client.executeMethod(filePost);
                System.out.println("Uploading " + targetFile.getName() + " to " + targetURL);
                if (status == HttpStatus.SC_OK) {
                    logger.fine("Upload complete");
                    result = filePost.getResponseBodyAsString();
                } else {
                    logger.warning("Upload failed, response=" + HttpStatus.getStatusText(status));
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getClass().getName() + " "+ ex.getMessage());
                ex.printStackTrace();
                result="Error:"+ex.getMessage();
            } finally {
                filePost.releaseConnection();
            }
            
            ResultPanel rp = new ResultPanel(result);
            tabbedPane.addTab("Result", rp);
            
        }
    }
    /*
     protected class ShowXmlAction extends MFAction {
        public ShowXmlAction(){
            super("showXml");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent e) {
            
            StringBuffer sb = new StringBuffer();
            try {
                // Create temp file.
                java.io.File targetFile = java.io.File.createTempFile("tmpSettings", ".xml");
                // Delete temp file when program exits.
                targetFile.deleteOnExit();                
                rootSettingsModel.saveSettingsFile(targetFile);
                BufferedReader in = new BufferedReader(new FileReader(targetFile));
                String str;
                while ((str = in.readLine()) != null) {
                    process(str);
                }
                in.close();
                
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getClass().getName() + " "+ ex.getMessage());
                ex.printStackTrace();
                result="Error:"+ex.getMessage();
            } finally {
                filePost.releaseConnection();
            }
            
            ResultPanel rp = new ResultPanel(result.toString());
            tabbedPane.addTab("Result", rp);
            
        }
    }
     **/

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
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel modifierPanel;
    private javax.swing.JPanel settingsAndTreePanel;
    private javax.swing.JTree settingsTree;
    private javax.swing.JPanel statusPanel;
    private static javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    
}
