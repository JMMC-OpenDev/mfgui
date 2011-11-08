/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
/*
 * SettingsViewerInterface.java
 *
 * Created on 6 decembre 2006, 12:07
 *
 */
package fr.jmmc.mf.gui;

import fr.jmmc.mf.gui.models.SettingsModel;

/**
 *
 * @author mella
 */
public interface SettingsViewerInterface
{
    /** Shows a given object to the left panel */
    //public void showElement(Object o);

    /** Gives access to the settings model */
    public SettingsModel getSettingsModel();

    /** Returns settingsPane reference */
    public SettingsPane getSettingsPane();
}
