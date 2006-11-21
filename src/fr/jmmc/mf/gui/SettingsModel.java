/*
   JMMC
 */
package jmmc.mf.gui;

import java.util.StringTokenizer;
import jmmc.mcs.gui.*;

import java.util.logging.Logger;

import jmmc.mf.models.*;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;

import nom.tam.fits.*;

import java.net.URL;
//import java.nio.*;
import java.io.FileOutputStream;


public class SettingsModel implements TreeModel {
    public final static String COMPOSITE_MODEL_TYPE = "composite";
    private Vector             treeModelListeners  = new Vector();
    private Settings              rootSettings = null;
    public Action loadFilesAction;
    public Action loadModelAction;
    public Action saveModelAction;

    /* both following members can be used to associate models to some GUI components.
     * they actually are static while no more than one settings can be run
     * at once
     */
    public static Hashtable fileListModels = new Hashtable();    
    public static DefaultListModel allFilesListModel = new DefaultListModel();
    public static DefaultListModel targetListModel = new DefaultListModel();
    public static DefaultComboBoxModel oiTargets = new DefaultComboBoxModel();

    static Logger logger = Logger.getLogger("jmmc.mf.gui.SettingsModel");

    public SettingsModel() {
        rootSettings = new Settings();
        Files f = new Files();
        rootSettings.setFiles(f);
        Targets t = new Targets();
        rootSettings.setTargets(t);
        setRootSettings(rootSettings);

        loadFilesAction = new LoadFilesAction();
        loadModelAction     = new LoadModelAction();
        saveModelAction     = new SaveModelAction();
    }

    /*
     * This method must be called by anyone that has modified the xml binded
     * document.
     */
    public void fireUpdate(){
        logger.entering(""+this.getClass(), "setRootSettings");
        logger.fine("update signal triggered");
        setRootSettings(rootSettings);
    }

    public void setRootSettings(Settings newRootModel) {
        logger.entering(""+this.getClass(), "setRootSettings");
        rootSettings = newRootModel;

        // clear list of files, oitargets, target and fileListModels
        // then add settings ones        
        allFilesListModel.clear();        
        oiTargets.removeAllElements();        
        fileListModels.clear();
        for(int i =0; i< rootSettings.getFiles().getFileCount(); i++){     
            File f = rootSettings.getFiles().getFile(i);
            String filename = f.getName();            
            allFilesListModel.addElement(new java.io.File(filename));            
            for(int j =0; j< f.getOitargetCount(); j++){
                String t = f.getOitarget(j).getTarget();
                if (oiTargets.getIndexOf(t)<0){
                    oiTargets.addElement(t);                     
                }
                addFileListModelForOiTarget(f.getOitarget(j), f);
            }
        }
        targetListModel.clear();
        for(int i =0; i< rootSettings.getTargets().getTargetCount(); i++){     
            Target t = rootSettings.getTargets().getTarget(i);
            targetListModel.addElement(t);
        }
            
            logger.fine("This rootSettings contains "
                + rootSettings.getFiles().getFileCount() + " files,"
                + rootSettings.getTargets().getTargetCount() + " targets,"
                );

        // fire general change event
        fireTreeStructureChanged(rootSettings);
    }

    public Settings getRootSettings() {
        logger.entering(""+this.getClass(), "getRootSettings");
        return rootSettings;
    }

    private void addFileListModelForOiTarget(Oitarget target, File file){
        logger.entering(""+this.getClass(), "addFileListModelForTarget");
        // we use target name as key
        String key = target.getTarget();
        DefaultListModel lm = (DefaultListModel) fileListModels.get(key);
        if(lm == null){
            // We need to create one new fileModel for this ident
            lm = new DefaultListModel();
            fileListModels.put(key, lm);            
        }
        if(!lm.contains(file)){
            logger.fine("adding file "+file.getName() +" to listmodel for "+ key );
            lm.addElement(file);
        }
    }

    // @todo place this method into jmmc.mf.util
    public void addFile(java.io.File fileToAdd){
        logger.entering(""+this.getClass(), "addFile");
        // Try to add list to comboModel if it is not already inside the listModel
        if (!allFilesListModel.contains(fileToAdd)){
            // The file must be one oidata file
            try{
                String fitsFileName=fileToAdd.getAbsolutePath();
                File newFile  = new File();
                newFile.setName(fitsFileName);
                newFile.setId("id"+rootSettings.getFiles().getFileCount());                    

                checkFile(newFile);
                // test succedded let's continue
                rootSettings.getFiles().addFile(newFile);
                logger.info("'"+fitsFileName+"' oifile added to file list");

                // fire modifications!
                fireUpdate();

            }catch(Exception exc){
                logger.warning("Rejecting non valid file '"+fileToAdd.getAbsolutePath()+"'");                
                // Get stackTrace
                java.io.StringWriter sw = new java.io.StringWriter();
                exc.printStackTrace(new java.io.PrintWriter(sw));
                logger.fine("Cause is:" +exc);
                logger.finest(sw.toString());                
            }
        }else{
            logger.warning("File '"+fileToAdd.getAbsolutePath()+"' already present");
        }
    }

    // @todo place this method into jmmc.mf.util
    public void addFileHref(File bindedFile) throws Exception{        
        logger.entering(""+this.getClass(), "addFileHref");
        java.io.File realFile = new java.io.File(bindedFile.getName());
        // Create a read-only memory-mapped file
        java.nio.channels.FileChannel roChannel = new java.io.RandomAccessFile(realFile, "r").getChannel();
        java.nio.ByteBuffer roBuf = roChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, (int)roChannel.size());
        String b64 = "data:image/fits;base64," + new sun.misc.BASE64Encoder().encode(roBuf);       
        bindedFile.setHref(b64);
    }

    public void addFileOiTargets(File bindedFile) throws Exception{
        logger.entering(""+this.getClass(), "addFileOiTarget");

    }

    // @todo place this method into jmmc.mf.util
    public void checkFile(File bindedFile)throws Exception{
        logger.entering(""+this.getClass(), "loadSettingsFile");

        String filename = bindedFile.getName(); 
        logger.fine("Checking file named '"+filename+"'");

        // Check href and return if no file exists
        java.io.File realFile = new java.io.File(filename);
        String href=bindedFile.getHref();
        if( href != null) {            
            logger.fine("Href already present");
            //@todo decode b64 and test filesize or md5sum 
            // and then test on oitarget list could be done...
            if(bindedFile.getOitargetCount()<1){
                logger.warning("No oitarget found");
                // try to continue next test creating a file based on the base64 href
                // Create temp file.
                java.io.File targetFile = java.io.File.createTempFile("tmpOifile", ".oifits");
                // Delete temp file when program exits.
                targetFile.deleteOnExit();                
                // fill file with href content
                StringBuffer sb = new StringBuffer();
                StringTokenizer st = new StringTokenizer(href.substring(23));
                while(st.hasMoreTokens()){
                    sb.append(st.nextToken());
                }                
                if (href.startsWith("data:image/fits;base64,")){                    
                    logger.warning("decoding base64 file into "+targetFile.getAbsolutePath());                    
                    byte[] buf = new sun.misc.BASE64Decoder().decodeBuffer(sb.toString());
                    FileOutputStream out = new FileOutputStream(targetFile);
                    out.write(buf);
                    out.flush();
                    out.close();
                    // tell rest of code to grab into new file
                    filename = targetFile.getPath();                                  
                    
                }                
                
            }else{
                return;
            }
        }else if (!realFile.exists()){            
            logger.warning("Can't locate file");
            throw new java.io.FileNotFoundException();
            // @todo We need to locate this file asking user?            
            // before returning        
        }else{
            addFileHref(bindedFile);
        }

        // test oi_targets
        TableHDU oiTargetHDU=null; 
        // will tell if there is one OI_TARGET extension in this file        
        int nbOfOiTargetExtensions=0;

        // scan given file and try to get reference of oi_target extension excepted primary hdu
        Fits fits = new Fits(filename);
       // fits.skipHDU();
        String list="";
        BasicHDU hdu;
        int index=1;
        while((hdu=fits.readHDU())!=null){           
            logger.fine("reading hdu n¡"+index);
            String extName = hdu.getTrimmedString("EXTNAME");
            list=list+" "+extName;
            // search for oi_targets
            if(extName!=null){
                if(extName.equals("OI_TARGET")){
                    nbOfOiTargetExtensions++;
                    oiTargetHDU = (TableHDU) hdu;
                }
            }
            index++;
        }
        logger.fine("file contains "+fits.getNumberOfHDUs()+" named extensions [ "+list+"]");
        if(nbOfOiTargetExtensions!=1){
            throw new Exception("File does no contain one OI_TARGET but "+nbOfOiTargetExtensions); 
        }
            
        String[] targets = (String[])oiTargetHDU.getColumn("TARGET");
        Oitarget[] modelOiTargets = bindedFile.getOitarget();  
        Vector modelOiTargetVector = new Vector();
        for(int i = 0; i < modelOiTargets.length; i++){
            modelOiTargetVector.addElement(((Oitarget)modelOiTargets[i]).getTarget());
        }
        if(targets.length>0){
            for(int i = 0; i< targets.length; i++){
                String s = targets[i];
                if (!modelOiTargetVector.contains(s)){
                    
                Oitarget t = new Oitarget();
                t.setTarget(s);
                bindedFile.addOitarget(t);
                logger.info("'"+s+"' oitarget associated");
                }else{
                logger.info("'"+s+"' oitarget was already associated");
                }
            }
        }
        if( bindedFile.getOitargetCount() != targets.length){
            throw new Exception("File does not contain same number of oitargets");
        }
    }

    // @todo place this method into jmmc.mf.util
    public void checkSettingsFormat(Settings s)throws Exception{
        logger.entering(""+this.getClass(), "loadSettingsFile");
        // try to locate files
        File[] files=s.getFiles().getFile();
        for (int i=0; i< files.length; i++){
            // Check file           
            checkFile(files[i]);
        }              
    }
    // @todo place this method into jmmc.mf.util
    public void loadSettingsFile(java.io.File fileToLoad) {
        logger.entering(""+this.getClass(), "loadSettingsFile");

        try {
            java.io.FileReader reader   = new java.io.FileReader(fileToLoad);
            Settings      newModel = (Settings) Settings.unmarshal(reader);
            checkSettingsFormat(newModel);                                    
            setRootSettings(newModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    // @todo place this method into jmmc.mf.util
    public void saveSettingsFile(java.io.File fileToSave)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException,
            org.exolab.castor.xml.ValidationException ,org.exolab.castor.mapping.MappingException{
        logger.entering(""+this.getClass(), "saveFile");
        
        // Read a File to unmarshal from
        java.io.FileWriter writer = new java.io.FileWriter(fileToSave);
        
        URL mappingURL = this.getClass().getClassLoader().getResource("jmmc/mf/gui/mapping.xml");
        logger.fine("Using mapping file :"+mappingURL);
        // Use a mapping file
        Mapping      mapping = new Mapping();
        mapping.loadMapping( mappingURL );
        
        // Do marshalling
        Marshaller marshaller = new Marshaller(writer);
        marshaller.setMapping(mapping);
        marshaller.marshal(rootSettings);
          /*
                   // Marshal objects
                   rootSettings.validate();
                   rootSettings.marshal(writer);
           */
        
    }

    //////////////// Fire events //////////////////////////////////////////////

    /**
     * One event raised by this model is TreeStructureChanged with the
     * root as path, i.e. the whole tree has changed.
     */
    protected void fireTreeStructureChanged(Settings newRoot) {
        logger.entering(""+this.getClass(), "fireTreeStructureChanged");
        int            len = treeModelListeners.size();
        TreeModelEvent e;
        TreePath treePath=null;
        if(newRoot==null){
            e  = new TreeModelEvent(this, treePath);
        }else{
            e   = new TreeModelEvent(this, new Object[] { newRoot });
        }

        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeStructureChanged(e);
        }
    }

    /**
     * One event raised by this model is TreeStructureChanged with the
     * changed node in argument.
     */
    protected void fireTreeNodesChanged(Object changedNode) {
        logger.entering(""+this.getClass(), "fireTreeNodesChanged");
        int            len = treeModelListeners.size();
        TreeModelEvent e   = new TreeModelEvent(this,
                new Object[] { changedNode });

        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
    }

    //////////////// TreeModel interface implementation ///////////////////////

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
        logger.entering(""+this.getClass(), "addTreeModelListener");
        treeModelListeners.addElement(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
        logger.entering(""+this.getClass(), "getChild");
        if (parent instanceof Settings ){
            Settings s = (Settings) parent;
            // select if child is one target or fitter node
            if( index ==0 ){
                return s.getFiles();
            }else if( index == 1 ){
                return s.getTargets();
            }else{
                logger.warning("This line must not occur");
                return "??";
            }
        }else if (parent instanceof Files){
            Files f = (Files) parent;
            return f.getFile(index);
        }else if (parent instanceof Targets){
            Targets t = (Targets) parent;
            return t.getTarget(index);
        }else if (parent instanceof Target ){
            Target t = (Target) parent;
            if (index<t.getFileLinkCount()){
                return t.getFileLink(index);
            }else{
                return t.getModel(index-t.getFileLinkCount());
            }
        }else{
            logger.warning("child n°"+index+" is a not handled");
            return "TBD";
        }
    }

    /**
     * Returns the number of children of parent.
     */
    public int getChildCount(Object parent) {
        logger.entering(""+this.getClass(), "getChildCount");
        if (parent instanceof Settings){
            Settings s = (Settings) parent;
            return 2;
        }else if (parent instanceof Files){
            Files f = (Files) parent;
            return f.getFileCount();
        }else if (parent instanceof Targets){
            Targets t = (Targets) parent;
            return t.getTargetCount();
        }else if (parent instanceof Target){
            Target t = (Target) parent;
            // return number of files and models
            // ident node is represented on target tree node
            return t.getFileLinkCount()+t.getModelCount();
        }else{
            return 1;
        }
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        logger.entering(""+this.getClass(), "getIndexOfChild");
        if ((parent == null) || (child == null)) {
            return 0;
        }
        /*
           Settings   s        = (Settings) parent;
           Settings[] children = p.getModel();
           int     count    = 0;

           for (int i = 0; i < children.length; i++) {
           if (children[i].equals(child)) {
           return count;
           }

           count++;
           }
         **/

        return 0;
    }

    /**
     * Returns the root of the tree.
     */
    public Object getRoot() {
        logger.entering(""+this.getClass(), "getRoot");
        return rootSettings;
    }

    /**
     * Returns true if node is a leaf.
     */
    public boolean isLeaf(Object node) {
        if ((node instanceof Settings ) ||
                (node instanceof Files ) ||
                (node instanceof Targets ) ||
                (node instanceof Target )){
            return false;

        }else{
            return true;
        }
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        logger.entering(""+this.getClass(), "removeTreeModelListener");
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        logger.entering(""+this.getClass(), "valueForPathChanged");
        Object modifiedObject = path.getLastPathComponent();
        /*Settings m = (Settings) path.getLastPathComponent();
          m.setName("" + newValue);
         */
        fireTreeNodesChanged(modifiedObject);
    }

    //
    // Inner classes used to manage actions
    //

    protected class LoadFilesAction extends MFAction {
        public String lastDir = System.getProperty("user.dir");

        public LoadFilesAction() {
            super("loadFiles");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering(""+this.getClass(), "actionPerformed");

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);

            // Set in previous load directory
            if (lastDir != null) {
                fileChooser.setCurrentDirectory(new java.io.File(lastDir));
            }

            // Open file chooser
            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                java.io.File[] files = fileChooser.getSelectedFiles();
                for (int i=0; i < files.length; i++){
                    addFile(files[i]);
                }
                lastDir = files[0].getParent();
            }
        }
    }

    protected class LoadModelAction extends MFAction {
        public String lastDir = System.getProperty("user.dir");

        public LoadModelAction() {
            super("loadModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering(""+this.getClass(), "actionPerformed");

            JFileChooser fileChooser = new JFileChooser();

            // Set in previous load directory
            if (lastDir != null) {
                fileChooser.setCurrentDirectory(new java.io.File(lastDir));
            }

            // Open file chooser
            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                lastDir = file.getParent();
                loadSettingsFile(file);
            }
        }
    }

    protected class SaveModelAction extends MFAction {
        public String lastDir = System.getProperty("user.dir");

        public SaveModelAction() {
            super("saveModel");
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            logger.entering(""+this.getClass(), "actionPerformed");

            JFileChooser fileChooser = new JFileChooser();

            // Set in previous save directory
            if (lastDir != null) {
                fileChooser.setCurrentDirectory(new java.io.File(lastDir));
            }

            try {
                // Open filechooser
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fileChooser.getSelectedFile();
                    lastDir = file.getParent();

                    // Ask to overwrite
                    if (file.exists()) {
                        String message = "File '" + file.getName() +
                            "' already exists\nDo you want to overwrite this file?";

                        // Modal dialog with yes/no button
                        int answer = JOptionPane.showConfirmDialog(null, message);

                        if (answer == JOptionPane.YES_OPTION) {
                            saveSettingsFile(file);
                        }
                    } else {
                        saveSettingsFile(file);
                    }
                }
            } catch (Exception exc) {
                ReportDialog dialog = new ReportDialog(null, true, exc);
                dialog.setVisible(true);

                // if (dialog.returnedValue="Report")
            }
        }
    }
}
