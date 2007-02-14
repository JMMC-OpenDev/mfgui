/*
 JMMC
*/
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ReportDialog;

import java.util.logging.*;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author mella
 */
public class ModelFitting
{
    final static String rcsId="$Id: ModelFitting.java,v 1.8 2007-02-14 14:44:03 mella Exp $";
    static Logger logger = Logger.getLogger("fr.jmmc.mf.gui.ModelFitting");                
    
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
            
            // Get all system properties
            Properties props = System.getProperties();            
            // Enumerate all system properties
            Enumeration e = props.propertyNames();
            for (; e.hasMoreElements(); ) {
                // Get property name
                String propName = (String)e.nextElement();
                
                // Get property value
                String propValue = (String)props.get(propName);
                logger.fine(propName+"="+propValue);
            }
            // Set default resource for application
            fr.jmmc.mcs.util.Resources.setResourceName("fr/jmmc/mf/gui/Resources");            
            logger.info("Starting ModelFitting Rev:"+rcsId);                    
            logger.info("Version:"+ fr.jmmc.mcs.util.Resources.getResource("mf.version"));             
            // Set the default locale to custom locale
            Locale locale = Locale.US;
            Locale.setDefault(locale);
            logger.info("Setting locale to:"+locale);
            
            ReportDialog.setDefaultComment( "This forms does not actually works but present requirements....\n\n Please complete above"+
                    " informations to improve this software.\n"+
                    //rcsId+"\n"+
                    "---\n");
            
            MainFrame myFrame = new MainFrame();     
            myFrame.setVisible(true); 
        }
        catch (Exception exc)
        {            
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);            
            System.exit(1);
        }                
    }
    
    public static void startFitsViewer(String filename){
        logger.fine("request fits viewer to show '"+filename+"'");
        uk.ac.starlink.topcat.ControlWindow topcat = uk.ac.starlink.topcat.ControlWindow.getInstance();
        try{
            topcat.addTable(topcat.getTableFactory().makeStarTable(filename)
                    , filename
                    , true);
        }catch (Exception exc)
        {            
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);                        
        }        
    }
    
    
}
