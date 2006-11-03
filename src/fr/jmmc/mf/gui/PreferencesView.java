/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: PreferencesView.java,v 1.2 2006-11-03 10:22:10 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/10/06 09:19:28  mella
 * Add missing files for a clean state
 *
 *
 ******************************************************************************/
package jmmc.mf.gui;

import jmmc.mcs.log.MCSLogger;

import jmmc.mcs.util.*;
import jmmc.mcs.gui.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.table.*;


// TODO handle close button correctly

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class PreferencesView extends McsPreferencesView
{    
    public PreferencesView()
    {
        super();

        _preferences = Preferences.getInstance();        

        // Append help component
        HelpSetupPreferencesView helpView = new HelpSetupPreferencesView(_preferences);
        addPane("Help", helpView);
    }

   
}



/**
 * This Panel is dedicated to manage help behaviour configuration.
 *
 */
class HelpSetupPreferencesView extends JPanel implements Observer,
    ChangeListener
{
    /**
     * DOCUMENT ME!
     */
    private Preferences _preferences;

    /**
     * DOCUMENT ME!
     */
    private JCheckBox _enableToolTipCheckBox;

    /**
     * DOCUMENT ME!
     */
    private ToolTipManager _sharedToolTipManager;

    /**
     * Constructor.
     * @param preferences the application preferences
     */
    public HelpSetupPreferencesView(Preferences preferences)
    {
        _preferences = preferences;
        _preferences.addObserver(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // get instance of shared tooltip to adjust behaviour in update code
        _sharedToolTipManager      = ToolTipManager.sharedInstance();

        // Handle tooltips
        _enableToolTipCheckBox     = new JCheckBox("Show tooltips");

        String ttt                 = Resources.getToolTipText(
                "_enableToolTipCheckBox");
        _enableToolTipCheckBox.setToolTipText(ttt);
        _sharedToolTipManager.registerComponent(_enableToolTipCheckBox);
        _enableToolTipCheckBox.addChangeListener(this);
        add(_enableToolTipCheckBox);

        // Make data filled
        update(null, null);
    }

    /**
     * Present fresh content according preference content.
     *
     * @param o preferences
     * @param arg not used
     */
    public void update(Observable o, Object arg)
    {
        // Adjust view and behaviour according preferences entries
        boolean b;

        // Tooltips
        b = _preferences.getPreferenceAsBoolean("help.tooltips.show");
        _enableToolTipCheckBox.setSelected(b);
        _sharedToolTipManager.setEnabled(b);
    }

    /**
     * Update preferences according buttons change
     */
    public void stateChanged(ChangeEvent e)
    {
        MCSLogger.trace();

        Object source = e.getSource();

        if (source.equals(_enableToolTipCheckBox))
        {
            try{
                _preferences.setPreference("help.tooltips.show",
                _enableToolTipCheckBox.isSelected());
            }catch(Exception exc){
                new ReportDialog(new JFrame(),true,exc).setVisible(true);
                
            }
        }
    }
}
