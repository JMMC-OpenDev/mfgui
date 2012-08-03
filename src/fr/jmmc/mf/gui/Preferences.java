/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.mf.ModelFitting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a preference dedicated to the java Model Fitting Client.
 */
public class Preferences extends fr.jmmc.jmcs.data.preference.Preferences
{    

    /** Singleton instance */
    private static Preferences _singleton = null;

    /** Class Name */
    private final static String className= Preferences.class.getName();
    /** Logger */
    static final Logger logger = LoggerFactory.getLogger(className);

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
        setDefaultPreference("save.results", "true");
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
        if (ModelFitting.isAlphaVersion())
        {
            return "fr.jmmc.modelfitting.alpha.properties";
        }else if (ModelFitting.isBetaVersion()){
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
        // 1 -> 2 v1.0.11b10
        return 2;
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
        logger.info("Upgrading preference file from version '{}' to version '{}'"
                , loadedVersionNumber ,loadedVersionNumber +1);

        switch (loadedVersionNumber) {
            // Wrong column identifiers in the the simple and detailed bright N columns order list
            case 1:
                return updateFromVersion1ToVersion2();
                
            // By default, triggers default values load.
            default:
                return false;
        }
    }

    // introduced in version 1.0.12
    private boolean updateFromVersion1ToVersion2() {
        
        String[] preferencesToReset = new String[] {"save.results", "yoga.remote.use","yoga.remote.url"};
        try {            
            for (int i = 0; i < preferencesToReset.length; i++) {
                String preferenceName = preferencesToReset[i];
                String defaultValue = _defaultProperties.getProperty(preferenceName);       
                setPreference(preferenceName, defaultValue);
            }            
        } catch (PreferencesException ex) {
            logger.error ( "Can't update preference version", ex);
            return false;
        }
        return true;        
    }
    
}
