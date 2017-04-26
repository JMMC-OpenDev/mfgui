/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.gui.util.ResourceImage;
import fr.jmmc.mf.gui.actions.AttachDetachFrameAction;
import fr.jmmc.mf.gui.actions.CloseModelAction;
import fr.jmmc.mf.gui.actions.DeleteTreeSelectionAction;
import fr.jmmc.mf.gui.actions.GetYogaVersionAction;
import fr.jmmc.mf.gui.actions.LoadDataFilesAction;
import fr.jmmc.mf.gui.actions.LoadDemoModelAction;
import fr.jmmc.mf.gui.actions.LoadModelAction;
import fr.jmmc.mf.gui.actions.LoadRemoteModelAction;
import fr.jmmc.mf.gui.actions.NewModelAction;
import fr.jmmc.mf.gui.actions.SaveSettingsAction;
import fr.jmmc.mf.gui.actions.ShowLitproSettingsFileAction;
import fr.jmmc.mf.gui.actions.ShowPrefAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.nom.tam.fits.FitsException;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  mella
 */
public final class MFGui extends JFrame {

    /** Class Name */
    static final String className = "fr.jmmc.mf.gui.MFGui";
    /** Class logger */
    static final Logger logger = LoggerFactory.getLogger(className);
    protected static Preferences myPreferences = Preferences.getInstance();
    protected static JToolBar toolBar;
    // Application actions
    static Action getYogaVersionAction;
    public static Action saveSettingsAction;
    public static DeleteTreeSelectionAction deleteTreeSelectionAction;
    public static AttachDetachFrameAction attachDetachFrameAction;
    /** instance link */
    private static MFGui instance = null;
    private static javax.swing.JTabbedPane tabbedPane_;
    private static final long serialVersionUID = 1L;
    /* Minimal size of main component */
    private static final Dimension INITIAL_DIMENSION = new java.awt.Dimension(1000, 700);
    public static final int TABLE_MAX_WIDTH = 330;

    public static JPanel welcomePanel = null;

    /** Creates new form MFGui */
    public MFGui(String[] filenames) {
        instance = this;
        // instanciate actions
        getYogaVersionAction = new GetYogaVersionAction(this);

        new ShowPrefAction();
        new NewModelAction(this);
        new CloseModelAction(this);
        new LoadModelAction(this);
        new LoadRemoteModelAction(this);
        new LoadDataFilesAction(this);
        // @todo use a preference to choose from one of the two following for default saveaction
        saveSettingsAction = new SaveSettingsAction(this, "saveSettings");

        deleteTreeSelectionAction = new DeleteTreeSelectionAction(this);
        attachDetachFrameAction = new AttachDetachFrameAction(this);

        tabbedPane_ = new javax.swing.JTabbedPane();
        tabbedPane_.setMinimumSize(INITIAL_DIMENSION);
        tabbedPane_.setPreferredSize(INITIAL_DIMENSION);
        tabbedPane_.addChangeListener(deleteTreeSelectionAction);
        tabbedPane_.addChangeListener(attachDetachFrameAction);
        tabbedPane_.addChangeListener(new LoadDataFilesAction(this));
        tabbedPane_.addChangeListener(new ShowLitproSettingsFileAction(this));

        getContentPane().add(tabbedPane_, java.awt.BorderLayout.CENTER);

        // Build demo action into initMenuBar
        initMenuBar();

        initTabbedPane();

        // Handle toolbar
        toolBar = new JToolBar();
        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);
        ActionRegistrar registrar = ActionRegistrar.getInstance();
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.NewModelAction", "newModel")).setHideActionText(true);;
        toolBar.add(registrar.get(LoadModelAction.className, LoadModelAction.actionName)).setHideActionText(true);;
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.SaveSettingsAction", "saveSettings")).setHideActionText(true);;
        toolBar.addSeparator();
        toolBar.add(registrar.get(LoadDataFilesAction.className, LoadDataFilesAction.actionName)).setHideActionText(true);;
        toolBar.addSeparator();
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.DeleteTreeSelectionAction", "deleteTreeSelection")).setHideActionText(true);;
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.AttachDetachFrameAction", "toggleFrameTreeSelection")).setHideActionText(true);;
        toolBar.addSeparator();
        toolBar.add(InternalActionFactory.showHelpAction()).setHideActionText(true);;
        toolBar.setVisible(myPreferences.getPreferenceAsBoolean(Preferences.SHOW_TOOLBAR));

        // Handle status bar
        getContentPane().add(StatusBar.getInstance(), java.awt.BorderLayout.SOUTH);

        String title = ApplicationDescription.getInstance().getProgramName();
        if (ApplicationDescription.isAlphaVersion() || ApplicationDescription.isBetaVersion()) {
            title += " " + ApplicationDescription.getInstance().getProgramVersion();
        }
        setTitle(title);

        // TODO check if filenames can be set, if not remove dead code
        if (filenames.length >= 1) {
            java.io.File file = new java.io.File(filenames[0]);
            try {
                addSettings(new SettingsModel(file));
            } catch (IOException ex) {
                MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
            } catch (ExecutionException ex) {
                MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
            } catch (FitsException ex) {
                MessagePane.showErrorMessage("Could not load file : " + file.getName(), ex);
            }
        }

        // handle frame icon
        final Image jmmcFavImage = ResourceImage.JMMC_FAVICON.icon().getImage();
        this.setIconImage(jmmcFavImage);

        fr.jmmc.jmcs.gui.component.StatusBar.show("Application inited");
    }

    public static void showToolbar(boolean visible) {
        if (toolBar == null) {
            return;
        }
        toolBar.setVisible(visible);
    }

    /**
     * Return the singleton instance.
     *
     * @return the singleton instance of MFGui.
     */
    public static MFGui getInstance() {
        return instance;
    }

    public void addSettings(SettingsModel settingsModel) {
        if (welcomePanel != null) {
            tabbedPane_.remove(welcomePanel);
            welcomePanel = null;
        }
        SettingsPane p = new SettingsPane(settingsModel);
        tabbedPane_.add(p, p.getSettingsModel().getAssociatedFilename());
        tabbedPane_.setSelectedComponent(p);
        fr.jmmc.jmcs.gui.component.StatusBar.show("Settings loaded");
    }

    public void closeSettings() {
        SettingsModel currentSettingsModel = getSelectedSettings();
        if (UtilsClass.askToSaveUserModification(currentSettingsModel)) {
            closeTab(tabbedPane_.getSelectedComponent());
            StatusBar.show("Settings closed");
        }
    }

    /* This method return selectedPane or null . It also updates titles */
    public SettingsModel getSelectedSettings() {
        int idx = tabbedPane_.getSelectedIndex();

        if (idx < 0) {
            return null;
        }

        SettingsPane sp = null;

        if (tabbedPane_.getComponentAt(idx) instanceof SettingsPane) {
            sp = (SettingsPane) tabbedPane_.getComponentAt(idx);
            tabbedPane_.setTitleAt(idx, sp.getSettingsModel().getAssociatedFilename());
            logger.debug("Selected settingsPane name: {}", sp.getSettingsModel().getAssociatedFilename());
            return sp.getSettingsModel();
        }

        return null;
    }

    public static void closeTab(java.awt.Component c) {
        tabbedPane_.remove(c);
        if (tabbedPane_.getComponentCount() == 0) {
            initTabbedPane();
        }
    }

    /** This method is called from within the constructor to
     * initialize the wizard/welcome panel.
     */
    public static void initTabbedPane() {
        welcomePanel = new JPanel();
        welcomePanel.setLayout(new GridBagLayout());
        JPanel thewelcomePanel = new JPanel();

        thewelcomePanel.setLayout(new BoxLayout(thewelcomePanel, BoxLayout.Y_AXIS));
        ActionRegistrar registrar = ActionRegistrar.getInstance();
        thewelcomePanel.add(new JButton(registrar.get("fr.jmmc.mf.gui.actions.NewModelAction", "newModel")));
        thewelcomePanel.add(new JButton(registrar.get(LoadDataFilesAction.className, LoadDataFilesAction.actionName)));

        welcomePanel.add(thewelcomePanel);

        //tabbedPane_.add(welcomePanel);
        //tabbedPane_.setTitleAt(0, "Starting LITpro");
    }

    /** This method is called from within the constructor to
     * initialize the form's menu bar.
     */
    public void initMenuBar() {
        // Second level menus
        JMenuItem menuItem;
        Action action;

        LinkedHashMap<String, String> demo = new LinkedHashMap<String, String>();
        demo.put("Tutorial example 1: angular diameter of a single star",
                "http://apps.jmmc.fr/modelfitting/xml/arcturus_1.79mu_tutorial.xml");
        demo.put("Tutorial example 2: sharing parameters",
                "http://apps.jmmc.fr/modelfitting/xml/arcturus_1.52-1.79mu_tutorial.xml");
        demo.put("Tutorial example 3: ﬁt with degenerated parameters ",
                "http://apps.jmmc.fr/modelfitting/xml/Theta1OriC_tutorial.xml");

        Iterator<String> iterator = demo.keySet().iterator();

        int i = 1;
        while (iterator.hasNext()) {
            String title = iterator.next();
            action = new LoadDemoModelAction("demoModel" + i, demo.get(title), title, this);
            i++;
        }
    }

    /**
     * App lifecycle canBeTerminatedNow step.
     *
     * @return true if App can quit or false.
     */
    public boolean finish() {
        if (welcomePanel != null) {
            return true;
        }

        java.awt.Component[] components = tabbedPane_.getComponents();
        ModifyAndSaveObject[] objs = new ModifyAndSaveObject[components.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = ((SettingsPane) components[i]).getSettingsModel();
        }
        return UtilsClass.checkUserModificationToQuit(objs);
    }
}
