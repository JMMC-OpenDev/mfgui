/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrPlotModelCB.cpp,v 1.9 2006-07-12 12:13:08 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2006/07/11 11:18:48  lsauge
 * introduce safe method to unlink temporary files
 *
 * Revision 1.7  2006/07/10 16:34:53  lsauge
 * Use mkstemp() to create a unique temporary file name using by gnuplot
 *
 * Revision 1.6  2006/07/10 12:22:19  lsauge
 * Add T3, split and errorbars flag (bispectrum plotting)
 * Enable EPS output
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
 *  Definition of PLOTMODEL command callback.
 */

static char *rcsId="@(#) $Id: mfsvrPlotModelCB.cpp,v 1.9 2006-07-12 12:13:08 lsauge Exp $"; 
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
#include "mfsvrPLOTMODEL_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

#define RADTODEG    0.5729577951e+002

/**
 *  Plot on the same graph, data and model. 
*/
evhCB_COMPL_STAT mfsvrSERVER::PlotModelCB(msgMESSAGE &msg, void*)
{
    
    if ( (_data == NULL)||(_model == NULL) )
    {
        PrintBody(msg,mcsTRUE,"No model or data set. Abort.");
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Build the command object
    mfsvrPLOTMODEL_CMD PlotModelCmd(msg.GetCommand(), msg.GetBody());
    if (PlotModelCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Get "T3" flag
    mcsLOGICAL T3Plot;
    if (PlotModelCmd.GetT3(&T3Plot) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Get "split" flag
    mcsLOGICAL splitPlotFrame;
    if (PlotModelCmd.GetSplit(&splitPlotFrame) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }


    // Get "Errorbars" flag
    mcsLOGICAL ErrorBarsPlot;
    if (PlotModelCmd.GetErrorbars(&ErrorBarsPlot) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Get "output" flag
    char *      outputEPSFilename;
    mcsLOGICAL  outputEPS = mcsFALSE;
    if (PlotModelCmd.GetOutput(&outputEPSFilename) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    if( strcmp(outputEPSFilename,"none") != 0 )
    {
        outputEPS = mcsTRUE;
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

    if (T3Plot == mcsFALSE)
    {
        ////////////////////////////////////////////////////////////
        //  VIS2
        ////////////////////////////////////////////////////////////
        // temporary output file

        for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
        {
            mfdataINPUT_DATA *dataNoCast = _data->GetDataSet(dsIdx) ; 

            if ( dataNoCast->GetType() == mfdataINPUT_VIS2_TYPE )
            {
                mfdataINPUT_VIS2 *dataVis2 = (mfdataINPUT_VIS2 *)dataNoCast; 

                mcsINT16  nbOfData = dataVis2->GetNbOfData();

                // structures to store model 
                mfmdlUV_COORDS *uvCoords = new mfmdlUV_COORDS(nbOfData);
                mfmdlVIS       *visCpt   = new mfmdlVIS(nbOfData);

                (void) dataVis2->GetUVCoords(*uvCoords);

                if (_model->Eval(*uvCoords,*visCpt) != mcsSUCCESS)
                {
                    PrintBody(msg,mcsTRUE,"An error occured during the model evaluation.");
                    return evhCB_NO_DELETE | evhCB_FAILURE;
                }


                for (int idx=0 ; idx < nbOfData ; idx++)
                {
                    mcsDOUBLE u = dataVis2->GetUCoord(idx);
                    mcsDOUBLE v = dataVis2->GetVCoord(idx);
                    mcsDOUBLE r = sqrt( u*u + v*v ) ;

                    fprintf(fileDesc,"%e \t%e\t%e\t%e\n",
                            r,
                            dataVis2->GetVis2Data(idx),
                            dataVis2->GetVis2Err (idx),
                            visCpt->GetVis2(idx)
                           );
                    fflush(fileDesc);
                }
            }
        }
        fflush(fileDesc);
        close(fileId);

        // Plot data
        if( outputEPS == mcsTRUE )
        {
            PlotSendCommand("set term postscript eps enhanced color\n"); 
            PlotSendCommand("set output \"%s\"\n",outputEPSFilename); 
        }
        PlotSendCommand("reset\n");
        PlotSendCommand("unset key\n");
        PlotSendCommand("set xlabel \"projected baseline (1/rad)\"\n");
        PlotSendCommand("set ylabel \"squared visibilities\"\n");

        if( ErrorBarsPlot == mcsTRUE)
        {
            PlotSendCommand("plot \"%s\" using 1:2:3 with errorbars, \"%s\"  using 1:4 with point 3 5\n",
                           fileName, 
                           fileName );
        }
        else
        {
            PlotSendCommand("plot \"%s\" using 1:2 , \"%s\"  using 1:4 with point 3 5\n",
                           fileName, 
                           fileName );

        }

        if( outputEPS == mcsTRUE )
        {
            PlotSendCommand("set output\n"); 
            PlotSendCommand("set term x11\n"); 
            PlotSendCommand("replot\n"); 
        }

    }
    else
    {    
        ////////////////////////////////////////////////////////////
        //  T3
        ////////////////////////////////////////////////////////////

        mcsUINT16 idx = 0;
        for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
        {
            mfdataINPUT_DATA *dataNoCast = _data->GetDataSet(dsIdx) ; 
    
            if ( dataNoCast->GetType() == mfdataINPUT_T3_TYPE )
            {
                mfdataINPUT_T3 *dataT3 = (mfdataINPUT_T3 *)dataNoCast; 

                mcsINT16  nbOfData = dataT3->GetNbOfData();

                mcsDOUBLE *oT3Amp ;   
                mcsDOUBLE *oT3AmpErr ;
                mcsDOUBLE *oT3Phi ;   
                mcsDOUBLE *oT3PhiErr;

                mfmdlUV_COORDS *uv1Coords = new mfmdlUV_COORDS(nbOfData);
                mfmdlUV_COORDS *uv2Coords = new mfmdlUV_COORDS(nbOfData);
                mfmdlUV_COORDS *uv3Coords = new mfmdlUV_COORDS(nbOfData);

                mfmdlVIS       *vis1Cpt   = new mfmdlVIS(nbOfData);
                mfmdlVIS       *vis2Cpt   = new mfmdlVIS(nbOfData);
                mfmdlVIS       *vis3Cpt   = new mfmdlVIS(nbOfData);
                dataT3->GetUVCoords  (*uv1Coords,*uv2Coords);

                // Get bispectrum components
                dataT3->GetT3Amp     (&oT3Amp   );
                dataT3->GetT3AmpErr  (&oT3AmpErr);
                dataT3->GetT3Phi     (&oT3Phi   );
                dataT3->GetT3PhiErr  (&oT3PhiErr);

                // Set uv3 (last baseline)
                uv3Coords->Add(*uv1Coords,*uv2Coords);

                // Evaluate models
                if ((_model->Eval(*uv1Coords,*vis1Cpt) != mcsSUCCESS)||
                    (_model->Eval(*uv2Coords,*vis2Cpt) != mcsSUCCESS)||
                    (_model->Eval(*uv3Coords,*vis3Cpt) != mcsSUCCESS))
                {
                    PrintBody(msg,mcsTRUE,"An error occured during the model evaluation.");
                    return evhCB_NO_DELETE | evhCB_FAILURE;
                }

                // Set residual values
                for (mcsUINT16 dataIdx=0 ; dataIdx < nbOfData ; dataIdx++)
                {

                    // 
                    {
                        mcsDOUBLE u1,v1,u2,v2,u3,v3;
                        uv1Coords->Get(dataIdx,u1,v1);
                        uv2Coords->Get(dataIdx,u2,v2);
                        uv3Coords->Get(dataIdx,u3,v3);
                    } 

                    mcsDOUBLE Re1,Im1;
                    mcsDOUBLE Re2,Im2;
                    mcsDOUBLE Re3,Im3;
                    // Get complex visibility components
                    vis1Cpt->Get(dataIdx,Re1,Im1);
                    vis2Cpt->Get(dataIdx,Re2,Im2);
                    vis3Cpt->Get(dataIdx,Re3,Im3);
                    // Compute usefull intermediate values (numerical
                    // optimization)
                    mcsDOUBLE t5 = Im1*Re2 ;
                    mcsDOUBLE t7 = Im1*Im2 ;
                    mcsDOUBLE t1 = Re1*Re2 - t7;
                    mcsDOUBLE t3 = Re1*Im2 + t5;
                    // Compute Bispectrum 
                    mcsDOUBLE cT3Re  =  t1*Re3 + t3*Im3 ;
                    mcsDOUBLE cT3Im  = -t1*Im3 + t3*Re3 ;
                    // Get Eurler's coordinate of the corrsponding
                    // complex number
                    mcsDOUBLE cT3Amp = sqrt(cT3Re*cT3Re + cT3Im*cT3Im) ; 
                    mcsDOUBLE cT3Phi = 180.0/M_PI*atan2(cT3Im,cT3Re) ; 

                    // put data into file
                    fprintf(fileDesc,"%e\t%e\t%e\t%e\t%e\t%e\t%d\n", 
                            *(oT3Amp   +dataIdx) ,*(oT3Phi   +dataIdx),
                            *(oT3AmpErr+dataIdx) ,*(oT3PhiErr+dataIdx),
                            cT3Amp               ,cT3Phi,
                            ++idx
                           );
                    fflush(fileDesc);

                }
            }
        }
        fflush(fileDesc);
        close(fileId);

        // Plot data
        if( outputEPS == mcsTRUE )
        {
            PlotSendCommand("set term postscript eps enhanced color\n"); 
            PlotSendCommand("set output \"%s\"\n",outputEPSFilename); 
        }
        
        PlotSendCommand("reset\n");
        PlotSendCommand("unset key\n");
       
        if (splitPlotFrame == mcsTRUE)
        {
            // Plot bispectrum in two different frame
            // Amplitude vs. index   and   Phase vs. index
       
            PlotSendCommand("set multiplot\n");
            // First frame
            PlotSendCommand("set origin 0.0,0.0\n");
            PlotSendCommand("set size   0.5,1.0\n");
            PlotSendCommand("set xlabel \"index\"\n");
            PlotSendCommand("set ylabel \"bispectrum - Amplitude\"\n");

            if( ErrorBarsPlot == mcsTRUE)
            {
                PlotSendCommand("plot \"%s\" using 7:1:3 with errorbars, \"%s\"  using 7:5 with point 3 5\n",
                           fileName, 
                           fileName );
            }
            else
            {
                PlotSendCommand("plot \"%s\" using 7:1, \"%s\"  using 7:5 with point 3 5\n",
                                fileName,
                                fileName);
            }

            // Second frame
            PlotSendCommand("set origin 0.5,0.0\n");
            PlotSendCommand("set size   0.5,1.0\n");
            PlotSendCommand("set xlabel \"index\"\n");
            PlotSendCommand("set ylabel \"bispectrum - Phi\"\n");

            if( ErrorBarsPlot == mcsTRUE)
            {
                PlotSendCommand("plot \"%s\" using 7:2:4 with errorbars, \"%s\"  using 7:6 with point 3 5\n",
                                fileName,
                                fileName);
            }
            else
            {
                PlotSendCommand("plot \"%s\" using 7:2, \"%s\"  using 7:6 with point 3 5\n",
                                fileName,
                                fileName);
            }

            PlotSendCommand("unset multiplot\n");
            PlotSendCommand("set origin\n");
            PlotSendCommand("set size\n");
        }
        else
        {
            // Plot bispectrum as Phase vs. Amplitude 
            PlotSendCommand("set xlabel \"bispectrum - Amplitude\"\n");
            PlotSendCommand("set ylabel \"bispectrum - Phi\"\n");
            if( ErrorBarsPlot == mcsTRUE)
            {
                PlotSendCommand("plot \"%s\" using 1:2:3:4 with xyerrorbars, \"%s\"  using 5:6 with point 3 5\n",
                                fileName,
                                fileName);
            }
            else
            {
                PlotSendCommand("plot \"%s\" using 1:2, \"%s\"  using 5:6 with point 3 5\n",
                                fileName,
                                fileName
                                );
            }

        }
        
        if( outputEPS == mcsTRUE )
        {
            PlotSendCommand("set term x11\n"); 
            PlotSendCommand("replot\n"); 
        }

    }
    
    // Send reply
    msg.SetBody("Done.");
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}

/*___oOo___*/
