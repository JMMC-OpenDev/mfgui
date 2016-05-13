/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.util;

import fr.jmmc.mf.ModelUtils;
import fr.jmmc.mf.models.Model;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Custom renderer to represent various models types and 
 * @author mella
 */
public class ModelListRenderer extends DefaultListCellRenderer {

    /* members */
    /**
     * Public constructor   
     */
    public ModelListRenderer() {
    }

    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     *
     * @param list The JList we're painting.
     * @param value The value returned by list.getModel().getElementAt(index).
     * @param index The cells index.
     * @param isSelected True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     *
     * @see JList
     * @see ListSelectionModel
     * @see ListModel
     */
    @Override
    public Component getListCellRendererComponent(
            final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {

        final String val;
        if (value == null) {
            val = null;
        } else if (value instanceof Model) {
            Model m = (Model) value;
            if (m.getOperatorCount() == 0) {
                val = m.getType();
            } else {
                StringBuilder sb = new StringBuilder(32);
                sb.append("( ");
                for (int i = 0; i < m.getOperatorCount(); i++) {
                    sb.append(m.getOperator(i).getName());
                    sb.append(" ");
                }
                sb.append(")");
                val = m.getType() + sb.toString();
            }

            /* TODO change name depending of operators
             // Handle polar and stretched attributes
             if (!m.getPolar() && !m.getStretched()) {
             val = m.getType();
             } else if (m.getPolar() && m.getStretched()) {
             val = m.getType() + " (polar, stretched)";
             } else if (m.getPolar()) {
             val = m.getType() + " (polar)";
             } else {
             val = m.getType() + " (stretched)";
             }
             */
        } else {
            val = value.toString();
        }

        super.getListCellRendererComponent(
                list, val, index,
                isSelected, cellHasFocus);

        if (value instanceof Model) {
            Model m = (Model) value;
            // put custom in italics
            if (ModelUtils.isUserModel(m)) {
                this.setFont(this.getFont().deriveFont(Font.ITALIC + Font.BOLD));
            }

            /* TODO set Icon : setIcon(this, (Model) value); */
        }

        return this;
    }
}
