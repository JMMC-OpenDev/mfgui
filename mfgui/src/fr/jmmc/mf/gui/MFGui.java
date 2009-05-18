/*
 * MFGui.java
 *
 * Created on 12 février 2008, 14:39
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.actions.SaveSettingsAction;
import fr.jmmc.mf.gui.actions.ShowPrefAction;
import fr.jmmc.mf.gui.actions.LoadDemoModelAction;
import fr.jmmc.mf.gui.actions.LoadModelAction;
import fr.jmmc.mf.gui.actions.GetYogaVersionAction;
import fr.jmmc.mf.gui.actions.LoadRemoteModelAction;
import fr.jmmc.mf.gui.actions.CloseModelAction;
import fr.jmmc.mf.gui.actions.NewModelAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.gui.MainMenuBar;
import fr.jmmc.mcs.gui.StatusBar;
import fr.jmmc.mcs.util.*;

import fr.jmmc.mf.gui.actions.AttachDetachFrameAction;
import fr.jmmc.mf.gui.actions.DeleteTreeSelectionAction;
import fr.jmmc.mf.gui.actions.LoadDataFilesAction;
import fr.jmmc.mf.gui.actions.ShowLitproSettingsFileAction;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


import java.util.*;

import javax.swing.*;


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

    protected static JToolBar toolBar;
    protected static StatusBar statusBar;

    // Application actions
    static Action getYogaVersionAction;
    public static Action saveSettingsAction;
    public static DeleteTreeSelectionAction deleteTreeSelectionAction;
    public static AttachDetachFrameAction attachDetachFrameAction;

    private static PlasticListener plasticServer_;

    /** instance link */
    private static MFGui instance = null;
    
    private static javax.swing.JTabbedPane tabbedPane_;

    /** Creates new form MFGui */
    public MFGui(String[] filenames)
    {
        instance                  = this;
        // instanciate actions       
        getYogaVersionAction      = new GetYogaVersionAction(this);

        new ShowPrefAction(this);
        new NewModelAction(this);
        new CloseModelAction(this);
        new LoadModelAction(this);
        new LoadRemoteModelAction(this);
        // @todo use a preference to choose from one of the two following for default saveaction
        saveSettingsAction=new SaveSettingsAction(this, "saveSettings");        
        
        deleteTreeSelectionAction =  new DeleteTreeSelectionAction(this) ;
        attachDetachFrameAction = new AttachDetachFrameAction(this);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);

        tabbedPane_ = new javax.swing.JTabbedPane();
        tabbedPane_.setMinimumSize(new java.awt.Dimension(980, 700));
        tabbedPane_.setPreferredSize(new java.awt.Dimension(980, 700));
        tabbedPane_.addChangeListener(deleteTreeSelectionAction);
        tabbedPane_.addChangeListener(attachDetachFrameAction);
        tabbedPane_.addChangeListener(new LoadDataFilesAction(this));
        tabbedPane_.addChangeListener(new ShowLitproSettingsFileAction(this));

        getContentPane().add(tabbedPane_, java.awt.BorderLayout.CENTER);

        /* Plastic transmitter. */
        plasticServer_ = new PlasticListener();

        // Build demo action into initMenuBar
        initMenuBar();
        // Handle menu bar
        setJMenuBar(new MainMenuBar(this));

        // Handle toolbar

        toolBar=new JToolBar();
        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);
        ActionRegistrar registrar = ActionRegistrar.getInstance();
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.NewModelAction","newModel"));
        toolBar.add(registrar.get(LoadModelAction.className,LoadModelAction.actionName));
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.SaveSettingsAction", "saveSettings"));
        toolBar.addSeparator();
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.DeleteTreeSelectionAction", "deleteTreeSelection"));
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.AttachDetachFrameAction", "toggleFrameTreeSelection"));
        toolBar.addSeparator();
        toolBar.add(ModelFitting.showHelpAction());
        toolBar.setVisible(myPreferences.getPreferenceAsBoolean("show.toolbar"));

        
        // Handle status bar
        statusBar = new StatusBar();
        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

        setTitle("ModelFitting V"+ModelFitting.getSharedApplicationDataModel().getProgramVersion());

        if (filenames.length >= 1)
        {
            try
            {
                java.io.File file = new java.io.File(filenames[0]);
                addSettings(new SettingsModel(file));
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }

        // handle frame icon
        this.setIconImage(new ImageIcon(Urls.fixJarURL(getClass().getResource("/fr/jmmc/mcs/gui/favicon.png"))).getImage());

        pack();
        fr.jmmc.mcs.gui.StatusBar.show("Application inited");
    }

    public static void showToolbar(boolean visible){
        if(toolBar==null){
            return;
        }
        toolBar.setVisible(visible);
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

   public void addSettings(SettingsModel settingsModel){
        SettingsPane p = new SettingsPane(settingsModel);
        tabbedPane_.add(p, p.getSettingsModel().getAssociatedFilename());
        tabbedPane_.setSelectedComponent(p);
        fr.jmmc.mcs.gui.StatusBar.show("Settings loaded");
    }
   
   public void closeSettings(){
       SettingsModel currentSettingsModel = getSelectedSettings();
       if( UtilsClass.askToSaveUserModification(currentSettingsModel) )
       {
          closeTab(tabbedPane_.getSelectedComponent());
          StatusBar.show("Settings closed");
       }
   }

    /* This method return selectedPane or null . It also updates titles */
   public SettingsModel getSelectedSettings()
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
            tabbedPane_.setTitleAt(idx, sp.getSettingsModel().getAssociatedFilename());
            logger.fine("Selected settingsPane name:" +
                sp.getSettingsModel().getAssociatedFilename());
        }

        return sp.getSettingsModel();
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
            "http://jmmc.fr/modelfitting/xml/Obj1_binary_disk_with_oidata.xml");
        demo.put("Binary punct (Obj1)",
            "http://jmmc.fr/modelfitting/xml/Obj1_binary_punct_with_oidata.xml");
        demo.put("Uniform disk (Obj1)",
            "http://jmmc.fr/modelfitting/xml/Obj1_uniform_disk_with_oidata.xml");
        demo.put("Binary punct (Obj2)",
            "http://jmmc.fr/modelfitting/xml/Obj2_binary_punct_with_oidata.xml");
        demo.put("Disk and punct (Obj2)",
            "http://jmmc.fr/modelfitting/xml/Obj2_disk_and_punct_with_oidata.xml");
        demo.put("Triple punct (Obj2)",
            "http://jmmc.fr/modelfitting/xml/Obj2_triple_punct_with_oidata.xml");
        demo.put("Uniform disk (Obj2)",
            "http://jmmc.fr/modelfitting/xml/Obj2_uniform_disk_with_oidata.xml");
        demo.put("Arcturus",
            "http://jmmc.fr/modelfitting/xml/Arcturus_uniform_disk_with_oidata.xml");
        
        Enumeration keys = demo.keys();
        int i=1;
        while (keys.hasMoreElements()){
            String title = (String)keys.nextElement();
            action = new LoadDemoModelAction("demoModel"+i, demo.get(title), title,this);
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
        java.awt.Component[] components = tabbedPane_.getComponents();
        ModifyAndSaveObject[] objs = new ModifyAndSaveObject[components.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i]=((SettingsPane)components[i]).getSettingsModel();
        }
        return UtilsClass.checkUserModificationToQuit(objs);
    }

    /**
     * Action which displays a window giving some information about
     * the state of the PLASTIC hub.
     *
     * this action must be refactored but it is leaved here without any idea
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
    }
