/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.data.ApplicationDescription;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.resource.image.ResourceImage;
import fr.jmmc.mf.LITpro;
import fr.jmmc.mf.gui.actions.*;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.nom.tam.fits.FitsException;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
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
    /* Minimal size of main component */
    private static final Dimension INITIAL_DIMENSION = new java.awt.Dimension(1000, 700);

    /** Creates new form MFGui */
    public MFGui(String[] filenames) {
        instance = this;
        // instanciate actions
        getYogaVersionAction = new GetYogaVersionAction(this);

        new ShowPrefAction(this);
        new NewModelAction(this);
        new CloseModelAction(this);
        new LoadModelAction(this);
        new LoadRemoteModelAction(this);
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

        // Handle toolbar

        toolBar = new JToolBar();
        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);
        ActionRegistrar registrar = ActionRegistrar.getInstance();
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.NewModelAction", "newModel"));
        toolBar.add(registrar.get(LoadModelAction.className, LoadModelAction.actionName));
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.SaveSettingsAction", "saveSettings"));
        toolBar.addSeparator();
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.DeleteTreeSelectionAction", "deleteTreeSelection"));
        toolBar.add(registrar.get("fr.jmmc.mf.gui.actions.AttachDetachFrameAction", "toggleFrameTreeSelection"));
        toolBar.addSeparator();
        toolBar.add(InternalActionFactory.showHelpAction());
        toolBar.setVisible(myPreferences.getPreferenceAsBoolean("show.toolbar"));


        // Handle status bar
        statusBar = new StatusBar();
        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

        String title = ApplicationDescription.getInstance().getProgramName();
        if (LITpro.isAlphaVersion() || LITpro.isBetaVersion()) {
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
        }

        return sp.getSettingsModel();
    }

    public static void closeTab(java.awt.Component c) {
        tabbedPane_.remove(c);
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
        java.awt.Component[] components = tabbedPane_.getComponents();
        ModifyAndSaveObject[] objs = new ModifyAndSaveObject[components.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = ((SettingsPane) components[i]).getSettingsModel();
        }
        return UtilsClass.checkUserModificationToQuit(objs);
    }
}
