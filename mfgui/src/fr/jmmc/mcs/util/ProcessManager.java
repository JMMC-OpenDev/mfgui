/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ProcessManager.java,v 1.2 2008-10-17 10:11:26 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2007/03/28 07:38:35  mella
 *  Added temporary into mfgui module until mcs requires java 1.5
 *
 * Revision 1.2  2007/03/22 15:10:46  mella
 * Removed because that makes 1.5 dependance
 *
 * Revision 1.1  2007/02/14 10:14:38  mella
 * First revision
 *
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;


/**
 * This interface expose process activity to interested listener.
 *
 */
public interface ProcessManager
{
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
