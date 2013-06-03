/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.models;

import fr.jmmc.jmcs.data.MimeType;
import fr.jmmc.jmcs.data.app.ApplicationDescription;
import fr.jmmc.jmcs.gui.component.GenericListModel;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.network.Http;
import fr.jmmc.jmcs.service.RecentFilesManager;
import fr.jmmc.jmcs.service.XslTransform;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.ObservableDelegate;
import fr.jmmc.jmcs.util.ResourceUtils;
import fr.jmmc.mf.LITpro;
import fr.jmmc.mf.gui.*;
import fr.jmmc.mf.models.*;
import fr.jmmc.oitools.model.OIData;
import fr.jmmc.oitools.model.OIFitsChecker;
import fr.jmmc.oitools.model.OIFitsFile;
import fr.jmmc.oitools.model.OIFitsLoader;
import fr.jmmc.oitools.model.OITarget;
import fr.nom.tam.fits.FitsException;
import fr.nom.tam.fits.FitsUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.ListModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the castor generated classes to bring
 * one interface over a more conventional object.
 * It implements:
 *  - the tree model to be used by the settingsPane tree
 *  - the modifyAndSaveObject to ensure that application save user modification on application exit
 *
 */
public class SettingsModel extends DefaultTreeSelectionModel implements TreeModel, ModifyAndSaveObject {

    public final static String className = SettingsModel.class.getName();
    /** Class logger */
    static Logger logger = LoggerFactory.getLogger(className);
    /** list of supported models   */
    private static List<Model> supportedModelsList = new LinkedList<Model>();
    /** list of supported models   */
    private static GenericListModel supportedModels = new GenericListModel<Model>(supportedModelsList);
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
    /** flag used to tell that the model is self consistent (not the case during update) */
    private boolean isSelfConsistent = true;
    /** Store a reference over the associated local file */
    public java.io.File associatedFile = null;
    private Hashtable<Result, ResultModel> resultToModel = new Hashtable<Result, ResultModel>();
    private static java.util.HashMap<fr.jmmc.mf.models.File, OIFitsFile> alreadyExpandedOifitsFiles = new java.util.HashMap<fr.jmmc.mf.models.File, OIFitsFile>();
    private HashMap<OIFitsFile, OIFitsChecker> oiFitsFileToOIFitsChecker = new HashMap<OIFitsFile, OIFitsChecker>();
    private DefaultMutableTreeNode plotContainerNode = new DefaultMutableTreeNode("Plots") {
        @Override
        public String toString() {
            return "Plots";
        }
    };
    // Use a delegate that will trigger listener on this model changes
    private ObservableDelegate observableDelegate;

    /**
     * Creates a new empty SettingsModel object.
     */
    public SettingsModel() throws ExecutionException {
        logger.info("Creating one new Settings");
        init();
    }

    /**
     * Creates a new empty SettingsModel object from given xml file.
     * @param fileToLoad file serialization to be used for model initialisation
     */
    public SettingsModel(java.io.File fileToLoad) throws IllegalStateException, IOException, FitsException, ExecutionException {
        logger.info("Loading new Settings from file" + fileToLoad.getAbsolutePath());
        init(FileUtils.readFile(fileToLoad));
        setAssociatedFile(fileToLoad, true);
    }

    /**
     * Creates a new empty SettingsModel object from given url file.
     * @param url remote file serialization to be used for model initialisation
     */
    public SettingsModel(String url) throws IllegalStateException, IOException, FitsException, ExecutionException {
        logger.info("Loading new Settings from : " + url);
        URI uriToLoad;
        try {
            uriToLoad = new URI(url);
        } catch (URISyntaxException ex) {
            logger.warn("Error in uri syntax for " + url, ex);
            throw new IOException("Error in uri syntax for " + url, ex);
        }
        java.io.File tmpFile = FileUtils.getTempFile(ResourceUtils.filenameFromResourcePath(url));
        if (Http.download(uriToLoad, tmpFile, false)) {
            init(FileUtils.readFile(tmpFile));
        } else {
            throw new IOException("Can't download " + url);
        }
        setAssociatedFile(tmpFile, false);
    }

    public final void init() throws ExecutionException {
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

        //assert that list has been inited
        getSupportedModels();
    }

    public final void init(String modelInXml) throws IllegalArgumentException, IOException, FitsException, ExecutionException {
        try {
            // perform default init 
            init();

            // and load new model from given string
            Settings newModel = (Settings) UtilsClass.unmarshal(Settings.class, modelInXml);

            checkSettingsFormat(newModel);

            setRootSettings(newModel);

            setModified(false);
        } catch (ExecutionException e) {
            throw new ExecutionException("Can't initialize properly one new setting.\nYou should provide one valid xml data file.", e);
        } catch (IllegalArgumentException e) {
            throw new ExecutionException("Can't initialize properly one new setting.\nYou should provide one valid xml data file.", e);
        }
    }

    /**
     * Return the supported models. This method automatically make a request to get server
     * list if the current list is empty.
     * @return the supportedModelsModel
     * @throws ExecutionException thrown if execution exception occurs
     */
    public static GenericListModel<Model> getSupportedModels() {
        // Fill modelTypeComboBox model if empty
        if (supportedModels.size() < 1) {
            Response r;
            try {
                r = LITpro.execMethod("getModelList", null);
                // Search model into return result
                Model newModel = UtilsClass.getModel(r);
                // update list of supported models
                supportedModels.clear();
                Model[] models = newModel.getModel();
                for (int i = 0; i < models.length; i++) {
                    Model model = models[i];
                    supportedModels.add(model);
                    logger.debug("Adding supported model:" + model.getType());
                }
            } catch (IllegalStateException ex) {
                throw new IllegalStateException("Can't get list of supported models", ex);
            } catch (ExecutionException ex) {
                MessagePane.showErrorMessage("Model supported by remote server can't been properly retrieved\n", ex.getMessage());
            }
        }
        return supportedModels;
    }
    
    public void addUserModel(){
        Model userModel = new Model();
        userModel.setType("custom");
        userModel.setCode(" ");

        Parameter flux_weight = new Parameter();
        flux_weight.setType("flux_weight");
        flux_weight.setName("flux_weight");
        flux_weight.setMinValue(0.0d);
        flux_weight.setMaxValue(1.0d);
        flux_weight.setValue(0.0d);
        flux_weight.setHasFixedValue(true);
        flux_weight.setHasFixedValue(false);

        // TODO add an util method to get pre-inited params
        Parameter x = new Parameter();
        x.setType("x");
        x.setName("x");
        x.setValue(0.0d);
        x.setHasFixedValue(true);
        x.setHasFixedValue(false);
        Parameter y = new Parameter();
        y.setType("y");
        y.setName("y");
        y.setValue(0.0d);
        y.setHasFixedValue(true);
        y.setHasFixedValue(false);

        userModel.addParameter(flux_weight);
        userModel.addParameter(x);
        userModel.addParameter(y);        
        
        
        getSupportedModels().add(userModel);
        getUserCode().addModel(userModel);        
        
        // Fire Change event and Select custom models in tree to be edited        
        fireTreeStructureChanged(rootSettings);
        selectUserModel(userModel);        
    }
    
    public void addUserModel(java.io.File f) throws IOException{        
        Model m = (Model) UtilsClass.unmarshal(Settings.class, FileUtils.readFile(f));
        getSupportedModels().add(m);
        getUserCode().addModel(m);        
    }
    
    public void selectUserModel(Model userModel){
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), getUserCode(), userModel}));
    }
    
    
    
    /**
     * Get the Model associated to the given model name     
     * @param type model type
     *
     * @return associated Model object or null if not present
     */
    public Model getSupportedModel(String type) {
        for (Model m : supportedModelsList) {
            if (m.getType().equalsIgnoreCase(type)) {
                return m;
            }
        }
        return null;
    }

    /** Add a new FileLink associated to the given File.
     *
     * @param target parent container for given file
     * @param file file to use for fileLink addition into the target
     */
    public void addFile(Target target, File file) {
        FileLink fileLink = new FileLink();
        fileLink.setFileRef(file);
        target.addFileLink(fileLink);
        fireTreeNodesInserted(
                new Object[]{rootSettings, rootSettings.getTargets(), target},
                target.getFileLinkCount() - 1,
                fileLink);
    }

    public void removeFile(Target target, File file) {
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
     * Return the number of measurements from every oidata tables.
     * @return the sum of every table measurements for every loaded oifits.
     */
    public int getNbMeasurements() {
        int count = 0;
        // add all points of each table entries for every files
        File[] files = this.rootSettings.getFiles().getFile();
        for (File file : files) {
            OIFitsFile oifits = getOIFitsFromFile(file);
            List<OIData> oidata = oifits.getOiDataList();
            for (OIData table : oidata) {
                count += table.getNbMeasurements();
            }
        }
        return count;
    }

    /**
     *  Add observer to be simply nofified by model content changes.
     */
    public void addObserver(Observer observer) {
        observableDelegate.addObserver(observer);
    }

    /**
     * Notify the observers of this model.
     * 
     * This method as been put public to so that panel can throw notification after modifications.
     * Notification is delivered if the state of the settings is not consistent.
     */
    public void notifyObservers() {
        if (isSelfConsistent) {
            observableDelegate.notifyObservers();
        }
    }

    public void setNormalize(Target target, boolean flag) {
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
        plotContainerNode.setUserObject(plotPanel);
    }

    public void addPlot(FrameTreeNode newPlotNode) {
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
        parentTarget.removeModel(currentModel);

        int modelIdx = UtilsClass.parseModelUniqueIndex(currentModel);
        newModel.setName(newModel.getType() + modelIdx);
        // try to recover previous parameters
        Parameter[] currentModelParams = currentModel.getParameter();
        Parameter[] newModelParams = newModel.getParameter();
        for (int i = 0; i < newModelParams.length; i++) {
            Parameter np = newModelParams[i];
            np.setName(np.getType() + modelIdx);
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

    /** try to tell if the data of the old parameter can be copied to new parameter.
     * according to both names. If they ends with the same string after the '_'
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
        // force another name with unique position
        String type = newModel.getType();
        int modelIdx = UtilsClass.findModelMaxUniqueIndex(getRootSettings()) + 1;
        newModel.setName(type + modelIdx);

        boolean firstModel = parentTarget.getModelCount() == 0;

        // and change parameters name also
        Parameter[] params = newModel.getParameter();
        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            // init some default parameters depending on first place
            if (firstModel) {
                if (p.getName().equals("x")
                        || p.getName().equals("y")
                        || p.getName().equals("rho")
                        || p.getName().equals("PA")) {
                    p.setHasFixedValue(true);
                }
            }
            // fix Claret limb darkening coeff
            if (type.contains("Claret") && p.getName().endsWith("_coeff")) {
                p.setHasFixedValue(true);
                p.setValue(.5);
            }

            if (p.getName().equals("flux_weight")) {
                p.setValue(1);
            }            
            p.setName(p.getType() + modelIdx);
            parameterComboBoxModel.addElement(p);
            parameterListModel.addElement(p);
        }

        setModified(true);

        // add the new element to current target
        parentTarget.addModel(newModel);
        fireTreeNodesInserted(new Object[]{rootSettings, rootSettings.getTargets(), parentTarget}, getIndexOfChild(parentTarget, newModel), newModel);               
    }

    public void removeModel(Target parentTarget, Model oldModel) {
        if (parentTarget == null) {
            logger.warn("Something strange appears while trying to remove one model without associated target");
            return;
        }
        final Model[] models = parentTarget.getModel();
        setModified(true);
        for (int i = 0; i < models.length; i++) {
            final Model model = models[i];
            if (model == oldModel) {
                Parameter[] params = model.getParameter();
                for (int j = 0; j < params.length; j++) {
                    Parameter p = params[j];
                    parameterComboBoxModel.removeElement(p);
                    parameterListModel.removeElement(p);
                }

                ParameterLink[] paramLinks = model.getParameterLink();
                for (int j = 0; j < paramLinks.length; j++) {
                    final ParameterLink parameterLink = paramLinks[j];
                    final Model[] ms = UtilsClass.getSharedParameterOwners(this, (Parameter) parameterLink.getParameterRef());
                    if (ms.length <= 1) {
                        parameterComboBoxModel.removeElement(parameterLink.getParameterRef());
                        parameterListModel.removeElement(parameterLink.getParameterRef());
                        rootSettings.getParameters().removeParameter((Parameter) parameterLink.getParameterRef());
                        logger.debug("removing shared parameter : " + parameterLink.getParameterRef());
                    }
                }

                int idx = getIndexOfChild(parentTarget, model);
                parentTarget.removeModel(model);
                fireTreeNodesRemoved(this,
                        new Object[]{rootSettings, rootSettings.getTargets(), parentTarget},
                        idx,
                        model);
            }
        }
        // force intermediate selection to guarantee the update of leef when they are updated more than once.
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets()}));
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), parentTarget}));
        notifyObservers();
    }

    public void removeModel(Model oldModel) {
        removeModel(getParent(oldModel), oldModel);
    }

    public Target addTarget(String targetIdent) {
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
            logger.debug("Adding default reference to file :" + targetFiles.getElementAt(i));
        }
        int indice = rootSettings.getTargets().getTargetCount();
        rootSettings.getTargets().addTarget(newTarget);
        targetListModel.addElement(newTarget);

        fireTreeNodesInserted(new Object[]{rootSettings, rootSettings.getTargets()}, indice, newTarget);
        setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getTargets(), newTarget}));

        return newTarget;
    }

    public void removeTarget(Target oldTarget) {
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
        setModified(true);

        if (UtilsClass.containsFile(this, oldFile)) {
            logger.debug("file " + oldFile + " has not been removed because it is used by one or more targets");
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
                logger.warn("No code implemented to remove : " + lastPathComponent.getClass());
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

    /** 
     * Tell if the inner model is well filled and consistent 
     * @return true if valid, else false
     */
    public boolean isValid() {
        boolean isValid = rootSettings.isValid();
        logger.trace("isValid=" + isValid);
        return isValid && isSelfConsistent;
    }

    /**
     * Change Model name and type with given type and fire event.
     * @param model model to change
     * @param newType new type for given model
     * @param userModel true if model gets user code
     */
    public void setModelType(Model model, String newType, boolean userModel) {
        model.setType(newType);
        model.setName(newType);
        if (userModel) {
            fireTreeNodesChanged(new Object[]{rootSettings,rootSettings.getUsercode()},
                    getIndexOfChild(rootSettings.getUsercode(),model),
                    model);
        } else {
            Target parentTarget = getParent(model);
            fireTreeNodesChanged(new Object[]{rootSettings},
                    getIndexOfChild(rootSettings.getTargets(), parentTarget),
                    model);

        }
    }

    /**
     * Return the filename that is used to store settings into.
     * @return the filename
     */
    public String getAssociatedFilename() {
        if (associatedFile == null) {
            return "Untitled." + MimeType.LITPRO_SETTINGS.getExtension();
        }
        return associatedFile.getName();
    }

    /**
     * Return the file that is used to store settings into.
     * @return the file
     */
    public java.io.File getAssociatedFile() {
        return associatedFile;
    }

    /**
     *  Tell to the model that given file is used by user to store its data.
     * @param fileToSave file to save
     * @param appendInRecentMenu request to append given file in 'Recent File' menu
     */
    public void setAssociatedFile(java.io.File fileToSave, boolean appendInRecentMenu) {
        associatedFile = fileToSave;
        if (appendInRecentMenu) {
            RecentFilesManager.addFile(associatedFile);
        }
    }

    /**
     * @see ModifyAndSave
     */
    public boolean isModified() {
        logger.trace("isModified={}", isModified);
        return isModified;
    }

    /**
     * Return the UserCode associated object and creates if not present.
     * @return the user code element
     */
    public Usercode getUserCode() {        
        if (rootSettings.getUsercode() == null) {
            Usercode uc = new Usercode();
            uc.setCommon(new Common());            
            rootSettings.setUsercode(uc);
            setModified(true);
        }
        return rootSettings.getUsercode();
    }
    /**
     * Return the list of shared parameters.
     * This method ensures to get a container for shared parameters.
     * @return the list of shared parameters
     */
    public Parameter[] getSharedParameters() {        
        if (rootSettings.getParameters() == null) {
            rootSettings.setParameters(new Parameters());
            setModified(true);
        }
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

    /**
     * Return the list of results
     * @return the list of results
     */
    private Result[] getResults(){       
        if (rootSettings.getResults() == null) {
            rootSettings.setResults(new Results());
        }
        return rootSettings.getResults().getResult();
    }
    
    /**
     * Replace the parameterToLink parameter by a link onto the given sharedParameter.
     * @param parameterToLink param to replace by a link
     * @param sharedParameter parameter to be linked to
     */
    void linkParameter(Parameter parameterToLink, Parameter sharedParameter) {
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

    /**
     * Replace the sharedParameterToLink parameter by a link onto the given sharedParameter.
     * @param sharedParameterToLink shared param to replace by a new link
     * @param sharedParameter parameter to be linked to
     */
    void linkSharedParameter(ParameterLink sharedParameterToLink, Parameter sharedParameter) {
        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(sharedParameter);
        newLink.setType(sharedParameterToLink.getType());

        Model parentModel = getParent(sharedParameterToLink);
        parentModel.removeParameterLink(sharedParameterToLink);
        parentModel.addParameterLink(newLink);

        Target parentTarget = getParent(parentModel);
        fireTreeNodesChanged(new Object[]{rootSettings, parentTarget},
                getIndexOfChild(parentTarget, parentModel), parentModel);
    }

    void shareParameter(Parameter parameterToShare) {
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

        // fire node changes
        fireTreeNodesChanged(new Object[]{rootSettings},
                getIndexOfChild(rootSettings, rootSettings.getParameters()),
                rootSettings.getParameters());
        notifyObservers();
    }

    private ResultModel getModel(Result r, boolean testDataBeforeShowing) {
        ResultModel rm = resultToModel.get(r);
        if (rm == null) {
            rm = new ResultModel(this, r, testDataBeforeShowing);
            resultToModel.put(r, rm);
        }
        return rm;
    }

    /**
     * Call setModified(true) to declare that something internally changed.
     * setModified(false) should be called only after a disk save.
     */
    private void setModified(boolean flag) {
        isModified = flag;
    }

    public java.io.File getTempFile(boolean keepResult) {
        java.io.File tmpFile = FileUtils.getTempFile("tmpSettings", ".xml");
        saveSettingsFile(tmpFile, keepResult);
        return tmpFile;
    }

    /**
     * This method updates the model using some elements of the given settings.
     * The given setting should have the same model and parameter sructure.
     *  @param s Settings to grab informations into.
     */
    public void updateWithNewSettings(Response newResponse) {
        isSelfConsistent = false;

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
        /*fireTreeNodesChanged(new Object[]{rootSettings},
         getIndexOfChild(rootSettings, params), params);
         */
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

        // tree branches to be updated are now all done
        isSelfConsistent = true;

        Result[] newResults = newSettings.getResults().getResult();
        // update settings results  with newResults
        for (int i = 0; i < newResults.length; i++) {
            Result newResult = newResults[i];
            if (newResult != null) {
                // define label for the new result nodes
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                newResult.setLabel("Fit Result " + ft.format(new Date()));

                // add it to the previous result list
                rootSettings.getResults().addResult(newResult);
                ResultModel r = getModel(newResult, false);
                // TODO: check if following call still get some files to display
                r.genPlots(null, null, UtilsClass.getResultFiles(newResponse));
                stampLastUserInfo(r);
                fireTreeNodesInserted(new Object[]{rootSettings, rootSettings.getResults()},
                        rootSettings.getResults().getResultCount() - 1, r);
                setSelectionPath(new TreePath(new Object[]{rootSettings, rootSettings.getResults(), r}));
            } else {
                logger.warn("found null result while updating with new settings");
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
        if (associatedFile != null) {
            saveSettingsFile(associatedFile, false);
        } else {
            // trigger saveSettingsAction
            MFGui.saveSettingsAction.actionPerformed(null);
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
                }
                addFileListModelForOiTarget(f.getOitarget(j), f);
            }
        }
    }

    public void setRootSettings(Settings newRootModel) {
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
       
        // assert that usercode is created.
        getUserCode();
        
        // add shared parameters to the parameter list containers
        Parameter[] params = getSharedParameters();
        for (int i = 0; i < params.length; i++) {
            parameterListModel.addElement(params[i]);
            parameterComboBoxModel.addElement(params[i]);
        }
      
        // build model for every result child
        Result[] results = getResults();
        for (int i = 0; i < results.length; i++) {
            getModel(results[i], true);
            StatusBar.show("Building result node " + (i + 1) + " / " + results.length);
        }

        String desc = "This rootSettings contains " + rootSettings.getFiles().getFileCount()
                + " files," + rootSettings.getTargets().getTargetCount() + " targets," + "User info: [ "
                + rootSettings.getUserInfo() + " ]" +
                " with "+getResults().length+"results section";
        logger.debug(desc);
        
        // verify that user models are in the supported ones        
        for (Model um : rootSettings.getUsercode().getModel()){
            if(getSupportedModel(um.getType())==null){
                getSupportedModels().add(um);
                logger.info("Add usermodel as new available model : " + um.getType());
            }
        }
                
        // fire general change event
        fireTreeStructureChanged(rootSettings);

    }

    public Settings getRootSettings() {
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
        logger.debug("Can't find parent of :" + child);
        logger.debug("Models are:");
        for (int i = 0; i < targets.length; i++) {
            Target target = targets[i];
            Model[] models = target.getModel();
            for (int j = 0; j < models.length; j++) {
                Model model = models[j];
                logger.debug("Models are:{}", model);
            }
        }
        logger.warn("Can't find parent of : {}", child, new Throwable());

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
        logger.warn("Can't find parent of : " + child, new Throwable());

        return null;
    }

    public Model getParent(ParameterLink child) {
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
        logger.warn("Can't find parent of : " + child, new Throwable());

        return null;
    }

    private void addFileListModelForOiTarget(Oitarget target, File file) {
        // we use target name as key
        String key = target.getTarget().trim();
        DefaultListModel lm = (DefaultListModel) fileListModels.get(key);

        if (lm == null) {
            // We need to create one new fileModel for this ident
            lm = new DefaultListModel();
            fileListModels.put(key, lm);
        }

        if (!lm.contains(file)) {
            logger.debug("adding file '" + file.getName() + "' to listmodel for '" + key + "'");
            lm.addElement(file);
        } else {
            logger.debug("file " + file.getName() + " already registered to listmodel for '" + key
                    + "'");
        }
    }

    public ListModel getFileListModelForOiTarget(String oiTargetName) {
        // we use target name as key
        String key = oiTargetName.trim();
        return (ListModel) fileListModels.get(key);
    }

    /**
     * This method add one oifits file to the given settingsModel.
     * The fits file is compressed if it is not yet the case.
     * @todo place this method into fr.jmmc.mf.util and make it much much simpler
     * @param fileToAdd OIFits file
     * @throws IllegalArgumentException if file can't be read to be integrated as part of the setting file
     * @throws IllegalStateException if fits exception occurs
     * @throws FitsException if file io errors occurs
     */
    public void addFile(java.io.File fileToAdd) throws IOException, IllegalArgumentException, FitsException {

        // Zip file if it is not the case to embedd in base64.
        if (!FitsUtil.isCompressed(fileToAdd)) {
            java.io.File zippedFile = FileUtils.getTempFile(fileToAdd.getName() + ".gz");
            FileUtils.zip(fileToAdd, zippedFile);
            fileToAdd = zippedFile;
        }

        Files files = rootSettings.getFiles();

        OIFitsFile oifitsFile = OIFitsLoader.loadOIFits(fileToAdd.getAbsolutePath());
        String fitsFileName = oifitsFile.getAbsoluteFilePath();

        File newFile = new File();
        newFile.setName(fitsFileName);
        newFile.setId(oifitsFile.getName());

        if (checkFile(newFile)) {
            allFilesListModel.addElement(newFile);
            logger.debug("'" + fitsFileName + "' oifile added to file list");
            int idx = files.getFileCount();
            files.addFile(newFile);
            updateOiTargetList();
            setModified(true);
            fireTreeNodesInserted(new Object[]{rootSettings, files}, idx, newFile);
            setSelectionPath(new TreePath(new Object[]{rootSettings, files, newFile}));
        }
    }

    // @todo place this method into fr.jmmc.mf.util
    // and refactor this CODE
    /**
     * 
     * @param boundFile
     * @return
     * @throws IllegalArgumentException if file can't be read to be integrated as part of the setting file
     * @throws IllegalStateException if fits exception occurs
     * @throws FitsException if file io errors occurs
     */
    public boolean checkFile(File boundFile) throws IllegalArgumentException, IOException, FitsException {
        //Store filename
        String filename = boundFile.getName();

        // shorten filename (WARNING newFile name MUST be used because it can be changed by checkFile
        java.io.File tmpFile = new java.io.File(boundFile.getName());
        boundFile.setName(tmpFile.getName());

        logger.debug("Checking file from xml name '" + filename + "'");

        // Check href and return if oitarget are found
        String href = boundFile.getHref();
        if (href != null) {
            logger.debug("Href already present");
            if (boundFile.getOitargetCount() < 1) {
                logger.warn("No oitarget found");
                // restore file from base64 and try to continue
                OIFitsFile f = getOIFitsFromFile(boundFile);
                filename = f.getAbsoluteFilePath();
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
     * @throws IllegalArgumentException if file can't be read to be integrated as part of the setting file
     * @throws IllegalStateException if fits exception occurs
     * @throws FitsException if file io errors occurs
     */
    private boolean populate(File fileToPopulate, String filename) throws IllegalArgumentException, IOException, FitsException {
        OIFitsFile fits;
        // Populate the boundFile with oifits content
        fileToPopulate.clearOitarget();
        // file extension can be *fits or *fits.gz
        OIFitsChecker checker = new OIFitsChecker();
        try {
            fits = OIFitsLoader.loadOIFits(checker, filename);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Can't add fits file '" + filename + "' to this setting ", ex);
        } catch (IOException ex) {
            throw new IOException("Can't add fits file '" + filename + "' to this setting ", ex);
        }

        // display validation messages anyway:
        final String checkReport = checker.getCheckReport();
        logger.info("validation results:\n{}", checkReport);

        MessagePane.showMessage(checkReport);

        // store association between check and oifits object
        setCheckerOfOiFitsFile(fits, checker);

        OITarget oiTarget = fits.getOiTarget();

        if (oiTarget == null) {
            // throw new FitsException("Your file must contains one target / one OITARGET table");
            MessagePane.showErrorMessage("Your file '" + filename + "' was not loaded because it does not contain one OI_TARGET table.");
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

    /**
     * Return the OIFitsFile associated to the given File. 
     * If no OIFitsFile is found, a new one is created and associated in the internal
     * Map.
     * @param dataFile the File object
     * @return the associated OIFitsFile
     */
    public static OIFitsFile getOIFitsFromFile(fr.jmmc.mf.models.File dataFile) {
        fr.jmmc.mf.models.File key = dataFile;
        // Search if this file has already been loaded
        OIFitsFile oifitsFile = (OIFitsFile) alreadyExpandedOifitsFiles.get(key);

        if (oifitsFile == null) {
            String filename = "tmpOifile";
            String fileExtension = ".oifits";
            java.io.File tmp = new java.io.File(dataFile.getName());

            int dotPos = tmp.getName().lastIndexOf(".");
            if (dotPos > 1) {
                filename = tmp.getName().substring(0, dotPos);
                if (tmp.getName().substring(dotPos).length() > 1) {
                    fileExtension = tmp.getName().substring(dotPos);
                }
            }

            java.io.File outputFile = FileUtils.getTempFile(filename, fileExtension);
            UtilsClass.saveBASE64ToFile(dataFile.getHref(), outputFile);
            try {
                oifitsFile = OIFitsLoader.loadOIFits(outputFile.getAbsolutePath());
                oifitsFile.analyze();
            } catch (FitsException fe) {
                throw new IllegalArgumentException("Can't extract fits file", fe);
            } catch (IOException ioe) {
                throw new IllegalArgumentException("Can't extract fits file", ioe);
            }

            alreadyExpandedOifitsFiles.put(key, oifitsFile);
            logger.debug("expanding '" + key + "' into " + oifitsFile.getAbsoluteFilePath());
        } else {
            logger.debug("oifitsfile '" + key + "' was already expanded into " + oifitsFile.getAbsoluteFilePath());
        }
        return oifitsFile;
    }

    /**
     *  Get the checker initialized during file loading
     * @param oifile oifits file to search checker for
     * @return the checker initialized during loading step
     */
    public OIFitsChecker getOiFitsFileChecker(OIFitsFile oifile) {
        OIFitsChecker checker = oiFitsFileToOIFitsChecker.get(oifile);

        if (checker == null) {
            // This case occurs when a settings file is open.

            String filename = oifile.getAbsoluteFilePath();
            checker = new OIFitsChecker();
            try {
                OIFitsFile fits = OIFitsLoader.loadOIFits(checker, filename);
            } catch (MalformedURLException ex) {
                throw new IllegalStateException("Can't load fits file '" + filename + "' to this setting to get checker result", ex);
            } catch (IOException ex) {
                throw new IllegalStateException("Can't load fits file '" + filename + "' to this setting to get checker result", ex);
            } catch (FitsException ex) {
                throw new IllegalStateException("Can't load fits file '" + filename + "' to this setting to get checker result", ex);
            }
            setCheckerOfOiFitsFile(oifile, checker);
        }

        return checker;
    }

    /**
     *  Link the checker onto the oifits file
     * @param oifile oifits file associated to the checker
     * @param checker checker associated to the oifits file
     */
    public void setCheckerOfOiFitsFile(OIFitsFile oifile, OIFitsChecker checker) {
        oiFitsFileToOIFitsChecker.put(oifile, checker);
    }

    // @todo think to move this method into fr.jmmc.mf.util
    /**
     * 
     * @param s 
     * @throws IllegalArgumentException if file can't be read to be integrated as part of the setting file
     * @throws IllegalStateException if fits exception occurs
     * @throws FitsException if file io errors occurs
     */
    public void checkSettingsFormat(Settings s) throws IllegalArgumentException, IOException, FitsException, ExecutionException {
        if (s == null) {
            throw new ExecutionException("Settings file invalid format", new Throwable("no valid root"));
        }
        // try to locate files
        Files files = s.getFiles();
        if (files == null) {
            throw new ExecutionException("Settings file invalid format", new Throwable(" missing Files)"));
        }

        File[] fileList = files.getFile();
        for (int i = 0; i < fileList.length; i++) {
            // Check file
            checkFile(fileList[i]);
        }

        // assert that one parameters section is present to allow param sharing
        if (s.getParameters() == null) {
            logger.debug("no parameter section, -> new one created");
            s.setParameters(new Parameters());
            setModified(true);
        }

        // assert that one userInfo section is present to allow traces
        if (s.getUserInfo() == null) {
            logger.debug("no userInfo section, -> new one created");
            s.setUserInfo("UserInfo added on " + new java.util.Date()
                    + " by ModelFitting GUI rev. "
                    + ApplicationDescription.getInstance().getProgramVersion());
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
        boolean saveResults = myPreferences.getPreferenceAsBoolean(Preferences.SAVE_RESULTS);
        saveSettingsFile(fileToSave, saveResults);
    }

    /**
     * Write serialisation into given file.
     * @todo place this method into fr.jmmc.mf.util
     * @param keepResult indicates that this file will not get result section. It is used in the runFit action for example.
     */
    public void saveSettingsFile(java.io.File fileToSave, boolean keepResult)
            throws IllegalStateException {
        Results oldResults = rootSettings.getResults();
        /* replace old result section by a new empty one according keepResult parameter
         oldResults are restored at end of the method */
        if (keepResult == false) {
            rootSettings.setResults(new Results());
        }

        // Write settings into a File
        logger.debug("start setting file writting");
        java.io.Writer writer = null;
        try {
            writer = FileUtils.openFile(fileToSave);
            UtilsClass.marshal(rootSettings, writer);
        } catch (IOException ex) {
            MessagePane.showErrorMessage("Can not save settings  to file : " + fileToSave.getAbsolutePath(), ex);
            return;
        } finally {
            FileUtils.closeFile(writer);
            if (keepResult == false) {
                rootSettings.setResults(oldResults);
            }
        }
        logger.info("Settings saved to file " + fileToSave.getAbsolutePath());

        if (fileToSave.equals(associatedFile)) {
            setModified(false);
        }        
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
        TreeModelEvent e;
        e = new TreeModelEvent(this, new Object[]{highlyChangedNode});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeStructureChanged(e);
        }
        notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesInserted(Object[] path, int childIndice,
            Object child) {
        TreeModelEvent e = new TreeModelEvent(this, path,
                new int[]{childIndice}, new Object[]{child});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesInserted(e);
        }
        notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
            int childIndice, Object child) {
        TreeModelEvent e = new TreeModelEvent(source, path,
                new int[]{childIndice}, new Object[]{child});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesRemoved(e);
        }
        notifyObservers();
    }

    /**
     * @see DefaultTreeModel implementation
     */
    protected void fireTreeNodesChanged(Object[] path, int childIndice,
            Object child) {
        TreeModelEvent e = new TreeModelEvent(this, path,
                new int[]{childIndice}, new Object[]{child});
        for (int i = 0; i < treeModelListeners.size(); i++) {
            ((TreeModelListener) treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
        notifyObservers();
    }

    //////////////// TreeModel interface implementation ///////////////////////
    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     */
    public Object getChild(Object parent, int index) {
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
                return s.getUsercode();
            }
            if (index == 3) {
                return s.getParameters();
            }
            if (index == 4) {
                return s.getResults();
            }
            if (index == 5) {
                return plotContainerNode;
            } else {
                logger.warn("This line must not occur");
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
        } else if (parent instanceof Usercode) {
            Usercode u = (Usercode) parent;
            return u.getModel(index);            
        } else if (parent instanceof Results) {
            Result r = ((Results) parent).getResult(index);
            return getModel(r, false);
        }
        logger.warn("child n=" + index + " of " + parent + " is not handled");
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
            // return files, targets, usercode, parameters, results, plots
            return 6;
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
        } else if (parent instanceof Usercode) {
            Usercode u = (Usercode) parent;
            return u.getModelCount();
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
            if (child == rootSettings.getUsercode()) {
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
            // TODO fix that point!
            logger.trace("parent:" + parent + " does not seem to contain:" + child);
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
            // TODO fix that point!
            logger.debug("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getTargets()) {
            Object[] elements = rootSettings.getTargets().getTarget();
            for (int i = 0; i
                    < elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }
            }
            // TODO fix that point!
            logger.debug("parent:" + parent + " does not seem to contain:" + child);
            return -1;
        } else if (parent == rootSettings.getParameters()) {
            Object[] elements = rootSettings.getParameters().getParameter();
            for (int i = 0; i
                    < elements.length; i++) {
                if (child == elements[i]) {
                    return i;
                }

            }
            // TODO fix that point!
            logger.debug("parent:" + parent + " does not seem to contain:" + child);
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
            // TODO fix that point!
            logger.debug("parent:" + parent + " does not seem to contain:" + child);
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
                    // TODO fix that point!
                    logger.debug("parent:" + parent + " does not seem to contain:" + child);
                    return -1;
                }
            }
        }
        // TODO fix that point!
        logger.debug("missing code for parent=" + parent + " and child=" + child);
        return 0;
    }

    /**
     * Returns the root of the tree.
     */
    public Object getRoot() {
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
        } else if (node instanceof Parameters 
                || node instanceof File
                || node instanceof FileLink
                || node instanceof Model) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        Object modifiedObject = path.getLastPathComponent();
        throw new IllegalStateException("This code should not be reached, what user interaction can go there?");
        //fireTreeNodesChanged(path,modifiedObject);
    }

    @Override
    public String toString() {
        return className + " " + getAssociatedFilename();
    }

    /** Marshal the settings and return it as a String */
    public String toXml() {
        logger.debug("Start of marshalling");
        java.io.StringWriter writer = new java.io.StringWriter(16384); // 16K buffer
        UtilsClass.marshal(rootSettings, writer);
        logger.debug("End of marshalling");
        writer.flush();
        logger.debug("Store xml as string");
        return writer.toString();
    }

    public String toLITproDesc() {
        return XslTransform.transform(toXml(), "fr/jmmc/mf/settingsToLITproDesc.xsl");
    }
}
