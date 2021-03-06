/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.mf.gui.*;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.nom.tam.fits.FitsException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;

public class LoadDemoModelAction extends RegisteredAction {

    final static String className = LoadDemoModelAction.class.getName();
    String demoURL_ = "";
    MFGui mfgui;

    public LoadDemoModelAction(String id, String demoURL, String demoDesc, MFGui mfgui) {
        super(className, id, demoDesc);
        this.mfgui = mfgui;
        this.putValue(Action.NAME, demoDesc);
        demoURL_ = demoURL;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            mfgui.addSettings(new SettingsModel(demoURL_));
        } catch (IOException ex) {
            MessagePane.showErrorMessage("Could not load url : " + demoURL_, ex);
        } catch (ExecutionException ex) {
            MessagePane.showErrorMessage("Could not load url : " + demoURL_, ex);
        } catch (FitsException ex) {
            MessagePane.showErrorMessage("Could not read fits file from remote setting: " + demoURL_, ex);
        }
    }
}
