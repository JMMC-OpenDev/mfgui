/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.mf.gui.*;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.mf.gui.models.SettingsModel;
import fr.nom.tam.fits.FitsException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;

public class LoadRemoteModelAction extends RegisteredAction {

    final static String className = LoadRemoteModelAction.class.getName();
    public String lastURL = "";
    MFGui mfgui;

    public LoadRemoteModelAction(MFGui mfgui) {
        super(className, "loadRemoteModel");
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        String url = (String) JOptionPane.showInputDialog("Enter URL of remote setting:", lastURL);
        
        // just return if user cancel action
        if (url == null){
            return;
        }
        
        try {
            mfgui.addSettings(new SettingsModel(url));
        } catch (IOException ex) {
            MessagePane.showErrorMessage("Could not load remote url : " + url, ex);
        } catch (ExecutionException ex) {
            MessagePane.showErrorMessage("Could not load remote url : " + url, ex);
        } catch (FitsException ex) {
            MessagePane.showErrorMessage("Could not load fits file from remote url : " + url, ex);
        }

        lastURL = url;
    }
}
