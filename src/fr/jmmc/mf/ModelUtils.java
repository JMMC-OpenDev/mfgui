/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf;

import fr.jmmc.jmal.model.gui.EditableRhoThetaParameter;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import java.text.NumberFormat;

/**
 * util function applied to models.
 * @author mella
 */
public class ModelUtils {

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

        model.setPolar(flag);
        if (flag) {
            // xy -> rho pa            
            Parameter p1 = getParameterOfType(model, "x");
            Parameter p2 = getParameterOfType(model, "y");
            double rho = EditableRhoThetaParameter.getRho(p1.getValue(), p2.getValue());
            double theta = EditableRhoThetaParameter.getTheta(p1.getValue(), p2.getValue());
            p1.setName(p1.getName().replace("x", EditableRhoThetaParameter.Type.RHO.getName()));
            p1.setValue(rho);
            p1.setType(EditableRhoThetaParameter.Type.RHO.getName());
            p1.setUnits(EditableRhoThetaParameter.Type.RHO.getUnits());

            p2.setName(p2.getName().replace("y", EditableRhoThetaParameter.Type.THETA.getName()));
            p2.setValue(theta);
            p2.setType(EditableRhoThetaParameter.Type.THETA.getName());
            p2.setUnits(EditableRhoThetaParameter.Type.THETA.getUnits());
        } else {
            // rho pa -> xy            
            Parameter p1 = getParameterOfType(model, EditableRhoThetaParameter.Type.RHO.getName());
            Parameter p2 = getParameterOfType(model, EditableRhoThetaParameter.Type.THETA.getName());
            double x = EditableRhoThetaParameter.getX(p1.getValue(), p2.getValue());
            double y = EditableRhoThetaParameter.getY(p1.getValue(), p2.getValue());
            p1.setName(p1.getName().replace(EditableRhoThetaParameter.Type.RHO.getName(), "x"));
            p1.setValue(x);
            p1.setType("x");
            p1.setUnits("mas");

            p2.setName(p2.getName().replace(EditableRhoThetaParameter.Type.THETA.getName(), "y"));
            p2.setValue(y);
            p2.setType("y");
            p2.setUnits("mas");
        }
        // TODO min max / fixed values

        return true;
    }

    public static boolean setStretchedModel(Model model, boolean flag) {
        if (model.getStretched() == flag) {
            return false;
        }

        return true;
    }

    public static boolean setRotatedModel(Model model, boolean flag) {
        if (model.getRotated() == flag) {
            return false;
        }

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
        double rho = 0;
        double pa = 0;
        boolean cartesianInput = true;
        Parameter[] params = m.getParameter();
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = params[i];
            if (parameter.getType().equalsIgnoreCase("x")) {
                x = parameter.getValue();
            } else if (parameter.getType().equalsIgnoreCase("y")) {
                y = parameter.getValue();
            } else if (parameter.getType().equalsIgnoreCase("rho")) {
                rho = parameter.getValue();
                cartesianInput = false;
            } else if (parameter.getType().equalsIgnoreCase("pa")) {
                pa = parameter.getValue();
                cartesianInput = false;
            }
        }
        ParameterLink[] paramLinks = m.getParameterLink();
        for (int i = 0; i < paramLinks.length; i++) {
            ParameterLink parameterLink = paramLinks[i];
            Parameter parameter = (Parameter) parameterLink.getParameterRef();
            if (parameterLink.getType().equalsIgnoreCase("x")) {
                x = parameter.getValue();
            } else if (parameterLink.getType().equalsIgnoreCase("y")) {
                y = parameter.getValue();
            } else if (parameter.getType().equalsIgnoreCase("rho")) {
                rho = parameter.getValue();
                cartesianInput = false;
            } else if (parameter.getType().equalsIgnoreCase("pa")) {
                pa = parameter.getValue();
                cartesianInput = false;
            }
        }
        String result = null;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        if (cartesianInput) {
            result = "rho='" + nf.format(EditableRhoThetaParameter.getRho(x, y)) + "' PA='" + nf.format(EditableRhoThetaParameter.getTheta(x, y)) + "'";
        } else {
            result = "x='" + nf.format(EditableRhoThetaParameter.getX(rho, pa)) + "' y='" + nf.format(EditableRhoThetaParameter.getY(rho, pa)) + "'";
        }
        return result;
    }
}
