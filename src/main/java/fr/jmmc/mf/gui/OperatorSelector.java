/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.mf.ModelUtils;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Model;
import fr.jmmc.mf.models.Operator;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Widget that show and modifiy the operators associated to a model.
 * @author mellag
 */
public class OperatorSelector extends JToolBar {
    ListSelectionEvent event;

    public OperatorSelector() {
        this.setFloatable(false);
        event = new ListSelectionEvent(this, 0, 0, false);
    }

    public void setModel(final Model m, final ListSelectionListener listener) {

        if (m == null) {
            setVisible(false);
            return;
        }
        setVisible(true);

        this.removeAll();

        // get list of supported operators
        ListModel operators = SettingsModel.getSupportedOperators();
        for (int i = 0; i < operators.getSize(); i++) {
            final Operator operator = (Operator) operators.getElementAt(i);
            // associated on toggle button per operator
            JToggleButton b = new JToggleButton(operator.getName());
            b.setToolTipText(operator.getDesc());
            // fix state reading the given model
            b.setSelected(ModelUtils.hasOperator(m, operator.getName()));

            // TODO attach a general event handler that will manage changes (hard : unset or soft : set within a group)
            // TODO append them to the current toolbar with separators between groups
            this.add(b);
            b.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent ev) {
                    modifyOperator(operator, ev.getStateChange() == ItemEvent.SELECTED, m, listener);
                }
            });
        }
    }

    private void modifyOperator(Operator operator, boolean set, Model m, ListSelectionListener listener) {
        if (set) {
            Operator o = (Operator) UtilsClass.clone(operator);
            m.addOperator(o);
        } else {
            ModelUtils.removeOperator(m, operator.getName());
        }
        listener.valueChanged(event);
    }
}
