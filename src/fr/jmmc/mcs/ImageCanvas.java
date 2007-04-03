/*
 * ImageCanvas.java
 *
 * Created on 19 mars 2007, 11:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.jmmc.mcs;

import fr.jmmc.mf.gui.UtilsClass;
import java.awt.*;
import java.awt.image.BufferedImage;


 import org.eso.fits.*;
import java.io.DataInput;
import java.io.RandomAccessFile;
/*
import nom.tam.fits.*;
*/

    import javax.xml.parsers.*;
    import org.w3c.dom.*;
    import org.xml.sax.*;

/**
 *
 * @author mella
 */
public class ImageCanvas extends Canvas{
    BufferedImage image;  
    int w,h;    
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("fr.jmmc.mcs.ImageCanvas");
    
    /**
     * Creates a new instance of ImageCanvas
     */
    public ImageCanvas() {
        image=null;      
    }
    
    /** Init image shown set width and height to fits naxis values */
    public void fitsInit(String fitsFilename){  
        logger.entering(""+this.getClass(), "fitsInit"); 
        try{     
            /* Try using nom.tam.fits library
             * but fits file generate error reading image hdu
             *
            Fits fitsFile = new Fits(fitsFilename); 
            BasicHDU[] hdus = fitsFile.read();
            System.out.println("Nb of HDU "+hdus.length);
            
            ImageHDU fitsImage = (ImageHDU)fitsFile.readHDU();
            fitsImage.info();
             */
            
          DataInput file = new RandomAccessFile(fitsFilename,"r");
            FitsHeader header = new FitsHeader(file);
            FitsMatrix fitsImage = new FitsMatrix(header,file,false);
            int[] naxis = fitsImage.getNaxis();
            w = naxis[0];            
            h = naxis[1];            
            logger.fine("image size: "+w+"x"+h);
            float[] array = fitsImage.getFloatValues(0, w*h, null);            
            // search min and max into array
            float aMin=array[0];
            float aMax=array[0];
            for (int i = 1; i < array.length; i++) {
                float a=array[i];
                if(aMin>=a){
                    aMin=a;
                }                
                if(aMax<=a){
                    aMax=a;
                }                
            }
            // modify to test image
            aMin=0;
            aMax=w*h+2;
            // set image min and image max values
            int iMin=0;
            int iMax=256;
            //int iMax=Integer.MAX_VALUE;
            float c = (iMax-iMin)/(aMax-aMin);
            image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);            
            for (int i=0;i<w; i++){
                for (int j = 0; j < h; j++) {
                    int v = (int)( (i+1)*(j+1) * c );                    
                    image.setRGB(i, j, v);
                    System.out.print(":"+image.getRGB(i,j));
                    System.out.print(","+v); 
                }
                System.out.println();
            }            
            /*
            // set image min and image max values
            int iMin=0;
            int iMax=255;
            float c = (iMax-iMin)/(aMax-aMin);
            image = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);            
            for (int i=0;i<w; i++){
                for (int j = 0; j < h; j++) {
                    System.out.print(" "+array[i+j*w]); 
                    // 1er essai int v = (int)(array[i+j*w]*20*256);
                    // Ne marche pas: int v = (int)(1/array[i+j*w]);
                    int v = (int)( ( array[i+j*w] - aMin ) * c );                    
                    image.setRGB(i, j, v);
                    System.out.print(":"+image.getRGB(i,j));
                    System.out.print(","+v); 
                }
                System.out.println();
            }            
             */
         
        }catch(Exception exc){                        
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
    }  
        
    /** Init image shown set width and height to fits naxis values. 
     * Only first table is used to init pixel values.
     * Table is supporsed to be rectangular because dimension is obtained
     * getting number of rows and number of columns in the first row. */
    public void xmlInit(String xmlStr){  
        logger.entering(""+this.getClass(), "xmlInit"); 
        logger.finest(xmlStr);
        try{
            Document doc = UtilsClass.parseXmlString(xmlStr, false);
            NodeList list = doc.getElementsByTagName("table");
            Element tableElement = (Element)list.item(0);
            NodeList trList = tableElement.getElementsByTagName("tr");            
            Element tr =  (Element)trList.item(0);
            NodeList tdList = tr.getElementsByTagName("td");
            int h=trList.getLength();
            int w=tdList.getLength();
            logger.fine("Xml Image: dims="+w+"x"+h+", min=");
            
            double[] array=new double[h*w];            
            // init array content
            for (int j = 0; j < h; j++) {
                tr =  (Element)trList.item(j);                                
                tdList = tr.getElementsByTagName("td");
                for (int i = 0; i < w; i++) {
                    Element td =  (Element)tdList.item((w-1)-i);                  
                    array[i+(j*w)]=Double.parseDouble(td.getTextContent());
                }
            }                    
            
            // search min and max into array
            double aMin=array[0];
            double aMax=array[0];
            for (int i = 1; i < array.length; i++) {
                double a=array[i];
                if(aMin>=a){
                    aMin=a;
                }                
                if(aMax<=a){
                    aMax=a;
                }                
            }                        
            
            // set image min and image max values
            int iMin=0;
            int iMax=256;            
            // set coefficient to normalize image values
            double c = (iMax-iMin)/(aMax-aMin);
            if(c==0){
                c=1;
            }
            logger.finest("Xml Image: dims="+w+"x"+h+", min="+aMin
                    +", max="+aMax+", c="+c
                    +", array[0]="+array[0]);
            
            image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);            
            for (int i=0;i<w; i++){
                for (int j = 0; j < h; j++) {
                    int v = (int)( array[i+j*w] * c );                    
                    image.setRGB(i, j, v);
                     if(aMax<=array[i+j*w]){
                        aMax=array[i+j*w];
                     }
                }                
            }         
        }catch(Exception exc){            
            logger.finest(xmlStr);
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
    }          
    
    public void paint(Graphics g){        
        Dimension d = new Dimension(this.getHeight(), this.getWidth());
        if (image!=null){
            g.drawImage(image, 0, 0, d.width, d.height, null);            
        }        
    }        
}