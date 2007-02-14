/*
 JMMC
*/
package fr.jmmc.mf.gui;

import jmmc.mcs.log.*;

import jmmc.mcs.util.*;

import javax.swing.*;


/**
 * Use this class  to define new Actions.
 */
public class MFAction extends AbstractAction
{
    /**
     * This constructor use the resource file to get text description and icon
     * of action.
     */
    public MFAction(String actionName)
    {
        // Collect action info
        String    text = Resources.getActionText(actionName);
        String    desc = Resources.getActionDescription(actionName);
        ImageIcon icon = Resources.getActionIcon(actionName);
        // Init action    
        // @TODO check if null must be checked...
        putValue(Action.NAME, text);
        putValue(Action.SHORT_DESCRIPTION, desc);
        putValue(Action.SMALL_ICON, icon);
    }

    public void actionPerformed(java.awt.event.ActionEvent e)
    {
        // Do nothing 
        // Sub action must overwrite this method
    }
}
/*___oOo___*/
