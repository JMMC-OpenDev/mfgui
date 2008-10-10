/*
 * MFGui.java
 *
 * Created on 12 février 2008, 14:39
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.gui.WindowCenterer;
import fr.jmmc.mcs.util.*;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.*;

import java.net.URL;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JOptionPane;


/**
 *
 * @author  mella
 */
public class MFGui extends javax.swing.JFrame implements WindowListener{
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.MFGui");
    static Preferences myPreferences = Preferences.getInstance();
    static StatusBar statusBar;

    // Application actions
    public static Action getYogaVersionAction;

    // Preference and general actions
    public static Action quitAction;
    public static Action showPrefAction;
    public static Action savePrefAction;
    public static Action restorePrefAction;
    public static Action showHelpAction;
    public static Action showLogGuiAction;

    // Model actions
    public static Action newModelAction;
    public static Action loadModelAction;
    public static Action loadRemoteModelAction;
    public static Action saveModelAction;
    private static PlasticListener plasticServer_;
    private static MFGui instance = null;
              
    private static javax.swing.JTabbedPane tabbedPane_;

    /** Creates new form MFGui */
    public MFGui(String[] filenames) {
        instance = this;
        // instanciate actions       
        getYogaVersionAction = new GetYogaVersionAction();
        quitAction = new QuitAction();
        savePrefAction = new SavePrefAction();
        restorePrefAction = new RestorePrefAction();
        showPrefAction = new ShowPrefAction();
        showHelpAction = new ShowHelpAction();
         showLogGuiAction = new ShowLogGuiAction();
        newModelAction = new NewModelAction();
        loadModelAction = new LoadModelAction();
        loadRemoteModelAction = new LoadRemoteModelAction();
        saveModelAction = new SaveModelAction();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);


        tabbedPane_ = new javax.swing.JTabbedPane();        
        tabbedPane_.setMinimumSize(new java.awt.Dimension(640, 480));
        tabbedPane_.setPreferredSize(new java.awt.Dimension(640, 480));
        getContentPane().add(tabbedPane_, java.awt.BorderLayout.CENTER);
        
    
        /* Plastic transmitter. */
        plasticServer_ = new PlasticListener();

        initMenuBar();
        statusBar = new StatusBar();
        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
        setStatus("Application started");
        setTitle("ModelFitting V" + Resources.getResource("mf.version"));

        if (filenames.length >= 1) {
            try {
                java.io.File file = new java.io.File(filenames[0]);
                addSettingsPane(new SettingsPane(file));
            } catch (Exception exc) {
                new FeedbackReport(null, true, exc);
            }
        }
        
        pack();    
    }

    public static MFGui getInstance() {
        return instance;
    }

    public static PlasticListener getPlasticServer() {
        return plasticServer_;
    }

    public void setStatus(String text) {
        statusBar.show(text);
    }

    private void addSettingsPane(SettingsPane p) {
        tabbedPane_.add(p, p.getSettingsModel().getAssociatedFilename());
        tabbedPane_.setSelectedComponent(p);
    }

    
    /* This method return selectedPane or null . It also updates titles */
    public SettingsPane getSelectedSettingsPane() {
        int idx = tabbedPane_.getSelectedIndex();
        if (idx < 0) {
            return null;
        }
        SettingsPane sp = null;
        if (tabbedPane_.getComponentAt(idx) instanceof SettingsPane) {
            sp = (SettingsPane) tabbedPane_.getComponentAt(idx);
            tabbedPane_.setTitleAt(idx, sp.rootSettingsModel.getAssociatedFilename());
            logger.fine("Selected settingsPane name:" +
                    sp.rootSettingsModel.getAssociatedFilename());
        }
        return sp;
    }

    public static void closeTab(java.awt.Component c) {
        // not static logger.entering(""+this.getClass(), "closeTab");
        tabbedPane_.remove(c);
    }

    /** This method is called from within the constructor to
     * initialize the form's menu bar.
     */
    public void initMenuBar() {
        logger.entering("" + this.getClass(), "initMenuBar");

        JMenuBar jMenuBar = new JMenuBar();
        this.setJMenuBar(jMenuBar);

        // First level menus

        // Add File
        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        jMenuBar.add(fileMenu);
        
        // Add Edit
        JMenu editMenu = new JMenu();
        editMenu.setText("Edit");
        jMenuBar.add(editMenu);

        // Add Advanced
        JMenu advancedMenu = new JMenu();
        advancedMenu.setText("Advanced");
        jMenuBar.add(advancedMenu);

        // Add Help
        JMenu helpMenu = new JMenu();
        helpMenu.setText("Help");
        jMenuBar.add(helpMenu);

        // Second level menus

        // Add File->NewModel
        JMenuItem menuItem = new JMenuItem();
        Action action = newModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);

        // Add File->LoadModel
        menuItem = new JMenuItem();
        action = loadModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);

        // Add File->LoadRemoteModel
        menuItem = new JMenuItem();
        action = loadRemoteModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);

        // Add LoadDemo File
        JMenu demoMenu = new JMenu();
        demoMenu.setText(" Load demos");
        fileMenu.add(demoMenu);

        
        Hashtable<String,String> demo = new Hashtable();
        demo.put("Binary disk (Obj1)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj1_binary_disk_with_oidata/Obj1_binary_disk_with_oidata.xml"
            );
        demo.put("Binary punct (Obj1)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj1_binary_punct_with_oidata/Obj1_binary_punct_with_oidata.xml"
            );
        demo.put("Uniform disk (Obj1)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj1_uniform_disk_with_oidata/Obj1_uniform_disk_with_oidata.xml"
        );
        demo.put("Binary punct (Obj2)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj1_binary_punct_with_oidata/Obj2_binary_punct_with_oidata.xml"
            );
        demo.put("Disk and punct (Obj2)",
        "http://jmmc.fr/~mella/mfRes/ref5/Obj2_disk_and_punct_with_oidata/Obj2_disk_and_punct_with_oidata.xml"
                );
        demo.put("Triple punct (Obj2)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj2_triple_punct_with_oidata/Obj2_binary_punct_with_oidata.xml"
            );
        demo.put("Uniform disk (Obj2)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj2_uniform_disk_with_oidata/Obj1_uniform_disk_with_oidata.xml"
        );
        
        Enumeration keys = demo.keys();
        while (keys.hasMoreElements()){
            String title = (String)keys.nextElement();
            // Add demoMenu->demo1
            menuItem = new JMenuItem();
            action = new LoadDemoModelAction(demo.get(title), title);
            menuItem.setAction(action);
            demoMenu.add(menuItem);
        }
        
        // Add File->SaveModel
        menuItem = new JMenuItem();
        action = saveModelAction;
        menuItem.setAction(action);
        fileMenu.add(menuItem);
        fileMenu.add(new JSeparator());

        // Add File->quit
        menuItem = new JMenuItem();
        menuItem.setAction(quitAction);
        fileMenu.add(menuItem);

        // Add Edit->ShowPrefs
        menuItem = new JMenuItem();
        menuItem.setAction(showPrefAction);
        editMenu.add(menuItem);

        // Add Edit->SavePrefs
        menuItem = new JMenuItem();
        menuItem.setAction(savePrefAction);
        editMenu.add(menuItem);

        // Add Edit->RestorePrefs
        menuItem = new JMenuItem();
        menuItem.setAction(restorePrefAction);
        editMenu.add(menuItem);
        editMenu.add(new JSeparator());
     
        // Add Help->ShowHelp
        menuItem = new JMenuItem();
        menuItem.setAction(showHelpAction);
        helpMenu.add(menuItem);
        
        // Add Help->ShowAbout
        menuItem = new JMenuItem();
        menuItem.setAction(ModelFitting.aboutBoxAction());
        helpMenu.add(menuItem);

        // Add Help->ShowFeedbackForm
        menuItem = new JMenuItem();
        menuItem.setAction(ModelFitting.feedbackReportAction());
        helpMenu.add(menuItem);

        helpMenu.add(new JSeparator());

        // Add Help->ShowTooltips
        menuItem = new JCheckBoxMenuItem("Tooltips");
        menuItem.setModel(fr.jmmc.mcs.util.PreferencedButtonModel.getInstance(
                myPreferences, "help.tooltips.show"));
        helpMenu.add(menuItem);

        // Add Advanced->Interop
        JMenu interopMenu = new JMenu();
        interopMenu.setText("Interop");
        advancedMenu.add(interopMenu);
        advancedMenu.addSeparator();

        // Fill Interop menu
        try {
            interopMenu.add(plasticServer_.getRegisterAction(true));
            interopMenu.add(plasticServer_.getRegisterAction(false));
            interopMenu.add(plasticServer_.getHubStartAction(true));
            interopMenu.add(plasticServer_.getHubStartAction(false));
            interopMenu.add(new HubWatchAction(plasticServer_));

            //          interopMenu.addSeparator();
            //          interopMenu.add( tableTransmitter_.getBroadcastAction() );
            //          interopMenu.add( tableTransmitter_.createSendMenu() );
            //          interopMenu.addSeparator();
            //          interopMenu.add( interophelpAct );
        } catch (SecurityException e) {
            interopMenu.setEnabled(false);
            logger.warning("Security manager denies use of PLASTIC");
        }

        // Add Advanced->Interop
        menuItem = new JMenuItem();
        menuItem.setAction(showLogGuiAction);
        advancedMenu.add(menuItem);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final String[] fargs = args;
        java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new MFGui(fargs).setVisible(true);
                }
            });
    }

    public void windowOpened(WindowEvent e) {        
    }

    public void windowClosing(WindowEvent e) {
       quitAction.actionPerformed(null);
    }

    public void windowClosed(WindowEvent e) {     
    }

    public void windowIconified(WindowEvent e) {        
    }

    public void windowDeiconified(WindowEvent e) {        
    }

    public void windowActivated(WindowEvent e) {        
    }

    public void windowDeactivated(WindowEvent e) {        
    }

    protected class GetYogaVersionAction extends fr.jmmc.mcs.util.MCSAction {
        String methodName = "getYogaVersion";

        public GetYogaVersionAction() {
            super("getYogaVersion");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.fine("Requesting yoga '" + methodName + "' call");

            String result = "";

            try {
                result = ModelFitting.instance_.execMethod(methodName, null);
                setStatus("Yoga version is '" + result.trim() + "'");
            } catch (Exception ex) {
                logger.warning(ex.getClass().getName() + " " + ex.getMessage());
                setStatus("Can't get Yoga version");
            }
        }
    }

    protected class QuitAction extends MCSAction {
        public QuitAction() {
            super("quit");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            // here will come a loop over every open settings
            ModifyAndSaveObject[] objs = new ModifyAndSaveObject[] {  };

            if (getSelectedSettingsPane() != null) {
                objs = new ModifyAndSaveObject[] {
                        getSelectedSettingsPane().rootSettingsModel
                    };

                // next line should quit if no modification occurs
                // or user answer yes to save every modifications
            }

            UtilsClass.checkUserModificationAndQuit(objs);
        }
    }

    protected class ShowPrefAction extends fr.jmmc.mcs.util.MCSAction {
        /** Preferences view */
        PreferencesView preferencesView;

        public ShowPrefAction() {
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

    protected class ShowLogGuiAction extends fr.jmmc.mcs.util.MCSAction {
        JFrame logGuiFrame;

        public ShowLogGuiAction() {
            super("showLogGui");
            logGuiFrame = new JFrame("Log GUI");
            logGuiFrame.getContentPane().add(imx.loggui.LogMaster.getLogView());
            logGuiFrame.setJMenuBar(new imx.loggui.LogMasterMenuBar(logGuiFrame));
            // HACK remove file-> exit
            logGuiFrame.getJMenuBar().getMenu(0).remove(5);
            imx.loggui.LogMaster.getLogMaster().configDefault();
            imx.loggui.LogMaster.getLogMaster().configExternalLHFF();
            imx.loggui.LogMaster.getLogMaster().refresh();
            logGuiFrame.setMinimumSize(new java.awt.Dimension(640, 480));
            logGuiFrame.setPreferredSize(new java.awt.Dimension(640, 480));
            logGuiFrame.pack();
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logGuiFrame.setVisible(true);
            imx.loggui.LogMaster.getLogMaster().refresh();
        }
    }

    // @todo try to move it into the mcs preferences area
    protected class SavePrefAction extends fr.jmmc.mcs.util.MCSAction {
        public SavePrefAction() {
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
        public RestorePrefAction() {
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

    /**
     * Action which displays a window giving some information about
     * the state of the PLASTIC hub.
     */
    private class HubWatchAction extends MCSAction {
        private final uk.ac.starlink.plastic.HubManager hubManager_;
        private JFrame hubWindow_;

        HubWatchAction(uk.ac.starlink.plastic.HubManager hubManager) {
            /*super( "Show Registered Applications", null,
                   "Display applications registered with the PLASTIC hub" );
             **/
            super("hubWatch");
            hubManager_ = hubManager;
        }

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (hubWindow_ == null) {
                hubWindow_ = new uk.ac.starlink.plastic.PlasticListWindow(hubManager_.getApplicationListModel());
                hubWindow_.setTitle("PLASTIC apps");
                //AuxWindow.positionAfter( ControlWindow.this, hubWindow_ );
                hubWindow_.pack();
            }

            hubWindow_.setVisible(true);
        }
    }

    protected class NewModelAction extends fr.jmmc.mcs.util.MCSAction {
        public String lastDir = System.getProperty("user.home");

        public NewModelAction() {
            super("newModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering("" + this.getClass(), "actionPerformed");
            addSettingsPane(new SettingsPane());
            setStatus("New model ready for modifications");
        }
    }

    protected class LoadModelAction extends fr.jmmc.mcs.util.MCSAction {
        public String lastDir = System.getProperty("user.home");

        public LoadModelAction() {
            super("loadModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering("" + this.getClass(), "actionPerformed");

            try {
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
                    addSettingsPane(new SettingsPane(file));
                }
            } catch (Exception exc) {
                new FeedbackReport(null, true, exc);
            }
        }
    }

    protected class LoadDemoModelAction extends javax.swing.AbstractAction {
        String demoURL_ = "";

        public LoadDemoModelAction(String demoURL, String demoDesc) {
            this.putValue(Action.NAME,demoDesc);
            demoURL_=demoURL;
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering("" + this.getClass(), "actionPerformed");
            try {               
                    addSettingsPane(new SettingsPane(new URL(demoURL_)));
            } catch (Exception exc) {
                new FeedbackReport(null, true, exc);
            }
        }
    }
     protected class LoadRemoteModelAction extends fr.jmmc.mcs.util.MCSAction {
        public String lastURL = "";

        public LoadRemoteModelAction() {
            super("loadRemoteModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering("" + this.getClass(), "actionPerformed");
            try {
                String s = (String) JOptionPane.showInputDialog(
                        "Enter URL", lastURL);
                if (s != null && s.length() > 5) {
                    lastURL = s;
                    URL url = new URL(s);
                    addSettingsPane(new SettingsPane(url));
                }

            } catch (Exception exc) {
                new FeedbackReport(null, true, exc);
            }
        }
    }

    protected class SaveModelAction extends fr.jmmc.mcs.util.MCSAction {
        public String lastDir = System.getProperty("user.home");

        public SaveModelAction() {
            super("saveModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering("" + this.getClass(), "actionPerformed");

            try {
                // @todo replace next coede part by enable and disabled event asociated to actions...
                if (getSelectedSettingsPane()==null){
                    return;                
                }
                SettingsModel settingsModel = getSelectedSettingsPane().rootSettingsModel;

                java.io.File file;
                file = settingsModel.associatedFile;

                
                    // Open a filechooser in previous save directory
                    JFileChooser fileChooser = new JFileChooser();

                    if (lastDir != null) {
                        fileChooser.setCurrentDirectory(new java.io.File(
                                lastDir));
                    }
                
                    if (file != null) {
                        fileChooser.setSelectedFile(file);
                    }

                    fileChooser.setDialogTitle("Save " +
                        settingsModel.getAssociatedFilename() + "?");

                    // Open filechooser
                    int returnVal = fileChooser.showSaveDialog(null);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = fileChooser.getSelectedFile();

                        // Ask to overwrite
                        if (file.exists()) {
                            String message = "File '" + file.getName() +
                                "' already exists\nDo you want to overwrite this file?";

                            // Modal dialog with yes/no button
                            int answer = JOptionPane.showConfirmDialog(null,
                                    message);

                            if (answer != JOptionPane.YES_OPTION) {
                                return;
                            }
                        }
                    } else {
                        return;
                    }

                lastDir = file.getParent();
                // Fix user associated file and save it with result
                settingsModel.setAssociatedFile(file);
                settingsModel.saveSettingsFile(file, false);
                /* ask to update title */
                getSelectedSettingsPane();
                
            } catch (Exception exc) {
                new FeedbackReport(null, true, exc);
            }
        }
    }

    /** Display another tab with help informations */
    protected class ShowHelpAction extends fr.jmmc.mcs.util.MCSAction {

        public ShowHelpAction() {
            super("showHelp");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.fine("Requesting Help display");

            try {
                URL url = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/Help.html");
                TabbedPanel rp = new TabbedPanel("");
                rp.setPage(url);
                tabbedPane_.addTab("Help", rp);
            } catch (Exception exc) {
                new FeedbackReport(null, true, exc);
            }
        }
    }       
}
