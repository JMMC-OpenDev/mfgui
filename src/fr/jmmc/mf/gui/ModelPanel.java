/*
 * ModelPanel.java
 *
 * Created on 27 février 2008, 08:57
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;

import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Parameter;
import fr.jmmc.mf.models.ParameterLink;

import java.lang.reflect.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


/**
 *
 * @author  mella
 */
public class ModelPanel extends javax.swing.JPanel
{
    /**
     * DOCUMENT ME!
     */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            "fr.jmmc.mf.gui.ModelPanel");

    /**
     * DOCUMENT ME!
     */
    ParametersTableModel parametersTableModel;

    /**
     * DOCUMENT ME!
     */
    Model current;

    /**
     * DOCUMENT ME!
     */
    SettingsModel settingsModel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea   descTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPopupMenu parameterPopupMenu;
    private javax.swing.JTable parametersTable;
    private javax.swing.JComboBox typeComboBox;
    // End of variables declaration//GEN-END:variables

    /** Creates new form ModelPanel */
    public ModelPanel()
    {
        // first create our parameters table model
        parametersTableModel = new ParametersTableModel();
        initComponents();
    }

    /**
     * DOCUMENT ME!
     *
     * @param m DOCUMENT ME!
     * @param s DOCUMENT ME!
     */
    public void show(Model m, SettingsModel s)
    {
        current           = m;
        settingsModel     = s;
        nameTextField.setText(m.getName());
        typeComboBox.setSelectedItem(m);
        parametersTableModel.setModel(m, false);
        descTextArea.setText(m.getDesc());
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh()
    {
        show(current, settingsModel);
    }

    /**
     * Replace given parameter by a parameterLink and place parameter
     * under settings/parameters.
     */
    public void shareParameter(Parameter myParam)
    {
        logger.entering("" + this.getClass(), "shareParameter");

        // Shared parameters must have one id
        if (myParam.getId() == null)
        {
            myParam.setId(myParam.getName() + Integer.toHexString(myParam.hashCode()));
        }

        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(myParam);
        newLink.setType(myParam.getType());
        current.addParameterLink(newLink);

        Parameter[] params = current.getParameter();

        for (int i = 0; i < params.length; i++)
        {
            if (params[i] == myParam)
            {
                current.removeParameter(i);
            }
        }

        //menuRequested(app);
        settingsModel.getRootSettings().getParameters().addParameter(myParam);
        settingsModel.fireUpdate();
        refresh();
    }

    /**
     * DOCUMENT ME!
     *
     * @param myParam DOCUMENT ME!
     * @param sharedParameter DOCUMENT ME!
     */
    public void linkParameter(Parameter myParam, Parameter sharedParameter)
    {
        logger.entering("" + this.getClass(), "linkParameter");

        ParameterLink newLink = new ParameterLink();
        newLink.setParameterRef(sharedParameter);
        current.addParameterLink(newLink);

        Parameter[] params = current.getParameter();

        for (int i = 0; i < params.length; i++)
        {
            if (params[i] == myParam)
            {
                current.removeParameter(i);
            }
        }

        settingsModel.fireUpdate();
        refresh();
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void checkPopupMenu(java.awt.event.MouseEvent evt)
    {
        if (evt.isPopupTrigger())
        {
            logger.finest("Menu required");
            parameterPopupMenu.removeAll();

            // Check if pointed row is positive
            int rowIdx = parametersTable.rowAtPoint(evt.getPoint());

            if (rowIdx == -1)
            {
                return;
            }

            // Show info
            // @todo I would prefere to get the under mouse parameter selected before that menu appears
            // to handle under the mouse param instead of the
            final Parameter p        = current.getParameter(rowIdx);
            JMenuItem       menuItem = new JMenuItem("Manage parameter " + p.getName() +
                    " of type " + p.getType());
            menuItem.setEnabled(false);
            parameterPopupMenu.add(menuItem);
            parameterPopupMenu.add(new JSeparator());

            // Show share parameter entry
            menuItem = new JMenuItem("Share this parameter");
            menuItem.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        shareParameter(p);
                    }
                });
            parameterPopupMenu.add(menuItem);

            // Associate with shared parameter
            JMenu shareMenu = new JMenu("Link to ");
            parameterPopupMenu.add(shareMenu);

            Parameter[] sharedParams = settingsModel.getRootSettings().getParameters().getParameter();

            for (int i = 0; i < sharedParams.length; i++)
            {
                final Parameter sp = sharedParams[i];
                menuItem = new JMenuItem(sp.getName());
                menuItem.addActionListener(new java.awt.event.ActionListener()
                    {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                            linkParameter(p, sp);
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
        }
        else
        {
            logger.finest("No menu required");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        parameterPopupMenu     = new javax.swing.JPopupMenu();
        jPanel1                = new javax.swing.JPanel();
        jPanel2                = new javax.swing.JPanel();
        jLabel2                = new javax.swing.JLabel();
        nameTextField          = new javax.swing.JTextField();
        jLabel1                = new javax.swing.JLabel();
        typeComboBox           = new javax.swing.JComboBox();
        jPanel3                = new javax.swing.JPanel();
        jPanel5                = new javax.swing.JPanel();
        jPanel6                = new javax.swing.JPanel();
        jScrollPane2           = new javax.swing.JScrollPane();
        descTextArea           = new javax.swing.JTextArea();
        jPanel4                = new javax.swing.JPanel();
        jScrollPane1           = new javax.swing.JScrollPane();
        parametersTable        = new javax.swing.JTable();
        jPanel7                = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Model panel:"));
        jPanel1.setLayout(new java.awt.BorderLayout());
        add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Type:");
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 1;
        jPanel2.add(jLabel2, gridBagConstraints);

        nameTextField.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                {
                    nameTextFieldActionPerformed(evt);
                }
            });
        gridBagConstraints             = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx       = 1;
        gridBagConstraints.gridy       = 0;
        gridBagConstraints.fill        = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx     = 1.0;
        jPanel2.add(nameTextField, gridBagConstraints);

        jLabel1.setText("Name:");
        gridBagConstraints           = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx     = 0;
        gridBagConstraints.gridy     = 0;
        jPanel2.add(jLabel1, gridBagConstraints);

        typeComboBox.setModel(SettingsModel.supportedModelsModel);
        typeComboBox.setEnabled(false);
        gridBagConstraints             = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx       = 1;
        gridBagConstraints.gridy       = 1;
        gridBagConstraints.fill        = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx     = 1.0;
        jPanel2.add(typeComboBox, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setEnabled(false);

        descTextArea.setColumns(20);
        descTextArea.setEditable(false);
        descTextArea.setRows(5);
        jScrollPane2.setViewportView(descTextArea);

        jPanel6.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel6);

        jPanel3.add(jPanel5);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        jPanel4.setLayout(new java.awt.BorderLayout());

        parametersTable.setModel(parametersTableModel);
        parametersTable.setToolTipText("Use right click to manage parameters");
        parametersTable.addMouseListener(new java.awt.event.MouseAdapter()
            {
                public void mouseClicked(java.awt.event.MouseEvent evt)
                {
                    parametersTableMouseClicked(evt);
                }

                public void mousePressed(java.awt.event.MouseEvent evt)
                {
                    parametersTableMousePressed(evt);
                }

                public void mouseReleased(java.awt.event.MouseEvent evt)
                {
                    parametersTableMouseReleased(evt);
                }
            });
        jScrollPane1.setViewportView(parametersTable);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel4);

        add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new java.awt.GridBagLayout());
        add(jPanel7, java.awt.BorderLayout.SOUTH);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt)
    { //GEN-FIRST:event_nameTextFieldActionPerformed
        logger.entering("" + this.getClass(), "nameTextFieldActionPerformed");
        current.setName(nameTextField.getText());
    } //GEN-LAST:event_nameTextFieldActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void parametersTableMouseClicked(java.awt.event.MouseEvent evt)
    { //GEN-FIRST:event_parametersTableMouseClicked
        logger.entering("" + this.getClass(), "parametersTableMouseClicked");
        checkPopupMenu(evt);
    } //GEN-LAST:event_parametersTableMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void parametersTableMousePressed(java.awt.event.MouseEvent evt)
    { //GEN-FIRST:event_parametersTableMousePressed
        logger.entering("" + this.getClass(), "parametersTableMousePressed");
        checkPopupMenu(evt);
    } //GEN-LAST:event_parametersTableMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void parametersTableMouseReleased(java.awt.event.MouseEvent evt)
    { //GEN-FIRST:event_parametersTableMouseReleased
        logger.entering("" + this.getClass(), "parametersTableMouseReleased");
        checkPopupMenu(evt);
    } //GEN-LAST:event_parametersTableMouseReleased

    /**
     * Implementation of a table model that is based on a given Model.
     */
    class ParametersTableModel extends AbstractTableModel
    {
        protected Model       currentModel = null;
        protected boolean     recursive;
        protected Parameter[] parameters;

        // Store model of corresponding parameter in parameters array
        protected Model[] modelOfParameters;

        // Init columns titles and types
        protected final String[] columnNames = new String[]
            {
                "Name", "Type", "Units", "Value", "MinValue", "MaxValue", "Scale", "HasFixedValue"
            };
        protected final Class[]  columnTypes = new Class[]
            {
                String.class, String.class, String.class, Double.class, Double.class, Double.class,
                Double.class, Boolean.class
            };

        public ParametersTableModel()
        {
            // next static line should be replaced by a preference listener            
            recursive = true;
        }

        /**
         * tell table model to represent the parameters of the given model.
         */
        public void setModel(Model modelToPresent, boolean recursive)
        {
            currentModel       = modelToPresent;
            this.recursive     = recursive;
            parameters         = new Parameter[] {  };

            if (currentModel != null)
            {
                // get list , create array and init array with content list
                Vector params = new Vector();
                Vector models = new Vector();
                addParamsFor(currentModel, params, models, recursive);
                parameters            = new Parameter[params.size()];
                modelOfParameters     = new Model[params.size()];

                for (int i = 0; i < parameters.length; i++)
                {
                    parameters[i]            = (Parameter) params.elementAt(i);
                    modelOfParameters[i]     = (Model) models.elementAt(i);
                }
            }

            // notify observers
            fireTableDataChanged();
        }

        protected void addParamsFor(Model model, Vector paramContainer, Vector modelContainer,
            boolean recursive)
        {
            // Start to append model parameters
            Parameter[] params     = model.getParameter();
            int         nbOfParams = params.length;

            // Create with initial data
            for (int i = 0; i < nbOfParams; i++)
            {
                Parameter p = params[i];
                paramContainer.add(p);
                modelContainer.add(model);
            }

            // Then append model parameters that are linked
            ParameterLink[] paramLinks = model.getParameterLink();
            nbOfParams = paramLinks.length;
            logger.fine("Adding " + nbOfParams + " shared parameters");

            // Create with initial data
            for (int i = 0; i < nbOfParams; i++)
            {
                ParameterLink link = paramLinks[i];
                Parameter     p    = (Parameter) link.getParameterRef();
                paramContainer.add(p);
                modelContainer.add(model);
            }

            if (recursive)
            {
                Model[] models = model.getModel();

                for (int i = 0; i < models.length; i++)
                {
                    addParamsFor(models[i], paramContainer, modelContainer, true);
                }
            }
        }

        // Next parts makes respond to the full TableModel interface        
        public Class getColumnClass(int columnIndex)
        {
            return columnTypes[columnIndex];
        }

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public String getColumnName(int columnIndex)
        {
            return columnNames[columnIndex];
        }

        public int getRowCount()
        {
            if (parameters != null)
            {
                return parameters.length;
            }

            return 0;
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            Parameter p = parameters[rowIndex];

            // return name
            if (columnIndex == 0)
            {
                if (recursive)
                {
                    Model model = modelOfParameters[rowIndex];

                    return model.getName() + "." + p.getName();
                }
                else
                {
                    return p.getName();
                }
            }
            else if (columnIndex == 1)
            {
                return p.getType();
            }

            // @todo ask quality software responsible to validate following code
            try
            {
                String getMethodName = "get" + columnNames[columnIndex];
                Method get           = Parameter.class.getMethod(getMethodName, new Class[0]);

                String hasMethodName = "has" + columnNames[columnIndex];

                try
                {
                    Method has = Parameter.class.getMethod(hasMethodName, new Class[0]);

                    if (has.invoke(p, new Object[0]).equals(new Boolean(false)))
                    {
                        return null;
                    }
                }
                catch (NoSuchMethodException e)
                {
                }

                Object ret = get.invoke(p, new Object[0]);

                return ret;
            }
            catch (Exception e)
            {
                new FeedbackReport(null, true, e);

                return "Error";
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            Parameter p = parameters[rowIndex];

            // return always true
            return true;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            Parameter p = parameters[rowIndex];
            Model     m = modelOfParameters[rowIndex];
            logger.fine("parameter " + p.getName() + "@" + m.getName() + " old:" +
                getValueAt(rowIndex, columnIndex) + " new:" + aValue + "(" + aValue.getClass() +
                ")");

            // Check all methods that accept something else than a String as param
            if (columnNames[columnIndex].equals("Value"))
            {
                Double v = (Double) aValue;
                p.setValue(v);
            }
            else if (columnNames[columnIndex].equals("MinValue"))
            {
                Double v = (Double) aValue;
                p.setMinValue(v);
            }
            else if (columnNames[columnIndex].equals("MaxValue"))
            {
                Double v = (Double) aValue;
                p.setMaxValue(v);
            }
            else if (columnNames[columnIndex].equals("Scale"))
            {
                Double v = (Double) aValue;
                p.setScale(v);
            }
            else if (columnNames[columnIndex].equals("HasFixedValue"))
            {
                Boolean b = (Boolean) aValue;
                p.setHasFixedValue(b);
            }
            else
            {
                try
                {
                    String   setMethodName = "set" + columnNames[columnIndex];

                    Class[]  c             = new Class[] { aValue.getClass() };
                    Method   set           = Parameter.class.getMethod(setMethodName, c);
                    Object[] o             = new Object[] { aValue };
                    set.invoke(p, o);
                    logger.fine("methode invoked using reflexion");
                }
                catch (Exception e)
                {
                    new FeedbackReport(null, true, e);
                }
            }
        }
    }

}
