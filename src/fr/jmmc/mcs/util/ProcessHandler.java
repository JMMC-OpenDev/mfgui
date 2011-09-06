/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.util;

import fr.jmmc.jmcs.util.FileUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Give access and control on one external program call. One ProcessManager
 * class can be attach to follow program activity.
 *
 */
public class ProcessHandler
{
    /**
     * DOCUMENT ME!
     */
    static final Logger logger = Logger.getLogger(ProcessHandler.class.getName());

    /**
     * DOCUMENT ME!
     */
    ProcessBuilder processBuilder = null;

    /**
     * DOCUMENT ME!
     */
    Process process = null;

    /* Listening threads */
    /**
     * DOCUMENT ME!
     */
    Thread t1;

    /* Reference one observing class */
    /**
     * DOCUMENT ME!
     */
    ProcessManager manager = null;

    /**
     * Creates a new ProcessHandler object.
     *
     * @param command DOCUMENT ME!
     */
    public ProcessHandler(String[] command)
    {
        processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        OutputHandler outputHandler = new OutputHandler();
        t1 = new Thread(outputHandler);
    }

    /**
     * DOCUMENT ME!
     *
     * @param manager DOCUMENT ME!
     */
    public void setProcessManager(ProcessManager manager)
    {
        this.manager = manager;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void start() throws IOException
    {
      if (process != null)
        {
            logger.warning("process already started");
        }
        else
        {
            logger.info("starting new process");
            process = processBuilder.start();

            if (manager != null)
            {
                manager.processStarted();
            }

            // Starting listening processes
            t1.start();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws InterruptedException DOCUMENT ME!
     */
    public int waitFor() throws InterruptedException, java.io.IOException
    {
        logger.info("waiting for process");

        int retValue = process.waitFor();
        logger.info("process terminated (returned " + retValue + ")");

        // send EOF if requested
        OutputStream out = process.getOutputStream();
        out.flush();
        out.close();

        // wait for output data flux
        t1.join();

        if (manager != null)
        {
            manager.processTerminated(retValue);
        }

        return retValue;
    }

    /**
     * DOCUMENT ME!
     */
    public void stop()
    {
        if (process != null)
        {
            // try to determine if process has terminated
            try
            {
                process.exitValue();
                logger.warning("process already terminated");

                // @todo: Would it be interresting to throw one exception ?
            }
            catch (IllegalThreadStateException e)
            {
                logger.info("stoping process");
                // Stoping listening processes
                t1.stop();
                process.destroy();

                if (manager != null)
                {
                    manager.processStoped();
                }
            }
        }
        else
        {
            logger.warning("process not yet started");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param data DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void sendToStdin(String data) throws IOException
    {
        if (process != null)
        {
            OutputStream out = process.getOutputStream();
            out.write(data.getBytes());
            out.flush();
        }
        else
        {
            logger.warning("process not yet started");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args)
    {
        String usage = "Usage:" + "   jmmc.common.ProcessHandler <command>";
        System.out.println("Hello World!");

        if (args.length < 1)
        {
            System.out.println(usage);
            System.exit(1);
        }

        // Set Logging level
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINEST);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        logger.addHandler(handler);

        // Run main application
        try
        {
            ProcessHandler pm = new ProcessHandler(args);
            pm.start();
            pm.waitFor();
            pm.stop();
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "failure : ", e);
        }
    }

    private class OutputHandler implements Runnable
    {
        public OutputHandler()
        {
        }

        public void run()
        {
            logger.finest("listening process output started");

            BufferedReader reader   = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String         line;

            try
            {
                while ((line = reader.readLine()) != null)
                {
                    if (manager != null)
                    {
                        //Exception exc = new Exception(line);
                        //manager.errorOccured(exc);
                        manager.outputOccured(line + "\n");
                    }
                }
            }
            catch (IOException e)
            {
                logger.finest("listening process output encountered one ioexception" +
                    e.getMessage());

                if (manager != null)
                {
                    manager.errorOccured(e);
                }
            } finally {
                FileUtils.closeFile(reader);
            }

            logger.finest("listening process output ended");
        }
    }
}
