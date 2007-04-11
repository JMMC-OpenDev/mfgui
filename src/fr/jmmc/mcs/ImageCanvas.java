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
import java.awt.image.*;
import java.awt.geom.*;


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
    Image image;
    WritableRaster imageRaster;
    Image wedge;
    IndexColorModel colorModel ;
    int w,h;
    
    //properties
    private boolean antiAliasing;
    
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("fr.jmmc.mcs.ImageCanvas");
    
    /**
     * Creates a new instance of ImageCanvas
     */
    public ImageCanvas() {
        image=null;
        imageRaster=null;
        // set default properties
        colorModel = ColorModels.colorModels[0];
        antiAliasing=false;
        
        setColorModel(colorModel);
    } 
    
    /** Change color model and repaint canvas */
    public void setColorModel(IndexColorModel cm){
        colorModel=cm;    
        
        buildWedge();
        buildImage();
        
        repaint();
    }
    
    private void buildWedge(){
        int wedgeSize=256;
        wedge = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(1, wedgeSize,
                colorModel, generateWedge(0,wedgeSize,wedgeSize), 0, 1));        
    }
    
    
    protected void initImage(int w, int h, float[] array){
        logger.entering(""+this.getClass(), "initImage");
        this.w=w;
        this.h=h;
        
        // search min and max of input array
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
        
        
        // Define image min and image max values. And set coefficient to normalize image values
        int iMin=0;
        int iMax=255;
        float c = (iMax-iMin)/(aMax-aMin);
        if(c==0){
            c=1;
        }
        
        logger.finest("Xml Image: dims="+w+"x"+h+", min="+aMin
                +", max="+aMax+", c="+c
                +", array[0]="+array[0]);
        System.out.println("Xml Image: dims="+w+"x"+h+", min="+aMin
                +", max="+aMax+", c="+c
                +", array[0]="+array[0]);
        
        imageRaster = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, w, h, new int[]{0xFF}, new Point(0,0));
        DataBuffer dataBuffer = imageRaster.getDataBuffer();
        
        // init raster pixels
        for (int i = 0; i < array.length; i++) {
          int v = (int)( array[i] * c );
          dataBuffer.setElem(i, v);  
        }
        
        buildImage();
               
        // set new canvas dimension 
        Dimension d = new Dimension(w,h);
        setMinimumSize(d);
        setPreferredSize(d);
    }
    
    private void buildImage(){
        if (imageRaster!= null){
            image=new BufferedImage(colorModel, imageRaster,false, null);
        }
    }
    
    private int[] generateWedge(int min, int max, int size) {
        int[] pixels = new int[size];
        for (int i = 0; i < size; i++) {
            pixels[size-i-1]=(i*(max-min)/size);
        }
        return pixels;
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
            float[] array = fitsImage.getFloatValues(0, w*h, null);
            initImage(w,h,array);
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
            
            float[] array=new float[h*w];
            // init array content
            for (int j = 0; j < h; j++) {
                tr =  (Element)trList.item(j);
                tdList = tr.getElementsByTagName("td");
                for (int i = 0; i < w; i++) {
                    Element td =  (Element)tdList.item((w-1)-i);
                    array[i+(j*w)]=Float.parseFloat(td.getTextContent());
                }
            }
            initImage(w,h,array);
        }catch(Exception exc){
            logger.finest(xmlStr);
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
    }
    
    public void paint(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        // set antiAliasing 
        if (antiAliasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        
        Dimension d = new Dimension(this.getWidth(), this.getHeight());
        int canvasWidth=(int)d.getWidth()-70;
        int canvasHeight=(int)d.getHeight()-34;        
        
        int margin=15;
        int wedgeWidth=10;
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, (int)d.getWidth(), (int)d.getHeight());
        g2d.setColor(Color.BLACK);
                
        if (canvasWidth>0 && canvasHeight>0){
            if (image!=null){
                // draw image into rect
                g2d.drawImage(image, margin+1, margin+1, canvasWidth, canvasHeight, null);
                g2d.drawRect(margin, margin, canvasWidth+1, canvasHeight+1);                
            }
            if (wedge!=null){
                int wedgeImageDist=10;
                g2d.drawRect(margin+canvasWidth+wedgeImageDist,margin, wedgeWidth+1, canvasHeight+1);
                g2d.drawString("255", margin+canvasWidth+wedgeImageDist+wedgeWidth+3, margin+6 );
                g2d.drawString("0", margin+canvasWidth+wedgeImageDist+wedgeWidth+3, canvasHeight+20 );
                g2d.drawImage(wedge, margin+canvasWidth+wedgeImageDist+1, margin+1, wedgeWidth, canvasHeight, null);    
            }
        }
       
        // display canvas size
        g2d.drawString("Image size:"+w+"x"+h+" - Canvas size:"+canvasWidth+"x"+canvasHeight, 14,14);        
    }       
}