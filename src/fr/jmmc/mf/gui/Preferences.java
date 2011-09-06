/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.mf.ModelFitting;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.mcs.util.*;

/**
 * This is a preference dedicated to the java Model Fitting Client.
 */
public class Preferences extends fr.jmmc.jmcs.data.preference.Preferences
{    

    /** Singleton instance */
    private static Preferences _singleton = null;

    /** Class Name */
    private final static String className_= "fr.jmmc.mf.gui.Preferences";
    /** Logger */
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className_);

    private static String _version = ModelFitting.getSharedApplicationDataModel().getProgramVersion();
    /**
     * Privatized constructor that must be empty.
     */
    private Preferences()
    {
        //_version=ApplicationDataModel.getProgramVersion();
        //_version=ModelFitting.getVersion()
    }

    /**
     * Return the singleton instance of Preferences.
     *
     * @return the singleton preference instance
     */
    public final synchronized static Preferences getInstance()
    {
        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null)
        {
            _singleton = new Preferences();
        }

        return _singleton;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws PreferencesException DOCUMENT ME!
     */
    protected void setDefaultPreferences() throws PreferencesException
    {
        setDefaultPreference("mf.version", _version);
        /* Place general preferences  */
        setDefaultPreference("help.tooltips.show", "true");
        setDefaultPreference("show.toolbar", "true");
        setDefaultPreference("show.recursive.parameters", "false");
        setDefaultPreference("save.results", "false");
        setDefaultPreference("user.fov", "30");
        setDefaultPreference("yoga.remote.use", "true");
        setDefaultPreference("yoga.local.home", "../ys");
        setDefaultPreference("yoga.local.progname", "/bin/yoga.sh");
        // our actual convention tells that a beta version ends with b1...bN
        if (ModelFitting.isAlphaVersion())
        {
            setDefaultPreference("yoga.remote.url", "http://jmmc.fr/~mella/LITproWebService/run.php");
        }
        else  if (ModelFitting.isBetaVersion())
        {
            setDefaultPreference("yoga.remote.url", "http://jmmc.fr/~betaswmgr/LITproWebService/run.php");
        }
        else
        {
            setDefaultPreference("yoga.remote.url", "http://jmmc.fr/~swmgr/LITproWebService/run.php");
        }
    }

    /**
     * Return preference filename.
     * 
     * @return preference filename.    
     */
    protected String getPreferenceFilename()
    {
        logger.entering("Preferences", "getPreferenceFilename");

        if (_version.contains("beta"))
        {
            return "fr.jmmc.modelfitting.beta.properties";
        }

        return "fr.jmmc.modelfitting.properties";
    }

    /**
     *  Return preference version number.
     *
     * @return preference version number.
     */
    protected int getPreferencesVersionNumber()
    {
        logger.entering("Preferences", "getPreferencesVersionNumber");

        return 1;
    }
    
    
    
}
