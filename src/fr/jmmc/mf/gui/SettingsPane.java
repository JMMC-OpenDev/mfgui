/*
 * SettingsPane.java
 *
 * Created on 12 février 2008, 14:19
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.StatusBar;

import fr.jmmc.mcs.util.ActionRegistrar;
import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Files;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Oitarget;
import fr.jmmc.mf.models.Parameters;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import java.lang.reflect.*;

import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;


/**
 *
 * @author  mella
 */
public class SettingsPane extends javax.swing.JPanel implements TreeSelectionListener,
    TreeModelListener, SettingsViewerInterface
{
    /** Main logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.SettingsPane");

    // Application actions
    /**
     * DOCUMENT ME!
     */
    public static Action runFitAction;
    public Action getModelListAction = new GetModelListAction();
    public Action saveSettingsAction;
    public Action closeSettingsAction;
    
    /** Model reference */
    public SettingsModel rootSettingsModel = null;

    // List of viewer panel used to display sub components
    TargetsPanel    targetsPanel;
    FilesPanel filesPanel;
    UserInfoPanel userInfoPanel;
    FitterPanel fitterPanel;
    SettingsPanel settingsPanel;
    TargetPanel targetPanel;
    FilePanel filePanel;
    ModelPanel modelPanel;
    ParametersPanel parametersPanel;
    ResultPanel resultPanel;
    PlotPanel plotPanel;
    FramePanel framePanel;

    // adjusted to true after user modification
    protected boolean modified;

    protected FrameList frameList;

    /** Creates new form SettingsPane */
    public SettingsPane(java.io.File file) throws Exception
    {
        init();
        rootSettingsModel.loadSettingsFile(file);        
    }

    /** Creates new form SettingsPane */
    public SettingsPane(java.net.URL url) throws Exception
    {
        init();
        rootSettingsModel.loadSettingsFile(url);        
    }

    /** Creates new form SettingsPane */
    public SettingsPane()
    {
        init();
    }

    /**
     * DOCUMENT ME!
     */
    private void init()
    {
        frameList = new FrameList(this);
        // instanciate actions
        runFitAction          = new RunFitAction();

        // Because settingsTree has rootSettingsModel as tree model,
        // we need to init rootSettingsModel before entering initComponents
        rootSettingsModel     = new SettingsModel();

        initComponents();
        settingsTree.setMinimumSize(new Dimension(200, 200));
        settingsTree.setPreferredSize(new Dimension(200, 200));

        ToolTipManager.sharedInstance().registerComponent(settingsTree);

        targetsPanel        = new TargetsPanel(this);
        filesPanel          = new FilesPanel(this);
        fitterPanel         = new FitterPanel(this);
        userInfoPanel       = new UserInfoPanel(this);
        settingsPanel       = new SettingsPanel(filesPanel, targetsPanel, fitterPanel, userInfoPanel);
        targetPanel         = new TargetPanel(this);
        filePanel           = new FilePanel();
        modelPanel          = new ModelPanel();
        parametersPanel     = new ParametersPanel(this);
        resultPanel         = new ResultPanel(this);
        plotPanel           = new PlotPanel(this);
        framePanel          = new FramePanel(this);

        ActionRegistrar actionRegistrar=ActionRegistrar.getInstance();
        saveSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.MFGui","saveModel");
        closeSettingsAction = actionRegistrar.get("fr.jmmc.mf.gui.MFGui","closeModel");


        // to permit modifier panel changes,
        // register as treeSelectionListener
        settingsTree.setModel(rootSettingsModel);
        settingsTree.addTreeSelectionListener(this);
        settingsTree.setCellRenderer(new MyCellRenderer());
        // to be notified of settingsModel changes
        // register as treeModelListener
        rootSettingsModel.addTreeModelListener(this); 
        // finaly ask to show top level element
        showSettingElement(rootSettingsModel.getRootSettings());
        
        runFitButton.setAction(runFitAction);
        controlPanel.add(runFitButton);

        
        JScrollPane scrollPane = new JScrollPane(frameList);
        controlPanel.add(scrollPane);
    }



    /**
     * DOCUMENT ME!
     */
    public static void expandSettingsTree()
    {
        // Next line does not work because node doesn't respond to treeNode interface
        //McsClass.expandAll(settingsTree,true);
    }

    /**
     * Responds to tree selection events.
     */
    public void valueChanged(TreeSelectionEvent e)
    {
        logger.entering("" + this.getClass(), "valueChanged");

        Object o = e.getPath().getLastPathComponent();
        if(e.getNewLeadSelectionPath()!=null){
            showSettingElement(o);
        }
        checkValidSettings();
    }

    public void showElement(Object o) {
        logger.entering("" + this.getClass(), "showElement");

        if (o == null)
        {
            return;
        }

        logger.finest("object to show is :" + o);
        modifierPanel.removeAll();

        if (o instanceof JFrame)
        {
            framePanel.show(rootSettingsModel,(JFrame)o);
            modifierPanel.add(framePanel);
            settingsTree.clearSelection();
            frameList.clearSelection();
        }
        else if (o instanceof PlotPanel)
        {
            plotPanel.show(rootSettingsModel);
            modifierPanel.add(plotPanel);
            settingsTree.clearSelection();
            frameList.clearSelection();
        }
        else
        {
            modifierPanel.add(new JLabel("missing modifier panel for '" + o.getClass() +
                    "' objects"));
        }
        modifierPanel.revalidate();
        modifierPanel.repaint();
        // check one more time that GUI view is up to date
        checkValidSettings();

    }
    /**
     *Call this method if you want to show something particular in the right
     * side of the SettingsPane.
     *
     * @param o the object you want to be displayed.
     */
    public void showSettingElement(Object o)
    {
        logger.entering("" + this.getClass(), "showSettingElement");

        if (o == null)
        {
            return;
        }

        logger.finest("object to show is :" + o);
        modifierPanel.removeAll();

        if (o instanceof Settings)
        {        
            settingsPanel.show(rootSettingsModel.getRootSettings(), rootSettingsModel);
            modifierPanel.add(settingsPanel);
        }
        else if (o instanceof Targets)
        {
            targetsPanel.show((Targets) o, rootSettingsModel);
            modifierPanel.add(targetsPanel);
        }
        else if (o instanceof Files)
        {
            filesPanel.show((Files) o, rootSettingsModel);
            modifierPanel.add(filesPanel);
        }
        else if (o instanceof Target)
        {
            targetPanel.show((Target) o, rootSettingsModel);
            modifierPanel.add(targetPanel);
        }
        else if (o instanceof Model)
        {
            modelPanel.show((Model) o, rootSettingsModel);
            modifierPanel.add(modelPanel);
        }
        else if (o instanceof File)
        {
            filePanel.show((File) o);
            modifierPanel.add(filePanel);
        }
        else if (o instanceof FileLink)
        {
            FileLink link = (FileLink) o;
            filePanel.show((File) link.getFileRef());
            modifierPanel.add(filePanel);
        }
        else if (o instanceof Parameters)
        {
            parametersPanel.show((Parameters) o);
            modifierPanel.add(parametersPanel);
        }
        else if (o instanceof Result)
        {
            resultPanel.show((Result) o, rootSettingsModel);
            modifierPanel.add(resultPanel);
        }
        else
        {
            modifierPanel.add(new JLabel("missing modifier panel for '" + o.getClass() +
                    "' objects"));           
        }
        modifierPanel.revalidate();
        modifierPanel.repaint();
        // check one more time that GUI view is up to date
        checkValidSettings();
    }

    /**
     * Responds to tree structure changes
     */
    public void treeStructureChanged(javax.swing.event.TreeModelEvent e)
    {
        logger.entering("" + this.getClass(), "treeStructureChanged");
        treeChanged(e);
        //showSettingElement(rootSettingsModel.getRoot());
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent e)
    {
        treeChanged(e);
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesInserted(javax.swing.event.TreeModelEvent e)
    {
        treeChanged(e);
    }

    /** Use to listen tree model elements that can indicate changes*/
    public void treeNodesChanged(javax.swing.event.TreeModelEvent e)
    {
        treeChanged(e);
    }
    
    private void treeChanged(javax.swing.event.TreeModelEvent e)
    {
        logger.fine("Listening for settingsModel tree event"+e);
        if (e.getSource() instanceof SettingsModel){
            if (rootSettingsModel.isModified())
            {
                //tabbedPane.setTitleAt(0, "Settings *");
                System.out.println("Flag modification");
            }
            else
            {
                // tabbedPane.setTitleAt(0, "Settings");
            }            
        }
        checkValidSettings();
    }

    /**
     * Call this method after any modification to be sure to present a valid
     * controller state. If the settings Model is not valid some GUI parts
     * will not be enabled.
     */
    protected void checkValidSettings(){
        boolean validSettingsModel=rootSettingsModel.isValid();
        runFitAction.setEnabled(validSettingsModel);
        showPlotButton.setEnabled(validSettingsModel);
        saveSettingsAction.setEnabled(validSettingsModel);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SettingsModel getSettingsModel()
    {
        return rootSettingsModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SettingsPane getSettingsPane()
    {
        return this;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        settingsTree = new javax.swing.JTree();
        controlPanel = new javax.swing.JPanel();
        runFitButton = new javax.swing.JButton();
        showPlotButton = new javax.swing.JButton();
        modifierPanel = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.5);

        settingsTree.setMinimumSize(new java.awt.Dimension(200, 0));
        jScrollPane1.setViewportView(settingsTree);

        jSplitPane2.setTopComponent(jScrollPane1);

        controlPanel.setLayout(new javax.swing.BoxLayout(controlPanel, javax.swing.BoxLayout.PAGE_AXIS));
        controlPanel.add(runFitButton);

        showPlotButton.setText("Show Plot Panel");
        showPlotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPlotButtonActionPerformed(evt);
            }
        });
        controlPanel.add(showPlotButton);

        jSplitPane2.setBottomComponent(controlPanel);

        jSplitPane1.setLeftComponent(jSplitPane2);

        modifierPanel.setLayout(new javax.swing.BoxLayout(modifierPanel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setRightComponent(modifierPanel);

        add(jSplitPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void showPlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPlotButtonActionPerformed
        showElement(plotPanel);
}//GEN-LAST:event_showPlotButtonActionPerformed

    /**
     * @return the frameList
     */
    public FrameList getFrameList() {
        return frameList;
    }

    protected class GetModelListAction extends fr.jmmc.mcs.util.MCSAction
    {
        String methodName = "getModelList";

        public GetModelListAction()
        {
            super("getModelList");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.fine("Requesting yoga '" + methodName + "' call");

            String result = "";

            try
            {
                result = ModelFitting.instance_.execMethod(methodName, null);
                logger.fine("Supported models:" + result);

                // Search model into return result
                java.io.StringReader reader   = new java.io.StringReader(result);
                Model                newModel = (Model) Model.unmarshal(reader);
                // Indicates to the rootSettingsModel list of availables models
                rootSettingsModel.setSupportedModels(newModel.getModel());
            }
            catch (Exception ex)
            {
                logger.warning(ex.getClass().getName() + " " + ex.getMessage());
                StatusBar.show("Can't get available models");
            }
        }
    }

    protected class RunFitAction extends fr.jmmc.mcs.util.MCSAction
    {
        String methodName = "runFit";

        public RunFitAction()
        {
            super("runFit");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            logger.fine("Requesting yoga '" + methodName + "' call");

            String result = "";

            try
            {
                // Get xml temp file with content of model
                java.io.File tmpFile = rootSettingsModel.getTempFile(false);
                result = ModelFitting.instance_.execMethod(methodName, tmpFile);
                StatusBar.show("Fitting process finished");
            }
            catch (Exception ex)
            {
                logger.warning(ex.getClass().getName() + " " + ex.getMessage());
                ex.printStackTrace();
                result = "Error:" + ex.getMessage();
                StatusBar.show("Error during fitting process");
            }

            int i1 = result.indexOf("START_XML_RESULT");
            int i2 = result.indexOf("END_XML_RESULT");

            //logger.finest("Xml result is:"+result);
            if ((i1 < 0) || (i2 < 0))
            {
                JTextArea textArea = new JTextArea(20, 80);
                textArea.setText(result);

                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setEditable(false);
                javax.swing.JOptionPane.showMessageDialog(controlPanel, scrollPane, "Error ",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                logger.severe("No xml returned by server side");

                return;
            }

            String xml = result.substring(i1 + "START_XML_RESULT".length(), i2);

            try
            {
                java.io.StringReader reader    = new java.io.StringReader(xml);
                Settings             newModel  = (Settings) Settings.unmarshal(reader);
                Settings             prevModel = rootSettingsModel.getRootSettings();
                Model                m;

                // add href from previous files
                File[] newFiles  = newModel.getFiles().getFile();
                File[] prevFiles = prevModel.getFiles().getFile();

                // no check is done to assert that every newFile have been updated
                for (int i = 0; i < newFiles.length; i++)
                {
                    File   newFile = newFiles[i];
                    String newId   = newFile.getId();

                    for (int j = 0; j < prevFiles.length; j++)
                    {
                        File   prevFile = prevFiles[j];
                        String prevId   = prevFile.getId();

                        if (prevId.equals(newId))
                        {
                            newFile.setHref(prevFile.getHref());

                            Oitarget[] oitargets = prevFile.getOitarget();

                            for (int k = 0; k < oitargets.length; k++)
                            {
                                newFile.addOitarget(oitargets[i]);
                            }
                        }
                    }
                }

                rootSettingsModel.setRootSettings(newModel);
                rootSettingsModel.setLastXml(xml);
                logger.info("Settings created");
                // display automatically the new result
                showSettingElement(rootSettingsModel.getRootSettings().getResult());                
            }
            catch (Exception ex)
            {                
                logger.log(Level.WARNING, "Can't use returned xml as result", ex);

                JTextArea textArea = new JTextArea(30, 50);

                String xslPath = "fr/jmmc/mf/gui/extractError.xsl";            
                java.net.URL url = this.getClass().getClassLoader().getResource(xslPath);
                String str = UtilsClass.xsl(xml, url, null);
                
                textArea.setText(str);

                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setEditable(false);
                javax.swing.JOptionPane.showMessageDialog(controlPanel, scrollPane, "Error ",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
             
            }
        }
    }

    // Cell renderer used by the settings tree
    // it make red faulty nodes and place help tooltips
    protected class MyCellRenderer extends DefaultTreeCellRenderer
    {
        //getTreeCellRendererComponent
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            //setToolTipText("TBD");

            // Next part try to get valide state of serialized xml objects
            try
            {
                String   methodName = "validate";
                Class[]  c          = new Class[0];
                Method   m          = value.getClass().getMethod(methodName, c);
                Object[] o          = new Object[0];
                m.invoke(value, o);

                //logger.fine("method invoked using reflexion");
            }
            catch (Exception e)
            {
                setForeground(Color.red);

                if (e.getCause() != null)
                {
                    setToolTipText(e.getCause().getMessage());
                }
                else
                {
                    setToolTipText(e.getMessage());
                }
            }

            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel modifierPanel;
    private javax.swing.JButton runFitButton;
    private javax.swing.JTree settingsTree;
    private javax.swing.JButton showPlotButton;
    // End of variables declaration//GEN-END:variables

}
