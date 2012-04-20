/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.network.interop.SampSubscriptionsComboBoxModel;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.ShowHelpAction;
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampManager;
import fr.jmmc.jmcs.util.JnlpStarter;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.File;
import fr.jmmc.oitools.model.*;
import fr.nom.tam.fits.FitsException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.astrogrid.samp.client.SampException;
import ptolemy.plot.plotml.*;
import javax.swing.*;
import javax.swing.event.*;
import org.astrogrid.samp.Client;

/**
 *
 * Display one data file content.
 */
public class FilePanel extends javax.swing.JPanel {

    final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            FilePanel.class.getName());
    protected SettingsModel settingsModel;
    File current = null;
    OIFitsFile oifitsFile_ = null;
    static Action saveEmbeddedFileAction;
    static Action checkEmbeddedFileAction;
    static Action showEmbeddedFileAction;    
    MyListSelectionListener myListSelectionListener;
    ListModel hduListModel = new DefaultListModel();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFileButton1;
    private javax.swing.JButton checkFileButton;
    private javax.swing.JPopupMenu fileListPopupMenu;
    private javax.swing.JComboBox fitsViewerComboBox;
    private javax.swing.JList hduList;
    private javax.swing.JButton helpButton1;
    private javax.swing.JButton helpButton2;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu listenersMenu;
    private javax.swing.JButton loadViewerButton;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton saveFileButton;
    private javax.swing.JButton showT3Button;
    private javax.swing.JButton showUVCoverageButton;
    private javax.swing.JButton showVis2Button;
    private javax.swing.JButton showVisButton;
    private javax.swing.JCheckBox t3ampCheckBox;
    private javax.swing.JCheckBox t3phiCheckBox;
    private javax.swing.JButton topcatButton;
    private javax.swing.JCheckBox visampCheckBox;
    private javax.swing.JCheckBox visphiCheckBox;
    // End of variables declaration//GEN-END:variables

    /** Creates new form FilePanel */
    public FilePanel() {
        // Prepare action before gui construction
        saveEmbeddedFileAction = new SaveEmbeddedFileAction();
        showEmbeddedFileAction = new ShowEmbeddedFileAction();
        checkEmbeddedFileAction = new CheckEmbeddedFileAction();        
        initComponents();

        myListSelectionListener = new MyListSelectionListener();
        hduList.addListSelectionListener(myListSelectionListener);
        //fix default state
        myListSelectionListener.valueChanged(null);

        // set help buttons
        helpButton1.setAction(new ShowHelpAction("_Check_embedded_Bt"));
        helpButton2.setAction(new ShowHelpAction("_fits_fields_and_Show_selected_Bt"));
        // next boutons are actually not usefull because they are linked onto the same page
        //helpButton3.setAction(new ShowHelpAction("_Show_UV_Bt"));
        //helpButton4.setAction(new ShowHelpAction("_Plot_Vis_Bt"));
        //helpButton5.setAction(new ShowHelpAction("_Plot_Vis2_Bt"));
        //helpButton6.setAction(new ShowHelpAction("_Plot_T3_Bt"));
        
        // sync content with current set of fits viewer samp clients
        fitsViewerComboBox.setModel(new SampSubscriptionsComboBoxModel(SampCapability.LOAD_FITS_TABLE));
    }

    /**
     *
     *
     * @param file
     * @param settingsModel
     * @throws IOException if an io exception occurs writing the file
     * @throws FitsException
     */
    public void show(File file, SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
        // Try to load data file
        oifitsFile_ = null;
        current = file;

        oifitsFile_ = settingsModel.getOIFitsFromFile(current);

        // update file info fields
        nameTextField.setText(file.getName());

        // update lists
        hduList.setListData(oifitsFile_.getOiTables()); 
       hduListModel = hduList.getModel();
        showUVCoverageButton.setEnabled(false);
        showVisButton.setEnabled(oifitsFile_.hasOiVis());
        visampCheckBox.setEnabled(oifitsFile_.hasOiVis());
        visphiCheckBox.setEnabled(oifitsFile_.hasOiVis());
        showVis2Button.setEnabled(oifitsFile_.hasOiVis2());
        showT3Button.setEnabled(oifitsFile_.hasOiT3());
        t3ampCheckBox.setEnabled(oifitsFile_.hasOiT3());
        t3phiCheckBox.setEnabled(oifitsFile_.hasOiT3());
        // update button state
        hduList.setSelectedIndices(new int[]{});
        
        topcatButton.setEnabled(fitsViewerComboBox.getModel().getSize()==0);        

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fileListPopupMenu = new javax.swing.JPopupMenu();
        listenersMenu = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        saveFileButton = new javax.swing.JButton();
        checkFileButton = new javax.swing.JButton();
        helpButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        loadViewerButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        hduList = new javax.swing.JList();
        showUVCoverageButton = new javax.swing.JButton();
        showVisButton = new javax.swing.JButton();
        showVis2Button = new javax.swing.JButton();
        showT3Button = new javax.swing.JButton();
        t3ampCheckBox = new javax.swing.JCheckBox();
        t3phiCheckBox = new javax.swing.JCheckBox();
        visampCheckBox = new javax.swing.JCheckBox();
        visphiCheckBox = new javax.swing.JCheckBox();
        helpButton2 = new javax.swing.JButton();
        fitsViewerComboBox = new javax.swing.JComboBox();
        topcatButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        addFileButton1 = new javax.swing.JButton();

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

        saveFileButton.setAction(this.saveEmbeddedFileAction);
        saveFileButton.setForeground(new java.awt.Color(51, 51, 52));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(saveFileButton, gridBagConstraints);

        checkFileButton.setAction(this.checkEmbeddedFileAction);
        checkFileButton.setForeground(new java.awt.Color(51, 51, 52));
        jPanel2.add(checkFileButton, new java.awt.GridBagConstraints());

        helpButton1.setText("jButton2");
        jPanel2.add(helpButton1, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel2, gridBagConstraints);

        jPanel1.setPreferredSize(new java.awt.Dimension(388, 291));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        loadViewerButton.setAction(showEmbeddedFileAction);
        loadViewerButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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
        hduList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hduListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(hduList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        showUVCoverageButton.setText("Show UV Coverage of selected tables"); // NOI18N
        showUVCoverageButton.setEnabled(false);
        showUVCoverageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUVCoverageButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showUVCoverageButton, gridBagConstraints);

        showVisButton.setText("showVisButton label inited in code"); // NOI18N
        showVisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showVisButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showVisButton, gridBagConstraints);

        showVis2Button.setText("showVis2Button label inited in code"); // NOI18N
        showVis2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showVis2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showVis2Button, gridBagConstraints);

        showT3Button.setText("showT3Button label inited in code"); // NOI18N
        showT3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showT3ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(showT3Button, gridBagConstraints);

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

        helpButton2.setText("jButton2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        jPanel1.add(helpButton2, gridBagConstraints);

        fitsViewerComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel1.add(fitsViewerComboBox, gridBagConstraints);

        topcatButton.setText("Launch Topcat");
        topcatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topcatButtonActionPerformed(evt);
            }
        });
        jPanel1.add(topcatButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jButton1.setAction(FilesPanel.loadFilesAction);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jButton1, gridBagConstraints);

        addFileButton1.setAction(FilesPanel.loadRemoteFilesAction);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(addFileButton1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void hduListMouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_hduListMouseClicked
        // launch viewer if double click
        if (evt.getClickCount() == 2)
        {
            loadViewerButton.doClick();
        }
    }//GEN-LAST:event_hduListMouseClicked

    /*TO PUT ON ANOTHER PLACE*/
    /** Build a ptplot xml file for given data */
    private String buildXmlPtPlotDataSet(String datasetName, double[] x, double[] y,
          double[] errorBar, boolean[] flags) {
    StringBuffer sb = new StringBuffer();

    if (logger.isLoggable(Level.FINE)) {
      if (errorBar != null) {
        logger.fine("x,y,err dimensions :" + x.length + "," + y.length + "," + errorBar.length);
      } else {
        logger.fine("x,y dimensions :" + x.length + "," + y.length);
      }
    }

    sb.append("<dataset connected=\"no\" marks=\"dots\" name=\"").append(datasetName).append("\">\n");
        for (int i = 0; i < x.length; i++)
        {
            // Output only if flags are given and are false
            if (flags==null || !flags[i])
            {
                sb.append("<m x=\"").append(x[i]).append("\" y=\"").append(y[i]).append("\"");
                if (errorBar != null)
                {
                    double lowEB = y[i] - errorBar[i];
                    double highEB = y[i] + errorBar[i];
                    sb.append(" lowErrorBar=\"").append(lowEB).append("\"");
                    sb.append(" highErrorBar=\"").append(highEB).append("\"");
                }
                sb.append("/>\n");
            }
        }
        sb.append("</dataset>");        
        return sb.toString();
    }
    
    /** Build a ptplot xml file for given data */
    private String buildXmlPtPlotDataSet(String datasetName, double[][] x, double[][] y,
            double[][] errorBar, boolean[][] flags)
    {
        StringBuffer sb = new StringBuffer();
        if(errorBar!=null){
            logger.fine("x,y,err dimensions :"+x.length+","+y.length+","+errorBar.length);
        }   else{
            logger.log(Level.FINE, "x,y dimensions :{0},{1}", new Object[]{x.length, y.length});
        }
        sb.append("<dataset connected=\"no\" marks=\"dots\" name=\"").append(datasetName).append("\">\n");
        for (int i = 0; i < x.length; i++)
        {
            for (int j = 0; j < x[0].length; j++)
            {
                // Output only if flags are given and are false
                if (flags==null || !flags[i][j])
                {
                    sb.append("<m x=\"").append(x[i][j]).append("\" y=\"").append(y[i][j]).append("\"");
                    if (errorBar != null)
                    {
                        double lowEB = y[i][j] - errorBar[i][j];
                        double highEB = y[i][j] + errorBar[i][j];
                        sb.append(" lowErrorBar=\"").append(lowEB).append("\"");
                        sb.append(" highErrorBar=\"").append(highEB).append("\"");
                    }
                    sb.append("/>\n");
                }
            }
        }
        sb.append("</dataset>");
        return sb.toString();
    }
    
    public void showData(String requestedTables, String [] requestedColumns)
    {
        logger.fine("Searching to plot "+requestedTables);        
        String plotName=current.getName()+"(";
        for (int i = 0; i < requestedColumns.length; i++) {
            plotName = plotName+requestedColumns[i]+" " ;
        }
        plotName = plotName+")" ;
           
            int retainedHdu = 0;
            // Select requested tables:
            // plot all reqeusted if nothing selected or selection does not contains requested
            // else plot only selected
            Object[] selected = hduList.getSelectedValues();
            OITable[] OITables = oifitsFile_.getOiTables();
            selected = hduList.getSelectedValues();
            // Search in selection
            for (int i = 0; i < selected.length; i++)
            {
                OITable t = (OITable) selected[i];
                if (t.getExtName().equals(requestedTables))
                {
                    OITables[retainedHdu] = t;
                    retainedHdu++;
                }
            }
            // or search on all if nothing found
            if (retainedHdu == 0)
            {
                // no hdu found, we will try again on all hdu
                for (int i = 0; i < OITables.length; i++)
                {
                    OITable t = (OITable) OITables[i];
                    if (t.getExtName().equals(requestedTables))
                    {
                        OITables[retainedHdu] = t;
                        retainedHdu++;
                    }
                }
            }

            /* Start plotting loop on retained HDU*/
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" standalone=\"yes\"?><plot><!-- Ptolemy plot, version 5.6 , PlotML format. --><title>data versus spatial frequency</title><xLabel>spatial frequency (1/rad)</xLabel><yLabel>");
            sb.append(requestedTables).append("</yLabel>");

            for (int i = 0; i < retainedHdu; i++)
            {
                OITable table = OITables[i];

                for (int j = 0; j < requestedColumns.length; j++)
                {
                    String requestedColumn = requestedColumns[j];
                    String units="";
                    if(requestedColumn.contains("PHI")){
                        units="(degrees)";
                    }
                    String label = table.getExtName()+"#"+
                            table.getExtNb()+":"+requestedColumn+ units;
                    double data[][] = null;
                    double err[][] = null;
                    double dist[][] = null;
                    boolean flags[][] = null;
                    if (table instanceof OIT3)
                    {
                        OIT3 t = (OIT3) table;
                        if (requestedColumn.equals("T3AMP"))
                        {
                            data = t.getT3Amp();
                            err = t.getT3AmpErr();                            
                        }
                        if (requestedColumn.equals("T3PHI"))
                        {                           
                            data = t.getT3Phi();
                            err = t.getT3PhiErr();
                        }
                        dist = t.getSpatial();
                        flags = t.getFlag();
                    } else if (table instanceof OIVis)
                    {
                        OIVis t = (OIVis) table;
                        if (requestedColumn.equals("VISAMP"))
                        {                            
                            data = t.getVisAmp();
                            err = t.getVisAmpErr();
                        }
                        if (requestedColumn.equals("VISPHI"))
                        {
                            data = t.getVisPhi();
                            err = t.getVisPhiErr();
                        }
                        dist = t.getSpatialFreq();
                        flags = t.getFlag();
                    } else
                    {
                        OIVis2 t = (OIVis2) table;
                        data = t.getVis2Data();
                        err = t.getVis2Err();
                        dist = t.getSpatialFreq();
                        flags = t.getFlag();
                    }
                    sb.append(buildXmlPtPlotDataSet(label, dist, data, err, flags));
                }
            }

            sb.append("</plot>");

            PlotMLFrame plotMLFrame = UtilsClass.getPlotMLFrame(sb.toString(), plotName);
           
        if (plotName.toLowerCase().contains("phi")) {
            UtilsClass.fixPlotAxesForPhases(plotMLFrame.plot);
        } else {
            UtilsClass.fixPlotAxesForAmp(plotMLFrame.plot);            
        }

        java.io.File tsv = UtilsClass.getPlotMLTSVFile(sb.toString());
        settingsModel.addPlot(new FrameTreeNode(plotMLFrame, plotName, tsv));
    }

    /*
     * TO PUT ON ANOTHER PLACE*/
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

    private void showVis2ButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_showVis2ButtonActionPerformed
        showData("OI_VIS2", new String[]{"VIS2DATA"});
    }//GEN-LAST:event_showVis2ButtonActionPerformed

    private void showT3ButtonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_showT3ButtonActionPerformed
        final String[] ampAndPhi = new String[]{"T3AMP", "T3PHI"};
        final String[] amp = new String[]{"T3AMP"};
        final String[] phi = new String[]{"T3PHI"};
        final String[] nothing = new String[]{};

        if (t3ampCheckBox.isSelected() && t3phiCheckBox.isSelected()) {
            showData("OI_T3", ampAndPhi);
        }
        if (t3ampCheckBox.isSelected() && !t3phiCheckBox.isSelected()) {
            showData("OI_T3", amp);
        }
        if (!t3ampCheckBox.isSelected() && t3phiCheckBox.isSelected()) {
            showData("OI_T3", phi);
        }
        if (!t3ampCheckBox.isSelected() && !t3phiCheckBox.isSelected()) {
            showData("OI_T3", nothing);
        }
    }//GEN-LAST:event_showT3ButtonActionPerformed

    private void showUVCoverageButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showUVCoverageButtonActionPerformed
    {//GEN-HEADEREND:event_showUVCoverageButtonActionPerformed
        // Iterate all selected items
        showUVCoverageButton.setEnabled(false);
        String plotName = current.getName() + "(UVCoverage)";
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" standalone=\"yes\"?>"
                //+"<!DOCTYPE plot PUBLIC \"-//UC Berkeley//DTD PlotML 1//EN\""
                //+ " \"http://ptolemy.eecs.berkeley.edu/xml/dtd/PlotML_1.dtd\">"
                + "<plot>"
                + "<!-- Ptolemy plot, version 5.6 , PlotML format. -->"
                + "<title>UV coverage</title>" + "<xLabel>UCOORD [1/rad] </xLabel>"
                + "<yLabel>VCOORD [1/rad]</yLabel>");

        Object selected[] = hduList.getSelectedValues();

        for (int i = 0; i < selected.length; i++) {
            OITable table = (OITable) selected[i];
            String label = table.getExtName() + "#" + table.getExtNb();

            double[][] ucoord = null;
            double[][] vcoord = null;

            if (table instanceof OIVis) {
                final OIVis t = (OIVis) table;

                ucoord = t.getSpatialUCoord();
                vcoord = t.getSpatialVCoord();
            } else if (table instanceof OIVis2) {
                final OIVis2 t = (OIVis2) table;

                ucoord = t.getSpatialUCoord();
                vcoord = t.getSpatialVCoord();
            } else if (table instanceof OIT3) {
                final OIT3 t = (OIT3) table;

                final int uvLen = t.getNbRows();
                final int wLen = t.getNWave();

                final double[][] u1s = t.getSpatialU1Coord();
                final double[][] u2s = t.getSpatialU2Coord();
                final double[][] v1s = t.getSpatialV1Coord();
                final double[][] v2s = t.getSpatialV2Coord();

                final double[][] us = new double[2 * uvLen][wLen];
                final double[][] vs = new double[2 * uvLen][wLen];

                for (int j = 0; j < uvLen; j++) {
                    final double[] u1 = u1s[j];
                    final double[] u2 = u2s[j];
                    final double[] v1 = v1s[j];
                    final double[] v2 = v2s[j];

                    for (int k = 0; k < wLen; k++) {
                        us[j][k] = u1[k];
                        us[j + uvLen][k] = u2[k];
                        vs[j][k] = v1[k];
                        vs[j + uvLen][k] = v2[k];
                    }
                }
                ucoord = us;
                vcoord = vs;
            }
            if (ucoord != null && vcoord != null) {
                // add symetrical part and request plot file building

                final int uvLen = ucoord.length;
                final int wLen = ucoord[0].length;

                final double[][] mus = new double[2 * uvLen][wLen];
                final double[][] mvs = new double[2 * uvLen][wLen];

                for (int j = 0; j < uvLen; j++) {
                    final double[] u = ucoord[j];
                    final double[] v = vcoord[j];

                    for (int k = 0; k < wLen; k++) {
                        mus[j][k] = u[k];
                        mvs[j][k] = v[k];
                        mus[j + uvLen][k] = -u[k];
                        mvs[j + uvLen][k] = -v[k];
                    }
                }
                sb.append(buildXmlPtPlotDataSet(label, mus, mvs, null, null));
            }
        }
        sb.append("</plot>");

        PlotMLFrame plotMLFrame = UtilsClass.getPlotMLFrame(sb.toString(), plotName);
        java.io.File tsv = UtilsClass.getPlotMLTSVFile(sb.toString());
        settingsModel.addPlot(new FrameTreeNode(plotMLFrame, plotName, tsv));

    }//GEN-LAST:event_showUVCoverageButtonActionPerformed

    private void topcatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topcatButtonActionPerformed
        launchTopcat();
        topcatButton.setEnabled(false);
    }//GEN-LAST:event_topcatButtonActionPerformed

    // End of variables declaration                   
    public void saveFile(java.io.File targetFile)
            throws java.io.IOException, java.io.FileNotFoundException {
        UtilsClass.saveBASE64ToFile(current.getHref(), targetFile);
    }

    // THIS CLASS handles button states according to the table selection
    class MyListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent evt) {
            // Iterate all selected items                
            showUVCoverageButton.setEnabled(false);
            showVisButton.setText("Plot data of all OI_VIS");
            showVis2Button.setText("Plot VIS2DATA of all OI_VIS2");
            showT3Button.setText("Plot data of all OI_T3");

            if (evt == null) {
                return;
            }

            // When the user release the mouse button and completes the selection,
            // getValueIsAdjusting() becomes false
            if (!evt.getValueIsAdjusting()) {
                if (!(evt.getSource() instanceof JList)) {
                    return;
                }

                if (hduList.getSelectedIndex() >= 0) {
                    loadViewerButton.setEnabled(true);
                } else {
                    loadViewerButton.setEnabled(false);
                }

                // Get all selected items
                Object[] selected = hduList.getSelectedValues();

                for (int i = 0; i < selected.length; i++) {
                    OITable t = (OITable) selected[i];

                    if (t instanceof OIData) {
                        showUVCoverageButton.setEnabled(true);
                    }
                    if (t instanceof OIVis) {
                        showVisButton.setText("Plot data of selected OI_VIS");
                    }

                    if (t instanceof OIVis2) {
                        showVis2Button.setText("Plot VIS2DATA of selected OI_VIS2");
                    }

                    if (t instanceof OIT3) {
                        showT3Button.setText("Plot data of selected OI_T3");
                    }

                }
            }
        }
    }

    /**
     * Launch the topcat Java WebStart application
     *
     * @return the job context identifier
     * @throws IllegalStateException if the job can not be submitted to the job
     * queue
     */
    public Long launchTopcat() throws IllegalStateException {
        return JnlpStarter.launch("http://www.star.bris.ac.uk/~mbt/topcat/topcat-full.jnlp");
    }

    protected class CheckEmbeddedFileAction extends fr.jmmc.jmcs.gui.action.MCSAction {

        public CheckEmbeddedFileAction() {
            super("checkEmbeddedFile");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            // file extension can be *fits or *fits.gz
            OIFitsChecker checker = settingsModel.getOiFitsFileChecker(oifitsFile_);
            MessagePane.showMessage(checker.getCheckReport());
        }
    }

    protected class ShowEmbeddedFileAction extends fr.jmmc.jmcs.gui.action.MCSAction {

        public ShowEmbeddedFileAction() {
            super("showEmbeddedFile");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {

            if (fitsViewerComboBox.getModel().getSize() > 0) {
                Client dest = (Client) (fitsViewerComboBox.getSelectedItem());

                int[] indices = hduList.getSelectedIndices();
                for (int i = 0; i < indices.length; i++) {

                    final Map<String, String> parameters = new HashMap<String, String>();
                    String filenameUri = "file://" + oifitsFile_.getAbsoluteFilePath() + "#" + ((indices[i]) + 1);

                    logger.fine("transmitting fits file using : uri = " + filenameUri);
                    parameters.put("url", filenameUri);
                    try {
                        SampManager.sendMessageTo(SampCapability.LOAD_FITS_TABLE.mType(), dest.getId(), parameters);
                    } catch (SampException ex) {
                        throw new IllegalStateException("Can't perform samp action", ex);
                    }
                }
            } else {
                MessagePane.showMessage("Please Launch Topcat (http://www.star.bris.ac.uk/~mbt/topcat/) or any VO compliant fits table viewer");
            }
        }
    }

    protected class SaveEmbeddedFileAction extends fr.jmmc.jmcs.gui.action.MCSAction {

        public String lastDir = System.getProperty("user.home");

        public SaveEmbeddedFileAction() {
            super("saveEmbeddedFile");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            //logger.entering(""+this.getClass(), "actionPerformed");
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

            // Set in previous save directory
            if (lastDir != null) {
                fileChooser.setCurrentDirectory(new java.io.File(lastDir));
            }

            fileChooser.setSelectedFile(new java.io.File(current.getName()));
            // Open filechooser
            int returnVal = fileChooser.showSaveDialog(null);

            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                lastDir = file.getParent();
                try {
                    // Save if it does not exist or ask to overwrite
                    if (file.exists()) {
                        if (MessagePane.showConfirmFileOverwrite(file.getName())) {
                            saveFile(file);
                        }
                    } else {
                        saveFile(file);
                    }
                } catch (IOException ioe) {
                    MessagePane.showErrorMessage("Sorry, cannot save your file into " + file.getName(), ioe);
                }
            }
        }
    }
}
