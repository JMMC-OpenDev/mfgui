/*
   JMMC
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.ReportDialog;
import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.Files;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Oitarget;
import fr.jmmc.mf.models.Parameters;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;
import java.util.Iterator;
import java.util.StringTokenizer;

import java.util.logging.Logger;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import nom.tam.util.ArrayDataInput;
import nom.tam.util.BufferedFile;
import org.eso.fits.FitsHDUnit;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;
/*
import nom.tam.fits.*;
import uk.ac.starlink.datanode.nodes.FITSDataNode;
import uk.ac.starlink.datanode.nodes.FITSDataNode.ArrayDataMaker;
import uk.ac.starlink.fits.FitsConstants;
import uk.ac.starlink.table.*;
import uk.ac.starlink.fits.FitsStarTable;
import uk.ac.starlink.datanode.nodes.FITSFileDataNode;
import uk.ac.starlink.datanode.nodes.TableHDUDataNode;
import uk.ac.starlink.datanode.nodes.HDUDataNode;
import uk.ac.starlink.datanode.nodes.DataType;
*/

import org.eso.fits.FitsFile;
import org.eso.fits.FitsHDUnit;
import org.eso.fits.FitsHeader;
import org.eso.fits.FitsKeyword;
import org.eso.fits.FitsColumn;
import org.eso.fits.FitsTable;

import java.net.URL;
//import java.nio.*;
import java.io.FileOutputStream;


public class SettingsModel implements TreeModel, ModifyAndSaveObject {
    private Vector             treeModelListeners  = new Vector();
    private Settings           rootSettings = null;
  

    /* both following members can be used to associate models to some GUI components.
     * while no settings can be loaded several times, some are static while other are not     
     */    
    public static Hashtable supportedModels = new Hashtable();
    public static DefaultComboBoxModel supportedModelsModel= new DefaultComboBoxModel();
    
    private Hashtable fileListModels = new Hashtable();    
    public DefaultListModel allFilesListModel;
    public DefaultListModel targetListModel;
    public DefaultComboBoxModel oiTargets;    
    
    // Logging
    static Logger logger = Logger.getLogger("fr.jmmc.mf.gui.SettingsModel");
    
    // ModifyAndSaveObject interface
    public boolean isModified=false;    
    public java.io.File associatedFile=null;
    
    public SettingsModel() {
        // Init members
        allFilesListModel = new DefaultListModel();    
        targetListModel = new DefaultListModel();
        oiTargets = new DefaultComboBoxModel();
        
        // Init children for a new settings 
        rootSettings = new Settings();        
        rootSettings.setFiles(new Files());        
        rootSettings.setTargets(new Targets());        
        rootSettings.setParameters(new Parameters());
        rootSettings.setFitter("standard");
        rootSettings.setUserInfo("Created on "+new java.util.Date()+" by ModelFitting GUI rev. "+ fr.jmmc.mcs.util.Resources.getResource("mf.version"));                
        setRootSettings(rootSettings);
    }

    // respond to ModifyAndSaveObject interface
    public boolean isModified(){
        logger.entering(""+this.getClass(), "isModified");
        logger.finest("isModified="+isModified);
        return isModified;
    }
    
    public void setModified(boolean flag){
        logger.entering(""+this.getClass(), "setModified");
        logger.finest("setModified to "+flag);
        isModified=flag;
    }
    
    // respond to ModifyAndSaveObject interface
    public void save(){
        logger.entering(""+this.getClass(), "save");
        if(associatedFile!=null){  
            try{
                saveSettingsFile(associatedFile,false);
            } catch (Exception exc) {
                 new ReportDialog(null, true, exc).setVisible(true);                
            }
        }else{
            // trigger saveModelAction
            MainFrame.saveModelAction.actionPerformed(null);                    
        }
    }
    
    // respond to ModifyAndSaveObject interface
    public java.awt.Component getComponent(){
        return MainFrame.getInstance();
    }                
        
    /**
     * This method must be called by anyone that has modified the xml binded
     * document.
     */
    public void fireUpdate(){
        logger.entering(""+this.getClass(), "fireUpdate");
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
            allFilesListModel.addElement(f);            
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
        
        String desc="This rootSettings contains "
                + rootSettings.getFiles().getFileCount() + " files,"
                + rootSettings.getTargets().getTargetCount() + " targets,"                
                + "User info: [ " + rootSettings.getUserInfo() + " ]"
// Some old settings files have no parameters and cause a null exception on this line
//                + rootSettings.getParameters().getParameterCount() + " shared parameters,"
                ;
        
        if( rootSettings.getResult() != null ){
                desc=desc+" with result section";                
        }
        
        logger.fine(desc);
        
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
        String key = target.getTarget().trim();
        DefaultListModel lm = (DefaultListModel) fileListModels.get(key);
        if(lm == null){
            // We need to create one new fileModel for this ident
            lm = new DefaultListModel();
            fileListModels.put(key, lm);            
        }
        if(!lm.contains(file)){
            logger.fine("adding file '"+file.getName() +"' to listmodel for '"+ key+ "'" );
            lm.addElement(file);
        }else{
            logger.fine("file "+file.getName() +" already registered to listmodel for '"+ key + "'" );
        }
        
    }

    
    public ListModel getFileListModelForOiTarget(String oiTargetName){
        logger.entering(""+this.getClass(), "getFileListModelForTarget");
        // we use target name as key
        String key = oiTargetName.trim();
        return (ListModel)fileListModels.get(key);
    }

    
    // @todo place this method into fr.jmmc.mf.util
    public void addFile(java.io.File fileToAdd)throws Exception{
        logger.entering(""+this.getClass(), "addFile");
        // Try to add list to comboModel if it is not already inside the listModel
        if (!allFilesListModel.contains(fileToAdd)){
            // The file must be one oidata file            
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
/*
 *@todo return a invalidFormat exception instead of Simple exception
            }catch(Exception exc){
                logger.warning("Rejecting non valid file '"+fileToAdd.getAbsolutePath()+"'");                
                throw new ...          
            }
 */
                setModified(true);
        }else{
            logger.warning("File '"+fileToAdd.getAbsolutePath()+"' already present");
        }        
    }

    // @todo place this method into fr.jmmc.mf.util
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

    // @todo place this method into fr.jmmc.mf.util
    public void checkFile(File bindedFile)throws Exception{
        logger.entering(""+this.getClass(), "checkFile");

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
                filename=UtilsClass.saveBASE64OifitsToFile(bindedFile.getId(), href);                
            }else{
                return;
            }
        }else if (!realFile.exists()){
            logger.warning("User should try to locate not found file");
            JFileChooser fc = new JFileChooser(realFile.getParentFile());
            int returnVal = fc.showDialog(new JFrame(), "Please try to locate requested file:"+realFile.getAbsolutePath());
           
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filename= fc.getSelectedFile().getCanonicalPath();
                bindedFile.setName(filename);
                logger.info("User asked to try again with new file ");
                addFileHref(bindedFile);
                checkFile(bindedFile);
                return;
            }else{
                logger.warning("Can't locate file");
                throw new java.io.FileNotFoundException();
            }
            
        }else{
            addFileHref(bindedFile);
        }

        //  oi_target HDU
//        TableHDU oiTargetHDU=null; 
        // will tell if there is one OI_TARGET extension in this file        
        int nbOfOiTargetExtensions=0;
/*
        // scan given file and try to get reference of oi_target extension excepted primary hdu
        Fits fits = new Fits(filename);        
        FITSFileDataNode dataNode = new FITSFileDataNode(new java.io.File(filename));        
        for (Iterator i = dataNode.getChildIterator(); i.hasNext();) {
            Object elem = (Object) i.next();                        
            logger.fine("next el is "+ elem.getClass()+" named "+elem);                  
            if (elem  instanceof TableHDUDataNode){
                TableHDUDataNode hdu = (TableHDUDataNode)elem;
                logger.fine("allows children -> " +hdu.allowsChildren());
                StarTable table = hdu.getStarTable();                
                // le problem est que l'on a pas a cces au header qui est en privé dans hdu    
            }
        }
        
        fits = new Fits(filename);       
        String list="";
        int index=0;
        BasicHDU hdu;
        while((hdu=fits.readHDU())!=null){           
            String extName = hdu.getTrimmedString("EXTNAME");
            logger.fine("reading hdu n "+index+ "["+extName+"]");
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
        logger.fine("file contains "+index+" named extensions [ "+list+"]");
 
 */
        String list="";
        FitsHDUnit oiTargetHDU=null;        
        FitsFile fits = new FitsFile(filename);
        int index = fits.getNoHDUnits();
        for (int i = 0; i < index; i++) {
            FitsHDUnit hdu = fits.getHDUnit(i);
            FitsHeader header = hdu.getHeader();
            FitsKeyword extName = header.getKeyword("EXTNAME");
            
            list=list+" "+extName;
            // search for oi_targets
            if(extName!=null){
                logger.fine("reading hdu n "+i+ "["+extName.getString()+"]");
                if(extName.getString().equals("OI_TARGET")){
                    nbOfOiTargetExtensions++;
                    oiTargetHDU = hdu;
                }
            }           
        }
                        
        if(nbOfOiTargetExtensions!=1){
            throw new Exception("File does no contain one OI_TARGET but "+nbOfOiTargetExtensions); 
        }
                    
        FitsColumn targetsColumn =  ((FitsTable)oiTargetHDU.getData()).getColumn("TARGET");
        //String[] targets = (String[])oiTargetHDU.getColumn("TARGET");
        int targetNb = ((FitsTable)oiTargetHDU.getData()).getNoRows();
        // Collect previous
        Oitarget[] modelOiTargets = bindedFile.getOitarget();
        Vector modelOiTargetVector = new Vector();
        for(int i = 0; i < modelOiTargets.length; i++){
            modelOiTargetVector.addElement(((Oitarget)modelOiTargets[i]).getTarget());
        }
        // And update list
        if(targetNb>0){
            for(int i = 0; i< targetNb; i++){
                String s = targetsColumn.getString(i);
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
        if( bindedFile.getOitargetCount() != targetNb){
            throw new Exception("File does not contain same number of oitargets");
        }
    }

    // @todo think to move this method into fr.jmmc.mf.util
    public void checkSettingsFormat(Settings s)throws Exception{
        logger.entering(""+this.getClass(), "checkSettingsFormat");
        // try to locate files
        File[] files=s.getFiles().getFile();
        for (int i=0; i< files.length; i++){
            // Check file           
            checkFile(files[i]);
        }
        
        // assert that one parameters section is present to allow param sharing
        if(s.getParameters()==null){
           logger.fine("no parameter section, -> new one created");
           s.setParameters(new Parameters());
           setModified(true);            
        }
        
        // assert that one userInfo section is present to allow traces
        if(s.getUserInfo()==null){
            logger.fine("no userInfo section, -> new one created");
            s.setUserInfo("UserInfo added on "+new java.util.Date()+" by ModelFitting GUI rev. "+ fr.jmmc.mcs.util.Resources.getResource("mf.version"));
            setModified(true);            
        }
        
    }
    
    // @todo place this method into fr.jmmc.mf.util
    public void loadSettingsFile(java.io.File fileToLoad) 
    throws java.io.FileNotFoundException,org.exolab.castor.xml.MarshalException,java.lang.Exception
    {
        logger.entering(""+this.getClass(), "loadSettingsFile");
        java.io.FileReader reader   = new java.io.FileReader(fileToLoad);
        Settings      newModel = (Settings) Settings.unmarshal(reader);
        checkSettingsFormat(newModel);                                   
        setRootSettings(newModel);
        setModified(false);
        associatedFile=fileToLoad;
    }
    
    /**
     * Write serialisation into given file.
     * @todo place this method into fr.jmmc.mf.util
     * @param temporaryRequest indicates that this file is not save by user but for internal use
     */     
    public void saveSettingsFile(java.io.File fileToSave, boolean temporaryRequest)
    throws java.io.IOException, org.exolab.castor.xml.MarshalException,
            org.exolab.castor.xml.ValidationException ,org.exolab.castor.mapping.MappingException{
        logger.entering(""+this.getClass(), "saveSettingsFile");
        
        // Read a File to unmarshal from
        java.io.FileWriter writer = new java.io.FileWriter(fileToSave);
        
        URL mappingURL = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/mapping.xml");
        logger.fine("Using mapping file :"+mappingURL);
        // Use a mapping file
        Mapping      mapping = new Mapping();
        mapping.loadMapping( mappingURL );
        
        // Do marshalling
        Marshaller marshaller = new Marshaller(writer);
        marshaller.setMapping(mapping);
        marshaller.marshal(rootSettings);
        /* old code sometimes break xml elements order 
         * then a mapping was added
           // Marshal objects
           rootSettings.validate();
           rootSettings.marshal(writer);
        */   
        if(! temporaryRequest){
            associatedFile=fileToSave;
            setModified(false);
        }
    }

    // 
    public void setSupportedModels(Model[] models){
        logger.entering(""+this.getClass(), "setSupportedModels");
        // update list of supported models
        supportedModels.clear();                
        // update MVC model of available models checkboxes
        supportedModelsModel.removeAllElements();
        for (int i=0; i< models.length; i++){
            Model newModel = models[i];
            supportedModelsModel.addElement(newModel);
            logger.info("Adding supported model:"+newModel.getType());                                    
            supportedModels.put(newModel.getType(), newModel);
        }
    }
    
    public Model getSupportedModel(String type){
        logger.entering(""+this.getClass(), "getSupportedModel"); 
        return (Model) supportedModels.get(type);
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
        //logger.entering(""+this.getClass(), "getChild");
        if (parent instanceof Settings ){
            Settings s = (Settings) parent;
            // select which settings child it is
            if( index == 0 ){
                return s.getFiles();
            }else if( index == 1 ){                                
                return s.getTargets();
            }else if( index == 2 && s.getParameters().getParameterCount() >=1 ){
                return s.getParameters();
            }else if( index == 2 && s.getParameters().getParameterCount() ==0 ){
                return s.getResult();            
            }else if( index == 3 ){
                return s.getResult();            
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
            logger.warning("child n "+index+" is a not handled");
            return "TBD";
        }
    }

    /**
     * Returns the number of children of parent.
     */
    public int getChildCount(Object parent) {
        //logger.entering(""+this.getClass(), "getChildCount");
        if (parent instanceof Settings){
            Settings s = (Settings) parent;
            // return files, targets, and parameters
            int i=1;
            if( s.getFiles().getFileCount() >= 1){
                logger.finest("file count:"+s.getFiles().getFileCount());
                logger.finest("target count:"+s.getTargets().getTargetCount());
                i++;                
            }
            if(s.getParameters()!=null){
                if( s.getParameters().getParameterCount() >= 1){
                    i++;                
                }
            }
            if( s.getResult() != null ){                            
                i++;                
            }            
            return i;
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

   
}
