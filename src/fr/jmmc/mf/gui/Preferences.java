/*
 JMMC
*/
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Preferences.java,v 1.2 2006-11-03 10:22:10 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/09/26 12:13:07  mella
 * First revision
 *
 *
 ******************************************************************************/
package jmmc.mf.gui;

import java.util.Properties;
import jmmc.mcs.log.MCSLogger;

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
            try{
            _singleton = new Preferences();
            _singleton.setShortPreferenceFilename(_shortPreferenceFilename);
            _singleton.loadFromFile();

            Preferences myDefaultProperties = new Preferences();

            // Store preference file version number
            myDefaultProperties.setPreference("mf.version", "1.0");

            /* Place general preferences  */
            myDefaultProperties.setPreference("show.recursive.parameters", "false");

            _singleton.setDefaultPreferences(myDefaultProperties);
            _singleton.loadFromFile();

            }catch(Exception e){
                MCSLogger.debug("Default preference values creation FAILED.");
            }
        }

        return _singleton;
    }
}
