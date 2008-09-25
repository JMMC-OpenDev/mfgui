/*
   JMMC
 */

/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.13 2008-09-25 09:24:29 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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

import java.util.Properties;
import fr.jmmc.mcs.util.*;

/**
 * This is a preference dedicated to the java Model Fitting Client.
 */
public class Preferences extends fr.jmmc.mcs.util.Preferences {
    /** Preference file name . Its value depends on mf.version */
    static String _shortPreferenceFilename;

    /** Singleton instance */
    private static Preferences _singleton = null;

    /** Logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.Preferences");

    /**
     * Privatized constructor that must be empty.
     */
    private Preferences() {
    }

    /**
     * Return the singleton instance of Preferences.
     *
     * @return the singleton preference instance
     */
    public final synchronized static Preferences getInstance() {
        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null) {
            _singleton = new Preferences();
        }
        return _singleton;
    }

    protected void setDefaultPreferences() throws PreferencesException{
        setDefaultPreference("mf.version",
                fr.jmmc.mcs.util.Resources.getResource("mf.version"));
        /* Place general preferences  */
        setDefaultPreference("show.recursive.parameters", "false");
        setDefaultPreference("yoga.remote.use", "true");
        setDefaultPreference("yoga.local.home", "../ys");
        setDefaultPreference("yoga.local.progname", "/bin/yoga.sh");
        if (fr.jmmc.mcs.util.Resources.getResource("mf.version")
                .endsWith("beta")) {
            setDefaultPreference("yoga.remote.url",
                    "http://jmmc.fr/~mella/LITproDev/run.php");
        } else {
            setDefaultPreference("yoga.remote.url",
                    "http://jmmc.fr/modelfitting/ys/html/run.php");
        }
    }

    protected String getPreferenceFilename()
    {
        logger.entering("Preferences", "getPreferenceFilename");
        if (fr.jmmc.mcs.util.Resources.getResource("mf.version")
                .endsWith("beta")) {
            return "fr.jmmc.modelfitting.beta.properties";
                } 

        return "fr.jmmc.modelfitting.properties";
    }

    protected int getPreferencesVersionNumber(){
        logger.entering("Preferences", "getPreferencesVersionNumber");
        return 1;
    }
}
