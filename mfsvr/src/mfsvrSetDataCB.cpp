/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrSetDataCB.cpp,v 1.5 2006-08-02 08:23:32 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/07/10 13:26:00  mella
 * Replcae data.h by mfdata.h...
 *
 * Revision 1.3  2006/07/05 15:21:50  lsauge
 * Implement bispectrum data handling
 *
 * Revision 1.2  2006/07/05 14:51:07  lsauge
 * added "add" method to setdata
 *
 * Revision 1.1  2006/06/27 08:58:06  lsauge
 * First implementation
 *
 *
******************************************************************************/

/**
 * @file
 *  Definition of SETMODEL command callback.
 */

static char *rcsId="@(#) $Id: mfsvrSetDataCB.cpp,v 1.5 2006-08-02 08:23:32 lsauge Exp $"; 
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
#include "mfsvrSETDATA_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

#include "mfdata.h"
#include "mfdataINPUT_DATA_LIST.h"
#include "mfdataINPUT_VIS2.h"

#include <sys/types.h>
#include <sys/stat.h>

/*
 * Public methods
 */

/*!
  Set data. Read oifits file and extract VIS2 table from it.
TODO :  - implement other file format (ascii...)
- implement other data format (T3, VIS ...)
*/
evhCB_COMPL_STAT mfsvrSERVER::SetDataCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrSETDATA_CMD setDataCmd(msg.GetCommand(), msg.GetBody());
    if (setDataCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }


    // Get filename parameter
    char *fileName;
    if (setDataCmd.GetFileName(&fileName) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Get "add" flag
    mcsLOGICAL add;
    if (setDataCmd.GetAdd(&add) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    if ( (_data == NULL)&&(add == mcsTRUE) )
    {
        PrintBody(msg,mcsFALSE,"No data set. Add method is not consistent in this context.");
        add = mcsFALSE;
    }

    // Extract VIS2 table from oifits
    mfdataINPUT_DATA_LIST *dataList; 
    if ( add == mcsFALSE )
    {
        if (_data != NULL)
        {
            PrintBody(msg,mcsFALSE,"Delete previous data from memory");
            delete(_data);
        }
        dataList = new mfdataINPUT_DATA_LIST(); 
        _data = dataList;

        mfdataINPUT_VIS2 *data = new mfdataINPUT_VIS2(); 
        data->SetDataDimensions(1,1);
    }
    else
    {
        dataList = _data ;
    }


    // Extract VIS2 table from file
    mcsUINT16 nVis2 = 0;
    dataList->ExtractVIS2DataFromOIFITS(fileName);
    PrintBody(msg,mcsFALSE,"nb of individual VIS table =%d",
            nVis2 = _data->GetNbOfDataSet());

    // Extract T3 table from file
    dataList->ExtractT3DataFromOIFITS(fileName);
    PrintBody(msg,mcsFALSE,"nb of individual T3 table =%d",
            _data->GetNbOfDataSet()-nVis2);
	
	// Reset fit engine
	this->ResetEngine();

    // Send reply
    msg.SetBody("Done.");
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
