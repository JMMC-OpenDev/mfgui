/*
 * FilePanel.java
 *
 * Created on 8 novembre 2006, 11:27
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mf.models.File;

import fr.jmmc.oifits.*;

import fr.jmmc.oifits.validator.GUIValidator;
import org.apache.commons.math.linear.RealVectorImpl;

//import nom.tam.fits.*;
import org.eso.fits.FitsFile;

import ptolemy.plot.*;

import ptolemy.plot.plotml.*;


import java.net.URI;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.math.linear.RealMatrixImpl;


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

    static OifitsFile oifitsFile_=null;
    /**
     * DOCUMENT ME!
     */
    static Action saveEmbeddedFileAction;

    /**
     * DOCUMENT ME!
     */
    static Action checkEmbeddedFileAction;

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
    static ListModel hduListModel = new DefaultListModel();

    /**
     * DOCUMENT ME!
     */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.FilePanel");

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton checkFileButton;
    private javax.swing.JPopupMenu fileListPopupMenu;
    private javax.swing.JList hduList;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu listenersMenu;
    private javax.swing.JButton loadViewerButton;
    private javax.swing.JTextField localNameTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton saveFileButton;
    private javax.swing.JButton showSketchButton;
    private javax.swing.JButton showT3Button;
    private javax.swing.JButton showUVCoverageButton;
    private javax.swing.JButton showVis2Button;
    private javax.swing.JButton showVisButton;
    private javax.swing.JCheckBox t3ampCheckBox;
    private javax.swing.JCheckBox t3phiCheckBox;
    private javax.swing.JCheckBox visampCheckBox;
    private javax.swing.JCheckBox visphiCheckBox;
    // End of variables declaration//GEN-END:variables

    /** Creates new form FilePanel */
    public FilePanel()
    {
        // Prepare action before gui construction
        saveEmbeddedFileAction     = new SaveEmbeddedFileAction();
        showEmbeddedFileAction     = new ShowEmbeddedFileAction();
        checkEmbeddedFileAction     = new CheckEmbeddedFileAction();
        initComponents();

        MyListSelectionListener myListSelectionListener = new MyListSelectionListener();
        hduList.addListSelectionListener(myListSelectionListener);
        //fix default state
        myListSelectionListener.valueChanged(null);
    }

    /**
     * This must be doced and improvedbecause it build a new fitsfile instance for each show method call...
     *
     * @param file DOCUMENT ME!
     */
    public void show(File file)
    {
        // Try to load data file
        oifitsFile_=null;
        try
        {
            current = file;            
            String     filename   = UtilsClass.saveBASE64OifitsToFile(current,
                    current.getHref());
            java.io.File f        = new java.io.File(filename);
            oifitsFile_  = new OifitsFile(filename);

            // update file info fields
            nameTextField.setText(file.getName());
            localNameTextField.setText(f.getAbsolutePath() +"  ("+(f.length() / 1000) + " kBytes)");         
           
            // update lists
            hduList.setListData(oifitsFile_.getOiTables());
            hduListModel=hduList.getModel();            
            showUVCoverageButton.setEnabled(false);
            showSketchButton.setEnabled(oifitsFile_.hasOiArray());
            showVisButton.setEnabled(oifitsFile_.hasOiVis());
            visampCheckBox.setEnabled(oifitsFile_.hasOiVis());
            visphiCheckBox.setEnabled(oifitsFile_.hasOiVis());
            showVis2Button.setEnabled(oifitsFile_.hasOiVis2());
            showT3Button.setEnabled(oifitsFile_.hasOiT3());
            t3ampCheckBox.setEnabled(oifitsFile_.hasOiT3());
            t3phiCheckBox.setEnabled(oifitsFile_.hasOiT3());
            // update button state
            hduList.setSelectedIndices(new int[]{});                            
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

        fileListPopupMenu = new javax.swing.JPopupMenu();
        listenersMenu = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        localNameTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        saveFileButton = new javax.swing.JButton();
        checkFileButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        loadViewerButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        hduList = new javax.swing.JList();
        showSketchButton = new javax.swing.JButton();
        showUVCoverageButton = new javax.swing.JButton();
        showVisButton = new javax.swing.JButton();
        showVis2Button = new javax.swing.JButton();
        showT3Button = new javax.swing.JButton();
        t3ampCheckBox = new javax.swing.JCheckBox();
        t3phiCheckBox = new javax.swing.JCheckBox();
        visampCheckBox = new javax.swing.JCheckBox();
        visphiCheckBox = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();

        listenersMenu.setText("Send to application"); // NOI18N
        fileListPopupMenu.add(listenersMenu);

        setBorder(javax.swing.BorderFactory.createTitledBorder("File panel"));
        setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Name:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel3, gridBagConstraints);

        nameTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(nameTextField, gridBagConstraints);

        localNameTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(localNameTextField, gridBagConstraints);

        jLabel6.setText("Local Name:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jLabel6, gridBagConstraints);

        saveFileButton.setAction(this.saveEmbeddedFileAction);
        saveFileButton.setForeground(new java.awt.Color(51, 51, 52));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(saveFileButton, gridBagConstraints);

        checkFileButton.setAction(this.checkEmbeddedFileAction);
        checkFileButton.setForeground(new java.awt.Color(51, 51, 52));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(checkFileButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel2, gridBagConstraints);

        jPanel1.setPreferredSize(new java.awt.Dimension(388, 291));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        loadViewerButton.setAction(showEmbeddedFileAction);
        loadViewerButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(loadViewerButton, gridBagConstraints);

        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("Use Shift or Ctrl keys to select multiple tables");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(22, 130));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(259, 431));

        hduList.setModel(hduListModel);
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        showSketchButton.setText("Show interferometer sketch"); // NOI18N
        showSketchButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showSketchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showSketchButton, gridBagConstraints);

        showUVCoverageButton.setText("Show UV Coverage of selected tables"); // NOI18N
        showUVCoverageButton.setEnabled(false);
        showUVCoverageButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showUVCoverageButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showUVCoverageButton, gridBagConstraints);

        showVisButton.setText("showVisButton label inited in code"); // NOI18N
        showVisButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showVisButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showVisButton, gridBagConstraints);

        showVis2Button.setText("showVis2Button label inited in code"); // NOI18N
        showVis2Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showVis2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showVis2Button, gridBagConstraints);

        showT3Button.setText("showT3Button label inited in code"); // NOI18N
        showT3Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showT3ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showT3Button, gridBagConstraints);

        t3ampCheckBox.setSelected(true);
        t3ampCheckBox.setText("T3AMP");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        jPanel1.add(t3ampCheckBox, gridBagConstraints);

        t3phiCheckBox.setSelected(true);
        t3phiCheckBox.setText("T3PHI");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        jPanel1.add(t3phiCheckBox, gridBagConstraints);

        visampCheckBox.setSelected(true);
        visampCheckBox.setText("VISAMP");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        jPanel1.add(visampCheckBox, gridBagConstraints);

        visphiCheckBox.setSelected(true);
        visphiCheckBox.setText("VISPHI");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        jPanel1.add(visphiCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jButton1.setAction(FilesPanel.loadFilesAction);
        jButton1.setText("Load one other file...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jButton1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showSketchButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_showSketchButtonActionPerformed

        try
        {
           String filename= oifitsFile_.getName();
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

            uk.ac.starlink.topcat.plot.PlotWindow plot = new uk.ac.starlink.topcat.plot.PlotWindow(jPanel1);
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
    }//GEN-LAST:event_showSketchButtonActionPerformed

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
                    String       filename = oifitsFile_.getName();
                    
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
    {//GEN-FIRST:event_hduListMousePressed
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
    }//GEN-LAST:event_hduListMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void hduListMouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_hduListMouseClicked
        logger.entering("" + this.getClass(), "hduListMouseClicked");
        // launch viewer if double click
        if (evt.getClickCount() == 2)
        {
            loadViewerButton.doClick();
        }
    }//GEN-LAST:event_hduListMouseClicked

    /*TO PUT ON ANOTHER PLACE*/
    /** Build a ptplot xml file for given data */
    private String buildXmlPtPlotDataSet(String datasetName, double[] x, double[] y,
        double[] errorBar)
    {
        StringBuffer sb = new StringBuffer();
        if(errorBar!=null){
            logger.fine("x,y,err dimensions :"+x.length+","+y.length+","+errorBar.length);
        }   else{
            logger.fine("x,y dimensions :"+x.length+","+y.length);
        }
            
        sb.append("<dataset connected=\"no\" marks=\"dots\" name=\"" + datasetName + "\">\n");
        for (int i = 0; i < x.length; i++)
        {
            sb.append("<m x=\"" + x[i] + "\" y=\"" + y[i] + "\"");
            if (errorBar != null)
            {
                double lowEB=y[i]-errorBar[i]/2;
                double highEB=y[i]+errorBar[i]/2;                
                sb.append(" lowErrorBar=\"" + lowEB + "\"");
                sb.append(" highErrorBar=\"" + highEB + "\"");
            }
            sb.append("/>\n");
        }
        sb.append("</dataset>");        
        return sb.toString();
    }
    /** Build a ptplot xml file for given data */
    private String buildXmlPtPlotDataSet(String datasetName, double[][] x, double[][] y,
            double[][] errorBar)
    {
        StringBuffer sb = new StringBuffer();
        logger.fine("Dims=" + x.length + "," + y.length + "," + errorBar.length + "," + errorBar.length + ",");
        sb.append("<dataset connected=\"no\" marks=\"dots\" name=\"" + datasetName + "\">\n");
        for (int i = 0; i < x.length; i++)
        {
            for (int j = 0; j < x[0].length; j++)
            {
                sb.append("<m x=\"" + x[i][j] + "\" y=\"" + y[i][j] + "\"");
                if (errorBar != null)
                {
                    double lowEB = y[i][j] - errorBar[i][j] / 2;
                    double highEB = y[i][j] + errorBar[i][j] / 2;
                    sb.append(" lowErrorBar=\"" + lowEB + "\"");
                    sb.append(" highErrorBar=\"" + highEB + "\"");
                }
                sb.append("/>\n");
            }
        }        
        sb.append("</dataset>");
        return sb.toString();
    }
    
    /**
     * DOCUMENT ME!     
     */
    public void showData(String requestedTables, String [] requestedColumns)
    {
        logger.fine("Searching to plot "+requestedTables);
        try
        {        
            int retainedHdu = 0;
            // Select requested tables:
            // plot all reqeusted if nothing selected or selection does not contains requested
            // else plot only selected
            Object[] selected = hduList.getSelectedValues();
            OiTable[] oiTables = oifitsFile_.getOiTables();
            selected = hduList.getSelectedValues();
            // Search in selection
            for (int i = 0; i < selected.length; i++)
            {
                OiTable t = (OiTable) selected[i];
                if (t.getExtName().equals(requestedTables))
                {
                    oiTables[retainedHdu] = t;
                    retainedHdu++;
                }
            }
            // or search on all if nothing found
            if (retainedHdu == 0)
            {
                // no hdu found, we will try again on all hdu
                for (int i = 0; i < oiTables.length; i++)
                {
                    OiTable t = (OiTable) oiTables[i];
                    if (t.getExtName().equals(requestedTables))
                    {
                        oiTables[retainedHdu] = t;
                        retainedHdu++;
                    }
                }
            }

            /* Start plotting loop on retained HDU*/
            StringBuffer sb = new StringBuffer();
            String dataName;
            String errName;
            sb.append("<?xml version=\"1.0\" standalone=\"yes\"?>" +
                    "<!DOCTYPE plot PUBLIC \"-//UC Berkeley//DTD PlotML 1//EN\"" +
                    " \"http://ptolemy.eecs.berkeley.edu/xml/dtd/PlotML_1.dtd\">" + "<plot>" +
                    "<!-- Ptolemy plot, version 5.6 , PlotML format. -->" +
                    "<title>data versus radial distance</title>" +
                    "<xLabel>spatial frequency (1/rad)</xLabel>" + "<yLabel>" + requestedTables + "</yLabel>");

            for (int i = 0; i < retainedHdu; i++)
            {
                OiTable table = oiTables[i];

                for (int j = 0; j < requestedColumns.length; j++)
                {
                    String requestedColumn = requestedColumns[j];
                    String label = table.getExtName()+"#"+table.getExtNb()+":"+requestedColumn;
                    double data[][] = null;
                    double err[][] = null;
                    double dist[][] = null;
                    if (table instanceof OiT3)
                    {
                        OiT3 t = (OiT3) table;
                        if (requestedColumn.equals("T3AMP"))
                        {
                            data = t.getT3Amp();
                            err = t.getT3AmpErr();
                        }
                        if (requestedColumn.equals("T3PHI"))
                        {
                            RealMatrixImpl m = new RealMatrixImpl(t.getT3Phi());
                            data = m.scalarMultiply(Math.PI/180).getData();                            
                            err = t.getT3PhiErr();
                        }
                        dist = t.getSpacial();
                    } else if (table instanceof OiVis)
                    {
                        OiVis t = (OiVis) table;
                        if (requestedColumn.equals("VISAMP"))
                        {                            
                            data = t.getVisAmp();
                            err = t.getVisAmpErr();
                        }
                        if (requestedColumn.equals("VISPHI"))
                        {
                            RealMatrixImpl m = new RealMatrixImpl(t.getVisPhi());
                            data = m.scalarMultiply(Math.PI/180).getData();                            
                            err = t.getVisPhiErr();
                        }
                        dist = t.getSpacial();
                    } else
                    {
                        OiVis2 t = (OiVis2) table;
                        data = t.getVis2Data();
                        err = t.getVis2Err();
                        dist = t.getSpacial();
                    }
                    sb.append(buildXmlPtPlotDataSet(label, dist, data, err));
                }
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
    {//GEN-FIRST:event_showVisButtonActionPerformed
        final String [] ampAndPhi=new String[]{"VISAMP", "VISPHI"};
        final String [] amp=new String[]{"VISAMP"};
        final String [] phi=new String[]{"VISPHI"};
        final String [] nothing = new String[]{};
        
        if(visampCheckBox.isSelected() && visphiCheckBox.isSelected()){
            showData("OI_VIS", ampAndPhi);    
        }
        if(visampCheckBox.isSelected() && !visphiCheckBox.isSelected()){
            showData("OI_VIS", amp);    
        }
        if(!visampCheckBox.isSelected() && visphiCheckBox.isSelected()){
            showData("OI_VIS", phi);    
        }
        if(!visampCheckBox.isSelected() && !visphiCheckBox.isSelected()){
            showData("OI_VIS", nothing);    
        }
        
    }//GEN-LAST:event_showVisButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showVis2ButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_showVis2ButtonActionPerformed
        showData("OI_VIS2", new String[]{"VIS2DATA"});
    }//GEN-LAST:event_showVis2ButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void showT3ButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_showT3ButtonActionPerformed
        final String [] ampAndPhi=new String[]{"T3AMP", "T3PHI"};
        final String [] amp=new String[]{"T3AMP"};
        final String [] phi=new String[]{"T3PHI"};
        final String [] nothing = new String[]{};
        
        if(t3ampCheckBox.isSelected() && t3phiCheckBox.isSelected()){
            showData("OI_T3", ampAndPhi);    
        }
        if(t3ampCheckBox.isSelected() && !t3phiCheckBox.isSelected()){
            showData("OI_T3", amp);    
        }
        if(!t3ampCheckBox.isSelected() && t3phiCheckBox.isSelected()){
            showData("OI_T3", phi);    
        }
        if(!t3ampCheckBox.isSelected() && !t3phiCheckBox.isSelected()){
            showData("OI_T3", nothing);    
        }
    }//GEN-LAST:event_showT3ButtonActionPerformed

    private void showUVCoverageButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showUVCoverageButtonActionPerformed
    {//GEN-HEADEREND:event_showUVCoverageButtonActionPerformed
        // Iterate all selected items
        showUVCoverageButton.setEnabled(false);
        try
        {           
            StringBuffer sb   = new StringBuffer();
            sb.append("<?xml version=\"1.0\" standalone=\"yes\"?>" +
                "<!DOCTYPE plot PUBLIC \"-//UC Berkeley//DTD PlotML 1//EN\"" +
                " \"http://ptolemy.eecs.berkeley.edu/xml/dtd/PlotML_1.dtd\">" + "<plot>" +
                "<!-- Ptolemy plot, version 5.6 , PlotML format. -->" +
                "<title>UV coverage</title>" + "<xLabel>UCOORD</xLabel>" +
                "<yLabel>VCOORD</yLabel>");
            
            Object selected[] = hduList.getSelectedValues();
            for (int i = 0; i < selected.length; i++)
            {
                OiTable table  = (OiTable)selected[i];
                String label = table.getExtName();
                RealVectorImpl ucoord=null;                        
                RealVectorImpl vcoord=null;

                if (table instanceof OiVis )
                {
                    OiVis t=(OiVis)table;
                    ucoord = new RealVectorImpl(t.getUCoord()); 
                    vcoord = new RealVectorImpl(t.getVCoord());                    
                }
                if (table instanceof OiVis2 )
                {
                    OiVis2 t=(OiVis2)table;
                    ucoord = new RealVectorImpl(t.getUCoord()); 
                    vcoord = new RealVectorImpl(t.getVCoord());
                }
                if (table instanceof OiT3 )
                {
                    OiT3 t=(OiT3)table;
                    ucoord = new RealVectorImpl(t.getU1Coord()); 
                    vcoord = new RealVectorImpl(t.getV1Coord());
                    ucoord.append(new RealVectorImpl(t.getU2Coord()));
                    vcoord.append(new RealVectorImpl(t.getV2Coord()));           
                }
                // add symetrical part and request plot file building
                sb.append(buildXmlPtPlotDataSet(label,
                        ucoord.append(ucoord.mapMultiply(-1)).getData(),
                        vcoord.append(vcoord.mapMultiply(-1)).getData(),  null));
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
                                                      
 
    }//GEN-LAST:event_showUVCoverageButtonActionPerformed

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

    // THIS CLASS handles button states according to the table selection
    class MyListSelectionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent evt)
        {
            // Iterate all selected items                
            showUVCoverageButton.setEnabled(false);
            showVisButton.setText("Plot VISPHI of all OI_VIS");
            showVis2Button.setText("Plot VIS2AMP of all OI_VIS2");
            showT3Button.setText("Plot T3PHI of all OI_T3");

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
                         
                if (hduList.getSelectedIndex() >= 0)
                {
                    loadViewerButton.setEnabled(true);
                } else
                {
                    loadViewerButton.setEnabled(false);
                }
                
                // Get all selected items
                Object[] selected=hduList.getSelectedValues();                
         
                for (int i = 0; i < selected.length; i++)
                {
                    OiTable t = (OiTable) selected[i];

                    if (t instanceof OiData)
                    {
                        showUVCoverageButton.setEnabled(true);
                    }
                    if (t instanceof OiVis)
                    {
                        showVisButton.setText("Plot VISPHI of selected OI_VIS");
                    }

                    if (t instanceof OiVis2)
                    {
                        showVis2Button.setText("Plot VIS2AMP of selected OI_VIS2");
                    }

                    if (t instanceof OiT3)
                    {
                        showT3Button.setText("Plot T3PHI of selected OI_T3");
                    }

                }
            }
        }
    }

     protected class CheckEmbeddedFileAction extends fr.jmmc.mcs.util.MCSAction
    {
        public CheckEmbeddedFileAction()
        {
            super("checkEmbeddedFile");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            GUIValidator val = new GUIValidator(null);
            val.checkFile(oifitsFile_);
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
                   String       filename = oifitsFile_.getName();
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
