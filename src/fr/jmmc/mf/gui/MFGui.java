/*
 * MFGui.java
 *
 * Created on 12 février 2008, 14:39
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mf.ModelFitting;
import fr.jmmc.jmcs.util.Urls;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.mf.gui.actions.SaveSettingsAction;
import fr.jmmc.mf.gui.actions.ShowPrefAction;
import fr.jmmc.mf.gui.actions.LoadDemoModelAction;
import fr.jmmc.mf.gui.actions.LoadModelAction;
import fr.jmmc.mf.gui.actions.GetYogaVersionAction;
import fr.jmmc.mf.gui.actions.LoadRemoteModelAction;
import fr.jmmc.mf.gui.actions.CloseModelAction;
import fr.jmmc.mf.gui.actions.NewModelAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.jmcs.gui.StatusBar;

import fr.jmmc.mf.gui.actions.AttachDetachFrameAction;
import fr.jmmc.mf.gui.actions.DeleteTreeSelectionAction;
import fr.jmmc.mf.gui.actions.LoadDataFilesAction;
import fr.jmmc.mf.gui.actions.LoadRemoteDataFilesAction;
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

    /** instance link */
    private static MFGui instance = null;
    
    private static javax.swing.JTabbedPane tabbedPane_;
    private static final long serialVersionUID = 1L;

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
        tabbedPane_.addChangeListener(new LoadRemoteDataFilesAction(this));
        tabbedPane_.addChangeListener(new ShowLitproSettingsFileAction(this));

        getContentPane().add(tabbedPane_, java.awt.BorderLayout.CENTER);
    
        // Build demo action into initMenuBar
        initMenuBar();
        
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

        String title=ModelFitting.getSharedApplicationDataModel().getProgramName();
        if(ModelFitting.isAlphaVersion()||ModelFitting.isBetaVersion()){
            title+=" "+ ModelFitting.getSharedApplicationDataModel().getProgramVersion();
        }
        setTitle(title);

        if (filenames.length >= 1) {
            java.io.File file = new java.io.File(filenames[0]);
            addSettings(new SettingsModel(file));
        }

        // handle frame icon
        this.setIconImage(new ImageIcon(Urls.fixJarURL(getClass().getResource("/fr/jmmc/jmcs/resource/favicon.png"))).getImage());

        pack();
        fr.jmmc.jmcs.gui.StatusBar.show("Application inited");
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

   public void addSettings(SettingsModel settingsModel){
        SettingsPane p = new SettingsPane(settingsModel);
        tabbedPane_.add(p, p.getSettingsModel().getAssociatedFilename());
        tabbedPane_.setSelectedComponent(p);
        fr.jmmc.jmcs.gui.StatusBar.show("Settings loaded");
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
        // Second level menus
        JMenuItem                 menuItem;
        Action                    action;

        LinkedHashMap<String, String> demo = new LinkedHashMap<String, String>();
        demo.put("Tutorial example 1: angular diameter of a single star",
            "http://apps.jmmc.fr/modelfitting/xml/arcturus_1.79mu_tutorial.xml");
        demo.put("Tutorial example 2: sharing parameters",
            "http://apps.jmmc.fr/modelfitting/xml/arcturus_1.52-1.79mu_tutorial.xml");
        demo.put("Tutorial example 3: ﬁt with degenerated parameters ",
            "http://apps.jmmc.fr/modelfitting/xml/Theta1OriC_tutorial.xml");
        
        Iterator<String> iterator = demo.keySet().iterator();
        
        int i=1;
        while (iterator.hasNext()){
            String title = iterator.next();
            action = new LoadDemoModelAction("demoModel"+i, demo.get(title), title,this);            
            i++;                 
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
            @Override
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
    @Override
    public void windowOpened(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    @Override
    public void windowClosing(WindowEvent e)
    {
        ModelFitting.quitAction().actionPerformed(null);
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    @Override
    public void windowClosed(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    @Override
    public void windowIconified(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    @Override
    public void windowDeiconified(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    @Override
    public void windowActivated(WindowEvent e)
    {
    }

    /**
     * Listen window event.
     * @param e window event.
     */
    @Override
    public void windowDeactivated(WindowEvent e)
    {
    }

    /**
     * App lifecycle finish step.
     *
     * @return true if App can quit or false.
     */
    public boolean finish()
    {
        java.awt.Component[] components = tabbedPane_.getComponents();
        ModifyAndSaveObject[] objs = new ModifyAndSaveObject[components.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i]=((SettingsPane)components[i]).getSettingsModel();
        }
        return UtilsClass.checkUserModificationToQuit(objs);
    }
}
