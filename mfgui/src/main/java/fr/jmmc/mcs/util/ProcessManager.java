/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;

/**
 * This interface expose process activity to interested listener.
 *
 */
public interface ProcessManager {

    /**
     * DOCUMENT ME!
     */
    public void processStarted();

    /**
     * DOCUMENT ME!
     */
    public void processStoped();

    /**
     * DOCUMENT ME!
     *
     * @param returnedValue DOCUMENT ME!
     */
    public void processTerminated(int returnedValue);

    /**
     * DOCUMENT ME!
     *
     * @param exception DOCUMENT ME!
     */
    public void errorOccured(Exception exception);

    /**
     * DOCUMENT ME!
     *
     * @param line DOCUMENT ME!
     */
    public void outputOccured(String line);
}
