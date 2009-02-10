/*
 * SettingsViewerInterface.java
 *
 * Created on 6 decembre 2006, 12:07
 *
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;
import fr.jmmc.mf.models.Response;
import javax.swing.JFrame;

/**
 *
 * @author mella
 */
public interface SettingsViewerInterface
{
    /** display all informations for the new settings and the returned response */
    public void genResultReport(SettingsModel settingsModel, Response r);

    /** Shows any object that is not part of setting*/
    public void showElement(Object o);
    
    /** Shows a given object that is part of setting*/
    public void showSettingElement(Object o);

    /** Gives access to the settings model */
    public SettingsModel getSettingsModel();

    /** Returns settingsPane reference */
    public SettingsPane getSettingsPane();

    /** Register a new Plot */
    public void addPlot(JFrame frame, String title);

    /** Unregister a new Plot */
    public void removeSelectedPlots();
}
