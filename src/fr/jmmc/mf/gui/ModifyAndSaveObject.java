/*
 * ModifyAndSaveObject.java
 *
 * Created on 22 décembre 2006, 14:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.jmmc.mf.gui;


/**
 * Make object able to be saved at end of application
 * @author mella
 */
public interface ModifyAndSaveObject {
    /**
     * tell if object have been modified.
     */
    public boolean isModified();

    /**
     * returns the parent component or null if no frame associated.
     */
    public java.awt.Component getComponent();

    /**
     * allow caller to request object save.
     */
    public void save();
}
