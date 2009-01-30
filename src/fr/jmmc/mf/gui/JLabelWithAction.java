/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.jmmc.mf.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author mella
 */
public class JLabelWithAction extends JPanel implements ListCellRenderer{
    JLabel label;
    JButton button;
    public JLabelWithAction(Action a){
        super();
         label= new JLabel();
        add(label);
        button = new JButton(a);
        add(button);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        label.setText(""+value);
        label.setBackground(isSelected ? Color.red : Color.white);
         label.setForeground(isSelected ? Color.white : Color.black);
        return this;
    }

}
