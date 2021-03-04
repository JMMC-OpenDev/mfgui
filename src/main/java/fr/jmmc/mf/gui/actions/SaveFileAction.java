/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.jmcs.gui.action.RegisteredAction;
import fr.jmmc.jmcs.gui.component.FileChooser;
import fr.jmmc.jmcs.gui.component.MessagePane;
import fr.jmmc.jmcs.util.FileUtils;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SaveFileAction extends RegisteredAction {

    private final static String className = SaveFileAction.class.getName();
    private File lastDir;
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
        // Open filechooser
        File newFile = FileChooser.showSaveFileChooser("Export as " + fileName + "?", lastDir, null, fileName);
        if (newFile == null) {
            return;
        }
        try {
            FileUtils.copy(fileToSave, newFile);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("Can't export data into " + newFile.getAbsolutePath(), ex);
        } catch (IOException ex) {
            MessagePane.showErrorMessage("Can't export data into " + newFile.getAbsolutePath(), ex);
            return;
        }
        lastDir = newFile.getParentFile();
        fileName = newFile.getName();
        this.putValue(SaveFileAction.NAME, originalActionName + " " + fileName);
        fr.jmmc.jmcs.gui.component.StatusBar.show("File saved");

    }
}
