/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.service.XslTransform;
import fr.jmmc.mf.gui.models.ResultModel;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Residual;
import fr.jmmc.mf.models.ResultFile;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ptolemy.plot.plotml.PlotMLFrame;

public class ResultPanel extends javax.swing.JPanel  implements ActionListener {

    public final static String className = ResultPanel.class.getName();
    /** Class logger */
    static final Logger logger = LoggerFactory.getLogger(className);
    ResultModel current;
    SettingsViewerInterface viewer = null;
    SettingsModel settingsModel = null;
    private UserInfoPanel userInfoPanel = null;
    
    private final List<String> observables = Arrays.asList("visamp", "visphi", "vis2", "t3amp", "t3phi");
    

    /** Creates new form ResultPanel */
    public ResultPanel(SettingsViewerInterface viewer) {
        this.viewer = viewer;
        userInfoPanel = new UserInfoPanel(viewer);
        initComponents();
        jPanel2.add(userInfoPanel);        
    }

    public void show(ResultModel r, SettingsModel s) {
        current = r;
        settingsModel = s;        
        resultTextArea.setText(r.getHtmlReport()); // ok this is not html ...
        resultTextArea.setCaretPosition(0);
        
        userInfoPanel.show(s.getRootSettings());
        
        plotButtonsPanel.removeAll();
        
        if ( current.getResult().getHref() != null ){
            plotButtonsPanel.add(new JLabel("Plot : ")); 
            Map<String, String> plots  = plotTobuild();
            for (String n : plots.keySet() ) {
                JButton b = new JButton(n);
                b.setActionCommand(plots.get(n));
                b.addActionListener(this);                
                plotButtonsPanel.add(b);
            }            
        }
        
        // because it is long - poor dev helper
        App.showFrameToFront();
    }
    
     /**
     * Handle event comming from plot button 
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {        
        // Would be very nice to move it in a swingworker  this can fresz the UI for 1.5min+
        
        final String plotName = e.getActionCommand();        
        FrameTreeNode ftn = null;
        ftn = ptplot(plotName, false, false);
        settingsModel.selectInTree(current, ftn);   
        
        // TODO add a nicer swing refresh line here
        SwingUtils.invokeLaterEDT(new Runnable() {
            @Override
            public void run() {App.showFrameToFront();}
                    
        });
        
        if(observables.contains(plotName)){
            ptplot(plotName, true, false); // TODO add a dedicated button for for residuals ?
            settingsModel.selectInTree(current, ftn);        
        }                
        
        // because it is long - poor dev helper
        App.showFrameToFront();
    }
    
    private LinkedHashMap<String, String> plotTobuild(){
        LinkedHashMap<String, String> plotTobuild = new LinkedHashMap<>();                
        
        plotTobuild.put("Baselines","plotBaselines");
        plotTobuild.put("UVCoverage", "plotUVCoverage");
        
        Settings settings = current.getResult().getSettings();        
        if (settings==null){
            // try with the current settings (which may be unsync...
            settings=settingsModel.getRootSettings();            
        }
        
        Target[] targets = settings.getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            if (target.getResiduals() != null) {
                Residual[] residuals = target.getResiduals().getResidual();
                for (int j = 0; j < residuals.length; j++) {
                    Residual residual = residuals[j];
                    plotTobuild.put(residual.getName(), residual.getName().toLowerCase());
                }
            }
        }

        if (plotTobuild.size()==2) {            
            for (String t :  observables) {
                plotTobuild.put(t,t);
            }
        }
                
        return plotTobuild;
    }

    
    void genPlots(String plotTitle, String plotDescription, ResultFile[] resultFiles) {
        // try to locate the corresponding pdf of each png file
        for (int i = 0; i < resultFiles.length; i++) {
            ResultFile r = resultFiles[i];

            String b64file;
            JFrame f = null;
            ArrayList<File> filesToExport = new ArrayList<File>();
            ArrayList<String> filenamesToExport = new ArrayList<String>();
            if (r.getHref().substring(1, 30).contains("png")) {
                for (int j = 0; j < resultFiles.length; j++) {
                    ResultFile r2 = resultFiles[j];
                    String filenameWOExt = r2.getName().substring(0, r2.getName().lastIndexOf('.'));
                    if (r.getName().startsWith(filenameWOExt + ".") && r2.getName().endsWith("pdf")) {
                        b64file = r2.getHref();
                        File pdfFile = UtilsClass.saveBASE64ToFile(b64file, "pdf");
                        filesToExport.add(pdfFile);
                        filenamesToExport.add(r.getName());
                    }
                }
                b64file = r.getHref();
                File pngFile = UtilsClass.saveBASE64ToFile(b64file, "png");
                filesToExport.add(pngFile);
                f = UtilsClass.buildFrameFor(plotTitle, plotDescription, new File[]{pngFile}, new String[]{r.getName()});
                current.add(new FrameTreeNode(f, filesToExport.toArray(new File[0]), filenamesToExport.toArray(new String[0])));
            }
        }
    }

    /** Plots using ptplot widgets */
    protected FrameTreeNode ptplot(String plotName, boolean residuals, boolean testDataBeforeShowing) {
        String xmlStr = null;

        logger.debug("Start plot generation:{}(residuals={})", plotName, residuals);
        // Construct xml document to plot
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("plotName", plotName);
        if (residuals) {
            // TODO test if we can use residuals directly
            args.put("residuals", Boolean.toString(residuals));
            plotName = plotName + " residuals";
        }

        // LBO: HOTSPOT (modeler to ML is slow !)
        // TODO: do it only when needed (lazy) if possible ?
        // or in background (not blocking EDT ie GUI)
        logger.debug("start xslt from yoga xml to ptolemy plot (args={})", args);
        xmlStr = XslTransform.transform(current.getXmlResult(), "fr/jmmc/mf/gui/yogaToPlotML.xsl", args);
        logger.debug("end xslt from yoga xml to ptolemy plot");

        // this test is perform during load of results element in settings file that may or not have all kind of dataset
        if (testDataBeforeShowing && !xmlStr.substring(0, Math.min(xmlStr.length(), 500)).contains("dataset")) {
            logger.debug("No dataset to display");
            return null;
        }
        // generate frame and tsv file
        PlotMLFrame plotMLFrame = UtilsClass.getPlotMLFrame(xmlStr, plotName);

        if (!residuals && plotName.toLowerCase().contains("phi")) {
            UtilsClass.fixPlotAxesForPhases(plotMLFrame.plot);
        } else {
            UtilsClass.fixPlotAxesForAmp(plotMLFrame.plot);
        }

        logger.debug("End plot generation");

        // LBO: HOTSPOT (ML to TSV is very slow !)
        // TODO: do it only when needed (lazy)
        logger.debug("Start tsv generation:{}", plotName);
        File tsv = UtilsClass.getPlotMLTSVFile(xmlStr);
        logger.debug("End tsv generation");

        // add frameTreeNode as child
        FrameTreeNode ftn = new FrameTreeNode(plotMLFrame, tsv);
        current.add(ftn);        
        return ftn;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel4 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTextArea = new javax.swing.JTextArea();
        plotButtonsPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Result panel:"));
        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.6);
        jSplitPane1.setMaximumSize(new java.awt.Dimension(1000, 600));

        jPanel2.setAlignmentX(1.0F);
        jPanel2.setMaximumSize(new java.awt.Dimension(100, 250));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 150));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 170));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanel3.add(filler1);

        jPanel2.add(jPanel3);

        jSplitPane1.setBottomComponent(jPanel2);

        jPanel1.setPreferredSize(new java.awt.Dimension(262, 410));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setAlignmentY(1.0F);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(1000, 800));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(25, 300));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(262, 800));

        resultTextArea.setEditable(false);
        resultTextArea.setColumns(20);
        resultTextArea.setRows(5);
        jScrollPane1.setViewportView(resultTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        plotButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(plotButtonsPanel, gridBagConstraints);

        jSplitPane1.setTopComponent(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel plotButtonsPanel;
    private javax.swing.JTextArea resultTextArea;
    // End of variables declaration//GEN-END:variables
}
