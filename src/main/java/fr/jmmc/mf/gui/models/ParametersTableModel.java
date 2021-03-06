/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.models;

import fr.jmmc.jmcs.util.IntrospectionUtils;
import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.mf.ModelUtils;
import fr.jmmc.mf.gui.UtilsClass;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;
import fr.jmmc.mf.models.Target;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a table model that is based on a given Model.
 * This model also respond to mouseListener Interface so it can show popup menu
 * to handle parameters sharing.
 *
 * @todo: check if model container should be vectors instead of arrays
 *
 */
public final class ParametersTableModel extends AbstractTableModel implements MouseListener {

    private final static Logger logger = LoggerFactory.getLogger(ParametersTableModel.class.getName());

    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_TYPE = "Type";

    // Init columns titles and types
    // TODO: refactor using generic jmcs ColumnDef ?
    private final static String[] columnNames = new String[]{COLUMN_NAME, COLUMN_TYPE, "Units", "Value", "MinValue", "MaxValue", "Scale", "HasFixedValue"};
    private final static Class[] columnTypes = new Class[]{String.class, String.class, String.class, Double.class, Double.class, Double.class, Double.class, Boolean.class};
    private final static Boolean[] columnEditableFlags = new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};

    /* members */
    protected boolean recursive;
    // Store model of corresponding parameter in parameters array
    protected Model[] modelOfParameters;
    private javax.swing.JPopupMenu parameterPopupMenu = new javax.swing.JPopupMenu();
    private SettingsModel settingsModel;
    private Target targetToPresent;
    private Model modelToPresent;
    private Object[] parametersOrParameterLinksToPresent;
    private boolean editForCustomModel;

    public ParametersTableModel() {
        super();
        // next static line should be replaced by a preference listener
        recursive = true;
    }

    /**
     * Tells to the table model to show the parameters of the given target.
     */
    public void setModel(SettingsModel settingsModel, Target targetToPresent, boolean recursive) {
        logger.debug("Updating model for given target {}", targetToPresent);
        refreshModel(settingsModel, targetToPresent, null, null, recursive);
    }

    /**
     * Tells to the table model to show the parameters of the given model.
     */
    public void setModel(SettingsModel settingsModel, Model modelToPresent, boolean recursive) {
        logger.debug("Updating model for given model {}", modelToPresent);
        refreshModel(settingsModel, null, modelToPresent, null, recursive);
    }

    /**
     * Tells to the table model to show the given parameters.
     */
    public void setModel(SettingsModel settingsModel, Parameter[] parametersToPresent, boolean recursive) {
        logger.debug("Updating model for a given parameter list");
        refreshModel(settingsModel, null, null, parametersToPresent, recursive);
    }

    private void refreshModel(SettingsModel settingsModel, Target target, Model model, Object[] parametersAndParameterLinks, boolean recursive) {
        this.settingsModel = settingsModel;
        this.targetToPresent = target;
        this.modelToPresent = model;
        this.parametersOrParameterLinksToPresent = parametersAndParameterLinks;
        this.recursive = recursive;

        // do not allow edition of param type by default
        editForCustomModel = false;

        if (targetToPresent != null) {
            parametersOrParameterLinksToPresent = new Parameter[]{};
            // get list , create array and init array with content list
            Vector params = new Vector();
            Vector<Model> models = new Vector();
            Model[] array = targetToPresent.getModel();
            for (int i = 0; i < array.length; i++) {
                Model m = array[i];
                addParamsFor(m, params, models, recursive);
            }
            // second stage: fill parameter and model 'rows'
            parametersOrParameterLinksToPresent = new Object[params.size()];
            modelOfParameters = new Model[params.size()];
            for (int i = 0; i < parametersOrParameterLinksToPresent.length; i++) {
                parametersOrParameterLinksToPresent[i] = params.elementAt(i);
                modelOfParameters[i] = (Model) models.elementAt(i);
            }
        } else if (modelToPresent != null) {
            // accept to edit only for custom models that have no instance
            editForCustomModel = StringUtils.isSet(modelToPresent.getCode())
                    && settingsModel != null
                    && ModelUtils.hasModelOfType(settingsModel.getUserCode().getModel(), modelToPresent.getType())
                    && !ModelUtils.hasModelOfType(settingsModel.getRootSettings(), modelToPresent.getType());
            parametersOrParameterLinksToPresent = new Parameter[]{};
            // get list , create array and init array with content list
            Vector params = new Vector();
            Vector<Model> models = new Vector();
            addParamsFor(modelToPresent, params, models, recursive);
            parametersOrParameterLinksToPresent = new Object[params.size()];
            modelOfParameters = new Model[params.size()];
            for (int i = 0; i < parametersOrParameterLinksToPresent.length; i++) {
                parametersOrParameterLinksToPresent[i] = params.elementAt(i);
                modelOfParameters[i] = (Model) models.elementAt(i);
            }
        } else if (parametersOrParameterLinksToPresent != null) {
            this.parametersOrParameterLinksToPresent = parametersAndParameterLinks;
            modelOfParameters = new Model[this.parametersOrParameterLinksToPresent.length];
            if (recursive) {
                for (int i = 0; i < parametersOrParameterLinksToPresent.length; i++) {
                    Object o = parametersOrParameterLinksToPresent[i];
                    if (isParameterLink(o)) {
                        modelOfParameters[i] = settingsModel.getParent((ParameterLink) o);
                    } else {
                        Parameter p = (Parameter) o;
                        if (settingsModel.isSharedParameter(p)) {
                            modelOfParameters[i] = null;
                        } else {
                            modelOfParameters[i] = settingsModel.getParent(p);
                        }
                    }
                }
            }
        }

        // notify observers
        fireTableDataChanged();
    }

    protected void addParamsFor(Model model, Vector paramContainer, Vector<Model> modelContainer, boolean recursive) {
        ModelUtils.addParamsFor(model, paramContainer, modelContainer, recursive, true, true);
    }

    /* TableModel interface implementation */
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
        if (parametersOrParameterLinksToPresent != null) {
            return parametersOrParameterLinksToPresent.length;
        }
        return 0;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object o = parametersOrParameterLinksToPresent[rowIndex];
        if (o == null) {
            return null;
        }
        final Parameter p;
        final ParameterLink pl;

        if (isParameterLink(o)) {
            p = (Parameter) ((ParameterLink) o).getParameterRef();
            pl = (ParameterLink) o;
        } else {
            pl = null;
            p = (Parameter) o;
        }

        // return name
        if (columnIndex == 0) {
            if (recursive) {
                if (pl != null) {
                    // hide the model name for one shared parameter
                    return p.getName();
                }
                final Model model = modelOfParameters[rowIndex];
                if (model != null) {
                    return model.getName() + "." + p.getName();
                }
                // there is no parent: it is probably a shared parameter
            }
            return p.getName();
        } else if (columnIndex == 1) {
            if (pl != null) {
                return pl.getType();
            }
            return p.getType();
        }
        // @todo ask quality software responsible to validate following code
        final String colName = columnNames[columnIndex];

        final Method hasMethod;
        if (!"Units".equals(colName)) {
            final String hasMethodName = "has" + colName;
            hasMethod = IntrospectionUtils.getMethod(Parameter.class, hasMethodName);
        } else {
            hasMethod = null;
        }

        // not illegal because most parameters haven't some attributes
        if (hasMethod != null) {
            final Object hasValue = IntrospectionUtils.getMethodValue(hasMethod, p);
            if (Boolean.FALSE.equals(hasValue)) {
                return null;
            }
        }

        final String getMethodName = "get" + colName;
        final Method getMethod = IntrospectionUtils.getMethod(Parameter.class, getMethodName);

        if (getMethod != null) {
            return IntrospectionUtils.getMethodValue(getMethod, p);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // in full editin mode, the name is a copy of type
        return (editForCustomModel && columnNames[columnIndex] != COLUMN_NAME) || columnEditableFlags[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Object o = parametersOrParameterLinksToPresent[rowIndex];

        final Parameter p;
        if (isParameterLink(o)) {
            p = (Parameter) ((ParameterLink) o).getParameterRef();
        } else {
            p = (Parameter) o;
        }

        final Parameter pprime = (Parameter) UtilsClass.clone(p);

        // Check all methods that accept something else than a String as param
        // introspection can't be used because Objects are given as parameter
        // and most of parameter ones accept double only :(
        if (columnNames[columnIndex].equals("Value")) {
            if (aValue != null) {
                Double v = (Double) aValue;
                p.setValue(v);
            }
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
                    // TODO: fix use IntrospectionUtils ?
                    Method set = Parameter.class.getMethod(methodName, c);
                    Object[] obj = new Object[]{aValue};
                    set.invoke(p, obj);
                    if (columnNames[columnIndex] == COLUMN_TYPE && editForCustomModel) {
                        p.setName((String) aValue);
                    }
                } else {
                    // TODO: fix use IntrospectionUtils ?
                    methodName = "delete" + columnNames[columnIndex];
                    Method m = Parameter.class.getMethod(methodName);
                    m.invoke(p);
                }
            } catch (InvocationTargetException ex) {
                throw new IllegalStateException("Can't set data onto setting", ex);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Can't set data onto setting", ex);
            } catch (SecurityException ex) {
                throw new IllegalStateException("Can't set data onto setting", ex);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Can't set data onto setting", ex);
            }
        }

        if (!ModelUtils.areEquals(p, pprime)) {
            settingsModel.setModified();
            settingsModel.notifyObservers();
            fireTableDataChanged();
        }
    }

    private void checkPopupMenu(java.awt.event.MouseEvent evt) {
        if (!(evt.getSource() instanceof JTable)) {
            logger.warn("Dropping Mouse event : {}", evt);
            return;
        }

        JTable parametersTable = (JTable) evt.getSource();
        if (evt.isPopupTrigger()) {
            logger.trace("Menu required");
            parameterPopupMenu.removeAll();

            // Check if pointed row is positive and select row
            int rowIdx = parametersTable.rowAtPoint(evt.getPoint());
            if (rowIdx == -1) {
                return;
            }
            parametersTable.getSelectionModel().setSelectionInterval(rowIdx, rowIdx);

            // Build menu
            final Object o = parametersOrParameterLinksToPresent[rowIdx];
            final Parameter p;
            if (isParameterLink(o)) {
                p = (Parameter) ((ParameterLink) o).getParameterRef();
            } else {
                p = (Parameter) o;
            }

            JMenuItem menuItem = new JMenuItem("Manage parameter " + p.getName()
                    + " of type " + p.getType());
            menuItem.setEnabled(false);
            parameterPopupMenu.add(menuItem);
            parameterPopupMenu.add(new JSeparator());

            if (!settingsModel.isSharedParameter(p)) {
                // Show share parameter entry
                menuItem = new JMenuItem("Share this parameter");
                menuItem.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        settingsModel.shareParameter(p);
                        refreshModel(settingsModel, targetToPresent, modelToPresent, parametersOrParameterLinksToPresent, recursive);
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
                        if (isParameterLink(o)) {
                            settingsModel.linkSharedParameter((ParameterLink) o, sp);
                        } else {
                            settingsModel.linkParameter(p, sp);
                        }
                        refreshModel(settingsModel, targetToPresent, modelToPresent, parametersOrParameterLinksToPresent, recursive);
                    }
                });
                /* we previously shared parameters with same types
                 if (!p.getType().equals(sp.getType())) {
                 menuItem.setEnabled(false);
                 }
                 */
                shareMenu.add(menuItem);
            }

            // Add model information into the
            parameterPopupMenu.add(new JSeparator());

            if (isParameterLink(o)) {
                Model m = settingsModel.getParent((ParameterLink) o);
                if (m != null) {
                    menuItem = new JMenuItem("This model is located at " + ModelUtils.getRelativeCoords(m) + " relatively to the center of this target.");
                    menuItem.setEnabled(false);
                    parameterPopupMenu.add(menuItem);
                }
            } else {
                Model m = modelToPresent == null ? settingsModel.getParent(p) : modelToPresent;
                menuItem = new JMenuItem("This model is located at " + ModelUtils.getRelativeCoords(m) + " relatively to the center of this target.");
                menuItem.setEnabled(false);
                parameterPopupMenu.add(menuItem);
            }

            parameterPopupMenu.validate();
            parameterPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
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

    private boolean isParameterLink(Object parameterOrParameterLink) {
        return parameterOrParameterLink instanceof ParameterLink;
    }
}
