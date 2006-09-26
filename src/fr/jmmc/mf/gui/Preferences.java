/*
 JMMC
*/
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.1 2006-09-26 12:13:07 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package jmmc.mf.gui;

import java.util.Properties;


/**
 * This is a preference dedicated to the java Model Fitting Client.
 */
public class Preferences extends jmmc.mcs.util.Preferences
{
    /** Preference file name */
    static String _shortPreferenceFilename = "fr.jmmc.mf.gui.properties";

    /** Singleton instance */
    private static Preferences _singleton = null;

    /**
     * Privatized constructor that must be empty.
     */
    private Preferences()
    {
    }

    /**
     * Return the singleton instance of Preferences.
     *
     * @return the singleton preference instance
     */
    public static Preferences getInstance()
    {
        // Build new reference if singleton does not already exist
        // or return previous reference
        if (_singleton == null)
        {
            _singleton = new Preferences();
            _singleton.setShortPreferenceFilename(_shortPreferenceFilename);
            _singleton.loadFromFile();

            Properties myDefaultProperties = new Properties();

            // Store preference file version number
            myDefaultProperties.put("mf.version", "1.0");

            /* Place general preferences  */
            myDefaultProperties.put("show.recursive.parameters", "false");

            _singleton.setDefaultPreferences(myDefaultProperties);
            _singleton.loadFromFile();
        }

        return _singleton;
    }
}
