/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf;

import fr.jmmc.jmal.model.gui.EditableSepPosAngleParameter;
import fr.jmmc.jmcs.gui.component.GenericListModel;
import fr.jmmc.jmcs.service.BrowserLauncher;
import fr.jmmc.jmcs.util.ObjectUtils;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.mf.gui.Preferences;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Operator;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * util function applied to models.
 * TODO, most of following functions could be added as method to the Model class 
 * @author mella
 */
public class ModelUtils {

    static Logger logger = LoggerFactory.getLogger(
            ModelUtils.class.getName());

    public final static String ROTATION_PARAMETER_NAME = "rotation";
    public final static String STRETCHED_PARAMETER_NAME = "stretched_ratio";

    public static boolean isUserModel(Model model) {
        return !StringUtils.isEmpty(model.getCode());
    }

    public static void moveParamUp(Model model, int paramIndex) {
        Parameter[] params = model.getParameter();
        Parameter p = params[paramIndex];
        params[paramIndex] = params[paramIndex + 1];
        params[paramIndex + 1] = p;
        model.setParameter(params);
    }

    public static void moveParamDown(Model model, int paramIndex) {
        Parameter[] params = model.getParameter();
        Parameter p = params[paramIndex];
        params[paramIndex] = params[paramIndex - 1];
        params[paramIndex - 1] = p;
        model.setParameter(params);
    }

    /* not used anylonger with this method : will be replaced by operators
     public static boolean setPolarModel(Model model, boolean flag) {
        if (model.getPolar() == flag) {
            return false;
        }

        // TODO add some tests to avoid duplicated types
        if (flag) {
            // xy -> rho pa            
            Parameter p1 = getParameterOfType(model, "x");
            Parameter p2 = getParameterOfType(model, "y");
            double sep = EditableSepPosAngleParameter.getSeparation(p1.getValue(), p2.getValue());
            double pa = EditableSepPosAngleParameter.getPosAngle(p1.getValue(), p2.getValue());
            p1.setName(p1.getName().replace("x", EditableSepPosAngleParameter.SepPosAngleType.SEPARATION.getName()));
            p1.setValue(sep);
            p1.setType(EditableSepPosAngleParameter.SepPosAngleType.SEPARATION.getName());
            p1.setUnits(EditableSepPosAngleParameter.SepPosAngleType.SEPARATION.getUnits());

            p2.setName(p2.getName().replace("y", EditableSepPosAngleParameter.SepPosAngleType.POS_ANGLE.getName()));
            p2.setValue(pa);
            p2.setType(EditableSepPosAngleParameter.SepPosAngleType.POS_ANGLE.getName());
            p2.setUnits(EditableSepPosAngleParameter.SepPosAngleType.POS_ANGLE.getUnits());
        } else {
            // rho pa -> xy            
            Parameter p1 = getParameterOfType(model, EditableSepPosAngleParameter.SepPosAngleType.SEPARATION.getName());
            Parameter p2 = getParameterOfType(model, EditableSepPosAngleParameter.SepPosAngleType.POS_ANGLE.getName());
            double x = EditableSepPosAngleParameter.getX(p1.getValue(), p2.getValue());
            double y = EditableSepPosAngleParameter.getY(p1.getValue(), p2.getValue());
            p1.setName(p1.getName().replace(EditableSepPosAngleParameter.SepPosAngleType.SEPARATION.getName(), "x"));
            p1.setValue(x);
            p1.setType("x");
            p1.setUnits("mas");

            p2.setName(p2.getName().replace(EditableSepPosAngleParameter.SepPosAngleType.POS_ANGLE.getName(), "y"));
            p2.setValue(y);
            p2.setType("y");
            p2.setUnits("mas");
        }
        // TODO add min max / fixed values conversions

        model.setPolar(flag);
        return true;
    }

    public static boolean setStretchedModel(Model model, boolean flag) {
        if (model.getStretched() == flag) {
            return false;
        }
        if (flag) {
            // add rotation            
            final Parameter s = new Parameter();
            s.setName(STRETCHED_PARAMETER_NAME + model.getName().replace(model.getType(), ""));
            s.setType(STRETCHED_PARAMETER_NAME);
            s.setValue(1.0);
            s.setHasFixedValue(true);
            s.setHasFixedValue(false);
            model.addParameter(s);

            // add rotation            
            final Parameter r = new Parameter();
            r.setName(ROTATION_PARAMETER_NAME + model.getName().replace(model.getType(), ""));
            r.setType(ROTATION_PARAMETER_NAME);
            r.setValue(0);
            r.setUnits("deg");
            r.setHasFixedValue(true);
            r.setHasFixedValue(false);
            model.addParameter(r);
        } else {
            // remove rotation parameter
            model.removeParameter(getParameterOfType(model, ROTATION_PARAMETER_NAME));

            // remove rotation parameter
            model.removeParameter(getParameterOfType(model, STRETCHED_PARAMETER_NAME));
        }

        model.setStretched(flag);
        return true;
    }
     */
    public static boolean hasPosition(Model selectedModel) {
        // test if marqued polar or has a classical x param
        //return selectedModel.getPolar() || ModelUtils.hasParameterOfType(selectedModel, "x");
        return ModelUtils.hasParameterOfType(selectedModel, "x");
    }

    public static boolean hasParameterOfType(Model model, String type) {
        return getParameterOfType(model, type) != null;
    }

    public static Parameter getParameterOfType(Model model, String type) {
        Parameter[] params = model.getParameter();
        for (Parameter parameter : params) {
            if (parameter.getType().equals(type)) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * Test if given settings uses model of given type in the list of targets.
     * @param s settings to search into
     * @param type model type
     * @return true if settings uses given model type, else false.
     */
    public static boolean hasModelOfType(Settings s, String type) {
        Target[] targets = s.getTargets().getTarget();
        for (Target target : targets) {
            if (hasModelOfType(target.getModel(), type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search in given list if only one model have the given type.
     * @param models model list
     * @param type model type
     * @return true if only one model has the given type, false else
     */
    public static boolean isModelTypeUniq(final GenericListModel<Model> models, final String type) {
        int count = 0;
        for (int i = 0; i < models.size(); i++) {
            if (type.equalsIgnoreCase(models.getElementAt(i).getType())) {
                count++;
            }
        }
        return count == 1;
    }

    public static boolean hasModelOfType(final GenericListModel<Model> models, String type) {
        for (int i = 0; i < models.size(); i++) {
            if (type.equalsIgnoreCase(models.getElementAt(i).getType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasModelOfType(Model[] models, String type) {
        return getModelOfType(models, type) != null;
    }

    public static Model getModelOfType(Model[] models, String type) {
        for (Model m : models) {
            if (m.getType().equals(type)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Return a string that describe the rho theta information of the given if
     * model has x and y and vice versa model.
     *
     * @param m
     * @return the rho/theta or x/y informations
     */
    public static String getRelativeCoords(Model m) {
        // compute rho theta from x y parameters
        double x = 0;
        double y = 0;
        double sep = 0;
        double pa = 0;
        boolean cartesianInput = true;
        Parameter[] params = m.getParameter();
        ParameterLink[] paramLinks = m.getParameterLink();
        for (int i = 0; i < (params.length + paramLinks.length); i++) {
            Parameter parameter;
            if (i < params.length) {
                parameter = params[i];
            } else {
                parameter = (Parameter) paramLinks[i - params.length].getParameterRef();
            }
            if (parameter.getType().equalsIgnoreCase("x")) {
                x = parameter.getValue();
            } else if (parameter.getType().equalsIgnoreCase("y")) {
                y = parameter.getValue();
            } else if (parameter.getType().equalsIgnoreCase("rho") | parameter.getType().equalsIgnoreCase(EditableSepPosAngleParameter.SepPosAngleType.SEPARATION.getName())) {
                sep = parameter.getValue();
                cartesianInput = false;
            } else if (parameter.getType().equalsIgnoreCase("pa") | parameter.getType().equalsIgnoreCase(EditableSepPosAngleParameter.SepPosAngleType.POS_ANGLE.getName())) {
                pa = parameter.getValue();
                cartesianInput = false;
            }
        }
        String result;
        if (cartesianInput) {
            result = EditableSepPosAngleParameter.getSepPosAngleCoords(x, y);
        } else {
            result = EditableSepPosAngleParameter.getXYCoords(sep, pa);
        }
        return result;
    }

    /** 
     * Forward the build model onto the user model web portal.
     * @param model to share
     * @throws IOException if uri can not be processed with xml serialisation
     */
    public static void share(final Model model) throws IOException {
        final StringWriter strWriter = new StringWriter();
        UtilsClass.marshal(model, strWriter);
        String xmlstr = URIUtil.encodePath(strWriter.toString());
        String umRepoUrl = Preferences.getInstance().getPreference(Preferences.USERMODEL_REPO_URL);
        BrowserLauncher.openURL(umRepoUrl + "add.html?xmlstr=" + xmlstr);
    }

    /**
     * Open the browser on the index page of the usermodel repository.
     */
    public static void visitUsermodelsRepository() {
        BrowserLauncher.openURL(Preferences.getInstance().getPreference(Preferences.USERMODEL_REPO_URL) + "index.html");
    }

    public static boolean areEquals(final Parameter p1, final Parameter p2) {
        boolean areEquals;
        if (ObjectUtils.areEquals(p1, p2)) {
            areEquals = true;
        } else if (p1.getValue() == p2.getValue()
                && p1.getEditable() == p2.getEditable()
                && p1.getHasFixedValue() == p2.getHasFixedValue()
                && p1.hasEditable() == p2.hasEditable()
                && p1.hasHasFixedValue() == p2.hasHasFixedValue()
                && p1.hasMaxValue() == p2.hasMaxValue()
                && p1.hasMinValue() == p2.hasMinValue()
                && p1.hasScale() == p2.hasScale()
                && p1.hasValue() == p2.hasValue()
                && ObjectUtils.areEquals(p1.getDesc(), p2.getDesc())
                && ObjectUtils.areEquals(p1.getId(), p2.getId())
                && p1.getMaxValue() == p2.getMaxValue()
                && p1.getMinValue() == p2.getMinValue()
                && ObjectUtils.areEquals(p1.getName(), p2.getName())
                && p1.getScale() == p2.getScale()
                && ObjectUtils.areEquals(p1.getType(), p2.getType())
                && ObjectUtils.areEquals(p1.getUnits(), p2.getUnits())) {
            areEquals = true;
        } else {
            areEquals = false;
        }
        return areEquals;
    }

    public static boolean hasOperator(Model model, String name) {
        Operator[] operators = model.getOperator();

        for (int i = 0; i < model.getOperatorCount(); i++) {
            Operator operator = model.getOperator(i);
            if (name.equals(operator.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean removeOperator(Model model, String name) {
        for (int i = 0; i < model.getOperatorCount(); i++) {
            Operator operator = model.getOperator(i);
            if (name.equals(operator.getName())) {
                model.removeOperatorAt(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Search if the given parameter is
     * @param model
     * @param parameter
     * @return
     */
    public static Operator getParentOperator(Model model, Parameter parameter) {
        for (int i = 0; i < model.getOperatorCount(); i++) {
            Operator operator = model.getOperator(i);
            for (int j = 0; j < operator.getParameterCount(); j++) {
                Parameter p = operator.getParameter(j);
                if (parameter == p) {
                    return operator;
                }
            }
        }
        return null;
    }

    public static void addParamsFor(Settings settings, Vector paramContainer, Vector<Model> modelContainer, boolean recursive, boolean addParams, boolean addSharedParams) {

        for (Target t : settings.getTargets().getTarget()) {
            addParamsFor(t, paramContainer, modelContainer, recursive, addParams, addSharedParams);
        }

        for (Parameter p : settings.getParameters().getParameter()) {
            if (paramContainer != null) {
                paramContainer.add(p);
            }
            if (modelContainer != null) {
                modelContainer.add(null);
            }
        }

    }

    public static void addParamsFor(Target target, Vector paramContainer, Vector<Model> modelContainer, boolean recursive, boolean addParams, boolean addSharedParams) {
        for (Model m : target.getModel()) {
            addParamsFor(m, paramContainer, modelContainer, recursive, addParams, addSharedParams);
        }
    }

    public static void addParamsFor(Model model, Vector paramContainer, Vector<Model> modelContainer, boolean recursive, boolean addParams, boolean addSharedParams) {

        int nbOfParams = 0;
        int nbOfSharedParams = 0;
        int nbOfOperatorParams = 0;
        int nbOfSharedOperatorParams = 0;

        // Start to append model parameters
        if (addParams) {
            nbOfParams = model.getParameterCount();
            for (Parameter p : model.getParameter()) {
                if (paramContainer != null) {
                    paramContainer.add(p);
                }
                if (modelContainer != null) {
                    modelContainer.add(model);
                }
            }
        }

        // Append model parameters that are linked
        if (addSharedParams) {
            nbOfSharedParams = model.getParameterLinkCount();
            for (ParameterLink link : model.getParameterLink()) {
                if (paramContainer != null) {
                    paramContainer.add(link);
                }
                if (modelContainer != null) {
                    modelContainer.add(model);
                }
            }
        }

        // Append params of operators
        Operator[] operators = model.getOperator();

        for (Operator operator : operators) {
            nbOfOperatorParams += operator.getParameterCount();
            nbOfSharedOperatorParams += operator.getParameterLinkCount();

            if (addParams) {
                for (Parameter p : operator.getParameter()) {
                    if (paramContainer != null) {
                        paramContainer.add(p);
                    }
                    if (modelContainer != null) {
                        modelContainer.add(model);
                    }
                }
            }

            if (addSharedParams) {
                for (ParameterLink link : operator.getParameterLink()) {
                    if (paramContainer != null) {
                        paramContainer.add(link);
                    }
                    if (modelContainer != null) {
                        modelContainer.add(model);
                    }
                }
            }
        }
        logger.debug("Adding {}/{} normal/shared params and {}/{} normal/shared parameters operators of model {}",
                nbOfParams, nbOfSharedParams, nbOfOperatorParams, nbOfSharedOperatorParams, model);

        if (recursive) {
            Model[] models = model.getModel();
            for (int i = 0; i < models.length; i++) {
                addParamsFor(models[i], paramContainer, modelContainer, true, addParams, addSharedParams);
            }
        }

    }
}
