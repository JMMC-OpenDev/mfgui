/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf;

import fr.jmmc.jmal.model.gui.EditableSepPosAngleParameter;
import fr.jmmc.jmcs.gui.component.GenericListModel;
import fr.jmmc.jmcs.service.BrowserLauncher;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.mf.gui.Preferences;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Target;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.httpclient.util.URIUtil;

/**
 * util function applied to models.
 * TODO, most of following functions could be added as method to the Model class 
 * @author mella
 */
public class ModelUtils {

    public final static String ROTATION_PARAMETER_NAME = "rotation";
    public final static String STRETCHED_PARAMETER_NAME = "stretched_ratio";

    public static boolean isUserModel(Model model) {
        return ! StringUtils.isEmpty(model.getCode());
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

    public static boolean hasPosition(Model selectedModel) {
        // test if marqued polar or has a classical x param
        return selectedModel.getPolar() || ModelUtils.hasParameterOfType(selectedModel, "x");
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
        return count==1;
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
        for (int i = 0; i < (params.length+paramLinks.length); i++) {
            Parameter parameter;
            if(i < params.length){
                parameter = params[i];
            }else{
                parameter = (Parameter) paramLinks[i-params.length].getParameterRef();
            }            
            if (parameter.getType().equalsIgnoreCase("x")) {
                x = parameter.getValue();
            } else if (parameter.getType().equalsIgnoreCase("y")) {
                y = parameter.getValue();
            } else if ( parameter.getType().equalsIgnoreCase("rho") | parameter.getType().equalsIgnoreCase(EditableSepPosAngleParameter.SepPosAngleType.SEPARATION.getName()) ) {
                sep = parameter.getValue();
                cartesianInput = false;
            } else if ( parameter.getType().equalsIgnoreCase("pa") | parameter.getType().equalsIgnoreCase(EditableSepPosAngleParameter.SepPosAngleType.POS_ANGLE.getName()) ) {
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
    public static void visitUsermodelsRepository(){
        BrowserLauncher.openURL(Preferences.getInstance().getPreference(Preferences.USERMODEL_REPO_URL)+"index.html");
    }   
}
