/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.util;

import fr.jmmc.jmcs.util.StringUtils;
import fr.jmmc.mf.ModelUtils;
import fr.jmmc.mf.models.Model;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 * Model list that just have a special renderer and generate tooltip messages.
 * @author mella
 */
public class ModelList extends JList {

    private boolean displayTooltips;

    /*
     * Constructor
     */
    public ModelList() {
        this.setCellRenderer(new ModelListRenderer());
    }

    public void displayTooltips(boolean displayTooltips) {
        this.displayTooltips = displayTooltips;
    }

    @Override
    public String getToolTipText(final MouseEvent evt) {
        if (!displayTooltips) {
            return null;
        }

        StringBuffer msg = new StringBuffer(256);
        msg.append("<html>");
        int pointedIndex = locationToIndex(evt.getPoint());
        if (pointedIndex != -1) {
            Model pointedModel = (Model) getModel().getElementAt(pointedIndex);
            // TODO move this block in ModelUtils common area so we can use it in other widgets
            msg.append("<h1>");
            String shortDesc = pointedModel.getShortdesc();
            if (StringUtils.isEmpty(shortDesc)) {
                msg.append(pointedModel.getType());
            } else {
                msg.append(shortDesc);
            }
            if (ModelUtils.isUserModel(pointedModel)) {
                msg.append("<em>( user model )</em>");
            }
            msg.append("</h1>");

            if (ModelUtils.isUserModel(pointedModel)) {
                msg.append("Usercode: <pre>").append(pointedModel.getCode()).append("</em>");
            }
            msg.append("<pre>").append(pointedModel.getDesc()).append("</pre>");
        }
        msg.append("</html>");
        return msg.toString();
    }

}
