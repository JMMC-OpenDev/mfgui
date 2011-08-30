package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.MessagePane;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;

public class SaveFileAction extends RegisteredAction {

    private final static String className = "fr.jmmc.mf.gui.actions.SaveFileAction";
    /** Class logger */
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    private String lastDir = System.getProperty("user.home");
    private File fileToSave;
    private String fileName;
    private String originalActionName;

    /**
     * Convenience constructor that use extension as type.
     * @param fileToSave
     */
    public SaveFileAction(File fileToSave) {
        super(className, "saveSpecificFile");
        init(fileToSave, null);
    }

    public SaveFileAction(File fileToSave, String fileType) {
        super(className, "saveSpecificFile");
        init(fileToSave, fileType);
    }

    protected void init(File fileToSave, String fileType) {
        // original name has certainly be inited from resources
        originalActionName = (String) this.getValue(SaveFileAction.NAME);
        if (fileType == null) {
            int dotIndex = fileToSave.getName().lastIndexOf(".");
            if (dotIndex < 0) {
                fileType = "???";
            } else {
                fileType = fileToSave.getName().substring(dotIndex + 1);
            }
        }
        this.putValue(SaveFileAction.NAME, originalActionName + " "
                + fileType.toUpperCase() + " ... ");
        this.fileToSave = fileToSave;
        this.fileName = "Untitled." + fileType.toLowerCase();
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering(className, "actionPerformed");
        // Open a filechooser in previous save directory
        JFileChooser fileChooser = new JFileChooser();
        if (lastDir != null) {
            fileChooser.setSelectedFile(new File(lastDir, fileName));
        } else if (fileToSave != null) {
            fileChooser.setSelectedFile(fileToSave);
        }
        fileChooser.setDialogTitle("Export as " + fileName + "?");
        // Open filechooser
        int returnVal = fileChooser.showSaveDialog(null);
        File newFile = fileChooser.getSelectedFile();
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        if (newFile.exists()) {
            if (!MessagePane.showConfirmFileOverwrite(newFile.getAbsolutePath())) {
                return;
            }
        }
        try {
            FileUtils.copy(fileToSave, newFile);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("Can't export data into " + newFile.getAbsolutePath(), ex);
        } catch (IOException ex) {
            MessagePane.showErrorMessage("Can't export data into " + newFile.getAbsolutePath(), ex);
            return;
        }
        lastDir = newFile.getParent();
        fileName = newFile.getName();
        this.putValue(SaveFileAction.NAME, originalActionName + " " + fileName);
        fr.jmmc.jmcs.gui.StatusBar.show("File saved");

    }
}
