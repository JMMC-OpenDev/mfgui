/*
 * FilePanel.java
 *
 * Created on 8 novembre 2006, 11:27
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mf.models.File;

import org.apache.commons.math.linear.RealVectorImpl;

//import nom.tam.fits.*;
import org.eso.fits.FitsFile;
import org.eso.fits.FitsTable;

import ptolemy.plot.*;

import ptolemy.plot.plotml.*;

import uk.ac.starlink.topcat.plot.GraphSurface;

import java.net.URI;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 *
 * @author  mella
 */
public class FilePanel extends javax.swing.JPanel
{
    /**
     * DOCUMENT ME!
     */
    static File current = null;

    /**
     * DOCUMENT ME!
     */
    static Action saveEmbeddedFileAction;

    /**
     * DOCUMENT ME!
     */
    static Action showEmbeddedFileAction;

    /**
     * DOCUMENT ME!
     */
    static MyListSelectionListener myListSelectionListener;

    /**
     * DOCUMENT ME!
     */
    static DefaultListModel hduListModel = new DefaultListModel();

    /**
     * DOCUMENT ME!
     */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.FilePanel");

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPopupMenu  fileListPopupMenu;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JList hduList;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextField infosTextField;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel2;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel3;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel5;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JLabel jLabel6;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel2;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JPanel jPanel3;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JScrollPane jScrollPane1;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JMenu listenersMenu;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton loadViewerButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextField localNameTextField;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextField nameTextField;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton saveFileButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton showSketchButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton showT3Button;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton showUVCoverageButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton showVis2Button;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JButton showVisButton;

    /**
     * DOCUMENT ME!
     */
    private javax.swing.JTextField sizeTextField;

    // End of variables declaration//GEN-END:variables

    /** Creates new form FilePanel */
    public FilePanel()
    {
        // Prepare action before gui construction
        saveEmbeddedFileAction     = new SaveEmbeddedFileAction();
        showEmbeddedFileAction     = new ShowEmbeddedFileAction();
        initComponents();

        MyListSelectionListener myListSelectionListener = new MyListSelectionListener();
        hduList.addListSelectionListener(myListSelectionListener);
        //fix default state
        myListSelectionListener.valueChanged(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     */
    public void show(File file)
    {
        try
        {
            current = file;

            nameTextField.setText(file.getName());

            // update name field
            java.io.File f        = new java.io.File(current.getName());
            String       filename = null;

            filename              = UtilsClass.saveBASE64OifitsToFile(current.getId(),
                    current.getHref());

            try
            {
                if (! f.exists())
                {
                    f = new java.io.File(filename);
                }

                localNameTextField.setText(f.getAbsolutePath());
            }
            catch (Exception exc)
            {
                localNameTextField.setText("Can't locate " + current.getName());
            }

            // update size field
            sizeTextField.setText("" + (f.length() / 1000) + " kBytes");

            // update info field
            String list = "";

            for (int i = 0; i < current.getOitargetCount(); i++)
            {
                list += ("," + current.getOitarget(i).getTarget());
            }

            if (current.getOitargetCount() > 0)
            {
                infosTextField.setText("Target list [" + list.substring(1) + "]");
            }
            else
            {
                infosTextField.setText("No Target present");
            }

            // update list of hdu list
            // clear and fill hduListModel
            hduListModel.clear();

            // scan given file and try to get reference of oi_target extension excepted primary hdu
            FitsFile fits  = new FitsFile(filename);
            int      index = fits.getNoHDUnits();

            // start from 1 to skip primary hdu
            for (int i = 1; i < index; i++)
            {
                String extName = fits.getHDUnit(i).getHeader().getKeyword("EXTNAME").getString() +
                    "[" + i + "]";
                hduListModel.addElement(extName);
            }
        }
        catch (Exception exc)
        {
            new FeedbackReport(null, true, exc);
        }
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

        fileListPopupMenu        = new javax.swing.JPopupMenu();
        listenersMenu            = new javax.swing.JMenu();
        jPanel2                  = new javax.swing.JPanel();
        jLabel3                  = new javax.swing.JLabel();
        nameTextField            = new javax.swing.JTextField();
        infosTextField           = new javax.swing.JTextField();
        jLabel5                  = new javax.swing.JLabel();
        jLabel2                  = new javax.swing.JLabel();
        sizeTextField            = new javax.swing.JTextField();
        localNameTextField       = new javax.swing.JTextField();
        jLabel6                  = new javax.swing.JLabel();
        saveFileButton           = new javax.swing.JButton();
        jPanel1                  = new javax.swing.JPanel();
        loadViewerButton         = new javax.swing.JButton();
        jLabel1                  = new javax.swing.JLabel();
        jPanel3                  = new javax.swing.JPanel();
        jScrollPane1             = new javax.swing.JScrollPane();
        hduList                  = new javax.swing.JList();
        showSketchButton         = new javax.swing.JButton();
        showUVCoverageButton     = new javax.swing.JButton();
        showVisButton            = new javax.swing.JButton();
        showVis2Button           = new javax.swing.JButton();
        showT3Button             = new javax.swing.JButton();

        listenersMenu.setText("Send to application"); // NOI18N
        fileListPopupMenu.add(listenersMenu);

        setBorder(javax.swing.BorderFactory.createTitledBorder("File panel"));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("First Name:"); // NOI18N
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel3, gridBagConstraints);

        nameTextField.setEditable(false);
        gridBagConstraints               = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth     = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill          = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx       = 1.0;
        jPanel2.add(nameTextField, gridBagConstraints);

        infosTextField.setEditable(false);
        gridBagConstraints               = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx         = 1;
        gridBagConstraints.gridy         = 5;
        gridBagConstraints.gridwidth     = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill          = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx       = 1.0;
        jPanel2.add(infosTextField, gridBagConstraints);

        jLabel5.setText("Infos:"); // NOI18N
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 0;
        gridBagConstraints.gridy      = 5;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel5, gridBagConstraints);

        jLabel2.setText("Size:"); // NOI18N
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 0;
        gridBagConstraints.gridy      = 4;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel2, gridBagConstraints);
        gridBagConstraints               = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx         = 1;
        gridBagConstraints.gridy         = 4;
        gridBagConstraints.gridwidth     = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill          = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(sizeTextField, gridBagConstraints);

        localNameTextField.setEditable(false);
        gridBagConstraints               = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx         = 1;
        gridBagConstraints.gridy         = 1;
        gridBagConstraints.gridwidth     = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill          = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx       = 1.0;
        jPanel2.add(localNameTextField, gridBagConstraints);

        jLabel6.setText("Local Name:"); // NOI18N
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 0;
        gridBagConstraints.gridy      = 1;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel6, gridBagConstraints);

        saveFileButton.setAction(this.saveEmbeddedFileAction);
        saveFileButton.setForeground(new java.awt.Color(51, 51, 52));
        gridBagConstraints             = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx       = 1;
        gridBagConstraints.gridy       = 3;
        gridBagConstraints.fill        = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx     = 1.0;
        jPanel2.add(saveFileButton, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        loadViewerButton.setAction(showEmbeddedFileAction);
        loadViewerButton.setEnabled(false);
        gridBagConstraints            = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx      = 0;
        gridBagConstraints.gridy      = 2;
        gridBagConstraints.fill       = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor     = java.awt.GridBagConstraints.WEST;
        jPanel1.add(loadViewerButton, gridBagConstraints);

        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("Use Shift or Ctrl keys to select multiple tables");
        jPanel1.add(jLabel1, new java.awt.GridBagConstraints());
        gridBagConstraints             = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx       = 0;
        gridBagConstraints.gridy       = 12;
        gridBagConstraints.fill        = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx     = 1.0;
        gridBagConstraints.weighty     = 1.0;
        jPanel1.add(jPanel3, gridBagConstraints);

        hduList.setModel(hduListModel);
        hduList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
            {
                public void valueChanged(javax.swing.event.ListSelectionEvent evt)
                {
                    hduListValueChanged(evt);
                }
            });
        hduList.addMouseListener(new java.awt.event.MouseAdapter()
            {
                public void mouseClicked(java.awt.event.MouseEvent evt)
                {
                    hduListMouseClicked(evt);
                }

                public void mousePressed(java.awt.event.MouseEvent evt)
                {
                    hduListMousePressed(evt);
                }
            });
        jScrollPane1.setViewportView(hduList);

        gridBagConstraints             = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx       = 0;
        gridBagConstraints.gridy       = 1;
        gridBagConstraints.fill        = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty     = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        showSketchButton.setText("Show interferometer sketch"); // NOI18N
        showSketchButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    showSketchButtonActionPerformed(evt);
                }
            });
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 4;
        gridBagConstraints.fill      = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showSketchButton, gridBagConstraints);

        showUVCoverageButton.setText("Show UV Coverage"); // NOI18N
        showUVCoverageButton.setEnabled(false);
        showUVCoverageButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    showUVCoverageButtonActionPerformed(evt);
                }
            });
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 5;
        gridBagConstraints.fill      = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showUVCoverageButton, gridBagConstraints);

        showVisButton.setText("Show VISDATA"); // NOI18N
        showVisButton.setEnabled(false);
        showVisButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    showVisButtonActionPerformed(evt);
                }
            });
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 6;
        gridBagConstraints.fill      = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showVisButton, gridBagConstraints);

        showVis2Button.setText("Show VIS2DATA"); // NOI18N
        showVis2Button.setEnabled(false);
        showVis2Button.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    showVis2ButtonActionPerformed(evt);
                }
            });
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 7;
        gridBagConstraints.fill      = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showVis2Button, gridBagConstraints);

        showT3Button.setText("Show T3"); // NOI18N
        showT3Button.setEnabled(false);
        showT3Button.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    showT3ButtonActionPerformed(evt);
                }
            });
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 8;
        gridBagConstraints.fill      = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showT3Button, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showUVCoverageButtonActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_showUVCoverageButtonActionPerformed

        /*previously used code:
           // @todo move next code part
                      // We could use the topcat.getTableListModel to open just once each tables
                      uk.ac.starlink.topcat.ControlWindow topcat = uk.ac.starlink.topcat.ControlWindow.getInstance();
                      uk.ac.starlink.table.StarTableFactory stFact = new uk.ac.starlink.table.StarTableFactory();
                      uk.ac.starlink.table.StarTable startab = stFact.makeStarTable(filename +
                              "#" + indice);
                      topcat.addTable(startab, filename + "#" + indice, true);
                      // add x column
                      uk.ac.starlink.table.DefaultValueInfo xValueInfo = new uk.ac.starlink.table.DefaultValueInfo(
                              "UCOORD");
                      uk.ac.starlink.table.DefaultValueInfo yValueInfo = new uk.ac.starlink.table.DefaultValueInfo(
                              "VCOORD");
                      uk.ac.starlink.topcat.SyntheticColumn c;
                      List l = new Vector();
                      l.add(uk.ac.starlink.topcat.RowSubset.ALL);
                      c = new uk.ac.starlink.topcat.SyntheticColumn(xValueInfo,
                              startab, l, "UCOORD", null);
                      topcat.getCurrentModel().appendColumn(c, 0);
                      c = new uk.ac.starlink.topcat.SyntheticColumn(yValueInfo,
                              startab, l, "VCOORD", null);
                      topcat.getCurrentModel().appendColumn(c, 1);
                      uk.ac.starlink.topcat.plot.PlotWindow plot = new uk.ac.starlink.topcat.plot.PlotWindow(jPanel3);
                      // setVisible must be called before model assignement
                      plot.setVisible(true);
                      plot.setMainTable(topcat.getCurrentModel());
         */

        // Get all selected items 
        int[] indices = hduList.getSelectedIndices();

        // Iterate all selected items
        showUVCoverageButton.setEnabled(false);

        String filename = "";

        try
        {
            filename = UtilsClass.saveBASE64OifitsToFile(current.getId(), current.getHref());

            FitsFile     fits = new FitsFile(filename);
            StringBuffer sb   = new StringBuffer();
            sb.append("<?xml version=\"1.0\" standalone=\"yes\"?>" +
                "<!DOCTYPE plot PUBLIC \"-//UC Berkeley//DTD PlotML 1//EN\"" +
                " \"http://ptolemy.eecs.berkeley.edu/xml/dtd/PlotML_1.dtd\">" + "<plot>" +
                "<!-- Ptolemy plot, version 5.6 , PlotML format. -->" +
                "<title>UV coverage</title>" + "<xLabel>UCOORD</xLabel>" +
                "<yLabel>VCOORD</yLabel>");

            for (int i = 0; i < indices.length; i++)
            {
                int    indice = indices[i];
                String label  = (String) hduList.getModel().getElementAt(indice);
                indice++; // 1 is not the first table

                FitsTable      table  = (FitsTable) fits.getHDUnit(indice).getData();
                RealVectorImpl ucoord = new RealVectorImpl(getRealsOfColumn(table, "UCOORD"));
                RealVectorImpl vcoord = new RealVectorImpl(getRealsOfColumn(table, "VCOORD"));
                // add symetrical part
                sb.append(buildXmlPtPlotDataSet(label,
                        ucoord.append(ucoord.mapMultiply(-1)).getData(),
                        vcoord.append(vcoord.mapMultiply(-1)).getData(), null, null));
            }

            sb.append("</plot>");

            Plot         plot         = new Plot();
            PlotMLParser plotMLParser = new PlotMLParser(plot);
            plotMLParser.parse(null, sb.toString());

            JFrame f = new JFrame();
            f.getContentPane().add(plot);
            f.pack();
            f.setVisible(true);
        }
        catch (Exception exc)
        {
            logger.warning("Can't do graph");
            new FeedbackReport(null, true, exc);
        }
    } //GEN-LAST:event_showUVCoverageButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showSketchButtonActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_showSketchButtonActionPerformed

        try
        {
            String filename = UtilsClass.saveBASE64OifitsToFile(current.getId(), current.getHref());

            // scan given file and try to get reference of oi_target extension excepted primary hdu
            FitsFile fits  = new FitsFile(filename);
            int      index = fits.getNoHDUnits();

            // start from 1 to skip primary hdu
            for (int i = 1; i < index; i++)
            {
                String extName = fits.getHDUnit(i).getHeader().getKeyword("EXTNAME").getString();

                if (extName.trim().equalsIgnoreCase("OI_ARRAY"))
                {
                    filename = filename + "#" + i;
                }
            }

            // We could use the topcat.getTableListModel to open just once each tables
            uk.ac.starlink.topcat.ControlWindow   topcat  = uk.ac.starlink.topcat.ControlWindow.getInstance();
            uk.ac.starlink.table.StarTableFactory stFact  = new uk.ac.starlink.table.StarTableFactory();
            uk.ac.starlink.table.StarTable        startab = stFact.makeStarTable(filename);
            topcat.addTable(startab, filename, true);

            // add x column            
            uk.ac.starlink.table.DefaultValueInfo xValueInfo = new uk.ac.starlink.table.DefaultValueInfo(
                    "X");
            uk.ac.starlink.table.DefaultValueInfo yValueInfo = new uk.ac.starlink.table.DefaultValueInfo(
                    "Y");
            uk.ac.starlink.topcat.SyntheticColumn c;

            List                                  l          = new Vector();
            l.add(uk.ac.starlink.topcat.RowSubset.ALL);
            c = new uk.ac.starlink.topcat.SyntheticColumn(xValueInfo, startab, l, "STAXYZ[0]", null);
            topcat.getCurrentModel().appendColumn(c, 0);
            c = new uk.ac.starlink.topcat.SyntheticColumn(yValueInfo, startab, l, "STAXYZ[1]", null);
            topcat.getCurrentModel().appendColumn(c, 1);

            uk.ac.starlink.topcat.plot.PlotWindow plot = new uk.ac.starlink.topcat.plot.PlotWindow(jPanel3);
            // setVisible must be called before model assignement
            plot.setVisible(true);
            plot.setMainTable(topcat.getCurrentModel());

            // choose other axis linke next lines seem imossible 
            // generated plotState uses n first column...
            // just gui seems too have access on it
            // next ps is generated one
            /*uk.ac.starlink.topcat.plot.PlotState ps = plot.getPlotState();
               ps.setAxes(new uk.ac.starlink.table.ValueInfo[]{xValueInfo, yValueInfo});
             * maybe that one more line code could take prev modification
             */
        }
        catch (Exception exc)
        {
            logger.warning("Can't do graph");
            new FeedbackReport(null, true, exc);
        }
    } //GEN-LAST:event_showSketchButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param app DOCUMENT ME!
     */
    public void menuRequested(uk.ac.starlink.plastic.ApplicationItem app)
    {
        try
        {
            URI loadFromURLURI = new URI("ivo://votech.org/votable/loadFromURL");

            if (app.getSupportedMessages().contains(loadFromURLURI))
            {
                int[] indices = hduList.getSelectedIndices();

                for (int i = 0; i < indices.length; i++)
                {
                    String       filename = current.getName();
                    java.io.File f        = new java.io.File(filename);

                    if (! f.exists())
                    {
                        filename     = UtilsClass.saveBASE64OifitsToFile(current.getId(),
                                current.getHref());
                        f            = new java.io.File(filename);
                    }

                    java.util.Vector args = new Vector();
                    args.addElement("file://localhost/" + filename + "#" + (indices[i]));
                    logger.warning("request app '" + app + "' to load:" + args.get(0));
                    MFGui.getPlasticServer().perform(app.getId(), loadFromURLURI, args);
                }
            }
            else
            {
                logger.info("app does not support:" + loadFromURLURI);
            }
        }
        catch (Exception exc)
        {
            new FeedbackReport(null, true, exc);
        }

        /*list.getElementAt();
           MainFrame.getPlasticServer().perform();
         **/
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void hduListMousePressed(java.awt.event.MouseEvent evt)
    { //GEN-FIRST:event_hduListMousePressed
        logger.entering("" + this.getClass(), "hduListMousePressed");

        //if (evt.isPopupTrigger() && hduList.getSelectedIndex()>0 ) {
        if (evt.isPopupTrigger())
        {
            logger.finest("Menu required");
            listenersMenu.removeAll();

            ListModel list = MFGui.getPlasticServer().getApplicationListModel();

            for (int i = 0; i < list.getSize(); i++)
            {
                final uk.ac.starlink.plastic.ApplicationItem app     = (uk.ac.starlink.plastic.ApplicationItem) list.getElementAt(i);
                String                                       appName = "" + app;

                if (! appName.equalsIgnoreCase("hub") &&
                        ! appName.equalsIgnoreCase("modelfitting"))
                {
                    JMenuItem menuItem = new JMenuItem(appName);
                    menuItem.addActionListener(new java.awt.event.ActionListener()
                        {
                            public void actionPerformed(java.awt.event.ActionEvent evt)
                            {
                                menuRequested(app);
                            }
                        });

                    listenersMenu.add(menuItem);
                }
            }

            if (listenersMenu.getComponentCount() == 0)
            {
                JMenuItem menuItem = new JMenuItem(
                        "Please register with plastic or start another interop application");
                menuItem.setEnabled(false);
                listenersMenu.add(menuItem);
                logger.fine("No application can handle the files");
            }

            fileListPopupMenu.validate();
            fileListPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        else
        {
            logger.finest("No menu required");
        }
    } //GEN-LAST:event_hduListMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void hduListMouseClicked(java.awt.event.MouseEvent evt)
    { //GEN-FIRST:event_hduListMouseClicked
        logger.entering("" + this.getClass(), "hduListMouseClicked");

        // launch viewer if double click else adjust view button state
        if (evt.getClickCount() == 2)
        {
            loadViewerButton.doClick();
        }

        if (hduList.getSelectedIndex() >= 0)
        {
            loadViewerButton.setEnabled(true);
        }
        else
        {
            loadViewerButton.setEnabled(false);
        }
    } //GEN-LAST:event_hduListMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void hduListValueChanged(javax.swing.event.ListSelectionEvent evt)
    { //GEN-FIRST:event_hduListValueChanged

        if (hduList.getSelectedIndex() >= 0)
        {
            loadViewerButton.setEnabled(true);
        }
        else
        {
            loadViewerButton.setEnabled(false);
        }
    } //GEN-LAST:event_hduListValueChanged

    /*TO PUT ON ANOTHER PLACE*/
    /** Build a ptplot xml file of given data */
    private String buildXmlPtPlot()
    {
        StringBuffer sb = new StringBuffer();

        return sb.toString();
    }

    /*TO PUT ON ANOTHER PLACE*/
    /** Build a ptplot xml file of given data */
    private String buildXmlPtPlotDataSet(String datasetName, double[] x, double[] y,
        double[] lowErrorBar, double[] highErrorBar)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<dataset connected=\"no\" marks=\"dots\" name=\"" + datasetName + "\">\n");

        for (int i = 0; i < x.length; i++)
        {
            sb.append("<m x=\"" + x[i] + "\" y=\"" + y[i] + "\"");

            if (lowErrorBar != null)
            {
                sb.append(" lowErrorBar=\"" + lowErrorBar[i] + "\"");
            }

            if (highErrorBar != null)
            {
                sb.append(" highErrorBar=\"" + highErrorBar[i] + "\"");
            }

            sb.append("/>\n");
        }

        sb.append("</dataset>");

        return sb.toString();
    }

    /*TO PUT ON ANOTHER PLACE*/
    /** Build a double array from a single column*/
    private double[] getRealsOfColumn(FitsTable table, String label)
    {
        int      nbRows = table.getNoRows();
        double[] d      = new double[nbRows];

        for (int rowIndex = 0; rowIndex < nbRows; rowIndex++)
        {
            d[rowIndex] = table.getColumn(label).getReal(rowIndex);
        }

        return d;
    }

    /**
     * DOCUMENT ME!
     */
    public void showVisData()
    {
        try
        {
            String filename = UtilsClass.saveBASE64OifitsToFile(current.getId(), current.getHref());

            // scan given file and try to get reference of oi_target extension excepted primary hdu
            FitsFile fits  = new FitsFile(filename);
            int      index = fits.getNoHDUnits();

            // Get all selected items 
            int[]        indices  = hduList.getSelectedIndices();
            StringBuffer sb       = new StringBuffer();
            String       dataName;
            String       errName;
            sb.append("<?xml version=\"1.0\" standalone=\"yes\"?>" +
                "<!DOCTYPE plot PUBLIC \"-//UC Berkeley//DTD PlotML 1//EN\"" +
                " \"http://ptolemy.eecs.berkeley.edu/xml/dtd/PlotML_1.dtd\">" + "<plot>" +
                "<!-- Ptolemy plot, version 5.6 , PlotML format. -->" +
                "<title>Plot VIS or VIS2 versus radial distance</title>" +
                "<xLabel>spatial frequency (1/rad)</xLabel>" + "<yLabel>(VIS or VIS2)</yLabel>");

            for (int i = 0; i < indices.length; i++)
            {
                int    indice = indices[i];
                String label  = (String) hduList.getModel().getElementAt(indice);

                if (label.startsWith("OI_VIS2"))
                {
                    dataName     = "VIS2DATA";
                    errName      = "VIS2ERR";
                }
                else
                {
                    dataName     = "VISDATA";
                    errName      = "VISERR";
                }

                indice++; // 1 is not the first table

                FitsTable      table    = (FitsTable) fits.getHDUnit(indice).getData();
                int            nbRows   = table.getNoRows();
                RealVectorImpl vis2data = new RealVectorImpl(getRealsOfColumn(table, dataName));
                RealVectorImpl vis2err  = new RealVectorImpl(getRealsOfColumn(table, errName));
                RealVectorImpl ucoord   = new RealVectorImpl(getRealsOfColumn(table, "UCOORD"));
                RealVectorImpl vcoord   = new RealVectorImpl(getRealsOfColumn(table, "VCOORD"));
                RealVectorImpl dist     = (RealVectorImpl) ucoord.ebeMultiply(ucoord)
                                                                 .add(vcoord.ebeMultiply(vcoord))
                                                                 .mapSqrt();
                sb.append(buildXmlPtPlotDataSet(label, dist.getData(), vis2data.getData(),
                        vis2data.add(vis2err.mapMultiply(-0.5)).getData(),
                        vis2data.add(vis2err.mapMultiply(0.5)).getData()));
            }

            sb.append("</plot>");

            Plot         plot         = new Plot();
            PlotMLParser plotMLParser = new PlotMLParser(plot);
            plotMLParser.parse(null, sb.toString());

            JFrame f = new JFrame();
            f.getContentPane().add(plot);
            f.pack();
            f.setVisible(true);
        }
        catch (Exception exc)
        {
            new FeedbackReport(null, true, exc);
        }
    }

    /*TO PUT ON ANOTHER PLACE*/
    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showVisButtonActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_showVisButtonActionPerformed
        showVisData();
    } //GEN-LAST:event_showVisButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showVis2ButtonActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_showVis2ButtonActionPerformed
        showVisData();
    } //GEN-LAST:event_showVis2ButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showT3ButtonActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_showT3ButtonActionPerformed
        showVisData();
    } //GEN-LAST:event_showT3ButtonActionPerformed

    // End of variables declaration                   
    /**
     * DOCUMENT ME!
     *
     * @param targetFile DOCUMENT ME!
     *
     */
    public static void saveFile(java.io.File targetFile)
        throws java.io.IOException, java.io.FileNotFoundException
    {
        UtilsClass.saveBASE64OifitsToFile(current.getHref(), targetFile);
    }

    // Enable or not the showUVCoverage Button on each new list selection
    class MyListSelectionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent evt)
        {
            showVisButton.setEnabled(false);
            showVis2Button.setEnabled(false);
            showT3Button.setEnabled(false);

            if (evt == null)
            {
                return;
            }

            // When the user release the mouse button and completes the selection,
            // getValueIsAdjusting() becomes false
            if (! evt.getValueIsAdjusting())
            {
                if (! (evt.getSource() instanceof JList))
                {
                    return;
                }

                JList list = (JList) evt.getSource();

                // Get all selected items
                Object[] selected = list.getSelectedValues();
                // Iterate all selected items
                showUVCoverageButton.setEnabled(false);
                showVisButton.setEnabled(true);
                showVis2Button.setEnabled(true);
                showT3Button.setEnabled(true);

                for (int i = 0; i < selected.length; i++)
                {
                    String extName = (String) selected[i];

                    if (extName.startsWith("OI_VIS"))
                    {
                        showUVCoverageButton.setEnabled(true);
                    }

                    if (! extName.startsWith("OI_VIS["))
                    {
                        showVisButton.setEnabled(false);
                    }

                    if (! extName.startsWith("OI_VIS2["))
                    {
                        showVis2Button.setEnabled(false);
                    }

                    if (! extName.startsWith("OI_T3["))
                    {
                        showT3Button.setEnabled(false);
                    }
                }
            }
        }
    }

    protected class ShowEmbeddedFileAction extends fr.jmmc.mcs.util.MCSAction
    {
        public ShowEmbeddedFileAction()
        {
            super("showEmbeddedFile");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            try
            {
                int[] indices = hduList.getSelectedIndices();

                for (int i = 0; i < indices.length; i++)
                {
                    java.io.File f        = new java.io.File(current.getName());
                    String       filename = current.getName();

                    if (! f.exists())
                    {
                        filename = UtilsClass.saveBASE64OifitsToFile(current.getId(),
                                current.getHref());
                    }

                    ModelFitting.startFitsViewer(filename + "#" + ((indices[i]) + 1));
                }
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }
    }

    protected class SaveEmbeddedFileAction extends fr.jmmc.mcs.util.MCSAction
    {
        public String lastDir = System.getProperty("user.home");

        public SaveEmbeddedFileAction()
        {
            super("saveEmbeddedFile");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            //logger.entering(""+this.getClass(), "actionPerformed");
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

            // Set in previous save directory
            if (lastDir != null)
            {
                fileChooser.setCurrentDirectory(new java.io.File(lastDir));
            }

            try
            {
                // Open filechooser
                int returnVal = fileChooser.showSaveDialog(null);

                if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION)
                {
                    java.io.File file = fileChooser.getSelectedFile();
                    lastDir = file.getParent();

                    // Ask to overwrite
                    if (file.exists())
                    {
                        String message = "File '" + file.getName() +
                            "' already exists\nDo you want to overwrite this file?";

                        // Modal dialog with yes/no button
                        int answer = javax.swing.JOptionPane.showConfirmDialog(null, message);

                        if (answer == javax.swing.JOptionPane.YES_OPTION)
                        {
                            saveFile(file);
                        }
                    }
                    else
                    {
                        saveFile(file);
                    }
                }
            }
            catch (Exception exc)
            {
                new FeedbackReport(null, true, exc);
            }
        }
    }
}
