package fr.jmmc.mf.gui;

import java.io.File;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;


/** This class is jst a typed container to present frames into the JTree */
public class FrameTreeNode extends DefaultMutableTreeNode{
    private String title;
    private File[] filesToExport;
    private JFrame frame;
    
    public FrameTreeNode(JFrame frame, String title){
        init(frame,title,null);
    }

    public FrameTreeNode(JFrame frame, String title, File fileToExport){
        init(frame,title, new File[]{fileToExport});
    }

    public FrameTreeNode(JFrame frame, String title, File[] filesToExport){
        init(frame,title, filesToExport);
    }

    private void init(JFrame frame, String title, File[] filesToExport){
        this.title = title;
        this.frame=frame;
        if (filesToExport==null){
            filesToExport=new File[0];
        }
        this.filesToExport=filesToExport;
        frame.setTitle(title);
        setUserObject(frame);
    }

    public JFrame getFrame(){
        return frame;
    }

    public File[] getFilesToExport(){
        return filesToExport;
    }

    @Override
    public String toString(){
        return title;
    }

}
