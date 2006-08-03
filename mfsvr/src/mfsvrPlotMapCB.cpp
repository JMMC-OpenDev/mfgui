/*******************************************************************************
 * JMMC project
 *
 * "@(#) : $Id: mfsvrPlotMapCB.cpp,v 1.7 2006-08-02 08:22:36 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2006/07/11 11:18:48  lsauge
 * introduce safe method to unlink temporary files
 *
 * Revision 1.5  2006/07/10 16:34:53  lsauge
 * Use mkstemp() to create a unique temporary file name using by gnuplot
 *
 * Revision 1.4  2006/07/10 12:21:50  lsauge
 * Enable EPS ouput
 *
 * Revision 1.3  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of PLOTMAP command callback.
 */

static char *rcsId="@(#) : $Id: mfsvrPlotMapCB.cpp,v 1.7 2006-08-02 08:22:36 lsauge Exp $"; 
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
#include "mfsvrPLOTMAP_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/**
 *  DOXYGEN HEADER
 */
evhCB_COMPL_STAT mfsvrSERVER::PlotMapCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrPLOTMAP_CMD PlotMapCmd(msg.GetCommand(), msg.GetBody());
    if (PlotMapCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    if( _model == NULL )
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
        
    // Get "output" flag
    char *      outputEPSFilename;
    mcsLOGICAL  outputEPS = mcsFALSE;
    if (PlotMapCmd.GetOutput(&outputEPSFilename) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    if( strcmp(outputEPSFilename,"none") != 0 )
    {
        outputEPS = mcsTRUE;
    }


    // Get parameter value
    mcsDOUBLE   uMin    = -50.0; 
    mcsDOUBLE   uMax    = +50.0; 
    mcsDOUBLE   vMin    = -50.0; 
    mcsDOUBLE   vMax    = +50.0; 
    mcsDOUBLE   lambda  =  1e-6;
    mcsLOGICAL  plotSurf= mcsFALSE ;

    if (PlotMapCmd.GetUmin(&uMin) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }
    if (PlotMapCmd.GetUmax(&uMax) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }
    if (PlotMapCmd.GetVmin(&vMin) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }
    if (PlotMapCmd.GetVmax(&vMax) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }
    if (PlotMapCmd.GetLambda(&lambda) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }
    if (PlotMapCmd.GetSurface(&plotSurf) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
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

    
    mcsINT16 nrow = 100;
    mcsINT16 ncol = 100;
        // structures to store model 
    mfmdlUV_COORDS uvCoords(1);
    mfmdlVIS       visCpt(1);

    mcsDOUBLE u = 0.0;
    mcsDOUBLE v = 0.0;
    for( mcsUINT16 i=0 ; i<nrow ; i++)
    {
        u = uMin+((mcsDOUBLE)i)*(uMax-uMin)/((mcsDOUBLE)nrow);
        for( mcsUINT16 j=0 ; j<ncol ; j++)
        {
            v = vMin+((mcsDOUBLE)j)*(vMax-vMin)/((mcsDOUBLE)ncol);
            uvCoords.Set(0,u/lambda,v/lambda);

            if (_model->Eval(uvCoords,visCpt) != mcsSUCCESS)
            {
                PrintBody(msg,mcsTRUE,"An error occured during the model evaluation.");
                return evhCB_NO_DELETE | evhCB_FAILURE;
            }

            fprintf(fileDesc,"%e \t%e\t%e\n",
                    u,v,
                    visCpt.GetVis2(0)
                   );
            fflush(fileDesc);
        }
        fprintf(fileDesc,"\n");
        fflush(fileDesc);
    }

    fflush(fileDesc) ;
    close(fileId) ;

    // Plot data
    if( outputEPS == mcsTRUE )
    {
        PlotSendCommand("set term postscript eps enhanced color\n"); 
        PlotSendCommand("set output \"%s\"\n",outputEPSFilename); 
    }
    (void) PlotSendCommand("reset\n");
    (void) PlotSendCommand("set size square\n");
    (void) PlotSendCommand("set xlabel \" u (meter)\"\n");
    (void) PlotSendCommand("set ylabel \" v (meter)\"\n");
    (void) PlotSendCommand("set title \"squared visibilities\"\n");
    (void) PlotSendCommand("set xrange [%g:%g]\n",uMin,uMax);
    (void) PlotSendCommand("set yrange [%g:%g]\n",vMin,vMax);
    (void) PlotSendCommand("set style data line\n");
    (void) PlotSendCommand("set palette\n");
    
    
    if (plotSurf == mcsTRUE)
    {
        (void) PlotSendCommand("set key\n");
        (void) PlotSendCommand("set pm3d at b\n");
        (void) PlotSendCommand("splot \"%s\"\n",fileName);
    }
    else
    {
        (void) PlotSendCommand("unset key\n");
        (void) PlotSendCommand("set pm3d map\n");
        (void) PlotSendCommand("splot \"%s\"\n",fileName);
    }
    if( outputEPS == mcsTRUE )
    {
        PlotSendCommand("set output\n"); 
        PlotSendCommand("set term x11\n"); 
        PlotSendCommand("replot\n"); 
    }

    (void) PlotSendCommand("unset xrange\n");
    (void) PlotSendCommand("unset yrange\n");

    // Send reply
    msg.SetBody("Done.");
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}

/*___oOo___*/
