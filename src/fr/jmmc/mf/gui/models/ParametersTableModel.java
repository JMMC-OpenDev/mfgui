package fr.jmmc.mf.gui.models;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Target;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.event.TreeModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * Implementation of a table model that is based on a given Model.
 * This model also respond to mouseListener Interface so it can show popup menu
 * to handle parameters sharing.
 *
 * @todo: check if model container should be vectors instead of arrays
 *
 */
public class ParametersTableModel extends AbstractTableModel implements MouseListener, Observer{

    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ParametersTableModel");
    protected boolean recursive;
    // Store model of corresponding parameter in parameters array
    protected Model[] modelOfParameters;
    // Init columns titles and types
    protected final String[] columnNames = new String[]{"Name", "Type", "Units", "Value", "MinValue", "MaxValue", "Scale", "HasFixedValue"};
    protected final Class[] columnTypes = new Class[]{String.class, String.class, String.class, Double.class, Double.class, Double.class, Double.class, Boolean.class};
    protected final Boolean[] columnEditableFlags = new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
    private javax.swing.JPopupMenu parameterPopupMenu = new javax.swing.JPopupMenu();
    ;
    private SettingsModel settingsModel;
    private Target targetToPresent;
    private Model modelToPresent;
    private Parameter[] parametersToPresent;
    private Vector<SettingsModel> knownSettingsModels = new Vector();

    public ParametersTableModel() {
        super();
        // next static line should be replaced by a preference listener
        recursive = true;
    }

    public void setModel(SettingsModel settingsModel, Target targetToPresent, boolean recursive) {
        refreshModel(settingsModel, targetToPresent, null, null, recursive);
    }

    /**
     * tell table model to represent the parameters of the given model.
     */
    public void setModel(SettingsModel settingsModel, Model modelToPresent, boolean recursive) {
        refreshModel(settingsModel, null, modelToPresent, null, recursive);
    }

    public void setModel(SettingsModel settingsModel, Parameter[] parametersToPresent, boolean recursive) {
        refreshModel(settingsModel, null, null, parametersToPresent, recursive);
    }

    private void refreshModel(SettingsModel settingsModel, Target target, Model model, Parameter[] parameters, boolean recursive) {
        this.settingsModel = settingsModel;
        this.targetToPresent = target;
        this.modelToPresent = model;
        this.parametersToPresent = parameters;
        this.recursive = recursive;

        // we want to listen model change events
        if (!knownSettingsModels.contains(settingsModel)) {
            settingsModel.addObserver(this);
            knownSettingsModels.add(settingsModel);
        }

        if (targetToPresent != null) {
            parametersToPresent = new Parameter[]{};
            // get list , create array and init array with content list
            Vector<Parameter> params = new Vector();
            Vector<Model> models = new Vector();
            Model[] array = targetToPresent.getModel();
            for (int i = 0; i < array.length; i++) {
                Model m = array[i];
                addParamsFor(m, params, models, recursive);
            }
            parametersToPresent = new Parameter[params.size()];
            modelOfParameters = new Model[params.size()];
            for (int i = 0; i < parametersToPresent.length; i++) {
                parametersToPresent[i] = params.elementAt(i);
                modelOfParameters[i] = (Model) models.elementAt(i);
            }
            // notify observers
            fireTableDataChanged();
            return;
        }

        if (modelToPresent != null) {
            parametersToPresent = new Parameter[]{};
            // get list , create array and init array with content list
            Vector<Parameter> params = new Vector();
            Vector<Model> models = new Vector();
            addParamsFor(modelToPresent, params, models, recursive);
            parametersToPresent = new Parameter[params.size()];
            modelOfParameters = new Model[params.size()];
            for (int i = 0; i < parametersToPresent.length; i++) {
                parametersToPresent[i] = params.elementAt(i);
                modelOfParameters[i] = (Model) models.elementAt(i);
            }
            // notify observers
            fireTableDataChanged();
            return;
        }

        if (parametersToPresent != null) {
            this.parametersToPresent = parameters;
            modelOfParameters = new Model[this.parametersToPresent.length];
            if (recursive) {
                for (int i = 0; i < parametersToPresent.length; i++) {
                    Parameter parameter = parametersToPresent[i];
                    System.out.println("parameter = " + parameter);
                    modelOfParameters[i] = settingsModel.getParent(parameter);
                }
            }
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                System.out.println("parameter = " + parameter);
            }
            // notify observers
            fireTableDataChanged();
            return;
        }
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
        if (parametersToPresent != null) {
            return parametersToPresent.length;
        }
        return 0;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Parameter p = parametersToPresent[rowIndex];
        if (p == null) {
            return null;
        }
        // return name
        if (columnIndex == 0) {
            if (recursive) {
                Model model = modelOfParameters[rowIndex];
                if(settingsModel.isSharedParameter(p)){
                    return p.getName();
                }else if (model != null) {
                    return model.getName() + "." + p.getName();
                } else {
                    logger.warning("Can't find model for parameter " + rowIndex);
                    return "???." + p.getName();
                }
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
        Parameter p = parametersToPresent[rowIndex];
        if (aValue != null) {
            logger.fine("parameter " + p.getName() + " old:" + getValueAt(rowIndex, columnIndex) + " new:" + aValue + "(" + aValue.getClass() + ")");
        } else {
            logger.fine("parameter " + p.getName() + " old:" + getValueAt(rowIndex, columnIndex) + " new:" + aValue + "(ie EMPTY CELL)");
        }
        // Check all methods that accept something else than a String as param
        // introspection can't be used because Objects are given as parmaeter
        // and most of parameter ones accept double only :(
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
                p.deleteMaxValue();
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
                String methodName;
                if (aValue != null) {
                    methodName = "set" + columnNames[columnIndex];
                    Class[] c = new Class[]{aValue.getClass()};
                    Method set = Parameter.class.getMethod(methodName, c);
                    Object[] o = new Object[]{aValue};
                    set.invoke(p, o);
                } else {
                    methodName = "delete" + columnNames[columnIndex];
                    Method m = Parameter.class.getMethod(methodName);
                    m.invoke(p);
                }
                logger.fine("method " + methodName + " invoked using reflexion");
            } catch (Exception e) {
                new FeedbackReport(null, true, e);
            }
        }
    }

    private void checkPopupMenu(java.awt.event.MouseEvent evt) {
        if (!(evt.getSource() instanceof JTable)) {
            logger.warning("Dropping Mouse event :" + evt);
            return;
        }

        JTable parametersTable = (JTable) evt.getSource();
        if (evt.isPopupTrigger()) {
            logger.finest("Menu required");
            parameterPopupMenu.removeAll();

            // Check if pointed row is positive and select row
            int rowIdx = parametersTable.rowAtPoint(evt.getPoint());
            if (rowIdx == -1) {
                return;
            }
            parametersTable.getSelectionModel().setSelectionInterval(rowIdx, rowIdx);

            // Build menu
            final Parameter p = parametersToPresent[rowIdx];
            JMenuItem menuItem = new JMenuItem("Manage parameter " + p.getName() +
                    " of type " + p.getType());
            menuItem.setEnabled(false);
            parameterPopupMenu.add(menuItem);
            parameterPopupMenu.add(new JSeparator());

            if (!settingsModel.isSharedParameter(p)) {
                // Show share parameter entry
                menuItem = new JMenuItem("Share this parameter");
                menuItem.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        settingsModel.shareParameter(p);
                        // notify observers
                        fireTableDataChanged();
                    }
                });
                parameterPopupMenu.add(menuItem);
            }

            // Associate with shared parameter
            JMenu shareMenu = new JMenu("Link to ");
            parameterPopupMenu.add(shareMenu);

            Parameter[] sharedParams = settingsModel.getSharedParameters();

            for (int i = 0; i < sharedParams.length; i++) {
                final Parameter sp = sharedParams[i];
                menuItem = new JMenuItem(sp.getName());
                menuItem.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        settingsModel.linkParameter(p, sp);
                        // notify observers
                        fireTableDataChanged();
                    }
                });
                /* we previously shared parameters with same types
                if (!p.getType().equals(sp.getType())) {
                menuItem.setEnabled(false);
                }
                 */
                shareMenu.add(menuItem);
            }
            parameterPopupMenu.validate();
            parameterPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        } else {
            logger.finest("No menu required");
        }
    }

    public void mouseClicked(MouseEvent e) {
        checkPopupMenu(e);
    }

    public void mousePressed(MouseEvent e) {
        checkPopupMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
        checkPopupMenu(e);
    }

    public void mouseEntered(MouseEvent e) {
        checkPopupMenu(e);
    }

    public void mouseExited(MouseEvent e) {
        checkPopupMenu(e);
    }

    public void treeNodesChanged(TreeModelEvent e) {
        refreshModel(settingsModel,targetToPresent, modelToPresent,parametersToPresent, recursive);
        System.out.println("toto"+e);
    }

    public void treeNodesInserted(TreeModelEvent e) {
    }

    public void treeNodesRemoved(TreeModelEvent e) {
    }

    public void treeStructureChanged(TreeModelEvent e) {
    }

    public void update(Observable o, Object arg) {
        refreshModel(settingsModel,targetToPresent, modelToPresent,parametersToPresent, recursive);
    }
}
