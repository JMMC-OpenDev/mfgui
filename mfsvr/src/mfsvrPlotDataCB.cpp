/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrPlotDataCB.cpp,v 1.8 2006-07-11 11:18:48 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2006/07/10 16:34:53  lsauge
 * Use mkstemp() to create a unique temporary file name using by gnuplot
 *
 * Revision 1.6  2006/07/05 15:21:50  lsauge
 * Implement bispectrum data handling
 *
 * Revision 1.5  2006/07/05 14:52:07  lsauge
 * Fix problem with replacement of data module name by mfdata
 *
 * Revision 1.4  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of PLOTDATA command callback.
 */

static char *rcsId="@(#) $Id: mfsvrPlotDataCB.cpp,v 1.8 2006-07-11 11:18:48 lsauge Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <iostream>
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "mfsvrSERVER.h"
#include "mfsvrPLOTDATA_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*!
  Plot data using gnuplot as external data viewer.
 */
evhCB_COMPL_STAT mfsvrSERVER::PlotDataCB(msgMESSAGE &msg, void*)
{

    if ( (_data == NULL) )
    {
        PrintBody(msg,mcsTRUE,"No data set. Abort.");
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Build the command object
    mfsvrPLOTDATA_CMD PlotDataCmd(msg.GetCommand(), msg.GetBody());
    if (PlotDataCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Create and open a temporary output file
    (void) RemoveFiles();
    char  fileName[]="/tmp/mfit.XXXXXX";
    int   fileId = -1 ;
    FILE *fileDesc = NULL;
    
    if ( ((fileId = mkstemp(fileName)) ==-1) ||
         ((fileDesc = fdopen(fileId,"w+")) == NULL))
    {
        if (fileId != -1)
        {
            unlink(fileName);
            close(fileId);
        }
        PrintBody(msg,mcsTRUE,"Cannot create temporary file. Abort.");
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    logInfo("Create and Open temporary file: %s",fileName);
  
    // Register it for forthcoming removing
    (void) RegisterFileForRemoving(fileName);
    
    
    // Looking for VIS2 Table
    
    PrintBody(msg,mcsFALSE,"Looking for VIS2 table ...");

    // loop other data set
    for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
    {
        // Get the next data set
        mfdataINPUT_DATA *data = _data->GetDataSet(dsIdx) ; 

        if (data->GetType() == mfdataINPUT_VIS2_TYPE)
        {
            // Cast
            mfdataINPUT_VIS2 *dataVis2 = (mfdataINPUT_VIS2 *)data ;

            // Print some info to the user console
            PrintBody(msg,mcsFALSE,"\tFound (%d,%d)",
                    dataVis2->GetNbOfData(),
                    dataVis2->GetNbOfWaves()
                    );

            mcsINT16  nbOfData = dataVis2->GetNbOfData();

            for (int idx=0 ; idx < nbOfData ; idx++)
            {
                mcsDOUBLE u = dataVis2->GetUCoord(idx);
                mcsDOUBLE v = dataVis2->GetVCoord(idx);
                mcsDOUBLE r = sqrt( u*u + v*v ) ;

                fprintf(fileDesc,"%e \t%e\t%e\n",
                        r,
                        dataVis2->GetVis2Data(idx),
                        dataVis2->GetVis2Err (idx)
                       );
                fflush(fileDesc);
            }
        }
    }
    fflush(fileDesc);
    close(fileId);
    // Plot data
    PlotSendCommand("reset\n");
    PlotSendCommand("unset key\n");
    PlotSendCommand("set xlabel \"projected baseline (1/rad)\"\n");
    PlotSendCommand("set ylabel \"squared visibilities\"\n");
    PlotSendCommand("set autoscale\n");
    PlotSendCommand("plot \"%s\" with errorbars\n",fileName);

    // Send reply
    msg.SetBody("Done.");
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}

/*___oOo___*/
