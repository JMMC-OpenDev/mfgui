/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui;

import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.mf.models.Response;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;

/** This class is jst a typed container to present frames into the JTree */
public class FrameTreeNode extends DefaultMutableTreeNode {

    private File[] filesToExport;
    private String[] filenamesToExport;
    private JFrame frame;
    private Response response;

    public FrameTreeNode(JFrame frame) {
        init(frame, null, null, null);
    }

    public FrameTreeNode(JFrame frame, File fileToExport) {
        init(frame, new File[]{fileToExport}, new String[]{"Untitled." + FileUtils.getExtension(fileToExport)}, null);
    }

    public FrameTreeNode(JFrame frame, File fileToExport, String filenameToExport) {
        init(frame, new File[]{fileToExport}, new String[]{filenameToExport}, null);
    }

    public FrameTreeNode(JFrame frame, File[] filesToExport, String[] filenamesToExport) {
        init(frame, filesToExport, filenamesToExport, null);
    }
    
    public FrameTreeNode(JFrame frame, File[] filesToExport, String[] filenamesToExport, Response response) {
        init(frame, filesToExport, filenamesToExport, response);
    }

    private void init(JFrame frame, File[] filesToExport, String[] filenamesToExport, Response response) {
        this.frame = frame;
        if (filesToExport == null) {
            filesToExport = new File[0];
        }
        if (filenamesToExport == null) {
            filenamesToExport = new String[0];
        }
        this.filesToExport = filesToExport;
        this.filenamesToExport = filenamesToExport;
        this.response = response;
        
        setUserObject(frame);
    }

    public JFrame getFrame() {
        return frame;
    }

    public File[] getFilesToExport() {
        return filesToExport;
    }

    public String[] getFilenamesToExport() {
        return filenamesToExport;
    }
    
    public Response getResponse() {
        return response;
    }
    
    public boolean hasResponse() {
        return response != null;
    }


    @Override
    public String toString() {
        return frame.getTitle();
    }
}
