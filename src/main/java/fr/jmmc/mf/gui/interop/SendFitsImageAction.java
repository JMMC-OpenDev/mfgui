/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.interop;

import fr.jmmc.jmcs.gui.component.StatusBar;
import fr.jmmc.jmcs.network.interop.SampCapability;
import fr.jmmc.jmcs.network.interop.SampCapabilityAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This registered action represents an Interop Menu entry to
 * send selected FITS image (OImaging, Aladin)...
 *
 * @author Guillaume MELLA
 */
public final class SendFitsImageAction extends SampCapabilityAction {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Class name. This name is used to register to the ActionRegistrar */
    private final static String className = SendFitsImageAction.class.getName();
    /** Action name. This name is used to register to the ActionRegistrar */
    public final static String actionName = "sendFitsImageAction";
    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(className);

    /* members */
    /** current fitsFile to send */
    static File currentFitsFile = null;

    /**
     * Public constructor that automatically register the action in RegisteredAction.
     */
    public SendFitsImageAction() {
        super(className, actionName, SampCapability.LOAD_FITS_IMAGE);
    }

    /**
     * Getter called by plot panel to set file reference of Fits to send.
     * @param currentFitsFile file to send or null to clean old reference.
     */
    public static void setFitsFileToSend(File fitsFile) {
        currentFitsFile = fitsFile;
    }

    
    /**
     * This method automatically sends the message returned by composeMessage()
     * to user selected client(s). Children classes should not overwrite this
     * method or must call super implementation to keep SAMP message management.
     *
     * @param e actionEvent coming from SWING objects. It contains in its
     * command the name of the destination.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (currentFitsFile == null) {
            StatusBar.show("Sorry, no fits file to send. Select your target, ask to plot image and run again.");
            return; // no data
        }

        final String command = e.getActionCommand();
        composeAndSendMessage(command);
    }

    /**
     * Should return the message you want to send
     * @throws IllegalStateException if the oifits file can not be written to a temporary file
     * @return Samp message parameters as a map
     */
    @Override
    public Map<?, ?> composeMessage() throws IllegalStateException {
        logger.debug("composeMessage");       
        // Store parameters into SAMP message:
        final Map<String, String> parameters = new HashMap<String, String>(4);
        addUrlParameter(parameters, this.currentFitsFile);
        return parameters;
    }
}
