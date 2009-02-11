package fr.jmmc.mf.gui.actions;

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

public class SaveFileAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.SaveFileAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    private String lastDir = System.getProperty("user.home");
    private File fileToSave;
    private String proposedFilename;
    private String originalActionName;

    public SaveFileAction(File fileToSave, String proposedFilename) {
        super(className, "saveSpecificFile");
        // original name has certainly be inited from resources
        originalActionName = (String) this.getValue(SaveFileAction.NAME);
        this.putValue(SaveFileAction.NAME, originalActionName + "(" + proposedFilename + ")");
        this.fileToSave = fileToSave;
        this.proposedFilename = proposedFilename;
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
        logger.entering("" + this.getClass(), "actionPerformed");
        try {
            // Open a filechooser in previous save directory
            JFileChooser fileChooser = new JFileChooser();
            if (lastDir != null) {
                fileChooser.setSelectedFile(new File(lastDir, proposedFilename));
            } else if (fileToSave != null) {
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
                        fr.jmmc.mcs.gui.StatusBar.show("Save action canceled");
                        return;
                    }
                }
            } else {
                fr.jmmc.mcs.gui.StatusBar.show("Save action canceled");
                return;
            }
            copy(fileToSave, newFile);
            lastDir = newFile.getParent();
            proposedFilename = newFile.getName();
            this.putValue(SaveFileAction.NAME, originalActionName + "(" + proposedFilename + ")");
            fr.jmmc.mcs.gui.StatusBar.show("File saved");
        } catch (Exception exc) {
            new FeedbackReport(null, true, exc);
        }
    }
}
