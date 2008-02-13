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

import org.eso.fits.*;

import org.w3c.dom.*;

import org.xml.sax.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.awt.image.*;

import java.io.DataInput;
import java.io.RandomAccessFile;

import java.util.Observable;
import java.util.Observer;

/*
import nom.tam.fits.*;
 */
import javax.xml.parsers.*;


/**
 *
 * @author mella
 */
public class ImageCanvas extends Canvas implements MouseMotionListener {
    static final int nbColors_ = 240;
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mcs.ImageCanvas");
    Image image_;
    WritableRaster imageRaster_;
    Image wedge_;
    IndexColorModel colorModel_;
    int w_;
    int h_;
    int mouseX_;
    int mouseY_;
    int mousePixel_;
    int canvasWidth_;
    int canvasHeight_;

    // Define constant to place differnet members of plot
    int leftInset = 20;
    int rightInset = 30;
    int topInset = 10;
    int bottomInset = 25;
    int wedgeWidth = 10;
    int wedgeImageDist = 10;

    //properties
    private boolean antiAliasing;
    ObservableImage observe_;

    /**
     * Creates a new instance of ImageCanvas
     */
    public ImageCanvas() {
        image_ = null;
        imageRaster_ = null;
        // set default properties
        colorModel_ = ColorModels.colorModels[0];
        antiAliasing = false;

        setColorModel(colorModel_);
        w_ = 0;
        h_ = 0;
        canvasWidth_ = 500;
        canvasHeight_ = 500;

        observe_ = new ObservableImage();
        this.addMouseMotionListener(this);
    }

    public void mouseMoved(MouseEvent e) {
        mouseX_ = ((e.getX() - leftInset) * w_) / canvasWidth_;
        mouseY_ = ((e.getY() - topInset) * h_) / canvasHeight_;

        if ((mouseX_ > 0) && (mouseY_ > 0) && (mouseX_ < w_) && (mouseY_ < h_)) {
            mousePixel_ = imageRaster_.getSample(mouseX_, mouseY_, 0);
            observe_.setChanged();
        }

        observe_.notifyObservers();
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void addObserver(Observer observer) {
        observe_.addObserver(observer);
    }

    /** Change color model and repaint canvas */
    public void setColorModel(IndexColorModel cm) {
        colorModel_ = cm;

        buildWedge();
        buildImage();

        repaint();
    }

    private void buildWedge() {
        int wedgeSize = nbColors_;
        wedge_ = Toolkit.getDefaultToolkit()
                        .createImage(new MemoryImageSource(1, wedgeSize,
                    colorModel_, generateWedge(0, wedgeSize, wedgeSize), 0, 1));
    }

    protected void initImage(int w, int h, float[] array) {
        logger.entering("" + this.getClass(), "initImage");
        this.w_ = w;
        this.h_ = h;
        logger.finest("initImage: using array of size  " + w + "x" + h +
            " array : nb of point is " + array.length);

        // search min and max of input array
        float aMin = array[0];
        float aMax = array[0];

        for (int i = 1; i < array.length; i++) {
            float a = array[i];

            if (aMin >= a) {
                aMin = a;
            }

            if (aMax <= a) {
                aMax = a;
            }
        }

        // Define image min and image max values. And set coefficient to normalize image values
        int iMin = 0;
        int iMax = nbColors_ - 1;
        float c = (iMax - iMin) / (aMax - aMin);

        if (c == 0) {
            c = 1;
        }

        logger.finest("Image: dims=" + w + "x" + h + ", min=" + aMin +
            ", max=" + aMax + ", c=" + c + ", array[0]=" + array[0]);

        imageRaster_ = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, w, h,
                new int[] { 0xFF }, new Point(0, 0));

        DataBuffer dataBuffer = imageRaster_.getDataBuffer();

        // init raster pixels
        for (int i = 0; i < array.length; i++) {
            int v = (int) ((array[i] - aMin) * c);

            if (v > nbColors_) {
                System.out.print("point nb " + i + " > 256 !!");
            }

            dataBuffer.setElem(i, v);
        }

        buildImage();

        // set new canvas dimension 
        Dimension d = new Dimension(w, h);
        setMinimumSize(d);
        setPreferredSize(d);
    }

    private void buildImage() {
        if (imageRaster_ != null) {
            image_ = new BufferedImage(colorModel_, imageRaster_, false, null);
        }
    }

    private int[] generateWedge(int min, int max, int size) {
        int[] pixels = new int[size];

        for (int i = 0; i < size; i++) {
            pixels[size - i - 1] = ((i * (max - min)) / size);
        }

        return pixels;
    }

    /** Init image shown set width and height to fits naxis values */
    public void fitsInit(String fitsFilename) {
        logger.entering("" + this.getClass(), "fitsInit");

        try {
            /* Try using nom.tam.fits library
             * but fits file generate error reading image hdu
             *
            Fits fitsFile = new Fits(fitsFilename);
            BasicHDU[] hdus = fitsFile.read();
            System.out.println("Nb of HDU "+hdus.length);
            ImageHDU fitsImage = (ImageHDU)fitsFile.readHDU();
            fitsImage.info();
             */
            DataInput file = new RandomAccessFile(fitsFilename, "r");
            FitsHeader header = new FitsHeader(file);
            FitsMatrix fitsImage = new FitsMatrix(header, file, false);
            int[] naxis = fitsImage.getNaxis();
            w_ = naxis[0];
            h_ = naxis[1];

            float[] array = fitsImage.getFloatValues(0, w_ * h_, null);
            initImage(w_, h_, array);
        } catch (Exception exc) {
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
    }

    public Dimension getCanvasDimension() {
        Dimension canvasDim = new Dimension();
        canvasDim.setSize(canvasHeight_, canvasWidth_);

        return canvasDim;
    }

    public Dimension getImageDimension() {
        Dimension imageDim = new Dimension();
        imageDim.setSize(h_, w_);

        return imageDim;
    }

    /** Init image shown set width and height to fits naxis values.
     * Only first table is used to init pixel values.
     * Table is supporsed to be rectangular because dimension is obtained
     * getting number of rows and number of columns in the first row. */
    public void xmlInit(String xmlStr) {
        logger.entering("" + this.getClass(), "xmlInit");

        try {
            Document doc = UtilsClass.parseXmlString(xmlStr, false);
            NodeList list = doc.getElementsByTagName("table");
            Element tableElement = (Element) list.item(0);
            NodeList trList = tableElement.getElementsByTagName("tr");
            Element tr = (Element) trList.item(0);
            NodeList tdList = tr.getElementsByTagName("td");
            int h = trList.getLength();
            int w = tdList.getLength();
            logger.fine("xmlInit: found " + w + "x" + h + " array");

            float[] array = new float[h * w];

            // init array content
            for (int i = 0; i < h; i++) {
                tr = (Element) trList.item(i);
                tdList = tr.getElementsByTagName("td");
                for (int j = 0; j < w; j++) {
                    Element td = (Element) tdList.item(j);                               
                    array[i + ((w - j - 1) * h)] = Float.parseFloat(td.getTextContent());
                }
            }
            initImage(w, h, array);
        } catch (Exception exc) {            
            new fr.jmmc.mcs.gui.ReportDialog(new javax.swing.JFrame(), true, exc).setVisible(true);
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // set antiAliasing 
        if (antiAliasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        Dimension d = new Dimension(this.getWidth(), this.getHeight());
        canvasWidth_ = (int) d.getWidth() - leftInset - rightInset -
            wedgeWidth - wedgeImageDist;
        canvasHeight_ = (int) d.getHeight() - topInset - bottomInset;

        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        g2d.setColor(Color.BLACK);

        if ((canvasWidth_ > 0) && (canvasHeight_ > 0)) {
            if (image_ != null) {
                // draw image into rect
                g2d.drawImage(image_, leftInset + 1, topInset + 1,
                    canvasWidth_, canvasHeight_, null);

                // draw vertical tics
                for (int i = 0; i < h_; i++) {
                    int y = topInset + ((canvasHeight_ * i) / h_) +
                        (canvasHeight_ / (2 * h_));
                    g2d.drawLine(leftInset - 2, y, leftInset, y);
                    g2d.drawString("" + i, leftInset - 20, y + 4);
                }

                // draw horizontal tics
                for (int i = 0; i < w_; i++) {
                    int x = leftInset + ((canvasWidth_ * i) / w_) +
                        (canvasWidth_ / (2 * w_));
                    g2d.drawLine(x, topInset + canvasHeight_, x,
                        topInset + canvasHeight_ + 3);
                    g2d.drawString("" + i, x - 4, topInset + canvasHeight_ +
                        15);
                }

                g2d.drawRect(leftInset, topInset, canvasWidth_ + 1,
                    canvasHeight_ + 1);
            }

            if (wedge_ != null) {
                g2d.drawRect(leftInset + canvasWidth_ + wedgeImageDist,
                    topInset, wedgeWidth + 1, canvasHeight_ + 1);
                g2d.drawString("" + nbColors_,
                    leftInset + canvasWidth_ + wedgeImageDist + wedgeWidth + 3,
                    topInset + 6);
                g2d.drawString("0",
                    leftInset + canvasWidth_ + wedgeImageDist + wedgeWidth + 3,
                    topInset + canvasHeight_);
                g2d.drawImage(wedge_,
                    leftInset + canvasWidth_ + wedgeImageDist + 1,
                    topInset + 1, wedgeWidth, canvasHeight_, null);
            }
        }

        observe_.notifyImageObservers();
    }

    public Dimension getPreferredSize() {
        return this.getCanvasDimension();
    }

    public Dimension getMinimumSize() {
        return this.getCanvasDimension();
    }

    public Dimension getMaximumSize() {
        return this.getCanvasDimension();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ImageViewer.main(args);
    }

    private class ObservableImage extends Observable {
        public void notifyImageObservers() {
            setChanged();
            notifyObservers();
        }

        public void setChanged() {
            super.setChanged();
        }
    }
}
