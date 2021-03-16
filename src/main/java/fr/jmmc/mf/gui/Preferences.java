/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a preference dedicated to the java Model Fitting Client.
 */
public class Preferences extends fr.jmmc.jmcs.data.preference.Preferences {

    /** Singleton instance */
    private static Preferences _singleton = null;
    /** Class Name */
    private final static String className = Preferences.class.getName();
    /** Logger */
    static final Logger logger = LoggerFactory.getLogger(className);
    public static final String MF_VERSION = "mf.version";
    public static final String HELP_TOOLTIPS_SHOW = "help.tooltips.show";
    public static final String SHOW_TOOLBAR = "show.toolbar";
    public static final String SHOW_RECURSIVE_PARAMETERS = "show.recursive.parameters";
    public static final String SAVE_RESULTS = "save.results";
    public static final String USER_FOV = "user.fov";
    public static final String USER_UVPOINT_LIMITFORPLOT = "user.uvpoint.limitforplot";
    public static final String YOGA_REMOTE_USE = "yoga.remote.use";
    public static final String YOGA_LOCAL_HOME = "yoga.local.home";
    public static final String YOGA_LOCAL_PROGNAME = "yoga.local.progname";
    public static final String YOGA_REMOTE_URL = "yoga.remote.url";
    public static final String USERMODEL_REPO_URL = "usermodel.repo.url";

    /**
     * Privatized constructor that must be empty.
     */
    private Preferences() {
        //_version=ApplicationDescription.getProgramVersion();
        //_version=LITpro.getVersion()
    }

    /**
     * Return the singleton instance of Preferences.
     *
     * @return the singleton preference instance
     */
    public static synchronized Preferences getInstance() {
        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null) {
            _singleton = new Preferences();
        }

        return _singleton;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws PreferencesException DOCUMENT ME!
     */
    @Override
    protected void setDefaultPreferences() throws PreferencesException {
        setDefaultPreference(MF_VERSION, ApplicationDescription.getInstance().getProgramVersion());
        /* Place general preferences  */
        setDefaultPreference(HELP_TOOLTIPS_SHOW, "true");
        setDefaultPreference(SHOW_TOOLBAR, "true");
        setDefaultPreference(SHOW_RECURSIVE_PARAMETERS, "false");
        setDefaultPreference(SAVE_RESULTS, "true");
        setDefaultPreference(USER_FOV, "30");
        setDefaultPreference(USER_UVPOINT_LIMITFORPLOT, "300");
        setDefaultPreference(YOGA_REMOTE_USE, "true");
        setDefaultPreference(YOGA_LOCAL_HOME, "../ys");
        setDefaultPreference(YOGA_LOCAL_PROGNAME, "/bin/yoga.sh");
        // our actual convention tells that a beta version ends with b1...bN
        if (ApplicationDescription.isAlphaVersion()) {
            setDefaultPreference(YOGA_REMOTE_URL, "http://jmmc.fr/~mellag/LITproWebService/run.php");
        } else if (ApplicationDescription.isBetaVersion()) {
            setDefaultPreference(YOGA_REMOTE_URL, "http://jmmc.fr/~betaswmgr/LITproWebService/run.php");
        } else {
            setDefaultPreference(YOGA_REMOTE_URL, "http://jmmc.fr/~swmgr/LITproWebService/run.php");
        }

        setDefaultPreference(USERMODEL_REPO_URL, "http://apps.jmmc.fr/exist/apps/usermodels/");

    }

    /**
     * Return preference filename.
     * 
     * @return preference filename.    
     */
    @Override
    protected String getPreferenceFilename() {
        if (ApplicationDescription.isAlphaVersion()) {
            return "fr.jmmc.modelfitting.alpha.properties";
        } else if (ApplicationDescription.isBetaVersion()) {
            return "fr.jmmc.modelfitting.beta.properties";
        }
        return "fr.jmmc.modelfitting.properties";
    }

    /**
     *  Return preference version number.
     *
     * @return preference version number.
     */
    @Override
    protected int getPreferencesVersionNumber() {
        // 1 -> 2 v1.0.11b10
        //return 2; v1.1.0
        return 3;
    }

    /**
     * Hook to handle updates of older preference file version.
     *
     * @param loadedVersionNumber the version of the loaded preference file.
     *
     * @return should return true if the update went fine and new values should
     * be saved, false otherwise to automatically trigger default values load.
     */
    @Override
    protected boolean updatePreferencesVersion(int loadedVersionNumber) {
        logger.info("Upgrading preference file from version '{}' to version '{}'", loadedVersionNumber, loadedVersionNumber + 1);

        switch (loadedVersionNumber) {            
            case 2:
                return updateFromVersion2ToVersion3();

            case 1:
                return updateFromVersion1ToVersion2();

            // By default, triggers default values load.
            default:
                return false;
        }
    }

    // introduced in version 1.0.12
    private boolean updateFromVersion1ToVersion2() {

        String[] preferencesToReset = new String[]{"save.results", "yoga.remote.use", "yoga.remote.url"};
        String[] defaultValues = new String[]{"true", "true", "http://jmmc.fr/~swmgr/LITproWebService/run.php"};
        try {
            for (int i = 0; i < preferencesToReset.length; i++) {
                String preferenceName = preferencesToReset[i];
                String defaultValue = defaultValues[i];
                setPreference(preferenceName, defaultValue);
            }
        } catch (PreferencesException ex) {
            logger.error("Can't update preference version", ex);
            return false;
        }
        return true;
    }
    // cleanup introduced in version 1.1.0
    private boolean updateFromVersion2ToVersion3() {             
        try {
                // force reset for remote server urls 
            if (ApplicationDescription.isAlphaVersion()) {
                setPreference(YOGA_REMOTE_URL, "http://apps.jmmc.fr/~mellag/LITproWebService/run.php");
            } else if (ApplicationDescription.isBetaVersion()) {
                setPreference(YOGA_REMOTE_URL, "http://apps.jmmc.fr/~betaswmgr/LITproWebService/run.php");
            } else {
                setPreference(YOGA_REMOTE_URL, "http://apps.jmmc.fr/~swmgr/LITproWebService/run.php");
            }
                                                        
        } catch (PreferencesException ex) {
            logger.error("Can't update preference version", ex);
            return false;
        }
        return true;
    }
}
