/*
 JMMC
*/

/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.10 2008-02-20 18:30:23 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
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


/**
 * This is a preference dedicated to the java Model Fitting Client.
 */
public class Preferences extends fr.jmmc.mcs.util.Preferences {
    /** Preference file name */
    static String _shortPreferenceFilename = "fr.jmmc.mf.gui.properties";

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
    public static Preferences getInstance() {
        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null) {
            try {
                _singleton = new Preferences();
                _singleton.setShortPreferenceFilename(_shortPreferenceFilename);
                _singleton.loadFromFile();

                Preferences myDefaultProperties = new Preferences();

                // Store preference file version number
                myDefaultProperties.setPreference("mf.version",
                    fr.jmmc.mcs.util.Resources.getResource("mf.version"));

                /* Place general preferences  */
                myDefaultProperties.setPreference("show.recursive.parameters",
                    "false");
                myDefaultProperties.setPreference("yoga.remote.use", "true");

                /* Set Dev or standard branch according beta or not extension into mf.revision resource entry */
                if (fr.jmmc.mcs.util.Resources.getResource("mf.version")
                                                  .endsWith("beta")) {
                    myDefaultProperties.setPreference("yoga.remote.url",
                        "http://jmmc.fr/~mella/LITproDev/run.php");
                } else {
                    myDefaultProperties.setPreference("yoga.remote.url",
                        "http://jmmc.fr/modelfitting/ys/html/run.php");
                }

                myDefaultProperties.setPreference("yoga.local.home", "../ys");

                _singleton.setDefaultPreferences(myDefaultProperties);
                _singleton.loadFromFile();
            } catch (Exception e) {
                logger.warning("Default preference values creation FAILED.");
                e.printStackTrace();
            }
        }

        return _singleton;
    }
}
