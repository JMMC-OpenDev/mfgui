/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: McsPreferencesView.java,v 1.5 2007-02-14 14:14:57 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/11/21 13:11:01  mella
 * blah
 *
 * Revision 1.1  2006/10/06 09:19:28  mella
 * Add missing files for a clean state
 *
 *
 ******************************************************************************/
package fr.jmmc.mf.gui;

import jmmc.mcs.util.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.table.*;


// @TODO handle close button correctly

/**
 * This is a preference dedicated to the java SearchCal Client.
 */
public class McsPreferencesView extends JFrame implements ActionListener
{
    /** Data model */
    Preferences _preferences;

    /** "Restore to Default Settings" button */
    protected JButton _restoreDefaultButton;

    /** "Save Modifications" button */
    protected JButton _saveModificationButton;

    /** Allow tabbed pane presentation */
    private JTabbedPane tabbedPane;
    
    /**
     * Constructor.
     */
    public McsPreferencesView()
    {
        super("Preferences");

        
        // Set Window sized and centered
        setSize(480, 360);
        setResizable(false);
        setLocationRelativeTo(null);

        // Get and listen to data model modifications
        _preferences = Preferences.getInstance();

        // Build the tabbed pane
        tabbedPane  = new JTabbedPane();
        Container   contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(tabbedPane, BorderLayout.CENTER);      

        // Add the restore and save buttons
        JPanel buttonsPanel = new JPanel();
        _restoreDefaultButton = new JButton("Restore to Default Settings");
        _restoreDefaultButton.addActionListener(this);
        buttonsPanel.add(_restoreDefaultButton);
        _saveModificationButton = new JButton("Save Modifications");
        _saveModificationButton.addActionListener(this);
        buttonsPanel.add(_saveModificationButton);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    /** Add a new pane */
    public void addPane(String title, Component c){
        tabbedPane.add(title, c);
    }

    /**
     * actionPerformed  -  Listener
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        // If the "Restore to default settings" button has been pressed
        if (evt.getSource().equals(_restoreDefaultButton))
        {
            try
            {
                _preferences.resetToDefaultPreferences();
            }
            catch (Exception e)
            {
                // TODO criez fort!!
                e.printStackTrace();
            }
        }

        // If the "Save modifications" button has been pressed
        if (evt.getSource().equals(_saveModificationButton))
        {
            try
            {
                _preferences.saveToFile();
            }
            catch (Exception e)
            {
                // TODO criez fort!!
                e.printStackTrace();
            }
        }
    }
}
