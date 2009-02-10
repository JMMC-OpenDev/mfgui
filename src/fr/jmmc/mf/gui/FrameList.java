package fr.jmmc.mf.gui;

import javax.swing.JFrame;
import javax.swing.JList;

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
        frame.setTitle(title);
        viewer.showElement(frame);
    }
    
    public void remove(JFrame frame){
        listModel.remove(frame);
    }
    
    public JFrame getSelectedFrame(){
        return listModel.getFrame(getSelectedIndex());
    }

    void removeSelectedPlots() {
        int[] indices = getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
            int idx = indices[i];
            listModel.remove(idx);
        }
    }
}