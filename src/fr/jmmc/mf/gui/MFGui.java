/*
 * MFGui.java
 *
 * Created on 12 février 2008, 14:39
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.MainMenuBar;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.util.*;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.net.URL;

import java.util.*;

import javax.swing.*;
import javax.swing.JOptionPane;


/**
 *
 * @author  mella
 */
public class MFGui extends javax.swing.JFrame implements WindowListener
{
    /** Class Name */
    static final String className_="fr.jmmc.mf.gui.MFGui";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className_);

   
    protected  static Preferences myPreferences = Preferences.getInstance();
    protected static StatusBar statusBar;

    // Application actions

    /**
     * DOCUMENT ME!
     */
    public static Action getYogaVersionAction;
    public static Action saveModelAction;

    // Model actions
   
   
    private static PlasticListener plasticServer_;

    /** instance link */
    private static MFGui instance = null;
    
    private static javax.swing.JTabbedPane tabbedPane_;

    /** Creates new form MFGui */
    public MFGui(String[] filenames)
    {
        instance                  = this;
        // instanciate actions       
        getYogaVersionAction      = new GetYogaVersionAction();
       
        new ShowPrefAction();
        new NewModelAction();
        new LoadModelAction();
        new LoadRemoteModelAction();
        saveModelAction=new SaveModelAction();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);

        tabbedPane_ = new javax.swing.JTabbedPane();
        tabbedPane_.setMinimumSize(new java.awt.Dimension(800, 800));
        tabbedPane_.setPreferredSize(new java.awt.Dimension(800, 800));
        getContentPane().add(tabbedPane_, java.awt.BorderLayout.CENTER);

        /* Plastic transmitter. */
        plasticServer_ = new PlasticListener();

        // Build demo action into initMenuBar
        initMenuBar();
        // Handle menu bar
        setJMenuBar(new MainMenuBar(this));

        // Handle status bar
        statusBar = new StatusBar();
        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
        setStatus("Application started");
        
        setTitle("ModelFitting V"+ModelFitting.getSharedApplicationDataModel().getProgramVersion());

        if (filenames.length >= 1)
        {
            try
            {
                java.io.File file = new java.io.File(filenames[0]);
                addSettingsPane(new SettingsPane(file));
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }

        pack();
    }

    /**
     * Return the singleton instance.
     *
     * @return the singleton instance of MFGui.
     */
    public static MFGui getInstance()
    {
        return instance;
    }

   public static PlasticListener getPlasticServer()
    {
        return plasticServer_;
    }

   public void setStatus(String text)
    {
        statusBar.show(text);
    }

   private void addSettingsPane(SettingsPane p)
    {
        tabbedPane_.add(p, p.getSettingsModel().getAssociatedFilename());
        tabbedPane_.setSelectedComponent(p);
    }

    /* This method return selectedPane or null . It also updates titles */
   public SettingsPane getSelectedSettingsPane()
    {
        int idx = tabbedPane_.getSelectedIndex();

        if (idx < 0)
        {
            return null;
        }

        SettingsPane sp = null;

        if (tabbedPane_.getComponentAt(idx) instanceof SettingsPane)
        {
            sp = (SettingsPane) tabbedPane_.getComponentAt(idx);
            tabbedPane_.setTitleAt(idx, sp.rootSettingsModel.getAssociatedFilename());
            logger.fine("Selected settingsPane name:" +
                sp.rootSettingsModel.getAssociatedFilename());
        }

        return sp;
    }

   public static void closeTab(java.awt.Component c)
    {
        // not static logger.entering(""+this.getClass(), "closeTab");
        tabbedPane_.remove(c);
    }

    /** This method is called from within the constructor to
     * initialize the form's menu bar.
     */
    public void initMenuBar()
    {
        logger.entering("" + this.getClass(), "initMenuBar");
    
        // Add Advanced
        JMenu advancedMenu = new JMenu();
        advancedMenu.setText("Advanced");
        //jMenuBar.add(advancedMenu);

        // Second level menus
        JMenuItem                 menuItem;
        Action                    action;

        Hashtable<String, String> demo = new Hashtable();
        demo.put("Binary disk (Obj1)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj1_binary_disk_with_oidata/Obj1_binary_disk_with_oidata.xml");
        demo.put("Binary punct (Obj1)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj1_binary_punct_with_oidata/Obj1_binary_punct_with_oidata.xml");
        demo.put("Uniform disk (Obj1)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj1_uniform_disk_with_oidata/Obj1_uniform_disk_with_oidata.xml");
        demo.put("Binary punct (Obj2)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj2_binary_punct_with_oidata/Obj2_binary_punct_with_oidata.xml");
        demo.put("Disk and punct (Obj2)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj2_disk_and_punct_with_oidata/Obj2_disk_and_punct_with_oidata.xml");
        demo.put("Triple punct (Obj2)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj2_triple_punct_with_oidata/Obj2_triple_punct_with_oidata.xml");
        demo.put("Uniform disk (Obj2)",
            "http://jmmc.fr/~mella/mfRes/ref5/Obj2_uniform_disk_with_oidata/Obj2_uniform_disk_with_oidata.xml");
        
        Enumeration keys = demo.keys();
        int i=1;
        while (keys.hasMoreElements()){
            String title = (String)keys.nextElement();
            action = new LoadDemoModelAction("demoModel"+i, demo.get(title), title);
            action.putValue(action.NAME, title);
            i++;                 
        }
    
        // Add Advanced->Interop
        JMenu interopMenu = new JMenu();
        interopMenu.setText("Interop");
        advancedMenu.add(interopMenu);
        advancedMenu.addSeparator();

        // Fill Interop menu
        try
        {
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
        }
        catch (SecurityException e)
        {
            interopMenu.setEnabled(false);
            logger.warning("Security manager denies use of PLASTIC");
        }
      
    }

    /** 
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        final String[] fargs = args;
        java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    new MFGui(fargs).setVisible(true);
                }
            });
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    public void windowOpened(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    public void windowClosing(WindowEvent e)
    {
        ModelFitting.quitAction().actionPerformed(null);
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    public void windowClosed(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    public void windowIconified(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    public void windowDeiconified(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    public void windowActivated(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    public void windowDeactivated(WindowEvent e)
    {
    }

    /**
     * App lifecycle finish step.
     *
     * @return true if App can quit or false.
     */
    protected boolean finish()
    {
        ModifyAndSaveObject[] objs = new ModifyAndSaveObject[] {  };

        if (getSelectedSettingsPane() != null)
        {
            objs = new ModifyAndSaveObject[] { getSelectedSettingsPane().rootSettingsModel };
        }

        return UtilsClass.checkUserModificationToQuit(objs);
    }

    protected class GetYogaVersionAction extends RegisteredAction
    {
        String methodName = "getYogaVersion";

        public GetYogaVersionAction()
        {
            super(className_,"getYogaVersion");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.fine("Requesting yoga '" + methodName + "' call");

            String result = "";

            try
            {
                result = ModelFitting.instance_.execMethod(methodName, null);
                setStatus("Yoga version is '" + result.trim() + "'");
            }
            catch (Exception ex)
            {
                logger.warning(ex.getClass().getName() + " " + ex.getMessage());
                setStatus("Can't get Yoga version");
            }
        }
    }

    protected class ShowPrefAction extends RegisteredAction
    {
        /** Preferences view */
        PreferencesView preferencesView;

        public ShowPrefAction()
        {
            super(className_,"showPreferences");
            flagAsPreferenceAction();
            preferencesView = new PreferencesView();
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            try
            {
                preferencesView.setVisible(true);
                logger.fine("Showing preferences");
            }
            catch (Exception exc)
            {
                // @todo handle this error at user level
                exc.printStackTrace();
            }
        }
    }   

    /**
     * Action which displays a window giving some information about
     * the state of the PLASTIC hub.
     */
    private class HubWatchAction extends MCSAction
    {
        private final uk.ac.starlink.plastic.HubManager hubManager_;
        private JFrame                                  hubWindow_;

        HubWatchAction(uk.ac.starlink.plastic.HubManager hubManager)
        {
            /*super( "Show Registered Applications", null,
               "Display applications registered with the PLASTIC hub" );
             **/
            super("hubWatch");
            hubManager_ = hubManager;
        }

        public void actionPerformed(java.awt.event.ActionEvent evt)
        {
            if (hubWindow_ == null)
            {
                hubWindow_ = new uk.ac.starlink.plastic.PlasticListWindow(hubManager_.getApplicationListModel());
                hubWindow_.setTitle("PLASTIC apps");
                //AuxWindow.positionAfter( ControlWindow.this, hubWindow_ );
                hubWindow_.pack();
            }

            hubWindow_.setVisible(true);
        }
    }

    protected class NewModelAction extends RegisteredAction
    {
        public String lastDir = System.getProperty("user.home");

        public NewModelAction()
        {
            super(className_,"newModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.entering("" + this.getClass(), "actionPerformed");
            addSettingsPane(new SettingsPane());
            setStatus("New model ready for modifications");
        }
    }

    protected class LoadModelAction extends RegisteredAction
    {
        public String lastDir = System.getProperty("user.home");

        public LoadModelAction()
        {
            super(className_,"loadModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.entering("" + this.getClass(), "actionPerformed");

            try
            {
                JFileChooser fileChooser = new JFileChooser();

                // Set in previous load directory
                if (lastDir != null)
                {
                    fileChooser.setCurrentDirectory(new java.io.File(lastDir));
                }

                // Open file chooser
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    java.io.File file = fileChooser.getSelectedFile();
                    lastDir = file.getParent();
                    addSettingsPane(new SettingsPane(file));
                }
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }
    }

    protected class LoadDemoModelAction extends RegisteredAction
    {
        String demoURL_ = "";

        public LoadDemoModelAction(String id, String demoURL, String demoDesc)
        {
            super(className_, id, demoDesc);
            this.putValue(Action.NAME, demoDesc);
            demoURL_ = demoURL;
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.entering("" + this.getClass(), "actionPerformed");

            try
            {
                addSettingsPane(new SettingsPane(new URL(demoURL_)));
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }
    }

    protected class LoadRemoteModelAction extends RegisteredAction
    {
        public String lastURL = "";

        public LoadRemoteModelAction()
        {
            super(className_,"loadRemoteModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.entering("" + this.getClass(), "actionPerformed");

            try
            {
                String s = (String) JOptionPane.showInputDialog("Enter URL of remote setting:", lastURL);

                if ((s != null) && (s.length() > 5))
                {
                    lastURL = s;

                    URL url = new URL(s);
                    addSettingsPane(new SettingsPane(url));
                }
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }
    }

    protected class SaveModelAction extends RegisteredAction
    {
        public String lastDir = System.getProperty("user.home");

        public SaveModelAction()
        {
            super(className_,"saveModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.entering("" + this.getClass(), "actionPerformed");

            try
            {
                // @todo replace next coede part by enable and disabled event asociated to actions...
                if (getSelectedSettingsPane() == null)
                {
                    return;
                }

                SettingsModel settingsModel = getSelectedSettingsPane().rootSettingsModel;

                java.io.File  file;
                file                        = settingsModel.associatedFile;

                // Open a filechooser in previous save directory
                JFileChooser fileChooser = new JFileChooser();

                if (lastDir != null)
                {
                    fileChooser.setCurrentDirectory(new java.io.File(lastDir));
                }

                if (file != null)
                {
                    fileChooser.setSelectedFile(file);
                }

                fileChooser.setDialogTitle("Save " + settingsModel.getAssociatedFilename() + "?");

                // Open filechooser
                int returnVal = fileChooser.showSaveDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    file = fileChooser.getSelectedFile();

                    // Ask to overwrite
                    if (file.exists())
                    {
                        String message = "File '" + file.getName() +
                            "' already exists\nDo you want to overwrite this file?";

                        // Modal dialog with yes/no button
                        int answer = JOptionPane.showConfirmDialog(null, message);

                        if (answer != JOptionPane.YES_OPTION)
                        {
                            return;
                        }
                    }
                }
                else
                {
                    return;
                }

                lastDir = file.getParent();
                // Fix user associated file and save it with result
                settingsModel.setAssociatedFile(file);
                settingsModel.saveSettingsFile(file, false);
                /* ask to update title */
                getSelectedSettingsPane();
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }
    }    
}
