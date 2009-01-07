/*
JMMC
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.Files;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Oitarget;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Parameters;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;

//import java.nio.*;

import java.net.URL;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import fr.jmmc.oifits.*;
import fr.jmmc.oifits.validator.GUIValidator;
import java.util.Observable;

/**
 * This class manages the castor generated classes to bring 
 * one interface over a more conventionnal object.
 *
 */
public class SettingsModel implements TreeModel, ModifyAndSaveObject {

    /** list of supported models   */
    protected static Hashtable supportedModels = new Hashtable();
    /** Combobox model of supported models */
    public static DefaultComboBoxModel supportedModelsModel = new DefaultComboBoxModel();
    /** Class logger */
    static Logger logger = Logger.getLogger(
            "fr.jmmc.mf.gui.SettingsModel");
    /** Vector of objects that want to listen tree modification */
    private Vector<TreeModelListener> treeModelListeners = new Vector();
    /** Reference onto the main castor root object */
    private Settings rootSettings = null;
    /** Hashtable used to return a ListModel object for the selected files of a dedicated target */
    private Hashtable<String, ListModel> fileListModels = new Hashtable();
    public DefaultListModel allFilesListModel;
    public DefaultComboBoxModel oiTargets;
    private DefaultListModel targetListModel;
    private DefaultListModel parameterListModel;
    private DefaultComboBoxModel targetComboBoxModel;
    private DefaultComboBoxModel parameterComboBoxModel;
    /** Store last xml buffer (it has been done to save time on marshal */
    private String lastXml;
    /** flag used to respond for the ModifyAndSaveObject interface */
    private boolean isModified = false;
    /** Store a reference over the associated local file */
    public java.io.File associatedFile = null;    

    /**
     * Creates a new SettingsModel object.
     */
    public SettingsModel() {
        // Init members
        allFilesListModel = new DefaultListModel();
        targetListModel = new DefaultListModel();
        parameterListModel = new DefaultListModel();
        targetComboBoxModel = new DefaultComboBoxModel();
        parameterComboBoxModel = new DefaultComboBoxModel();
        oiTargets = new DefaultComboBoxModel();

        // Init children for a new settings 
        rootSettings = new Settings();
        rootSettings.setFiles(new Files());
        rootSettings.setTargets(new Targets());
        rootSettings.setParameters(new Parameters());
        rootSettings.setFitter("standard");
        rootSettings.setUserInfo("Created on " + new java.util.Date() +
                " by ModelFitting GUI rev. " + fr.jmmc.mcs.util.Resources.getResource("mf.version"));
        setRootSettings(rootSettings);        
    }

    /** Tell if the inner model is well filled */
    public boolean isValid() {
        return rootSettings.isValid();
    }


    /** Returns a new uniq file Id */
    public String getNewFileId() {
        Vector<String> ids = new Vector();
        File[] files = rootSettings.getFiles().getFile();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            ids.add(file.getId());
        }
        return getNewId("fileId", ids.size()+1, ids);
    }

    /** Returns a new uniq Model Name */
    public String getNewModelName(String prefix) {
        Vector<String> ids = new Vector();
        Target[] targets = rootSettings.getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];              
                ids.add(model.getName());
            }
        }
        return getNewId(prefix, ids.size()+1, ids);
    }
    
    /** Returns a new uniq param name */
    public String getNewParamName(String prefix) {
        Vector<String> ids = new Vector();
        Target[] targets = rootSettings.getTargets().getTarget();
        int nextId=1;
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                // parameter construction will try to start from model count
                // instead of parameter count
                nextId++;
                Parameter[] params = model.getParameter();
                for (int k = 0; k < params.length; k++) {
                    Parameter parameter = params[k];
                    ids.add(parameter.getName());
                }
            }
        }
        return getNewId(prefix, nextId, ids);
    }

    /**
     * Return a new id according to the given prefix, starting index and list of previous ids.
     * @param prefix prefix of the new id
     * @param firstId staring index of new id
     * @param previousIds list of previous ids
     * @return the new id
     */
    protected String getNewId(String prefix, int firstId, Vector<String> previousIds) {
        String newId = prefix + firstId;        
        while (previousIds.contains(newId)) {
            firstId++;
            newId = prefix + firstId;
        }
        return newId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAssociatedFilename() {
        if (associatedFile == null) {
            return "*";
        }

        return associatedFile.getName();
    }

    /**
     *  Tell to the model that given file is used by user to store its data
     * @param fileToSave
     */
    public void setAssociatedFile(java.io.File fileToSave) {
        logger.entering("" + this.getClass(), "setAssociatedFile");
        associatedFile = fileToSave;
    }

    // respond to ModifyAndSaveObject interface
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isModified() {
        logger.entering("" + this.getClass(), "isModified");
        logger.finest("isModified=" + isModified);

        return isModified;
    }

    /**
     * Call setModified(true) to declare that something internally changed.
     * setModified(false) should be called only after a disk save.
     */
    private void setModified(boolean flag) {
        logger.entering("" + this.getClass(), "setModified");
        logger.finest("setModified to " + flag);
        isModified = flag;
        if(isModified)
        {
            fireUpdate();
        }
    }

    /**
     * This method must be called by anyone that has modified the xml binded
     * document.
     */
    public void fireUpdate() {
        logger.entering("" + this.getClass(), "fireUpdate");
        logger.fine("update signal triggered");
        setRootSettings(rootSettings);
        isModified=true;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param xml DOCUMENT ME!
     */
    public void setLastXml(String xml) {
        logger.entering("" + this.getClass(), "setLastXml");
        this.lastXml = xml;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String getLastXml() throws Exception {
        logger.entering("" + this.getClass(), "getLastXml");

        if (lastXml == null) {
            logger.fine("Start of marshalling");

            java.io.StringWriter writer = new java.io.StringWriter();
            rootSettings.marshal(writer);
            logger.fine("End of marshalling");

            writer.flush();
            logger.fine("Store xml as string");
            this.lastXml = writer.toString();
        }

        return this.lastXml;
    }

    /**
     * DOCUMENT ME!
     *
     * @param keepResult DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public java.io.File getTempFile(boolean keepResult)
            throws Exception {
        logger.entering("" + this.getClass(), "getTempFile");

        java.io.File tmpFile;
        tmpFile = java.io.File.createTempFile("tmpSettings", ".xml");
        // Delete temp file when program exits.
        tmpFile.deleteOnExit();
        saveSettingsFile(tmpFile, keepResult);

        return tmpFile;
    }

    // respond to ModifyAndSaveObject interface
    /**
     * DOCUMENT ME!
     */
    public void save() {
        logger.entering("" + this.getClass(), "save");

        if (associatedFile != null) {
            try {
                saveSettingsFile(associatedFile, false);
            } catch (Exception exc) {
                new FeedbackReport(null, true, exc);
            }
        } else {
            // trigger saveModelAction
            MFGui.saveModelAction.actionPerformed(null);
        }
    }

    // respond to ModifyAndSaveObject interface
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public java.awt.Component getComponent() {
        return MFGui.getInstance();
    }    

    /**
     * DOCUMENT ME!
     *
     * @param newRootModel DOCUMENT ME!
     */
    public void setRootSettings(Settings newRootModel) {
        logger.entering("" + this.getClass(), "setRootSettings");
        rootSettings = newRootModel;

        // clear list of files, oitargets, target and fileListModels
        // then add settings ones
        allFilesListModel.clear();
        oiTargets.removeAllElements();
        fileListModels.clear();

        for (int i = 0; i < rootSettings.getFiles().getFileCount(); i++) {
            File f = rootSettings.getFiles().getFile(i);
            allFilesListModel.addElement(f);

            for (int j = 0; j < f.getOitargetCount(); j++) {
                String t = f.getOitarget(j).getTarget();

                if (oiTargets.getIndexOf(t) < 0) {
                    oiTargets.addElement(t);
                }

                addFileListModelForOiTarget(f.getOitarget(j), f);
            }
        }

        targetListModel.clear();
        parameterListModel.clear();
        targetComboBoxModel.removeAllElements();
        getParameterComboBoxModel().removeAllElements();

        for (int i = 0; i < rootSettings.getTargets().getTargetCount(); i++) {
            Target t = rootSettings.getTargets().getTarget(i);
            targetListModel.addElement(t);
            targetComboBoxModel.addElement(t);
            Model[] models = t.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                Parameter[] params = model.getParameter();
                for (int k = 0; k < params.length; k++) {
                    Parameter param = params[k];
                    parameterComboBoxModel.addElement(param);
                    parameterListModel.addElement(param);
                }
                ParameterLink[] paramLinks = model.getParameterLink();
                for (int k = 0; k < paramLinks.length; k++) {
                    ParameterLink parameterLink = paramLinks[k];
                    Parameter param = (Parameter) parameterLink.getParameterRef();
                    parameterComboBoxModel.addElement(param);
                    parameterListModel.addElement(param);
                }
            }
        }

        String desc = "This rootSettings contains " + rootSettings.getFiles().getFileCount() +
                " files," + rootSettings.getTargets().getTargetCount() + " targets," + "User info: [ " +
                rootSettings.getUserInfo() + " ]";

        if (rootSettings.getResult() != null) {
            desc = desc + " with result section";
        }

        logger.fine(desc);

        // fire general change event
        fireTreeStructureChanged(rootSettings);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Settings getRootSettings() {
        logger.entering("" + this.getClass(), "getRootSettings");

        return rootSettings;
    }

    /**
     * DOCUMENT ME!
     *
     * @param target DOCUMENT ME!
     * @param file DOCUMENT ME!
     */
    private void addFileListModelForOiTarget(Oitarget target, File file) {
        logger.entering("" + this.getClass(), "addFileListModelForTarget");

        // we use target name as key
        String key = target.getTarget().trim();
        DefaultListModel lm = (DefaultListModel) fileListModels.get(key);

        if (lm == null) {
            // We need to create one new fileModel for this ident
            lm = new DefaultListModel();
            fileListModels.put(key, lm);
        }

        if (!lm.contains(file)) {
            logger.fine("adding file '" + file.getName() + "' to listmodel for '" + key + "'");
            lm.addElement(file);
        } else {
            logger.fine("file " + file.getName() + " already registered to listmodel for '" + key +
                    "'");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param oiTargetName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ListModel getFileListModelForOiTarget(String oiTargetName) {
        logger.entering("" + this.getClass(), "getFileListModelForTarget");

        // we use target name as key
        String key = oiTargetName.trim();

        return (ListModel) fileListModels.get(key);
    }

    // @todo place this method into fr.jmmc.mf.util
    /**
     * DOCUMENT ME!
     *
     * @param fileToAdd DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void addFile(java.io.File fileToAdd) throws Exception {
        logger.entering("" + this.getClass(), "addFile");

        // Try to add list to comboModel if it is not already inside the listModel
        if (!allFilesListModel.contains(fileToAdd)) {
            // The file must be one oidata file            
            String fitsFileName = fileToAdd.getAbsolutePath();
            File newFile = new File();
            newFile.setName(fitsFileName);
            newFile.setId(getNewFileId());
            if (checkFile(newFile)) {
                // make shorter filename (this line must be kept after checkFile,
                // because is must be retrieved using full qualified name)
                newFile.setName(fileToAdd.getName());

                // test succedded let's continue
                rootSettings.getFiles().addFile(newFile);
                logger.info("'" + fitsFileName + "' oifile added to file list");

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
            }
        } else {
            logger.warning("File '" + fileToAdd.getAbsolutePath() + "' already present");
        }
    }

    // @todo place this method into fr.jmmc.mf.util
    /**
     * DOCUMENT ME!
     *
     * @param bindedFile DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void addFileHref(File bindedFile) throws Exception {
        logger.entering("" + this.getClass(), "addFileHref");

        java.io.File realFile = new java.io.File(bindedFile.getName());

        // Create a read-only memory-mapped file
        java.nio.channels.FileChannel roChannel = new java.io.RandomAccessFile(realFile, "r").getChannel();
        java.nio.ByteBuffer roBuf = roChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY,
                0, (int) roChannel.size());
        String b64 = "data:image/fits;base64," +
                new sun.misc.BASE64Encoder().encode(roBuf);
        bindedFile.setHref(b64);
    }

    /**
     * DOCUMENT ME!
     *
     * @param bindedFile DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void addFileOiTargets(File bindedFile) throws Exception {
        logger.entering("" + this.getClass(), "addFileOiTarget");
    }

    // @todo place this method into fr.jmmc.mf.util
    /**
     * DOCUMENT ME!
     *
     * @param bindedFile DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws . DOCUMENT ME!
     */
    public boolean checkFile(File bindedFile) throws Exception {
        logger.entering("" + this.getClass(), "checkFile");

        String filename = bindedFile.getName();
        logger.fine("Checking file from xml name '" + filename + "'");

        // Check href and return if no file exists
        java.io.File realFile = new java.io.File(filename);
        String href = bindedFile.getHref();

        if (href != null) {
            logger.fine("Href already present");

            //@todo decode b64 and test filesize or md5sum 
            // and then test on oitarget list could be done...
            if (bindedFile.getOitargetCount() < 1) {
                logger.warning("No oitarget found");
                // try to continue next test creating a file based on the base64 href                                
                filename = UtilsClass.saveBASE64ToFile(bindedFile, href);
            } else {
                return true;
            }
        } else if (!realFile.exists()) {
            logger.warning("User should try to locate not found file");

            JFileChooser fc = new JFileChooser(realFile.getParentFile());
            int returnVal = fc.showDialog(new JFrame(),
                    "Please try to locate requested file:" + realFile.getAbsolutePath());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filename = fc.getSelectedFile().getCanonicalPath();
                bindedFile.setName(filename);
                logger.info("User asked to try again with new file ");
                addFileHref(bindedFile);
                return checkFile(bindedFile);
            } else {
                logger.warning("Can't locate file");
                throw new java.io.FileNotFoundException();
            }
        } else {
            addFileHref(bindedFile);
        }

        bindedFile.removeAllOitarget();
        OifitsFile fits = new OifitsFile(filename);
        try {
            OiTarget oiTarget = fits.getOiTarget();
            String[] targetNames = oiTarget.getTargetNames();
            for (int i = 0; i < targetNames.length; i++) {
                String targetName = targetNames[i];
                Oitarget t = new Oitarget();
                t.setTarget(targetName);
                bindedFile.addOitarget(t);
            }
        } catch (Exception e) {
            GUIValidator val = new GUIValidator(null);
            val.checkFile(fits);
            return false;
        }
        return true;
    }

    // @todo think to move this method into fr.jmmc.mf.util
    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void checkSettingsFormat(Settings s) throws Exception {
        logger.entering("" + this.getClass(), "checkSettingsFormat");

        // try to locate files
        File[] files = s.getFiles().getFile();

        for (int i = 0; i < files.length; i++) {
            // Check file           
            checkFile(files[i]);
        }

        // assert that one parameters section is present to allow param sharing
        if (s.getParameters() == null) {
            logger.fine("no parameter section, -> new one created");
            s.setParameters(new Parameters());
            setModified(true);
        }

        // assert that one userInfo section is present to allow traces
        if (s.getUserInfo() == null) {
            logger.fine("no userInfo section, -> new one created");
            s.setUserInfo("UserInfo added on " + new java.util.Date() +
                    " by ModelFitting GUI rev. " +
                    fr.jmmc.mcs.util.Resources.getResource("mf.version"));
            setModified(true);
        }
    }

    // @todo place this method into fr.jmmc.mf.util
    /**
     * DOCUMENT ME!
     *
     * @param fileToLoad DOCUMENT ME!
     *
     */
    public void loadSettingsFile(java.io.File fileToLoad)
            throws java.io.FileNotFoundException, org.exolab.castor.xml.MarshalException,
            java.lang.Exception {
        logger.entering("" + this.getClass(), "loadSettingsFile(" + fileToLoad + ")");

        java.io.FileReader reader = new java.io.FileReader(fileToLoad);
        Settings newModel = (Settings) Settings.unmarshal(reader);

        checkSettingsFormat(newModel);
        setRootSettings(newModel);
        setModified(false);
        associatedFile = fileToLoad;

        reader = new java.io.FileReader(fileToLoad);

        java.io.BufferedReader in = new java.io.BufferedReader(reader);
        StringBuffer sb = new StringBuffer();
        String str;

        while ((str = in.readLine()) != null) {
            sb.append(str);
        }

        in.close();
        setLastXml(sb.toString());
    }

    // @todo place this method into fr.jmmc.mf.util
    /**
     * DOCUMENT ME!
     *
     * @param urlToLoad DOCUMENT ME!
     *
     */
    public void loadSettingsFile(java.net.URL urlToLoad)
            throws java.io.FileNotFoundException, org.exolab.castor.xml.MarshalException,
            java.lang.Exception {
        logger.entering("" + this.getClass(), "loadSettingsFile(" + urlToLoad + ")");

        java.io.InputStreamReader reader = new java.io.InputStreamReader(urlToLoad.openStream());
        Settings newModel = (Settings) Settings.unmarshal(reader);
        checkSettingsFormat(newModel);
        setRootSettings(newModel);
        setModified(false);
        associatedFile = null;
    }

    /**
     * Write serialisation into given file.
     * @todo place this method into fr.jmmc.mf.util
     * @param keepResult indicates that this file will not get result section. It is used in the runFit action for example.
     */
    public void saveSettingsFile(java.io.File fileToSave, boolean keepResult)
            throws java.io.IOException, org.exolab.castor.xml.MarshalException,
            org.exolab.castor.xml.ValidationException, org.exolab.castor.mapping.MappingException {
        logger.entering("" + this.getClass(), "saveSettingsFile");

        /* replace old result section by a new empty one according keepResult parameter */
        if (keepResult == false) {
            rootSettings.setResult(new Result());
        }

        logger.fine("start setting file writting");

        // Read a File to unmarshal from
        java.io.FileWriter writer = new java.io.FileWriter(fileToSave);

        URL mappingURL = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/mapping.xml");
        logger.fine("Using mapping file :" + mappingURL);

        // Do marshalling
        Marshaller marshaller = new Marshaller(writer);

        // old code sometimes break xml elements order then use a mapping file
        Mapping mapping = new Mapping();
        mapping.loadMapping(mappingURL);
        marshaller.setMapping(mapping);
        rootSettings.validate();
        marshaller.marshal(rootSettings);
        writer.flush();
        logger.fine("Ending file writting into " + fileToSave);

        if (fileToSave.equals(associatedFile)) {
            setModified(false);
        }
    }

    // 
    /**
     * DOCUMENT ME!
     *
     * @param models DOCUMENT ME!
     */
    public void setSupportedModels(Model[] models) {
        logger.entering("" + this.getClass(), "setSupportedModels");
        // update list of supported models
        supportedModels.clear();
        // update MVC model of available models checkboxes
        supportedModelsModel.removeAllElements();

        for (int i = 0; i < models.length; i++) {
            Model newModel = models[i];
            supportedModelsModel.addElement(newModel);
            logger.info("Adding supported model:" + newModel.getType());
            supportedModels.put(newModel.getType(), newModel);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Model getSupportedModel(String type) {
        logger.entering("" + this.getClass(), "getSupportedModel");

        return (Model) supportedModels.get(type);
    }

    //////////////// Fire events //////////////////////////////////////////////
    /**
     * One event raised by this model is TreeStructureChanged with the
     * root as path, i.e. the whole tree has changed.
     */
    protected void fireTreeStructureChanged(Settings newRoot) {
        logger.entering("" + this.getClass(), "fireTreeStructureChanged");

        int len = treeModelListeners.size();
        TreeModelEvent e;
        TreePath treePath = null;

        if (newRoot == null) {
            e = new TreeModelEvent(this, treePath);
        } else {
            e = new TreeModelEvent(this, new Object[]{newRoot});
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
        logger.entering("" + this.getClass(), "fireTreeNodesChanged");

        int len = treeModelListeners.size();
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{changedNode});

        for (int i = 0; i < len; i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
    }

    //////////////// TreeModel interface implementation ///////////////////////
    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
        logger.entering("" + this.getClass(), "addTreeModelListener");
        treeModelListeners.addElement(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
        //logger.entering(""+this.getClass(), "getChild");
        if (parent instanceof Settings) {
            Settings s = (Settings) parent;

            // select which settings child it is
            if (index == 0) {
                return s.getFiles();
            } else if (index == 1) {
                return s.getTargets();
            } else if ((index == 2) && (s.getParameters().getParameterCount() >= 1)) {
                return s.getParameters();
            } else if ((index == 2) && (s.getParameters().getParameterCount() == 0)) {
                return s.getResult();
            } else if (index == 3) {
                return s.getResult();
            } else {
                logger.warning("This line must not occur");

                return "??";
            }
        } else if (parent instanceof Files) {
            Files f = (Files) parent;

            return f.getFile(index);
        } else if (parent instanceof Targets) {
            Targets t = (Targets) parent;

            return t.getTarget(index);
        } else if (parent instanceof Target) {
            Target t = (Target) parent;

            if (index < t.getFileLinkCount()) {
                return t.getFileLink(index);
            } else {
                return t.getModel(index - t.getFileLinkCount());
            }
        } else {
            logger.warning("child n " + index + " is a not handled");

            return "TBD";
        }
    }

    /**
     * Returns the number of children of parent.
     */
    public int getChildCount(Object parent) {
        //logger.entering(""+this.getClass(), "getChildCount");
        if (parent instanceof Settings) {
            Settings s = (Settings) parent;

            // return files, targets, and parameters
            int i = 1;

            if (s.getFiles().getFileCount() >= 1) {
                i++;
            }

            if (s.getParameters() != null) {
                if (s.getParameters().getParameterCount() >= 1) {
                    i++;
                }
            }

            if (s.getResult() != null) {
                i++;
            }

            return i;
        } else if (parent instanceof Files) {
            Files f = (Files) parent;

            return f.getFileCount();
        } else if (parent instanceof Targets) {
            Targets t = (Targets) parent;

            return t.getTargetCount();
        } else if (parent instanceof Target) {
            Target t = (Target) parent;

            // return number of files and models
            // ident node is represented on target tree node
            return t.getFileLinkCount() + t.getModelCount();
        } else {
            return 1;
        }
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        logger.entering("" + this.getClass(), "getIndexOfChild");

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
        logger.entering("" + this.getClass(), "getRoot");

        return rootSettings;
    }

    /**
     * Returns true if node is a leaf.
     */
    public boolean isLeaf(Object node) {
        if ((node instanceof Settings) || (node instanceof Files) || (node instanceof Targets) ||
                (node instanceof Target)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        logger.entering("" + this.getClass(), "removeTreeModelListener");
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        logger.entering("" + this.getClass(), "valueForPathChanged");

        Object modifiedObject = path.getLastPathComponent();
        /*Settings m = (Settings) path.getLastPathComponent();
        m.setName("" + newValue);
         */
        fireTreeNodesChanged(modifiedObject);
    }

    /**
     * @return the targetListModel
     */
    public DefaultListModel getTargetListModel() {
        return targetListModel;
    }

    /**
     * @return the targetComboBoxModel
     */
    public DefaultComboBoxModel getTargetComboBoxModel() {
        return targetComboBoxModel;
    }

    /**
     * @return the parameterComboBoxModel
     */
    public DefaultComboBoxModel getParameterComboBoxModel() {
        return parameterComboBoxModel;
    }

    /**
     * @return the parameterListModel
     */
    public DefaultListModel getParameterListModel() {
        return parameterListModel;
    }
}
