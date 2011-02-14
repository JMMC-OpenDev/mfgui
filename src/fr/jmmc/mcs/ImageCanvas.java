package fr.jmmc.mcs;

import fr.jmmc.mf.gui.UtilsClass;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.*;


import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;


import java.util.Observable;
import java.util.Observer;
import org.xml.sax.SAXException;

/*
   import nom.tam.fits.*;
 */


/**
 * Canvas dedicated to plot one image with interactive informations.
 * @author mella
 */
public class ImageCanvas extends Canvas implements MouseMotionListener
{
    /**
     * DOCUMENT ME!
     */
    static final int nbColors_ = 240;

    /**
     * DOCUMENT ME!
     */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mcs.ImageCanvas");

    /**
     * DOCUMENT ME!
     */
    Image image_;

    /**
     * DOCUMENT ME!
     */
    WritableRaster imageRaster_;

    /**
     * DOCUMENT ME!
     */
    Image wedge_;

    /**
     * DOCUMENT ME!
     */
    IndexColorModel colorModel_;

    /**
     * DOCUMENT ME!
     */
    int w_;

    /**
     * DOCUMENT ME!
     */
    int h_;

    /**
     * DOCUMENT ME!
     */
    int mouseX_;

    /**
     * DOCUMENT ME!
     */
    int mouseY_;

    /**
     * DOCUMENT ME!
     */
    int mousePixel_;    
    float normalisePixelCoefficient_;
    

    /**
     * DOCUMENT ME!
     */
    int canvasWidth_;

    /**
     * DOCUMENT ME!
     */
    int canvasHeight_;

    // Define constant to place differnet members of plot
    /**
     * DOCUMENT ME!
     */
    int leftInset      = 20;

    /**
     * DOCUMENT ME!
     */
    int rightInset = 30;

    /**
     * DOCUMENT ME!
     */
    int topInset = 10;

    /**
     * DOCUMENT ME!
     */
    int bottomInset = 25;

    /**
     * DOCUMENT ME!
     */
    int wedgeWidth = 10;

    /**
     * DOCUMENT ME!
     */
    int wedgeImageDist = 10;

    //properties
    /**
     * DOCUMENT ME!
     */
    private boolean antiAliasing;

    /**
     * DOCUMENT ME!
     */
    ObservableImage observe_;

    /**
     * Creates a new instance of ImageCanvas
     */
    public ImageCanvas()
    {
        image_         = null;
        imageRaster_   = null;
        // set default properties
        colorModel_    = ColorModels.colorModels[0];
        antiAliasing   = false;

        setColorModel(colorModel_);
        w_                = 0;
        h_                = 0;
        canvasWidth_      = 500;
        canvasHeight_     = 500;

        observe_          = new ObservableImage();
        this.addMouseMotionListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseMoved(MouseEvent e)
    {
        mouseX_     = ((e.getX() - leftInset) * w_) / canvasWidth_;
        mouseY_     = ((e.getY() - topInset) * h_) / canvasHeight_;

        if ((mouseX_ >= 0) && (mouseY_ >= 0) && (mouseX_ < w_) && (mouseY_ < h_))
        {
            mousePixel_ = imageRaster_.getSample(mouseX_, mouseY_, 0);
            observe_.setChanged();
        }

        observe_.notifyObservers();
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseDragged(MouseEvent e)
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @param observer DOCUMENT ME!
     */
    public void addObserver(Observer observer)
    {
        observe_.addObserver(observer);
    }

    /** Change color model and repaint canvas */
    public void setColorModel(IndexColorModel cm)
    {
        colorModel_ = cm;

        buildWedge();
        buildImage();

        repaint();
    }

    /**
     * DOCUMENT ME!
     */
    private void buildWedge()
    {
        int wedgeSize = nbColors_;
        wedge_ = Toolkit.getDefaultToolkit()
                        .createImage(new MemoryImageSource(1, wedgeSize, colorModel_,
                    generateWedge(0, wedgeSize, wedgeSize), 0, 1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param w DOCUMENT ME!
     * @param h DOCUMENT ME!
     * @param array DOCUMENT ME!
     */
    protected void initImage(int w, int h, float[] array)
    {
        logger.entering("" + this.getClass(), "initImage");
        this.w_     = w;
        this.h_     = h;
        logger.finest("initImage: using array of size  " + w + "x" + h +
            " array : nb of point is " + array.length);

        // search min and max of input array
        float aMin = array[0];
        float aMax = array[0];

        for (int i = 1; i < array.length; i++)
        {
            float a = array[i];

            if (aMin >= a)
            {
                aMin = a;
            }

            if (aMax <= a)
            {
                aMax = a;
            }
        }

        // Define image min and image max values. And set coefficient to normalize image values
        int   iMin = 0;
        int   iMax = nbColors_ - 1;
        float c    = (iMax - iMin) / (aMax - aMin);

        if (c == 0)
        {
            c = 1;
        }

        logger.finest("Image: dims=" + w + "x" + h + ", min=" + aMin + ", max=" + aMax + ", c=" +
            c + ", array[0]=" + array[0]);

        imageRaster_ = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, w, h, new int[] { 0xFF },
                new Point(0, 0));

        DataBuffer dataBuffer = imageRaster_.getDataBuffer();

        // init raster pixels
        for (int i = 0; i < array.length; i++)
        {
            int v = (int) ((array[i] - aMin) * c);

            if (v > nbColors_)
            {
                System.out.print("point nb " + i + " > 256 !!");
            }

            dataBuffer.setElem(i, v);
        }

        normalisePixelCoefficient_=c;
        buildImage();

        // set new canvas dimension 
        Dimension d = new Dimension(w, h);
        setMinimumSize(d);
        setPreferredSize(d);
    }

    /**
     * DOCUMENT ME!
     */
    private void buildImage()
    {
        if (imageRaster_ != null)
        {
            image_ = new BufferedImage(colorModel_, imageRaster_, false, null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param min DOCUMENT ME!
     * @param max DOCUMENT ME!
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int[] generateWedge(int min, int max, int size)
    {
        int[] pixels = new int[size];

        for (int i = 0; i < size; i++)
        {
            pixels[size - i - 1] = ((i * (max - min)) / size);
        }

        return pixels;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getCanvasDimension()
    {
        Dimension canvasDim = new Dimension();
        canvasDim.setSize(canvasHeight_, canvasWidth_);

        return canvasDim;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getImageDimension()
    {
        Dimension imageDim = new Dimension();
        imageDim.setSize(h_, w_);

        return imageDim;
    }

    /** Init image shown set width and height to fits naxis values.
     * Only first table is used to init pixel values.
     * Table is supporsed to be rectangular because dimension is obtained
     * getting number of rows and number of columns in the first row.
     * @throws IllegalStateException
     */
    public void xmlInit(String xmlStr)throws IllegalStateException
    {
        try {
            logger.entering("" + this.getClass(), "xmlInit");
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
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException("Can't init image with given xml",ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't init image with given xml",ex);
        } catch (SAXException ex) {
            throw new IllegalStateException("Can't init image with given xml",ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        // set antiAliasing 
        if (antiAliasing)
        {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        else
        {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        Dimension d = new Dimension(this.getWidth(), this.getHeight());
        canvasWidth_      = (int) d.getWidth() - leftInset - rightInset - wedgeWidth -
            wedgeImageDist;
        canvasHeight_     = (int) d.getHeight() - topInset - bottomInset;

        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        g2d.setColor(Color.BLACK);

        if ((canvasWidth_ > 0) && (canvasHeight_ > 0))
        {
            if (image_ != null)
            {
                // draw image into rect
                g2d.drawImage(image_, leftInset + 1, topInset + 1, canvasWidth_, canvasHeight_, null);

                // draw vertical tics
                for (int i = 0; i < h_; i++)
                {
                    int y = topInset + ((canvasHeight_ * i) / h_) + (canvasHeight_ / (2 * h_));
                    g2d.drawLine(leftInset - 2, y, leftInset, y);
                    g2d.drawString("" + i, leftInset - 20, y + 4);
                }

                // draw horizontal tics
                for (int i = 0; i < w_; i++)
                {
                    int x = leftInset + ((canvasWidth_ * i) / w_) + (canvasWidth_ / (2 * w_));
                    g2d.drawLine(x, topInset + canvasHeight_, x, topInset + canvasHeight_ + 3);
                    g2d.drawString("" + i, x - 4, topInset + canvasHeight_ + 15);
                }

                g2d.drawRect(leftInset, topInset, canvasWidth_ + 1, canvasHeight_ + 1);
            }

            if (wedge_ != null)
            {
                g2d.drawRect(leftInset + canvasWidth_ + wedgeImageDist, topInset, wedgeWidth + 1,
                    canvasHeight_ + 1);
                g2d.drawString("" + nbColors_,
                    leftInset + canvasWidth_ + wedgeImageDist + wedgeWidth + 3, topInset + 6);
                g2d.drawString("0", leftInset + canvasWidth_ + wedgeImageDist + wedgeWidth + 3,
                    topInset + canvasHeight_);
                g2d.drawImage(wedge_, leftInset + canvasWidth_ + wedgeImageDist + 1, topInset + 1,
                    wedgeWidth, canvasHeight_, null);
            }
        }

        observe_.notifyImageObservers();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getPreferredSize()
    {
        return this.getCanvasDimension();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getMinimumSize()
    {
        return this.getCanvasDimension();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getMaximumSize()
    {
        return this.getCanvasDimension();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ImageViewer.main(args);
    }

    private class ObservableImage extends Observable
    {
        public void notifyImageObservers()
        {
            setChanged();
            notifyObservers();
        }

        public void setChanged()
        {
            super.setChanged();
        }
    }
}
