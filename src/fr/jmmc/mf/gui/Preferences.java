/*
   JMMC
 */

/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.22 2011-01-04 10:40:38 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.21  2010/09/02 09:55:49  mella
 * Use savePreferences and restoreDefaultPreferences actions of Preferences mother Class
 *
 * Revision 1.20  2010/03/30 15:53:25  mella
 * fix detection handing of beta versions
 *
 * Revision 1.19  2009/05/15 20:02:13  mella
 * Add one preference (user.fov to  define some initial values into Plot panels
 *
 * Revision 1.18  2009/05/15 14:32:28  mella
 * add save.results preference
 *
 * Revision 1.17  2009/04/15 11:56:17  mella
 * manage show.toolbar preference
 *
 * Revision 1.16  2008/11/20 14:10:17  mella
 * Change default location of beta test url
 *
 * Revision 1.15  2008/10/17 10:11:27  mella
 * Jalopization of major refactoring mimicking searchcal template
 *
 * Revision 1.14  2008/10/10 09:27:42  mella
 * Add default true help.tooltips.show pref
 *
 * Revision 1.13  2008/09/25 09:24:29  mella
 * follow rules of new jmcs Preferences
 *
 * Revision 1.12  2008/05/14 11:50:07  mella
 * add yoga.local.progname preference
 *
 * Revision 1.11  2008/02/26 09:16:21  mella
 * Set different name for preference depending on beta version or not
 *
 * Revision 1.10  2008/02/20 18:30:23  mella
 * Jalopization on 1.0.7beta
 *
 * Revision 1.9  2007/03/12 17:46:59  mella
 * Check if rev should use Dev or not dev url
 *
 * Revision 1.8  2007/02/16 17:38:13  mella
 * use dev url for http service
 *
 * Revision 1.7  2007/02/14 14:44:03  mella
 * Files should now be ok to compile and be modified by netbeans
 *
 * Revision 1.6  2007/02/14 14:14:57  mella
 * Use netbeans refactoring to save form<->java consistency
 *
 * Revision 1.3  2006/11/21 13:11:01  mella
 * blah
 *
 * Revision 1.2  2006/11/03 10:22:10  mella
 * Make it compile according mcs changes
 *
 * Revision 1.1  2006/09/26 12:13:07  mella
 * First revision
 *
 *
 ******************************************************************************/
package fr.jmmc.mf.gui;

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
