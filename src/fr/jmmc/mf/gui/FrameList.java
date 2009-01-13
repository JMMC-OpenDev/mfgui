/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.jmmc.mf.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author mella
 */
public class FrameList extends JList{

    FrameListModel listModel;
    SettingsViewerInterface viewer;

    public FrameList(SettingsViewerInterface settingsViewerInterface){
        listModel = new FrameListModel();
        viewer = settingsViewerInterface;
        this.setModel(listModel);
        this.setSelectionModel(new FrameListSelectionModel(listModel, viewer));
    }

    public void add(JFrame frame, String title){
        listModel.add(frame, title);
    }
    
    public void remove(JFrame frame){
        listModel.remove(frame);
    }
}