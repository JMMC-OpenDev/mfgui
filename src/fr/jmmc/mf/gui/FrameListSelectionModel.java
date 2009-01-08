/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.jmmc.mf.gui;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;

/**
 *
 * @author mella
 */
public class FrameListSelectionModel extends DefaultListSelectionModel {

    FrameListModel frameListModel_;
    SettingsViewerInterface viewer_;

    public FrameListSelectionModel(FrameListModel frameListModel, SettingsViewerInterface viewer){
        frameListModel_=frameListModel;
        viewer_=viewer;
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        super.setSelectionInterval(index0, index1);
        viewer_.showElement(frameListModel_.getFrame(index0));
    }
    
}
