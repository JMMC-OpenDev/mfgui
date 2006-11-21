/*
 * NewClass.java
 *
 * Created on 4 octobre 2006, 15:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jmmc.mf.gui;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;

/**
 * This class is here just to locate code parts that should be moved under 
 * one jmmc.mcs.* package
 * @author mella
 */
public class McsClass {
    
     /*
     * This method picks good column sizes with given maxWidth limit.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     *@todo move this function into one mcs common area
     */
    public static void initColumnSizes(JTable table, int maxWidth) {        
        TableModel model = table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                column = table.getColumnModel().getColumn(j);
                
                comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, j);
                headerWidth = comp.getPreferredSize().width;
                
                comp = table.getDefaultRenderer(model.getColumnClass(j)).
                    getTableCellRendererComponent(
                    table, model.getValueAt(i,j),
                    false, false, i, j);
                cellWidth = comp.getPreferredSize().width;
                
                column.setPreferredWidth(
                    Math.min(maxWidth, Math.max(headerWidth, cellWidth)));
            }
        }
    }
    
}
