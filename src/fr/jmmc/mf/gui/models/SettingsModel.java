package fr.jmmc.mf.gui.models;

import fr.jmmc.mf.gui.*;
import fr.jmmc.mcs.gui.MessagePane;
import fr.jmmc.mcs.util.FileUtils;
import fr.jmmc.mcs.util.MimeType;

import fr.jmmc.mcs.util.ObservableDelegate;
import fr.jmmc.mcs.util.Urls;
import fr.jmmc.mf.models.File;
import fr.jmmc.mf.models.FileLink;
import fr.jmmc.mf.models.Files;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Oitarget;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Parameters;
import fr.jmmc.mf.models.Residual;
import fr.jmmc.mf.models.Residuals;
import fr.jmmc.mf.models.Response;
import fr.jmmc.mf.models.Result;
import fr.jmmc.mf.models.Results;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import fr.jmmc.mf.models.Targets;
import fr.jmmc.oitools.model.OIFitsChecker;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

//import fr.jmmc.oifits.validator.GUIValidator;
import fr.jmmc.oitools.model.OIFitsFile;
import fr.jmmc.oitools.model.OIFitsLoader;
import fr.jmmc.oitools.model.OITarget;
import java.util.Enumeration;
import java.util.Observer;
import java.util.logging.Level;
import fr.nom.tam.fits.FitsException;
import java.io.Reader;
import java.net.URL;

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
    private static Hashtable supportedModels = new Hashtable();
    /** Combobox model of supported models */
    private static DefaultComboBoxModel supportedModelsModel = new DefaultComboBoxModel();
    public final static String className = SettingsModel.class.getName();
    /** Class logger */
    static Logger logger = Logger.getLogger(className);
    /** Vector of objects that want to listen tree modification */
    private Vector<TreeModelListener> treeModelListeners = new Vector();
    /** Reference onto the main castor root object */
    private Settings rootSettings = null;
    /** User preferences */
    protected static Preferences myPreferences = Preferences.getInstance();
    /** Hashtable used to return a ListModel object for the selected files of a dedicated target */
    private Hashtable<String, ListModel> fileListModels = new Hashtable();
    public DefaultListModel allFilesListModel;
    public DefaultComboBoxModel oiTargets;
    private DefaultListModel targetListModel;
    private DefaultListModel parameterListModel;
    private DefaultComboBoxModel targetComboBoxModel;
    private DefaultComboBoxModel parameterComboBoxModel;
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
    // Use a delegate that will trigger listener on this model changes
    private ObservableDelegate observableDelegate;
    /** store the resultModelIndex returned by getNewResultModelIndex() */
    private int resultModelIndex;

    /**
     * Creates a new empty SettingsModel object.
     */
    public SettingsModel() {
        logger.entering(className, "SettingsModel()");
        init(null);
    }

    /**
     * Creates a new empty SettingsModel object.
     */
    public SettingsModel(java.io.File fileToLoad)  throws IllegalStateException {
        logger.entering(className, "SettingsModel", fileToLoad);
        try {
            init(new java.io.FileReader(fileToLoad));
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
        associatedFile = fileToLoad;
    }
    
    public SettingsModel(java.net.URL urlToLoad) throws IllegalStateException {
        logger.entering(className, "SettingsModel", urlToLoad);
        try {
            init(new java.io.InputStreamReader(urlToLoad.openStream()));
            associatedFile = new java.io.File(urlToLoad.getFile());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public SettingsModel(String url) throws IllegalStateException {
        try {
            URL urlToLoad = Urls.parseURL(url);
            
            init(new java.io.InputStreamReader(urlToLoad.openStream()));

            associatedFile = new java.io.File(urlToLoad.getFile());

        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public final void init(Reader reader) {
        logger.entering(className, "init");

        observableDelegate = new ObservableDelegate(this);

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

        if (reader != null) {
            Settings newModel;
            try {
                newModel = (Settings) UtilsClass.unmarshal(Settings.class, reader);
            } catch (IllegalStateException ilse) {
                // try to extract settings from a response file as fallback
                Response r = (Response) UtilsClass.unmarshal(Response.class, reader);
                newModel = UtilsClass.getSettings(r);
            }
            checkSettingsFormat(newModel);
            setRootSettings(newModel);
            setModified(false);
        }
    }

    /**
     * Return the supported models. This method autoatically make a request to get server
     * list if the current list is empty.
     * @return the supportedModelsModel
     */
    public static DefaultComboBoxModel getSupportedModelsModel() {
        // Fill modelTypeComboBox model if empty
        if (supportedModelsModel.getSize() < 1) {
            Response r = ModelFitting.execMethod("getModelList", null);
            // Search model into return result
            Model newModel = UtilsClass.getModel(r);
            // update list of supported models
            supportedModels.clear();
            // update MVC model of available models checkboxes
            supportedModelsModel.removeAllElements();
            Model[] models = newModel.getModel();
            for (int i = 0; i < models.length; i++) {
                Model model = models[i];
                supportedModelsModel.addElement(model);
                logger.fine("Adding supported model:" + model.getType());
                supportedModels.put(model.getType(), model);
            }
        }
        return supportedModelsModel;
    }

    /** Add a new FileLink associated to the given File.
     *
     * @param target parent container for given file
     * @param file file to use for fileLink addition into the target
     */
    public void addFile(Target target, File file) {
        logger.entering(className, "addFile", new Object[]{target, file});
        FileLink fileLink = new FileLink();
        fileLink.setFileRef(file);
        target.addFileLink(fileLink);
        fireTreeNodesInserted(
                new Object[]{rootSettings, rootSettings.getTargets(), target},
                target.getFileLinkCount() - 1,
                fileLink);
    }

    public void removeFile(Target target, File file) {
        logger.entering(className, "removeFile", new Object[]{target, file});
        FileLink[] links = target.getFileLink();

        for (int i = 0; i < links.length; i++) {
            FileLink fileLink = links[i];
            if (fileLink.getFileRef() == file) {
                target.removeFileLink(fileLink);
                fireTreeNodesRemoved(this,
                        new Object[]{rootSettings, rootSettings.getTargets(), target},
                        i, fileLink);
            }
        }
    }

    /**
     *  Add observer to be simply nofified by model content changes.
     */
    public void addObserver(Observer observer) {
        logger.entering(className, "addObserver", observer);
        observableDelegate.addObserver(observer);
    }

    /**
     * Notify the observers of this model.
     * As been put public to
     */
    public void notifyObservers() {
        observableDelegate.notifyObservers();
    }

    public void setNormalize(Target target, boolean flag) {
        logger.entering(className, "setNormalize", new Object[]{target, flag});
        target.setNormalize(flag);
        fireTreeNodesChanged(new Object[]{rootSettings, rootSettings.getTargets()},
                getIndexOfChild(rootSettings.getTargets(), target),
                target);
    }

    /**
     *
     * @param target
     * @param residualModuleName name of residual module : VIS2 VISamp VISphi T3phi T3Amp
     * @param residualModuleValue default,
     */
    public void setResiduals(Target target, String visAmpValue,
            String visPhiValue, String vis2Value, String t3AmpValue,
            String t3PhiValue) {
        logger.entering(className, "setResidual", new Object[]{target,
                    visAmpValue, visPhiValue, vis2Value, t3AmpValue, t3PhiValue});

        Residuals residuals = new Residuals();

        String[] moduleNames = new String[]{"VISamp", "VISphi", "VIS2", "T3amp", "T3phi"};
        String[] moduleValues = new String[]{visAmpValue, visPhiValue, vis2Value, t3AmpValue, t3PhiValue};

        for (int i = 0; i < moduleValues.length; i++) {
            String val = moduleValues[i];
            if (val != null) {
                Residual r = new Residual();
                r.setName(moduleNames[i]);
                r.setType(val);
                residuals.addResidual(r);
            }
        }

        target.setResiduals(residuals);
        fireTreeNodesChanged(new Object[]{rootSettings, rootSettings.getTargets()},
                getIndexOfChild(rootSettings.getTargets(), target),
                target);
    }

    public void setPlotPanel(PlotPanel plotPanel) {
        logger.entering(className, "setPlotPanel", plotPanel);
        plotContainerNode.setUserObject(plotPanel);
    }

    public void addPlot(FrameTreeNode newPlotNode) {
        logger.entering(className, "addPlot", newPlotNode);
        plotContainerNode.add(newPlotNode);
        fireTreeNodesInserted(new Object[]{rootSettings, plotContainerNode},
                plotContainerNode.getChildCount() - 1,
                newPlotNode);
        setSelectionPath(new TreePath(new Object[]{rootSettings, plotContainerNode, newPlotNode}));
    }

    /**
     * Replace one model of the given target.
     * The model should keep as many parameters as possible.
     * @param parentTarget
     * @param newModel
     */
    public void replaceModel(Target parentTarget, Model currentModel, Model newModel) {
        logger.entering(className, "replaceModel", new Object[]{parentTarget, newModel});

        parentTarget.removeModel(currentModel);

        int modelIdx = getNewModelIdx(newModel.getType());
        newModel.setName(newModel.getType() + modelIdx);
        // try to recover previous parameters
        Parameter[] currentModelParams = currentModel.getParameter();
        Parameter[] newModelParams = newModel.getParameter();
        for (int i = 0; i < newModelParams.length; i++) {
            Parameter np = newModelParams[i];
            np.setName(getNewParamName(np.getName(), modelIdx));
            for (int j = 0; j < currentModelParams.length; j++) {
                Parameter cp = currentModelParams[j];
                if (matchType(np.getType(), cp.getType())) {
                    np.setValue(cp.getValue());
                    np.setHasFixedValue(cp.getHasFixedValue());
                    if (cp.hasScale()) {
                        np.setScale(cp.getScale());
                    }
                    if (cp.hasMaxValue()) {
                        np.setMaxValue(cp.getMaxValue());
                    }
                    if (cp.hasMinValue()) {
                        np.setMinValue(cp.getMinValue());
                    }
                    if (np.getType().equals(cp.getType())) {
                        // the name is not always copyed because min_diameter matches with diameter
                        np.setName(cp.getName());
                    }
                }
            }
        }
        // try to recover previous shared parameters
        ParameterLink[] currentParameterLinks = currentModel.getParameterLink();
        for (int i = 0; i < newModelParams.length; i++) {
            Parameter np = newModelParams[i];
            for (int j = 0; j < currentParameterLinks.length; j++) {
                ParameterLink cpl = currentParameterLinks[j];
                if (np.getType().equals(cpl.getType())) {
                    newModel.removeParameter(np);
                    newModel.addParameterLink(cpl);
                }
            }
        }

        //update content of contentModels
        for (int i = 0; i < currentModelParams.length; i++) {
            Parameter p = currentModelParams[i];
            parameterComboBoxModel.removeElement(p);
            parameterListModel.removeElement(p);
        }
        for (int i = 0; i < newModelParams.length; i++) {
            Parameter p = newModelParams[i];
            parameterComboBoxModel.addElement(p);
            parameterListModel.addElement(p);
        }

        setModified(true);
        // add the new element to current target at the same position
        int childPosition = 0;
        Model[] models = parentTarget.getModel();
        for (int i = 0; i < models.length; i++) {
            Model model = models[i];
            if (model == currentModel) {
                childPosition = i;
            }
        }

        parentTarget.addModel(childPosition, newModel);
        //update treenode and ask to update viewer of new model
        fireTreeNodesChanged(new Object[]{rootSettings, rootSettings.getTargets(), parentTarget}, getIndexOfChild(parentTarget, newModel), newModel);
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), parentTarget, newModel}));
    }

    /** try to tell if the data of the old param can be copied to new Param
     * according to both names. If they ends with te same string after the '_'
     * character, then this method returns true.
     * @return true if both string ends with same keyword, else returns false
     */
    private boolean matchType(String oldParamType, String newParamType) {
        int idx;
        idx = oldParamType.lastIndexOf('_');
        if (idx >= 0) {
            oldParamType = oldParamType.substring(idx + 1);
        }
        idx = newParamType.lastIndexOf('_');
        if (idx >= 0) {
            newParamType = newParamType.substring(idx + 1);
        }


        if (newParamType.equals(oldParamType)) {
            return true;
        }
        if (newParamType.contains(oldParamType)) {
            return true;
        }

        return false;
    }

    /**
     * Replace one model of the given target.
     * The model should keep as many parameters as possible.
     * @param parentTarget
     * @param newModel
     */
    public void replaceModel(Model currentModel, Model newModel) {
        replaceModel(getParent(currentModel), currentModel, newModel);
    }

    /**
     * Add the given model to the given target.
     * The model will change parameter name
     * @param parentTarget
     * @param newModel
     */
    public void addModel(Target parentTarget, Model newModel) {
        logger.entering(className, "addModel", new Object[]{parentTarget, newModel});

        // force another name with unique position
        String type = newModel.getType();
        int modelIdx = getNewModelIdx(type);
        newModel.setName(type + modelIdx);

        boolean firstModel = parentTarget.getModelCount() == 0;

        // and change parameters name also
        Parameter[] params = newModel.getParameter();
        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            // init some default parameters depending on first place
            if (firstModel) {
                if (p.getName().equals("x") || p.getName().equals("y")) {
                    p.setHasFixedValue(true);
                }
            }
            if (p.getName().equals("flux_weight")) {
                p.setValue(1);
            }
            p.setName(getNewParamName(p.getName(), modelIdx));
            parameterComboBoxModel.addElement(p);
            parameterListModel.addElement(p);
        }

        setModified(true);

        // add the new element to current target
        parentTarget.addModel(newModel);
        fireTreeNodesInserted(new Object[]{rootSettings, rootSettings.getTargets(), parentTarget}, getIndexOfChild(parentTarget, newModel), newModel);
    }

    public void removeModel(Target parentTarget, Model oldModel) {
        logger.entering(className, "removeModel", new Object[]{parentTarget, oldModel});
        if(parentTarget==null){
            logger.warning("Something strange appears while trying to remove one model without associated target");
            return;
        }
        Model[] models = parentTarget.getModel();
        setModified(true);
        for (int i = 0; i < models.length; i++) {
            Model model = models[i];
            if (model == oldModel) {
                Parameter[] params = model.getParameter();
                for (int j = 0; j < params.length; j++) {
                    Parameter p = params[j];
                    parameterComboBoxModel.removeElement(p);
                    parameterListModel.removeElement(p);
                }

                ParameterLink[] paramLinks = model.getParameterLink();
                for (int j = 0; j < paramLinks.length; j++) {
                    ParameterLink parameterLink = paramLinks[j];
                    Model[] ms = UtilsClass.getSharedParameterOwners(this, (Parameter) parameterLink.getParameterRef());
                    if (ms.length <= 1) {
                        parameterComboBoxModel.removeElement(parameterLink.getParameterRef());
                        parameterListModel.removeElement(parameterLink.getParameterRef());
                        rootSettings.getParameters().removeParameter((Parameter) parameterLink.getParameterRef());
                        logger.fine("removing shared parameter : " + parameterLink.getParameterRef());
                    }
                }

                int idx = getIndexOfChild(parentTarget, model);
                fireTreeNodesRemoved(this,
                        new Object[]{rootSettings, rootSettings.getTargets(), parentTarget},
                        idx,
                        oldModel);
                parentTarget.removeModel(models[i]);
                setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), parentTarget}));
            }
        }
    }

    public void removeModel(Model oldModel) {
        logger.entering(className, "removeModel", oldModel);
        removeModel(getParent(oldModel), oldModel);
    }

    public Target addTarget(String targetIdent) {
        logger.entering(className, "addTarget", targetIdent);
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

        fireTreeNodesInserted(new Object[]{rootSettings, rootSettings.getTargets()}, indice, newTarget);
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), newTarget}));

        return newTarget;
    }

    public void removeTarget(Target oldTarget) {
        logger.entering(className, "removeTarget", oldTarget);
        setModified(true);

        int indice = getIndexOfChild(rootSettings.getTargets(), oldTarget);
        targetListModel.removeElement(oldTarget);
        rootSettings.getTargets().removeTarget(oldTarget);
        fireTreeNodesRemoved(this,
                new Object[]{rootSettings, rootSettings.getTargets()},
                indice, oldTarget);
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets()}));
    }

    public void removeFile(File oldFile) {
        logger.entering(className, "removeFile", oldFile);
        setModified(true);

        if (UtilsClass.containsFile(this, oldFile)) {
            logger.fine("file " + oldFile + " has not been removed because it is used by one or more targets");
            return;
        }

        int indice = getIndexOfChild(rootSettings.getFiles(), oldFile);
        allFilesListModel.removeElement(oldFile);
        rootSettings.getFiles().removeFile(oldFile);
        fireTreeNodesRemoved(this,
                new Object[]{rootSettings, rootSettings.getFiles()},
                indice, oldFile);
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getFiles()}));
        updateOiTargetList();
    }

    public void removeFrame(FrameTreeNode frameNode) {
        logger.entering(className, "removeFrame", frameNode);
        // actually only plotContainer or Result can have such elements as child
        int idx = plotContainerNode.getIndex(frameNode);
        if (idx >= 0) {
            plotContainerNode.remove(frameNode);
            fireTreeNodesRemoved(this,
                    new Object[]{rootSettings, plotContainerNode},
                    idx, frameNode);
            setSelectionPath(new TreePath(new Object[]{rootSettings, plotContainerNode}));
            return;
        }
        Enumeration<ResultModel> resModels = resultToModel.elements();
        while (resModels.hasMoreElements()) {
            ResultModel resModel = resModels.nextElement();
            idx = resModel.getIndex(frameNode);
            if (idx >= 0) {
                resModel.remove(frameNode);
                fireTreeNodesRemoved(this,
                        new Object[]{rootSettings, rootSettings.getResults(), resModel},
                        idx, frameNode);
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
        logger.entering(className, "isFrameSelectionEmpty");
        TreePath[] treePaths = getSelectionPaths();
        if (treePaths == null) {
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
        logger.entering(className, "isSelectionRemovable");
        TreePath[] treePaths = getSelectionPaths();
        if (treePaths == null) {
            return false;
        }
        for (int i = 0; i < treePaths.length; i++) {
            TreePath treePath = treePaths[i];
            Object object = treePath.getLastPathComponent();
            if (object instanceof ResultModel
                    || object instanceof Target
                    || object instanceof ResultModel
                    || object instanceof Model
                    || object instanceof FrameTreeNode) {
                return true;
            }
            if (object instanceof File) {
                return !UtilsClass.containsFile(this, (File) object);
            }
        }
        return false;
    }

    public void removeTreeSelection() {
        logger.entering(className, "removeTreeSelection");

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
                        rootSettings.getResults().removeResult(result);
                        fireTreeNodesRemoved(this,
                                new Object[]{rootSettings, rootSettings.getResults()},
                                j, lastPathComponent);
                        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getResults()}));
                    }
                }
            } else if (lastPathComponent instanceof Target) {
                removeTarget((Target) lastPathComponent);
            } else if (lastPathComponent instanceof Model) {
                removeModel((Model) lastPathComponent);
            } else if (lastPathComponent instanceof FrameTreeNode) {
                removeFrame((FrameTreeNode) lastPathComponent);
            } else if (lastPathComponent instanceof File) {
                removeFile((File) lastPathComponent);
            } else {
                logger.warning("No code implemented to remove : " + lastPathComponent.getClass());
            }
        }

    }

    public void toggleSelectedFrames() {
        logger.entering(className, "toggleSelectedFrames");
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

    /** Tell if the inner model is well filled */
    public boolean isValid() {
        logger.entering(className, "isValid");
        boolean isValid = rootSettings.isValid();
        logger.finest("isValid=" + isValid);
        return isValid;
    }

    /** Returns a new uniq file Id */
    public String getNewFileId() {
        logger.entering(className, "getNewFileId");
        Vector<String> ids = new Vector();
        File[] files = rootSettings.getFiles().getFile();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            ids.add(file.getId());
        }
        return "fileId" + getNewId("fileId", ids, 1);
    }

    /** Returns a new id to build a new uniq Model Name by suffixing given prefix */
    public int getNewModelIdx(String prefix) {
        logger.entering(className, "getNewModelName", prefix);

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
        return getNewId(prefix, ids, 1);
    }

    /** Returns a new uniq param name with a preferenced suffixe idx*/
    public String getNewParamName(String prefix, int suffix) {
        logger.entering(className, "getNewParamName", new Object[]{prefix, suffix});
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
        return prefix + getNewId(prefix, ids, suffix);
    }

    /** Returns a new uniq param name */
    public String getNewParamName(String prefix) {
        return getNewParamName(prefix, 1);
    }

    public void setModelName(Model model, String newName) {
        logger.entering(className, "setModelName", new Object[]{model, newName});
        model.setName(newName);
        Target parentTarget = getParent(model);
        fireTreeNodesChanged(new Object[]{rootSettings},
                getIndexOfChild(rootSettings.getTargets(), parentTarget),
                model);
    }

    /**
     * Return a new id according to the given prefix, starting index and list of previous ids.
     * @param prefix prefix of the new id
     * @param firstId staring index of new id
     * @param previousIds list of previous ids
     * @return the new id
     */
    protected int getNewId(String prefix, Vector<String> previousIds, int firstId) {
        String newId = prefix + firstId;
        while (previousIds.contains(newId)) {
            firstId++;
            newId = prefix + firstId;
        }
        return firstId;
    }

    /**
     * Return the filename that could be used to store settings into.
     * @return the filename
     */
    public String getAssociatedFilename() {
        logger.entering(className, "getAssociatedFilename");
        if (associatedFile == null) {
            return "Untitled." + MimeType.LITPRO_SETTINGS.getExtension();
        }
        return associatedFile.getName();
    }

    /**
     *  Tell to the model that given file is used by user to store its data
     * @param fileToSave
     */
    public void setAssociatedFile(java.io.File fileToSave) {
        logger.entering(className, "setAssociatedFile", fileToSave);
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
        logger.entering(className, "getSharedParameters");
        return rootSettings.getParameters().getParameter();
    }

    public boolean isSharedParameter(Parameter p) {
        logger.entering(className, "isSharedParameter", p);
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
        logger.entering(className, "linkParameter", new Object[]{parameterToLink, sharedParameter});

        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(sharedParameter);
        newLink.setType(parameterToLink.getType());

        Model parentModel = getParent(parameterToLink);
        parameterComboBoxModel.removeElement(parameterToLink);
        parameterListModel.removeElement(parameterToLink);
        parentModel.removeParameter(parameterToLink);
        parentModel.addParameterLink(newLink);

        Target parentTarget = getParent(parentModel);
        fireTreeNodesChanged(new Object[]{rootSettings, parentTarget},
                getIndexOfChild(parentTarget, parentModel), parentModel);
    }

    void shareParameter(Parameter parameterToShare) {
        logger.entering(className, "shareParameter", parameterToShare);
        Model associatedModel = getParent(parameterToShare);

        // Shared parameters must have one id
        if (parameterToShare.getId() == null) {
            parameterToShare.setId(parameterToShare.getName() + Integer.toHexString(parameterToShare.hashCode()));
        }

        // Proceed to exchange of parameters
        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(parameterToShare);
        newLink.setType(parameterToShare.getType());
        associatedModel.addParameterLink(newLink);
        associatedModel.removeParameter(parameterToShare);

        // add shared parameter
        rootSettings.getParameters().addParameter(parameterToShare);
        fireTreeNodesChanged(new Object[]{rootSettings},
                getIndexOfChild(rootSettings, rootSettings.getParameters()),
                rootSettings.getParameters());
    }

    /**
     * Returns one incremented index to be used as label by new ResultModel.
     */
    private int getNewResultModelIndex() {
        return resultModelIndex++;
    }

    private ResultModel getModel(Result r) {
        logger.entering(className, "getModel", r);
        ResultModel rm = resultToModel.get(r);
        if (rm == null) {
            rm = new ResultModel(this, r, getNewResultModelIndex());
            resultToModel.put(r, rm);
        }
        return rm;
    }

    /**
     * Call setModified(true) to declare that something internally changed.
     * setModified(false) should be called only after a disk save.
     */
    private void setModified(boolean flag) {
        logger.entering(className, "setModified", flag);
        isModified = flag;
    }

    public java.io.File getTempFile(boolean keepResult){
        logger.entering(className, "getTempFile", keepResult);
        java.io.File tmpFile;
        tmpFile = FileUtils.getTempFile("tmpSettings", ".xml");
        saveSettingsFile(tmpFile, keepResult);

        return tmpFile;
    }

    /**
     * This method updates the model using some elements of the given settings.
     * The given setting should have the same model and parameter sructure.
     *  @param s Settings to grab informations into.
     */
    public void updateWithNewSettings(Response newResponse) {
        logger.entering(className, "updateWithNewSettings", newResponse);
        Settings newSettings = UtilsClass.getSettings(newResponse);
        // we can ignore all but:
        //  parameters, result
        parameterComboBoxModel.removeAllElements();
        parameterListModel.removeAllElements();
        setModified(true);

        // update shared parameters
        Parameters params = newSettings.getParameters();
        rootSettings.setParameters(params);
        // add shared parameters to the parameter list containers
        Parameter[] sharedParams = newSettings.getParameters().getParameter();
        for (int i = 0; i < sharedParams.length; i++) {
            parameterListModel.addElement(sharedParams[i]);
            parameterComboBoxModel.addElement(sharedParams[i]);
        }
        fireTreeNodesChanged(new Object[]{rootSettings},
                getIndexOfChild(rootSettings, params), params);

        // update parameters and parameterLinks of every targets
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
                for (int k = 0; k < newParameters.length; k++) {
                    Parameter parameter = newParameters[k];
                    parameterComboBoxModel.addElement(parameter);
                    parameterListModel.addElement(parameter);
                }
                Model model = models[j];
                model.setParameter(newParameters);
                model.setParameterLink(newModel.getParameterLink());
                fireTreeNodesChanged(new Object[]{rootSettings, target},
                        getIndexOfChild(target, model), model);
            }
        }

        // update userinfo
        rootSettings.setUserInfo(newSettings.getUserInfo());

        Result[] newResults = newSettings.getResults().getResult();
        // update settings results  with newResults
        for (int i = 0; i < newResults.length; i++) {
            Result newResult = newResults[i];
            if (newResult != null) {
                rootSettings.getResults().addResult(newResult);
                ResultModel r = getModel(newResult);
                r.genPlots(UtilsClass.getResultFiles(newResponse));
                stampLastUserInfo(r);
                fireTreeNodesInserted(new Object[]{rootSettings, rootSettings.getResults()},
                        rootSettings.getResults().getResultCount() - 1,
                        r);
                setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getResults(), r}));
            } else {
                logger.warning("found null result while updating with new settings");
            }
        }
    }

    private void stampLastUserInfo(ResultModel r) {
        String info = rootSettings.getUserInfo();
        String update = info.replaceAll("new fit occured", "\n" + r + " occured");
        rootSettings.setUserInfo(update);
    }

    // respond to ModifyAndSaveObject interface
    public void save() {
        logger.entering(className, "save");

        if (associatedFile != null) {
            saveSettingsFile(associatedFile, false);            
        } else {
            // trigger saveSettingsAction
            MFGui.saveSettingsAction.actionPerformed(null);
        }
    }

    // respond to ModifyAndSaveObject interface
    public java.awt.Component getComponent() {
        logger.entering(className, "getComponent");
        return MFGui.getInstance();
    }

    public void updateOiTargetList() {
        logger.entering(className, "updateOiTargetList");
        oiTargets.removeAllElements();
        File[] files = rootSettings.getFiles().getFile();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            for (int j = 0; j < f.getOitargetCount(); j++) {
                String t = f.getOitarget(j).getTarget();
                if (oiTargets.getIndexOf(t) < 0) {
                    oiTargets.addElement(t);
                }
                addFileListModelForOiTarget(f.getOitarget(j), f);
            }
        }
    }

    public void setRootSettings(Settings newRootModel) {
        logger.entering(className, "setRootSettings", newRootModel);
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
        parameterComboBoxModel.removeAllElements();

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

        // assert that rootSettings get container for shared parameters
        if (rootSettings.getParameters() == null) {
            rootSettings.setParameters(new Parameters());
        } else {
            // add shared parameters to the parameter list containers
            Parameter[] params = rootSettings.getParameters().getParameter();
            for (int i = 0; i < params.length; i++) {
                parameterListModel.addElement(params[i]);
                parameterComboBoxModel.addElement(params[i]);
            }
        }




        // assert that rootSettings get container for results and build model for every result child
        if (rootSettings.getResults() == null) {
            rootSettings.setResults(new Results());
        }
        Result[] results = rootSettings.getResults().getResult();
        for (int i = 0; i < results.length; i++) {
            getModel(results[i]);
        }
        resultModelIndex = results.length;


        String desc = "This rootSettings contains " + rootSettings.getFiles().getFileCount()
                + " files," + rootSettings.getTargets().getTargetCount() + " targets," + "User info: [ "
                + rootSettings.getUserInfo() + " ]";
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
        logger.entering(className, "getParent", child);
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
        logger.warning("Models are:");
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                logger.warning("Models are:" + model);
            }
        }
        logger.log(Level.WARNING, "Can't find parent of : " + child, new Throwable());

        return null;
    }

    public Model getParent(Parameter child) {
        logger.entering(className, "getParent", child);
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
        logger.log(Level.WARNING, "Can't find parent of : " + child, new Throwable());

        return null;
    }

    public Model getParent(ParameterLink child) {
        logger.entering(className, "getParent", child);
        Target[] targets = rootSettings.getTargets().getTarget();
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                ParameterLink[] parameterLinks = model.getParameterLink();
                for (int k = 0; k < parameterLinks.length; k++) {
                    ParameterLink parameterLink = parameterLinks[k];
                    if (parameterLink == child) {
                        return model;
                    }
                }
            }
        }
        logger.log(Level.WARNING, "Can't find parent of : " + child, new Throwable());

        return null;
    }

    private void addFileListModelForOiTarget(Oitarget target, File file) {
        logger.entering(className, "addFileListModelForTarget", new Object[]{target, file});

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
            logger.fine("file " + file.getName() + " already registered to listmodel for '" + key
                    + "'");
        }
    }

    public ListModel getFileListModelForOiTarget(String oiTargetName) {
        logger.entering(className, "getFileListModelForTarget", oiTargetName);
        // we use target name as key
        String key = oiTargetName.trim();
        return (ListModel) fileListModels.get(key);
    }

    // @todo place this method into fr.jmmc.mf.util
    // and make it much much simpler
    public void addFile(java.io.File fileToAdd) {
        logger.entering(className, "addFile", fileToAdd);
        Files files = rootSettings.getFiles();

        // The file must be one oidata file (next line automatically unzip gz files)
        OIFitsFile oifitsFile = new OIFitsFile(fileToAdd.getAbsolutePath());
        String fitsFileName = oifitsFile.getName();

        File newFile = new File();
        newFile.setName(fitsFileName);
        newFile.setId(getNewFileId());
        if (checkFile(newFile)) {
            allFilesListModel.addElement(newFile);
            logger.fine("'" + fitsFileName + "' oifile added to file list");
            int idx = files.getFileCount();
            files.addFile(newFile);
            updateOiTargetList();
            setModified(true);
            fireTreeNodesInserted(new Object[]{rootSettings, files},
                    idx,
                    newFile);
            setSelectionPath(new TreePath(new Object[]{rootSettings, files, newFile}));
        }
    }

    // @todo place this method into fr.jmmc.mf.util
    // and refactor this CODE
    public boolean checkFile(File boundFile) {
        logger.entering(className, "checkFile", boundFile);
        //Store filename
        String filename = boundFile.getName();

        // shorten filename (WARNING newFile name MUST be used because it can be changed by checkFile
        java.io.File tmpFile = new java.io.File(boundFile.getName());
        boundFile.setName(tmpFile.getName());

        logger.fine("Checking file from xml name '" + filename + "'");

        // Check href and return if oitarget are found
        String href = boundFile.getHref();
        if (href != null) {
            logger.fine("Href already present");
            if (boundFile.getOitargetCount() < 1) {
                logger.warning("No oitarget found");
                // restore file from base64 and try to continue
                filename = UtilsClass.saveBASE64ToFile(boundFile).getAbsoluteFilePath();
            } else {
                return true;
            }
        }

        return populate(boundFile, filename);
    }

    /**      
     * Show validation GUI, set href attribute and search oitarget in
     * io.File associated to filename.
     *
     * @param fileToPopulate
     * @param filename
     * @throws IllegalStateException
     *      if file can't be read to be integrated as part of the setting file
     * @return
     */
    private boolean populate(File fileToPopulate, String filename) throws IllegalStateException {
        logger.entering(className, "populate", new Object[]{fileToPopulate, filename});
        OIFitsFile fits;
        // Populate the boundFile with oifits content
        fileToPopulate.clearOitarget();
        // file extension can be *fits or *fits.gz
        OIFitsChecker checker = new OIFitsChecker();
        try {
            fits = OIFitsLoader.loadOIFits(checker, filename);
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Can't add fits file '" + filename + "' to this setting ", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't add fits file '" + filename + "' to this setting ", ex);
        } catch (FitsException ex) {
            throw new IllegalStateException("Can't add fits file '" + filename + "' to this setting ", ex);
        }

        MessagePane.showMessage(checker.getCheckReport());

        OITarget oiTarget = fits.getOiTarget();

        oiTarget.getTarget();
        if (oiTarget == null) {
            // throw new FitsException("Your file must contains one target / one OITARGET table");
            return false;
        }
        String[] targetNames;
        targetNames = oiTarget.getTarget();

        //generate and store base64 href
        fileToPopulate.setHref(UtilsClass.getBase64Href(filename, UtilsClass.IMAGE_FITS_DATATYPE));

        // search oitargets
        for (int i = 0; i < targetNames.length; i++) {
            String targetName = targetNames[i];
            Oitarget t = new Oitarget();
            t.setTarget(targetName);
            fileToPopulate.addOitarget(t);
        }

        return true;
    }

    // @todo think to move this method into fr.jmmc.mf.util
    public void checkSettingsFormat(Settings s) {
        logger.entering(className, "checkSettingsFormat", s);

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
            s.setUserInfo("UserInfo added on " + new java.util.Date()
                    + " by ModelFitting GUI rev. "
                    + ModelFitting.getSharedApplicationDataModel().getProgramVersion());
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
     * Save settings into given file.
     * result are included in file depending on the user preference value.
     * @todo place this method into fr.jmmc.mf.util
     */
    public void saveSettingsFile(java.io.File fileToSave)
            throws IllegalStateException {
        boolean saveResults = myPreferences.getPreferenceAsBoolean("save.results");
        saveSettingsFile(fileToSave, saveResults);
    }

    /**
     * Write serialisation into given file.
     * @todo place this method into fr.jmmc.mf.util
     * @param keepResult indicates that this file will not get result section. It is used in the runFit action for example.
     */
    public void saveSettingsFile(java.io.File fileToSave, boolean keepResult)
            throws IllegalStateException {
        logger.entering(className, "saveSettingsFile", new Object[]{fileToSave, keepResult});
        Results oldResults = rootSettings.getResults();
        /* replace old result section by a new empty one according keepResult parameter
        oldResults are restored at end of the method */
        if (keepResult == false) {
            rootSettings.setResults(new Results());
        }

        // Write settings into a File
        logger.fine("start setting file writting");
        java.io.Writer writer = null;
        try {
            writer = FileUtils.openFile(fileToSave);
            UtilsClass.marshal(rootSettings, writer);
        } catch (IOException ex) {
            throw new IllegalStateException("Can't write settings file into " + fileToSave.getAbsolutePath(), ex);
        } finally {
            FileUtils.closeFile(writer);
            if (keepResult == false) {
                rootSettings.setResults(oldResults);
            }
        }
        logger.fine("finish file writting into " + fileToSave);

        if (fileToSave.equals(associatedFile)) {
            setModified(false);
        }

        if (rootSettings.getResults() == null) {
            rootSettings.setResults(new Results());
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
        logger.entering(className, "getSupportedModel", type);
        //assert that list has been inited
        getSupportedModelsModel();
        return (Model) supportedModels.get(type);
    }

    /**
     * @return the targetListModel
     */
    public DefaultListModel getTargetListModel() {
        logger.entering(className, "getTargetListModel");
        return targetListModel;
    }

    /**
     * @return the targetComboBoxModel
     */
    public DefaultComboBoxModel getTargetComboBoxModel() {
        logger.entering(className, "getTargetComboBoxModel");
        return targetComboBoxModel;
    }

    /**
     * @return the parameterComboBoxModel
     */
    public DefaultComboBoxModel getParameterComboBoxModel() {
        logger.entering(className, "getParameterComboBoxModel");
        return parameterComboBoxModel;
    }

    /**
     * @return the parameterListModel
     */
    public DefaultListModel getParameterListModel() {
        logger.entering(className, "getParameterListModel");
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
        observableDelegate.notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesInserted(Object[] path,
            int childIndice,
            Object child) {
        logger.entering(className, "fireTreeNodesInserted", new Object[]{path, childIndice, child});
        TreeModelEvent e = new TreeModelEvent(this, path,
                new int[]{childIndice}, new Object[]{child});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesInserted(e);
        }
        observableDelegate.notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
            int childIndice,
            Object child) {
        logger.entering(className, "fireTreeNodesRemoved", new Object[]{path, childIndice, child});
        TreeModelEvent e = new TreeModelEvent(source, path,
                new int[]{childIndice}, new Object[]{child});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesRemoved(e);
        }
        observableDelegate.notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesChanged(Object[] path,
            int childIndice,
            Object child) {
        logger.entering(className, "fireTreeNodesChanged", new Object[]{path, childIndice, child});
        TreeModelEvent e = new TreeModelEvent(this, path,
                new int[]{childIndice}, new Object[]{child});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
        observableDelegate.notifyObservers();
    }

    //////////////// TreeModel interface implementation ///////////////////////
    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
        logger.entering(className, "addTreeModelListener", l);
        treeModelListeners.addElement(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
        logger.entering(className, "getChild", new Object[]{parent, new Integer(index)});
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
                return s.getParameters();
            }
            if (index == 3) {
                return s.getResults();
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
            // return files, targets, parameters, results, plots
            return 5;
        } else if (parent instanceof Files) {
            Files f = (Files) parent;
            return f.getFileCount();
        } else if (parent instanceof Targets) {
            Targets t = (Targets) parent;
            return t.getTargetCount();
        } else if (parent instanceof Target) {
            Target t = (Target) parent;
            // return number of files and models
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
        //logger.entering(className, "getIndexOfChild", new Object[]{parent, child});
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
            idx++;
            if (child == rootSettings.getResults()) {
                return idx;
            }
            idx++;
            if (child == plotContainerNode) {
                return idx;
            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getFiles()) {
            File[] elements = rootSettings.getFiles().getFile();
            for (int i = 0; i
                    < elements.length; i++) {
                File element = elements[i];
                if (element == child) {
                    return i;
                }
            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getTargets()) {
            Object[] elements = rootSettings.getTargets().getTarget();
            for (int i = 0; i
                    < elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }
            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getParameters()) {
            Object[] elements = rootSettings.getParameters().getParameter();
            for (int i = 0; i
                    < elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }

            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getResults()) {
            Object[] elements = rootSettings.getResults().getResult();
            // search for Result or ResultModel children
            // we must search resultModel according ordered list of Result
            // because tha hastable is not ordered!!
            for (int i = 0; i
                    < elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }
                if (child == resultToModel.get(elements[i])) {
                    return i;
                }
            }
            logger.warning("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else {
            // Search for fileLinks or models children into Target
            Target[] targets = rootSettings.getTargets().getTarget();
            for (int i = 0; i
                    < targets.length; i++) {
                Target target = targets[i];
                if (parent == target) {

                    Object[] all = new Object[target.getFileLinkCount() + target.getModelCount()];
                    Object[] elements = target.getFileLink();
                    System.arraycopy(elements, 0, all, 0, target.getFileLinkCount());
                    elements =
                            target.getModel();
                    System.arraycopy(elements, 0, all, target.getFileLinkCount(), target.getModelCount());

                    for (int j = 0; j
                            < all.length; j++) {
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
        } else if ((node instanceof Settings) || (node instanceof Files) || (node instanceof Targets)
                || (node instanceof Target) || (node instanceof Results)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        logger.entering(className, "removeTreeModelListener", l);
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        logger.entering(className, "valueForPathChanged", new Object[]{path, newValue});
        Object modifiedObject = path.getLastPathComponent();
        throw new IllegalStateException("This code should not be reached, what user interaction can go there?");
        //fireTreeNodesChanged(path,modifiedObject);
    }

    @Override
    public String toString() {
        return className + " " + getAssociatedFilename();
    }

    /** Marshal the settings and return it as a String */
    public String toXml(){
        logger.entering(className, "toXml");
        logger.fine("Start of marshalling");
        java.io.StringWriter writer = new java.io.StringWriter();
        UtilsClass.marshal(rootSettings, writer);
        logger.fine("End of marshalling");

        writer.flush();
        logger.fine("Store xml as string");
        return writer.toString();
    }

    public String toLITproDesc() {
        logger.entering(className, "toLITproDesc");
        return UtilsClass.xsl(toXml(), "fr/jmmc/mf/settingsToLITproDesc.xsl",
                    new String[]{});        
    }
}
