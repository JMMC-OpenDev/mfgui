package fr.jmmc.mf.gui;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;


/** This class is jst a typed container to present frames into the JTree */
public class FrameTreeNode extends DefaultMutableTreeNode{
    private String title;

    public FrameTreeNode(JFrame frame, String title){
        this.title = title;
        setUserObject(frame);
    }

    @Override
    public String toString(){
        return title;
    }

}
