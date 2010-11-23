package fr.jmmc.mf.gui.actions;

import fr.jmmc.mcs.util.RegisteredAction;
import fr.jmmc.mf.gui.MFGui;
import fr.jmmc.mf.gui.models.SettingsModel;
//import fr.jmmc.oidata.RepositoryBrowser;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class LoadRemoteDataFilesAction extends RegisteredAction implements TreeSelectionListener, ChangeListener {

    public final static String className = "fr.jmmc.mf.gui.actions.LoadRemoteDataFileAction";
    public final static String actionName = "loadRemoteDataFiles";
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            className);
    MFGui mfgui;
    SettingsModel settingsModel;
    Vector<SettingsModel> settingsModelListener = new Vector();

    public LoadRemoteDataFilesAction(MFGui mfgui) {
        super(className, actionName);
        this.mfgui = mfgui;
    }

    public void actionPerformed(ActionEvent e) {
        logger.entering(className, "actionPerformed");

         throw new UnsupportedOperationException("This action will be back in the comming releases. Now you have to visit the http://jmmc.fr/oidata url and download manually the files.");
        /* TODO replace next part of code
         RepositoryBrowser browser = new RepositoryBrowser(new JFrame(), true);

        int returnVal = browser.showBrowser();

        if (returnVal == RepositoryBrowser.YES_OPTION) {
            String browserUrl = browser.getSelectedUrl();
            try {
                System.out.println("browserUrl = " + browserUrl);

                // Create a URL for the desired page
                URL url = new URL(browserUrl);

                // Create a temporary file to receive data
                File tmpFile = new File(url.getPath());
                String fileName = tmpFile.getName();
                String name = fileName.substring(0, fileName.lastIndexOf("."));
                String ext = fileName.substring(fileName.lastIndexOf("."));
                fileName = System.getProperties().getProperty("java.io.tmpdir") +
                        File.separator + name + ext;
                tmpFile = new File(fileName);
                tmpFile.deleteOnExit();

                InputStream in = url.openStream();
                OutputStream out = new FileOutputStream(tmpFile);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                settingsModel.addFile(tmpFile);
            } catch (Exception ex) {
                String msg = "Can't read remote file '" + browserUrl + "'.\n\n(" +
                        ex.getMessage() + ")";
                JOptionPane.showMessageDialog(null, msg, "Error ", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.WARNING, msg, ex);
            }
        }
         */

    }

    /** Listen to the settings pane selection changes
     *
     * @param e ChangeEvent
     */
    public void stateChanged(ChangeEvent e) {
        settingsModel = mfgui.getSelectedSettings();
        if (settingsModel == null) {
            return;
        }
        if (!settingsModelListener.contains(settingsModel)) {
            settingsModel.addTreeSelectionListener(this);
        }
    }

    /** Listen to the settings tree selection changes
     *
     * @param e TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        if (e.getSource() instanceof SettingsModel) {
            settingsModel = (SettingsModel) e.getSource();
        } else {
            logger.warning("dropped treeSelectionEvent from " + e.getSource());
        }
    }
}
