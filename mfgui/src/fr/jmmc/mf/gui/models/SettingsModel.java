package fr.jmmc.mf.gui.models;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Files;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Oitarget;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Parameters;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mf.models.Results;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;

import java.net.URL;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import fr.jmmc.oifits.*;
import fr.jmmc.oifits.validator.GUIValidator;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

/**
 * This class manages the castor generated classes to bring 
 * one interface over a more conventionnal object.
 * It implements:
 *  - the treemodel to be used by the settingsPane tree
 *  - the modifyAndSaveObject to ensure that application save user modification on application exit
 *
 *
 */
public class SettingsModel extends DefaultTreeSelectionModel implements TreeModel, ModifyAndSaveObject {

    /** list of supported models   */
    protected static Hashtable supportedModels = new Hashtable();
    /** Combobox model of supported models */
    public static DefaultComboBoxModel supportedModelsModel = new DefaultComboBoxModel();
    public final static String className = "fr.jmmc.mf.gui.models.SettingsModel";
    /** Class logger */
    static Logger logger = Logger.getLogger(className);
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
    private Hashtable<Result, ResultModel> resultToModel = new Hashtable<Result, ResultModel>();
    private DefaultMutableTreeNode plotContainerNode = new DefaultMutableTreeNode("Plots") {

        public String toString() {
            return "Plots";
        }
    };
    private Observable delegateObservable = new Observable() {

        public boolean hasChanged() {
            return isModified;
        }
    };

    /**
     * Creates a new empty SettingsModel object.
     */
    public SettingsModel() {
        logger.entering(className, "SettingsModel()");
        init();
    }

    /**
     * Creates a new empty SettingsModel object.
     */
    public SettingsModel(java.io.File fileToLoad)
            throws java.io.FileNotFoundException, org.exolab.castor.xml.MarshalException,
            java.lang.Exception {
        logger.entering(className, "SettingsModel", fileToLoad);
        init();
        java.io.FileReader reader = new java.io.FileReader(fileToLoad);
        Settings newModel = (Settings) Settings.unmarshal(reader);
        checkSettingsFormat(newModel);
        setRootSettings(newModel);
        setModified(false);
        associatedFile = fileToLoad;
    }

    /**
     * Creates a new empty SettingsModel object.
     */
    public SettingsModel(java.net.URL urlToLoad)
            throws java.io.FileNotFoundException, org.exolab.castor.xml.MarshalException,
            java.lang.Exception {
        logger.entering(className, "SettingsModel", urlToLoad);
        init();
        java.io.InputStreamReader reader = new java.io.InputStreamReader(urlToLoad.openStream());
        Settings newModel = (Settings) Settings.unmarshal(reader);
        checkSettingsFormat(newModel);
        setRootSettings(newModel);
        setModified(false);
        associatedFile = new java.io.File(urlToLoad.getFile());
    }

    /**
     *  Add observer to be simply nofified by model content changes.
     */
    public void addObserver(Observer observer) {
        delegateObservable.addObserver(observer);
    }

    public void setNormalize(Target target, boolean flag) {
        target.setNormalize(flag);
        fireTreeNodesChanged(target);
    }

    public void setPlotPanel(PlotPanel plotPanel) {
        logger.entering(className, "setPlotPanel", new Object[]{plotPanel});
        plotContainerNode.setUserObject(plotPanel);
    }

    public void addLITproSettings() {
    }

    public void addPlot(JFrame frame, final String title) {
        FrameTreeNode newPlotNode = new FrameTreeNode(frame, title);
        plotContainerNode.add(newPlotNode);
        fireTreeNodesInserted(this,
                new Object[]{rootSettings, plotContainerNode},
                new int[]{plotContainerNode.getChildCount() - 1},
                new Object[]{newPlotNode});
        setSelectionPath(new TreePath(new Object[]{rootSettings, plotContainerNode, newPlotNode}));
    }

    /**
     * Add the given model to the given target.
     * The model will change parameter name
     * @param parentTarget
     * @param newModel
     */
    public void addModel(Target parentTarget, Model newModel) {
        logger.entering(className, "addModel", new Object[]{parentTarget, newModel});
        setModified(true);

        // force another name with unique position
        String type = newModel.getType();
        newModel.setName(getNewModelName(type));

        boolean firstModel = parentTarget.getModelCount() == 0;

        // and change parameters name also
        Parameter[] params = newModel.getParameter();
        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            // init some parameters of first elements
            if (firstModel) {
                if (p.getName().equals("x") || p.getName().equals("y")) {
                    p.setHasFixedValue(true);
                }
                if (p.getName().equals("flux_weight")) {
                    p.setValue(1);
                }
            }
            p.setName(getNewParamName(p.getName()));
        }

        // add the new element to current target
        parentTarget.addModel(newModel);
        fireTreeNodesInserted(this,
                new Object[]{rootSettings, rootSettings.getTargets(), parentTarget},
                new int[]{parentTarget.getModelCount()},
                new Object[]{newModel});
    }

    public void removeModel(Target parentTarget, Model oldModel) {
        logger.entering(className, "removeModel", new Object[]{parentTarget, oldModel});
        setModified(true);
        Model[] models = parentTarget.getModel();
        for (int i = 0; i < models.length; i++) {
            Model model = models[i];
            if (model == oldModel) {
                int idx=getIndexOfChild(parentTarget, model);
                fireTreeNodesRemoved(this,
                        new Object[]{rootSettings, rootSettings.getTargets(), parentTarget},
                        new int[]{idx},
                        new Object[]{oldModel});
                parentTarget.removeModel(i);
                setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), parentTarget}));
            }
        }
    }

    public void removeModel(Model oldModel) {
        logger.entering(className, "removeModel", new Object[]{oldModel});
        removeModel(getParent(oldModel), oldModel);
    }

    public Target addTarget(String targetIdent) {
        logger.entering(className, "addTarget", new Object[]{targetIdent});
        setModified(true);

        Target newTarget = new Target();
        newTarget.setIdent(targetIdent);
        newTarget.setNormalize(true);

        // Add list of currently files that contain this target
        ListModel targetFiles = getFileListModelForOiTarget(targetIdent);
        for (int i = 0; i < targetFiles.getSize(); i++) {
            FileLink fileLink = new FileLink();
            fileLink.setFileRef(targetFiles.getElementAt(i));
            newTarget.addFileLink(fileLink);
            logger.fine("Adding default reference to file :" + targetFiles.getElementAt(i));
        }
        int indice = rootSettings.getTargets().getTargetCount();
        rootSettings.getTargets().addTarget(newTarget);
        targetListModel.addElement(newTarget);

        fireTreeNodesInserted(this,
                new Object[]{rootSettings, rootSettings.getTargets()},
                new int[]{indice},
                new Object[]{newTarget});
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), newTarget}));

        return newTarget;
    }

    public void removeTarget(Target oldTarget) {
        logger.entering(className, "removeTarget", new Object[]{oldTarget});
        setModified(true);

        int indice = getIndexOfChild(rootSettings.getTargets(), oldTarget);
        targetListModel.removeElement(oldTarget);
        rootSettings.getTargets().removeTarget(indice);
        fireTreeNodesRemoved(this,
                new Object[]{rootSettings, rootSettings.getTargets()},
                new int[]{indice},
                new Object[]{oldTarget});
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets()}));
    }

    public void removeFrame(FrameTreeNode frameNode) {
        // actually only plotContainer or Result can have such elements as child
        int idx = plotContainerNode.getIndex(frameNode);
        if (idx >= 0) {
            plotContainerNode.remove(frameNode);
            fireTreeNodesRemoved(this,
                    new Object[]{rootSettings, plotContainerNode},
                    new int[]{idx},
                    new Object[]{frameNode});
            setSelectionPath(new TreePath(new Object[]{rootSettings, plotContainerNode}));
            return;
        }
        Enumeration<ResultModel> resModels = resultToModel.elements();
        while (resModels.hasMoreElements()) {
            ResultModel resModel = resModels.nextElement();
            idx=resModel.getIndex(frameNode);
            if (idx >= 0) {
                resModel.remove(frameNode);
                fireTreeNodesRemoved(this,
                        new Object[]{rootSettings, rootSettings.getResults(), resModel},
                        new int[]{idx},
                        new Object[]{frameNode});                
                setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getResults(), resModel}));
                return;
            }
        }
    }


    /** Just tell if the tree selection contains one (or more) Frame associated to a FrameTreeNode.
     *
     * @return true if the selection contains some frames or false
     */
    public boolean isFrameSelectionEmpty() {
        TreePath[] treePaths = getSelectionPaths();
        if(treePaths==null){
           return true;
        }
        for (int i = 0; i < treePaths.length; i++) {
            TreePath treePath = treePaths[i];
            Object object = treePath.getLastPathComponent();
            if (object instanceof FrameTreeNode) {
                return false;
            }
        }
        return true;
    }

   public boolean isSelectionRemovable() {
       TreePath[] treePaths = getSelectionPaths();
       if(treePaths==null){
           return false;
       }
       for (int i = 0; i < treePaths.length; i++) {
            TreePath treePath = treePaths[i];
            Object object = treePath.getLastPathComponent();
            if (object instanceof ResultModel ||
                    object instanceof Target ||
                    object instanceof ResultModel ||
                    object instanceof Model ||
                    object instanceof FrameTreeNode) {
                return true;

            }
        }
        return false;
    }


    public void removeTreeSelection() {
        TreePath[] treepaths = getSelectionPaths();
        for (int i = 0; i < treepaths.length; i++) {
            TreePath treePath = treepaths[i];
            Object lastPathComponent = treePath.getLastPathComponent();
            if (lastPathComponent instanceof ResultModel) {
                ResultModel resultModel = (ResultModel) lastPathComponent;
                Result selectedResult = resultModel.getResult();
                Result[] results = rootSettings.getResults().getResult();
                for (int j = 0; j < results.length; j++) {
                    Result result = results[j];
                    if (result == selectedResult) {
                        rootSettings.getResults().removeResult(j);
                        fireTreeNodesRemoved(this,
                                new Object[]{rootSettings, rootSettings.getResults()},
                                new int[]{j},
                                new Object[]{lastPathComponent});
                        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getResults()}));
                    }
                }
            } else if (lastPathComponent instanceof Target) {
                removeTarget((Target) lastPathComponent);
            } else if (lastPathComponent instanceof Model) {
                removeModel((Model) lastPathComponent);
            } else if (lastPathComponent instanceof FrameTreeNode) {
                removeFrame((FrameTreeNode) lastPathComponent);
            } else {
                logger.warning("No code implemented to remove : " + lastPathComponent.getClass());
            }
        }

    }

    public void toggleSelectedFrames() {
        TreePath[] treepaths = getSelectionPaths();
        setSelectionPath(new TreePath(new Object[]{rootSettings, plotContainerNode}));
        for (int i = 0; i < treepaths.length; i++) {
            TreePath treePath = treepaths[i];
            Object lastPathComponent = treePath.getLastPathComponent();
            if (lastPathComponent instanceof DefaultMutableTreeNode) {
                Object lastUserObject = ((DefaultMutableTreeNode) lastPathComponent).getUserObject();
                if (lastUserObject instanceof JFrame) {
                    JFrame frame = (JFrame) lastUserObject;
                    frame.setContentPane(frame.getContentPane());
                    frame.setVisible(!frame.isVisible());
                    setSelectionPath(new TreePath(new Object[]{rootSettings, plotContainerNode, lastPathComponent}));
                }
            }
        }
    }

    public void init() {
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
        rootSettings.setResults(new Results());
        rootSettings.setParameters(new Parameters());
        rootSettings.setFitter("standard");
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
        return getNewId("fileId", ids.size() + 1, ids);
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
        return getNewId(prefix, ids.size() + 1, ids);
    }

    /** Returns a new uniq param name */
    public String getNewParamName(String prefix) {
        Vector<String> ids = new Vector();
        Target[] targets = rootSettings.getTargets().getTarget();
        int nextId = 1;
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

    public void setModelName(Model model, String newName) {
        model.setName(newName);
        Target parentTarget = getParent(model);
        fireTreeNodesChanged(this,
                new Object[]{rootSettings},
                new int[]{getIndexOfChild(rootSettings.getTargets(), parentTarget)},
                new Object[]{model});
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
     * Return the filename that could be used to store settings into.
     * @return the filename
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
        logger.entering(className, "setAssociatedFile");
        associatedFile = fileToSave;
    }

    /**
     * @see ModifyAndSave
     */
    public boolean isModified() {
        logger.entering(className, "isModified");
        logger.finest("isModified=" + isModified);
        return isModified;
    }

    public Parameter[] getSharedParameters() {
        return rootSettings.getParameters().getParameter();
    }

    public boolean isSharedParameter(Parameter p) {
        Parameter[] params = rootSettings.getParameters().getParameter();
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = params[i];
            if (parameter == p) {
                return true;
            }
        }
        return false;
    }

    void linkParameter(Parameter parameterToLink, Parameter sharedParameter) {
        logger.entering(className, "linkParameter");

        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(sharedParameter);

        Model parentModel = getParent(parameterToLink);
        parentModel.addParameterLink(newLink);
        Parameter[] params = parentModel.getParameter();
        for (int i = 0; i < params.length; i++) {
            if (params[i] == parameterToLink) {
                parentModel.removeParameter(i);
            }
        }
        fireTreeNodesChanged(parentModel);
    }

    void shareParameter(Parameter parameterToShare) {
        logger.entering(className, "shareParameter");
        Model associatedModel = getParent(parameterToShare);

        // Shared parameters must have one id
        if (parameterToShare.getId() == null) {
            parameterToShare.setId(parameterToShare.getName() + Integer.toHexString(parameterToShare.hashCode()));
        }

        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(parameterToShare);
        newLink.setType(parameterToShare.getType());
        associatedModel.addParameterLink(newLink);

        Parameter[] params = associatedModel.getParameter();

        for (int i = 0; i < params.length; i++) {
            if (params[i] == parameterToShare) {
                associatedModel.removeParameter(i);
            }
        }
        rootSettings.getParameters().addParameter(parameterToShare);

        // if it is the first time that a shared parameters has been added
        if (rootSettings.getParameters().getParameterCount() == 1) {
            fireTreeNodesInserted(this, new Object[]{rootSettings}, new int[]{getIndexOfChild(rootSettings, rootSettings.getParameters())}, new Object[]{rootSettings.getParameters()});
        }
        fireTreeNodesChanged(rootSettings.getParameters());
    }

    private ResultModel getModel(Result r) {
        ResultModel rm = resultToModel.get(r);
        if (rm == null) {
            rm = new ResultModel(this, r);
            resultToModel.put(r, rm);
        }
        return rm;
    }

    /**
     * Call setModified(true) to declare that something internally changed.
     * setModified(false) should be called only after a disk save.
     */
    private void setModified(boolean flag) {
        logger.entering(className, "setModified");
        logger.finest("setModified to " + flag);
        isModified = flag;
        if (isModified) {
            fireUpdate();
        }
    }

    /**
     * This method must be called by anyone that has modified the xml binded
     * document.
     */
    private void fireUpdate() {
        logger.entering(className, "fireUpdate");
        logger.fine("update signal triggered");
        //@fire the model listener
        fireTreeNodesChanged(rootSettings);
        isModified = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param xml DOCUMENT ME!
     */
    public void setLastXml(String xml) {
        logger.entering(className, "setLastXml");
        this.lastXml = xml;
    }

    public String getLastXml() throws Exception {
        logger.entering(className, "getLastXml");

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

    public java.io.File getTempFile(boolean keepResult)
            throws Exception {
        logger.entering(className, "getTempFile");

        java.io.File tmpFile;
        tmpFile = java.io.File.createTempFile("tmpSettings", ".xml");
        // Delete temp file when program exits.
        tmpFile.deleteOnExit();
        saveSettingsFile(tmpFile, keepResult);

        return tmpFile;
    }

    /**
     * This method updates the model using some elements of the given settings.
     * The given setting should have the same model and parameter sructure.
     *  @param s Settings to grab informations into.
     */
    public void updateWithNewSettings(Response newResponse) {
        Settings newSettings = UtilsClass.getSettings(newResponse);
        // we can ignore all but:
        //  parameters, result
        setModified(true);

        // update shared parameters
        Parameters params = newSettings.getParameters();
        rootSettings.setParameters(params);
        // @todo handle fireTreeEvent(...) update

        // update parameters of every targets
        Target[] newTargets = newSettings.getTargets().getTarget();
        Target[] targets = rootSettings.getTargets().getTarget();
        for (int i = 0; i < newTargets.length; i++) {
            Target newTarget = newTargets[i];
            Model[] newModels = newTarget.getModel();
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < newModels.length; j++) {
                Model newModel = newModels[j];
                Parameter[] newParameters = newModel.getParameter();
                Model model = models[j];
                model.setParameter(newParameters);
            }
        }

        // update results
        Result[] newResults = newSettings.getResults().getResult();

        boolean firstResult = false;
        // if we are receiving the first result(s)
        if (newResults.length > 0 && rootSettings.getResults().getResultCount() == 0) {
            firstResult = true;
        }
        int nbResults = rootSettings.getResults().getResultCount();
        for (int i = 0; i < newResults.length; i++) {
            Result result = newResults[i];
            if (result != null) {
                rootSettings.getResults().addResult(result);
                ResultModel r = getModel(result);
                fireTreeNodesInserted(this,
                        new Object[]{rootSettings, rootSettings.getResults()},
                        new int[]{i + nbResults},
                        new Object[]{r});
            } else {
                logger.warning("found null result while updating with new settings");
            }
        }
        if (firstResult) {
            fireTreeNodesInserted(this,
                    new Object[]{rootSettings},
                    new int[]{getIndexOfChild(rootSettings, rootSettings.getResults())},
                    new Object[]{rootSettings.getResults()});
        }
    }

    // respond to ModifyAndSaveObject interface
    public void save() {
        logger.entering(className, "save");

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
    public java.awt.Component getComponent() {
        return MFGui.getInstance();
    }

    public void updateOiTargetList() {
        oiTargets.removeAllElements();
        File[] files = rootSettings.getFiles().getFile();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            for (int j = 0; j < f.getOitargetCount(); j++) {
                String t = f.getOitarget(j).getTarget();
                if (oiTargets.getIndexOf(t) < 0) {
                    oiTargets.addElement(t);
                    addFileListModelForOiTarget(f.getOitarget(j), f);
                }
            }
        }
    }

    public void setRootSettings(Settings newRootModel) {
        logger.entering(className, "setRootSettings");
        rootSettings = newRootModel;

        // clear list of files, oitargets, target and fileListModels
        // then add settings ones
        allFilesListModel.clear();
        fileListModels.clear();
        File[] files = rootSettings.getFiles().getFile();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            allFilesListModel.addElement(file);
        }
        updateOiTargetList();
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

        // build model for every result
        if (rootSettings.getResults() != null) {
            Result[] results = rootSettings.getResults().getResult();
            for (int i = 0; i < results.length; i++) {
                getModel(results[i]);
            }
        }

        String desc = "This rootSettings contains " + rootSettings.getFiles().getFileCount() +
                " files," + rootSettings.getTargets().getTargetCount() + " targets," + "User info: [ " +
                rootSettings.getUserInfo() + " ]";

        if (rootSettings.getResults() != null) {
            desc = desc + " with results section";
        }

        logger.fine(desc);

        // fire general change event
        fireTreeStructureChanged(rootSettings);

    }

    public Settings getRootSettings() {
        logger.entering(className, "getRootSettings");

        return rootSettings;
    }

    public Target getParent(Model child) {
        Target[] targets = rootSettings.getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                if (model == child) {
                    return target;
                }
            }
        }
        logger.warning("Can't find parent of :" + child);
        new Throwable("Can't find parent of :" + child).printStackTrace();
        return null;
    }

    public Model getParent(Parameter child) {
        Target[] targets = rootSettings.getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                Parameter[] parameters = model.getParameter();
                for (int k = 0; k < parameters.length; k++) {
                    Parameter parameter = parameters[k];
                    if (parameter == child) {
                        return model;
                    }
                }
            }
        }
        logger.warning("Can't find parent of :" + child);
        new Throwable("Can't find parent of :" + child).printStackTrace();
        return null;
    }

    private void addFileListModelForOiTarget(Oitarget target, File file) {
        logger.entering(className, "addFileListModelForTarget");

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

    public ListModel getFileListModelForOiTarget(String oiTargetName) {
        logger.entering(className, "getFileListModelForTarget");

        // we use target name as key
        String key = oiTargetName.trim();

        return (ListModel) fileListModels.get(key);
    }

    // @todo place this method into fr.jmmc.mf.util
    public void addFile(java.io.File fileToAdd) throws Exception {
        logger.entering(className, "addFile");

        Files files = rootSettings.getFiles();
        // The file must be one oidata file
        String fitsFileName = fileToAdd.getAbsolutePath();
        File newFile = new File();
        newFile.setName(fitsFileName);
        newFile.setId(getNewFileId());
        if (checkFile(newFile)) {
            // make shorter filename (this line must be kept after checkFile,
            // because is must be retrieved using full qualified name)
            newFile.setName(fileToAdd.getName());
            allFilesListModel.addElement(newFile);
            int idx = files.getFileCount();
            rootSettings.getFiles().addFile(newFile);
            updateOiTargetList();
            logger.info("'" + fitsFileName + "' oifile added to file list");
            setModified(true);
            fireTreeNodesInserted(this,
                    new Object[]{rootSettings, files},
                    new int[]{idx},
                    new Object[]{newFile});
        /*@todo return a invalidFormat exception instead of Simple exception
        }catch(Exception exc){
        logger.warning("Rejecting non valid file '"+fileToAdd.getAbsolutePath()+"'");
        throw new ...
        }*/
        }
    }

    // @todo place this method into fr.jmmc.mf.util
    public void addFileHref(File bindedFile) throws Exception {
        logger.entering(className, "addFileHref");
        java.io.File realFile = new java.io.File(bindedFile.getName());
        // Create a read-only memory-mapped file
        java.nio.channels.FileChannel roChannel = new java.io.RandomAccessFile(realFile, "r").getChannel();
        java.nio.ByteBuffer roBuf = roChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY,
                0, (int) roChannel.size());
        String b64 = "data:image/fits;base64," +
                new sun.misc.BASE64Encoder().encode(roBuf);
        bindedFile.setHref(b64);
    }

    /* public void addFileOiTargets(File bindedFile) throws Exception {
    logger.entering(className, "addFileOiTarget");
    }*/

    // @todo place this method into fr.jmmc.mf.util
    public boolean checkFile(File bindedFile) throws Exception {
        logger.entering(className, "checkFile");

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
    public void checkSettingsFormat(Settings s) throws Exception {
        logger.entering(className, "checkSettingsFormat");

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
                    ModelFitting.getSharedApplicationDataModel().getProgramVersion());
            setModified(true);
        }

        // next line fix normalize flag with default value to true
        // (castor does not handle this schema feature)
        Target[] targets = s.getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            target.setNormalize(target.getNormalize() || (!target.hasNormalize()));
        }

    }

    /**
     * Write serialisation into given file.
     * @todo place this method into fr.jmmc.mf.util
     * @param keepResult indicates that this file will not get result section. It is used in the runFit action for example.
     */
    public void saveSettingsFile(java.io.File fileToSave, boolean keepResult)
            throws java.io.IOException, org.exolab.castor.xml.MarshalException,
            org.exolab.castor.xml.ValidationException, org.exolab.castor.mapping.MappingException {
        logger.entering(className, "saveSettingsFile");
        Results oldResults = rootSettings.getResults();
        /* replace old result section by a new empty one according keepResult parameter
        oldResults are restored at end of the method */
        if (keepResult == false) {
            rootSettings.setResults(new Results());
        }

        logger.fine("start setting file writting");
        // Read a File to unmarshal from
        java.io.FileWriter writer = new java.io.FileWriter(fileToSave);

        // Do marshalling
        URL mappingURL = this.getClass().getClassLoader().getResource("fr/jmmc/mf/gui/mapping.xml");
        logger.fine("Using mapping file :" + mappingURL);
        Marshaller marshaller = new Marshaller(writer);
        // old simple code sometimes break xml elements order then use a mapping file
        Mapping mapping = new Mapping();
        mapping.loadMapping(mappingURL);
        marshaller.setMapping(mapping);
        rootSettings.validate();
        marshaller.marshal(rootSettings);
        writer.flush();
        logger.fine("finish file writting into " + fileToSave);

        if (fileToSave.equals(associatedFile)) {
            setModified(false);
        }
        if (keepResult == false) {
            rootSettings.setResults(oldResults);
        }
        if (rootSettings.getResults() == null) {
            rootSettings.setResults(new Results());
        }
    }

    // 
    /**
     * Register the given model array as supported models.
     * @param models array of models to be supported
     */
    public void setSupportedModels(Model[] models) {
        logger.entering(className, "setSupportedModels");
        // update list of supported models
        supportedModels.clear();
        // update MVC model of available models checkboxes
        supportedModelsModel.removeAllElements();

        for (int i = 0; i < models.length; i++) {
            Model newModel = models[i];
            supportedModelsModel.addElement(newModel);
            logger.fine("Adding supported model:" + newModel.getType());
            supportedModels.put(newModel.getType(), newModel);
        }
    }

    /**
     * Get the Model associated to the given model name
     *
     * @param type model type
     *
     * @return associated Model object
     */
    public Model getSupportedModel(String type) {
        logger.entering(className, "getSupportedModel");
        return (Model) supportedModels.get(type);
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

    //////////////// Fire events //////////////////////////////////////////////
    protected void fireTreeStructureChanged(Object highlyChangedNode) {
        logger.entering(className, "fireTreeStructureChanged", highlyChangedNode);
        TreeModelEvent e;
        e = new TreeModelEvent(this, new Object[]{highlyChangedNode});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeStructureChanged(e);
        }
        delegateObservable.notifyObservers();
    }

    protected void fireTreeNodesChanged(Object changedNode) {
        logger.entering(className, "fireTreeNodesChanged");
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{changedNode});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
        delegateObservable.notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
            int[] childIndices,
            Object[] children) {
        logger.entering(className, "fireTreeNodesInserted", new Object[]{source, path, children});
        TreeModelEvent e = new TreeModelEvent(source, path,
                childIndices, children);
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesInserted(e);
        }
        delegateObservable.notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
            int[] childIndices,
            Object[] children) {
        TreeModelEvent e = new TreeModelEvent(source, path,
                childIndices, children);
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesRemoved(e);
        }
        delegateObservable.notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
            int[] childIndices,
            Object[] children) {
        TreeModelEvent e = new TreeModelEvent(source, path,
                childIndices, children);
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
        delegateObservable.notifyObservers();
    }

    //////////////// TreeModel interface implementation ///////////////////////
    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
        logger.entering(className, "addTreeModelListener");
        treeModelListeners.addElement(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
        logger.entering(className, "getChild(" + parent + "," + index + ")");
        if (parent instanceof TreeNode) {
            return ((TreeNode) parent).getChildAt(index);
        } else if (parent instanceof Settings) {
            Settings s = (Settings) parent;
            // select which settings child it is
            if (index == 0) {
                return s.getFiles();
            }
            if (index == 1) {
                return s.getTargets();
            }
            if (index == 2) {
                if (s.getParameters() != null) {
                    if (s.getParameters().getParameterCount() != 0) {
                        return s.getParameters();
                    }
                }
            }
            // if given index was 2 and no parameter is shared then return results
            // if given index was 3 and no parameter is shared then return plotContainer
            if (s.getParameters() != null && s.getParameters().getParameterCount() == 0) {
                index++;
            }
            if (index == 3) {
                if (s.getResults() != null && s.getResults().getResultCount() != 0) {
                    return s.getResults();
                }
                index++;
            }
            if (index == 4) {
                return plotContainerNode;
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
        } else if (parent instanceof Results) {
            Result r = ((Results) parent).getResult(index);
            return getModel(r);
        }
        logger.warning("child n=" + index + " of " + parent + " is not handled");
        return "unknown " + parent + ".child[" + index + "]";
    }

    /**
     * Returns the number of children of parent.
     */
    public int getChildCount(Object parent) {
        //logger.entering(""+this.getClass(), "getChildCount");
        if (parent instanceof TreeNode) {
            return ((TreeNode) parent).getChildCount();
        } else if (parent instanceof Settings) {
            Settings s = (Settings) parent;
            // return files, targets, [parameters][results]plots
            int i = 3;
            if (s.getParameters() != null) {
                if (s.getParameters().getParameterCount() >= 1) {
                    i++;
                }
            }
            if (s.getResults() != null) {
                if (s.getResults().getResultCount() != 0) {
                    i++;
                }
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
        } else if (parent instanceof Results) {
            Results r = (Results) parent;
            return r.getResultCount();
        } else {
            return 1;
        }
    }

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        logger.entering(className, "getIndexOfChild");
        if ((parent == null) || (child == null)) {
            return -1;
        }

        if (parent instanceof TreeNode && child instanceof TreeNode) {
            return ((TreeNode) parent).getIndex((TreeNode) child);
        } else if (parent == rootSettings) {
            int idx = 0;
            if (child == rootSettings.getFiles()) {
                return idx;
            }
            idx++;
            if (child == rootSettings.getTargets()) {
                return idx;
            }
            idx++;

            if (child == rootSettings.getParameters()) {
                return idx;
            }
            if (rootSettings.getParameters() != null) {
                if (rootSettings.getParameters().getParameterCount() >= 1) {
                    idx++;
                }
            }

            if (child == rootSettings.getResults()) {
                return idx;
            }

            if (rootSettings.getResults() != null) {
                if (rootSettings.getResults().getResultCount() >= 1) {
                    idx++;
                }
            }

            if (child == plotContainerNode) {
                return idx;
            }

            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getFiles()) {
            File[] elements = rootSettings.getFiles().getFile();
            for (int i = 0; i <
                    elements.length; i++) {
                File element = elements[i];
                if (element == child) {
                    return i;
                }
            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getTargets()) {
            Object[] elements = rootSettings.getTargets().getTarget();
            for (int i = 0; i <
                    elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }
            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getParameters()) {
            Object[] elements = rootSettings.getParameters().getParameter();
            for (int i = 0; i <
                    elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }

            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getResults()) {
            Object[] elements = rootSettings.getResults().getResult();
            for (int i = 0; i <
                    elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }
            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else {
            // Search for fileLinks or models children into Target
            Target[] targets = rootSettings.getTargets().getTarget();
            for (int i = 0; i <
                    targets.length; i++) {
                Target target = targets[i];
                if (parent == target) {

                    Object[] all = new Object[target.getFileLinkCount() + target.getModelCount()];
                    Object[] elements = target.getFileLink();
                    System.arraycopy(elements, 0, all, 0, target.getFileLinkCount());
                    elements =
                            target.getModel();
                    System.arraycopy(elements, 0, all, target.getFileLinkCount(), target.getModelCount());

                    for (int j = 0; j <
                            all.length; j++) {
                        if (child == all[j]) {
                            return j;
                        }
                    }
                    logger.warning("parent:" + parent + " does not seem to contain:" + child);
                    return -1;
                }
            }
        }
        logger.warning("missing code for parent=" + parent + " and child=" + child);
        return 0;
    }

    /**
     * Returns the root of the tree.
     */
    public Object getRoot() {
        logger.entering(className, "getRoot");
        return rootSettings;
    }

    /**
     * Returns true if node is a leaf.
     */
    public boolean isLeaf(Object node) {
        if (node == plotContainerNode) {
            return false;
        }
        if (node instanceof TreeNode) {
            return ((TreeNode) node).isLeaf();
        } else if ((node instanceof Settings) || (node instanceof Files) || (node instanceof Targets) ||
                (node instanceof Target) || (node instanceof Results)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        logger.entering(className, "removeTreeModelListener");
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        logger.entering(className, "valueForPathChanged");

        Object modifiedObject = path.getLastPathComponent();
        /*Settings m = (Settings) path.getLastPathComponent();
        m.setName("" + newValue);
         */
        new FeedbackReport();
        fireTreeNodesChanged(modifiedObject);
    }

    public String toString() {
        return className + " " + getAssociatedFilename();

    }
}
