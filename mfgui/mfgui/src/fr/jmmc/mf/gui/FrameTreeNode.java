/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.util.FileUtils;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;


/** This class is jst a typed container to present frames into the JTree */
public class FrameTreeNode extends DefaultMutableTreeNode{
    private String title;
    private File[] filesToExport;
    private String[] filenamesToExport;
    private JFrame frame;
    
    public FrameTreeNode(JFrame frame, String title){
        init(frame,title,null,null);
    }

    public FrameTreeNode(JFrame frame, String title, File fileToExport){
        init(frame,title, new File[]{fileToExport}, new String[]{"Untitled."+FileUtils.getExtension(fileToExport)});
    }
    
    public FrameTreeNode(JFrame frame, String title, File fileToExport, String filenameToExport){
        init(frame,title, new File[]{fileToExport}, new String[]{filenameToExport});
    }

    public FrameTreeNode(JFrame frame, String title, File[] filesToExport, String[] filenamesToExport){
        init(frame,title, filesToExport, filenamesToExport);
    }

    private void init(JFrame frame, String title, File[] filesToExport, String[] filenamesToExport){
        this.title = title;
        this.frame=frame;
        if (filesToExport==null){
            filesToExport=new File[0];
        }
        if (filenamesToExport==null){
            filenamesToExport=new String[0];
        }
        this.filesToExport=filesToExport;
        this.filenamesToExport=filenamesToExport;
        frame.setTitle(title);
        setUserObject(frame);
    }

    public JFrame getFrame(){
        return frame;
    }

    public File[] getFilesToExport(){
        return filesToExport;
    }
    
    public String[] getFilenamesToExport(){
        return filenamesToExport;
    }

    @Override
    public String toString(){
        return title;
    }

    public String getTitle() {
        return title;
    }

}
