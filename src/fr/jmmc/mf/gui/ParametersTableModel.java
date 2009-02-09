package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Target;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * Implementation of a table model that is based on a given Model.
 * todo: check if model container should be vectors instead of arrays
 */
class ParametersTableModel extends AbstractTableModel {

    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ParametersTableModel");
    protected boolean recursive;
    protected Parameter[] parameters;
    // Store model of corresponding parameter in parameters array
    protected Model[] modelOfParameters;
    // Init columns titles and types
    protected final String[] columnNames = new String[]{"Name", "Type", "Units", "Value", "MinValue", "MaxValue", "Scale", "HasFixedValue"};
    protected final Class[] columnTypes = new Class[]{String.class, String.class, String.class, Double.class, Double.class, Double.class, Double.class, Boolean.class};
    protected final Boolean[] columnEditableFlags = new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
    
    public ParametersTableModel() {
        super();
        // next static line should be replaced by a preference listener
        recursive = true;
    }

    
    public void setModel(Target target, boolean recursive) {
        this.recursive = recursive;
        parameters = new Parameter[]{};
        if (target != null) {
            // get list , create array and init array with content list
            Vector <Parameter>params = new Vector();
            Vector <Model>models = new Vector();
            Model [] array = target.getModel();
            for (int i = 0; i < array.length; i++) {
                Model model = array[i];
                addParamsFor(model, params, models, recursive);
            }
            parameters = new Parameter[params.size()];
            modelOfParameters = new Model[params.size()];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = params.elementAt(i);
                modelOfParameters[i] = (Model) models.elementAt(i);
            }
        }
        // notify observers
        fireTableDataChanged();
    }

    /**
     * tell table model to represent the parameters of the given model.
     */
    public void setModel(Model modelToPresent, boolean recursive) {
        this.recursive = recursive;
        parameters = new Parameter[]{};
        if (modelToPresent != null) {
            // get list , create array and init array with content list
            Vector <Parameter>params = new Vector();
            Vector <Model>models = new Vector();
            addParamsFor(modelToPresent, params, models, recursive);
            parameters = new Parameter[params.size()];
            modelOfParameters = new Model[params.size()];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = params.elementAt(i);
                modelOfParameters[i] = (Model) models.elementAt(i);
            }
        }
        // notify observers
        fireTableDataChanged();
    }

    public void setParameters(Parameter[] params){
        recursive=false;
        parameters=params;
        // notify observers
        fireTableDataChanged();
    }

    protected void addParamsFor(Model model, Vector<Parameter> paramContainer, Vector<Model> modelContainer, boolean recursive) {
        // Start to append model parameters
        Parameter[] params = model.getParameter();
        int nbOfParams = params.length;
        // Create with initial data
        for (int i = 0; i < nbOfParams; i++) {
            Parameter p = params[i];
            paramContainer.add(p);
            modelContainer.add(model);
        }
        // Then append model parameters that are linked
        ParameterLink[] paramLinks = model.getParameterLink();
        nbOfParams = paramLinks.length;
        logger.fine("Adding " + nbOfParams + " shared parameters");
        // Create with initial data
        for (int i = 0; i < nbOfParams; i++) {
            ParameterLink link = paramLinks[i];
            Parameter p = (Parameter) link.getParameterRef();
            paramContainer.add(p);
            modelContainer.add(model);
        }
        if (recursive) {
            Model[] models = model.getModel();
            for (int i = 0; i < models.length; i++) {
                addParamsFor(models[i], paramContainer, modelContainer, true);
            }
        }
    }

    // Next parts makes respond to the full TableModel interface
    @Override
    public Class getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public int getRowCount() {
        if (parameters != null) {
            return parameters.length;
        }
        return 0;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Parameter p = parameters[rowIndex];
        if(p==null){
            return null;
        }
        // return name
        if (columnIndex == 0) {
            if (recursive) {
                Model model = modelOfParameters[rowIndex];
                return model.getName() + "." + p.getName();
            } else {
                return p.getName();
            }
        } else if (columnIndex == 1) {
            return p.getType();
        }
        // @todo ask quality software responsible to validate following code
        try {
            String getMethodName = "get" + columnNames[columnIndex];
            Method get = Parameter.class.getMethod(getMethodName, new Class[0]);
            String hasMethodName = "has" + columnNames[columnIndex];
            try {
                Method has = Parameter.class.getMethod(hasMethodName, new Class[0]);
                if (has.invoke(p, new Object[0]).equals(new Boolean(false))) {
                    return null;
                }
            } catch (NoSuchMethodException e) {
            }
            Object ret = get.invoke(p, new Object[0]);
            return ret;
        } catch (Exception e) {
            new FeedbackReport(null, true, e);
            return "Error";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnEditableFlags[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Parameter p = parameters[rowIndex];
        //logger.fine("parameter " + p.getName() + "@" + m.getName() + " old:" + getValueAt(rowIndex, columnIndex) + " new:" + aValue + "(" + aValue.getClass() + ")");
        // Check all methods that accept something else than a String as param
        if (columnNames[columnIndex].equals("Value")) {
            Double v = (Double) aValue;
            p.setValue(v);
        } else if (columnNames[columnIndex].equals("MinValue")) {
            if (aValue == null) {
                p.deleteMinValue();
            } else {
                Double v = (Double) aValue;
                p.setMinValue(v);
            }
        } else if (columnNames[columnIndex].equals("MaxValue")) {
            if (aValue == null) {
                p.deleteMinValue();
            } else {
                Double v = (Double) aValue;
                p.setMaxValue(v);
            }
        } else if (columnNames[columnIndex].equals("Scale")) {
            if (aValue == null) {
                p.deleteScale();
            } else {
                Double v = (Double) aValue;
                p.setScale(v);
            }
        } else if (columnNames[columnIndex].equals("HasFixedValue")) {
            Boolean b = (Boolean) aValue;
            p.setHasFixedValue(b);
        } else {
            try {
                String setMethodName = "set" + columnNames[columnIndex];
                Class[] c = new Class[]{aValue.getClass()};
                Method set = Parameter.class.getMethod(setMethodName, c);
                Object[] o = new Object[]{aValue};
                set.invoke(p, o);
                logger.fine("methode invoked using reflexion");
            } catch (Exception e) {
                new FeedbackReport(null, true, e);
            }
        }
    }
}
