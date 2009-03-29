/*
 * ImageViewer.java
 *
 * Created on 5 avril 2007, 21:35
 */
package fr.jmmc.mcs;


import java.util.Observable;
import java.util.Observer;



/**
 *
 * @author  mella
 */
public class ImageViewer extends javax.swing.JFrame implements Observer
{
    /**
     * DOCUMENT ME!
     */
    ImageCanvas imageCanvas;

    /**
     * DOCUMENT ME!
     */
    int nbColors = 240; // colorModels have 
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox  colorModelComboBox;
    private javax.swing.JTextField imageInfoTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    /** Creates new form ImageViewer */
    public ImageViewer()
    {
        init();
    }

    /**
     * Creates a new ImageViewer object.
     *
     * @param xmlStr DOCUMENT ME!
     */
    public ImageViewer(String xmlStr)throws Exception
    {
        init();
        imageCanvas.xmlInit(xmlStr);
    }

    /**
     * DOCUMENT ME!
     */
    protected void init()
    {
        initComponents();
        // add image canvas
        imageCanvas = new ImageCanvas();
        imageCanvas.addObserver(this);
        getContentPane().add(imageCanvas, java.awt.BorderLayout.CENTER);
        // set items for colormodels
        colorModelComboBox.setModel(new javax.swing.DefaultComboBoxModel(
                ColorModels.colorModelNames));
    }

    /**
     * DOCUMENT ME!
     *
     * @param observable DOCUMENT ME!
     * @param object DOCUMENT ME!
     */
    public void update(Observable observable, Object object)
    {
        String info = +imageCanvas.getImageDimension().height + "x" +
            imageCanvas.getImageDimension().width + "Image " +
            imageCanvas.getCanvasDimension().height + "x" + imageCanvas.getCanvasDimension().width +
            " px ( " + imageCanvas.mouseX_ + "," + imageCanvas.mouseY_ + " : " +
            imageCanvas.mousePixel_ + "(" 
            + (imageCanvas.mousePixel_*imageCanvas.normalisePixelCoefficient_ )
            + ") )";
        imageInfoTextField.setText(info);
        imageInfoTextField.validate();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1                = new javax.swing.JPanel();
        jLabel1                = new javax.swing.JLabel();
        colorModelComboBox     = new javax.swing.JComboBox();
        imageInfoTextField     = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Color model:");
        jPanel1.add(jLabel1, new java.awt.GridBagConstraints());

        colorModelComboBox.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    colorModelComboBoxActionPerformed(evt);
                }
            });
        jPanel1.add(colorModelComboBox, new java.awt.GridBagConstraints());

        imageInfoTextField.setBorder(null);
        imageInfoTextField.setOpaque(false);
        gridBagConstraints             = new java.awt.GridBagConstraints();
        gridBagConstraints.fill        = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx     = 1.0;
        jPanel1.add(imageInfoTextField, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void colorModelComboBoxActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_colorModelComboBoxActionPerformed
        imageCanvas.setColorModel(ColorModels.colorModels[colorModelComboBox.getSelectedIndex()]);
    }//GEN-LAST:event_colorModelComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        final String[] fargs = args;
        java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    if ((fargs.length >= 1) && fargs[0].equals("-png"))
                    {
                        // Build images
                        for (int i = 1; i < fargs.length; i++)
                        {
                            try
                            {
                                System.out.println("Reading " + fargs[i]);

                                java.io.BufferedReader in  = new java.io.BufferedReader(new java.io.FileReader(
                                            fargs[i]));
                                String                 str;
                                StringBuffer           sb  = new StringBuffer();

                                while ((str = in.readLine()) != null)
                                {
                                    sb.append(str);
                                }

                                in.close();

                                ImageCanvas imageCanvas = new ImageCanvas();
                                imageCanvas.xmlInit("" + sb);

                                // Save as PNG
                                java.io.File file = new java.io.File(fargs[i] + ".png");
                                System.out.println("Generating " + fargs[i] + ".png");
                                javax.imageio.ImageIO.write((java.awt.image.BufferedImage) imageCanvas.image_,
                                    "png", file);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if (fargs.length >= 1)
                    {
                        // show images
                        for (int i = 0; i < fargs.length; i++)
                        {
                            try
                            {
                                java.io.BufferedReader in  = new java.io.BufferedReader(new java.io.FileReader(
                                            fargs[i]));
                                String                 str;
                                StringBuffer           sb  = new StringBuffer();

                                while ((str = in.readLine()) != null)
                                {
                                    sb.append(str);
                                }

                                in.close();

                                ImageViewer viewer = new ImageViewer("" + sb);
                                viewer.setVisible(true);
                                viewer.setTitle(fargs[i]);
                                viewer.pack();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        ImageViewer viewer = new ImageViewer();
                        viewer.setVisible(true);

                        int     w   = 32;
                        int     h   = 32;
                        float[] img = new float[w * h];

                        for (int i = 0; i < img.length; i++)
                        {
                            img[i] = i;
                        }

                        viewer.imageCanvas.initImage(w, h, img);
                    }
                }
            });
    }
}