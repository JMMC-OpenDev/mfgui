package fr.jmmc.mf.gui;

import fr.jmmc.mcs.gui.FeedbackReport;
import fr.jmmc.mcs.util.RegisteredAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

class SaveFileAction extends RegisteredAction {

    private String lastDir = System.getProperty("user.home");
    private File fileToSave;
    private String proposedFilename;
    private String originalActionName;

    public SaveFileAction(File fileToSave, String proposedFilename) {
        super(MFGui.className_, "saveSpecificFile");
        // original name has certainly be inited from resources
        originalActionName=(String)this.getValue(this.NAME);
        this.putValue(this.NAME
                , originalActionName+"("+proposedFilename+")");
        this.fileToSave = fileToSave;
        this.proposedFilename=proposedFilename;
    }

    void copy(File src, File dst) throws IOException, FileNotFoundException, IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void actionPerformed(ActionEvent e) {
        MFGui.logger.entering("" + this.getClass(), "actionPerformed");
        try {
            // Open a filechooser in previous save directory
            JFileChooser fileChooser = new JFileChooser();
            if(lastDir != null) {
                fileChooser.setSelectedFile(new File(lastDir,proposedFilename));
            }else if (fileToSave != null) {
                fileChooser.setSelectedFile(fileToSave);
            }
            fileChooser.setDialogTitle("Save " + proposedFilename + "?");
            // Open filechooser
            int returnVal = fileChooser.showSaveDialog(null);
            File newFile = fileChooser.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (newFile.exists()) {
                    String message = "File \'" + newFile.getName() + "\' already exists\nDo you want to overwrite this file?";
                    int answer = JOptionPane.showConfirmDialog(null, message);
                    if (answer != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            } else {
                return;
            }
            copy(fileToSave,newFile);
            lastDir = newFile.getParent();
            proposedFilename=newFile.getName();
            this.putValue(this.NAME
                , originalActionName+"("+proposedFilename+")");
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }
}
