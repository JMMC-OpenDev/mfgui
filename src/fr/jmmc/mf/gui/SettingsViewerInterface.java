/*
 * SettingsViewerInterface.java
 *
 * Created on 6 decembre 2006, 12:07
 *
 */
package fr.jmmc.mf.gui;


/**
 *
 * @author mella
 */
public interface SettingsViewerInterface
{
    /** Shows a given object */
    public void showSettingElement(Object o);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SettingsModel getSettingsModel();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SettingsPane getSettingsPane();
}
