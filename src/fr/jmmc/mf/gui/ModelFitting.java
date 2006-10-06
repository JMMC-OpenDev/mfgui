/*
 JMMC
*/
package jmmc.mf.gui;

import jmmc.mcs.gui.*;

import jmmc.mcs.log.MCSLogger;

import java.util.logging.*;

import javax.swing.JButton;
import javax.swing.JOptionPane;


/**
 *
 * @author mella
 */
public class ModelFitting
{
    final static String rcsId="$Id: ModelFitting.java,v 1.2 2006-10-06 09:19:28 mella Exp $";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {        
        try
        {
            // Get a MCSLogger reference  and adjust for convenience
            // @TODO : move this in MCSLogger
            Logger logger = MCSLogger.getLogger();
            logger.setLevel(java.util.logging.Level.ALL);
            MCSLogger.trace();
            logger.info("Starting ModelFitting");
            logger.info("Rev:"+rcsId);           

            // Create a specific console handler
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(java.util.logging.Level.ALL);
            //logger.addHandler(handler);                      
            
            ReportDialog.setDefaultComment( "Please complete above"+
                    " informations to improve this software.\n"+
                    rcsId+"\n"+
                    "---\n");
                        
            MainFrame myFrame = new MainFrame();
            myFrame.setVisible(true);
        }
        catch (Exception e)
        {            
            new ReportDialog(new javax.swing.JFrame(), true, e).setVisible(true);            
            System.exit(1);
        }                
    }
}
