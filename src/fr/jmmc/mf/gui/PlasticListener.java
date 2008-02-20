/*
 * PlasticListener.java
 *
 * Created on 14 décembre 2006, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.jmmc.mf.gui;

import java.io.IOException;

import java.net.URI;

import java.util.List;
import java.util.logging.Logger;


/**
 *
 * @author mella
 */
public class PlasticListener extends uk.ac.starlink.plastic.HubManager {
    static Logger logger = Logger.getLogger("fr.jmmc.mf.gui.PlasticListener");

    /** Creates a new instance of PlasticListener */
    public PlasticListener() {
        super("ModelFitting", new URI[0]);
    }

    public Object doPerform(URI sender, URI message, List args)
        throws IOException {
        logger.warning("Add code to respond to message '" + message +
            "' from '" + sender + "' with args='" + args + "'");

        return null;
    }
}
