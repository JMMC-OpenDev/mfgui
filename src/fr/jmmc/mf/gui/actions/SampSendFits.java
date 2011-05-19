/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mf.gui.actions;

import fr.jmmc.mcs.interop.SampCapability;
import fr.jmmc.mcs.interop.SampCapabilityAction;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Called to export current data as local VOTable to another SAMP application.
 *  url must be set before first call.
 * @author mella
 */
public class SampSendFits extends SampCapabilityAction {

    public final static String className=SampSendFits.class.getName();
    /** logger */
    private final static Logger logger = Logger.getLogger(className);
    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    private String uri = null;

    public SampSendFits() {
        super(className, className, SampCapability.LOAD_FITS_TABLE);
        couldBeEnabled(false);
    }

    public Map<?, ?> composeMessage() {
        final Map<String, String> parameters = new HashMap<String, String>();
        logger.fine("transmitting fits file using : uri = " + uri);
        parameters.put("url", uri);
        return parameters;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
