/*
 JMMC
*/
package jmmc.mf.gui;

import jmmc.mcs.gui.*;

import java.util.logging.*;

import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author mella
 */
public class ModelFitting
{
    final static String rcsId="$Id: ModelFitting.java,v 1.4 2006-11-21 13:11:01 mella Exp $";
    
    static String getVersion(){
        return rcsId;
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {        
        try
        {            
            Logger logger = Logger.getLogger("jmmc.mf");
            logger.info("Starting ModelFitting");
            logger.info("Rev:"+rcsId);        
            
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
