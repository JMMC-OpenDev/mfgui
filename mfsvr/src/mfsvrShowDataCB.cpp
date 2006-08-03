/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrShowDataCB.cpp,v 1.1 2006-07-10 12:26:34 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SHOWDATA command callback.
 */

static char *rcsId="@(#) $Id: mfsvrShowDataCB.cpp,v 1.1 2006-07-10 12:26:34 lsauge Exp $"; 
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
#include "mfsvrSHOWDATA_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/**
 *  DOXYGEN HEADER
 */
evhCB_COMPL_STAT mfsvrSERVER::ShowDataCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrSHOWDATA_CMD ShowDataCmd(msg.GetCommand(), msg.GetBody());
    if (ShowDataCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    if( _data !=NULL )
    {
        for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
        {
            mfdataINPUT_DATA *data= _data->GetDataSet(dsIdx) ; 

            // Get data type
            char type[5];
            switch ( data->GetType() )
            {
                case mfdataINPUT_VIS2_TYPE:
                    strcpy(type,"VIS2");
                    break;
                case mfdataINPUT_T3_TYPE:
                    strcpy(type,"T3");
                    break;
                default:
                    strcpy(type,"unknw");
                    break;
            }

            // Display some infos
            PrintBody(msg,mcsFALSE,"%03d %-5s %-10s %-10s %-10s [%-5d %-2d]",
                    dsIdx,
                    type,
                    data->GetDateObs(),
                    data->GetInsName(),
                    data->GetArrName(),
                    data->GetNbOfData(),
                    data->GetNbOfWaves()
                    ) ;
        }
    }
    else
    {
            PrintBody(msg,mcsFALSE,"No data to display.");
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
