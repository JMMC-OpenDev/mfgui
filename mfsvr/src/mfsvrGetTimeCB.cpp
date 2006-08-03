/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrGetTimeCB.cpp,v 1.1 2006-06-27 08:58:06 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SETMODEL command callback.
 */

static char *rcsId="@(#) $Id: mfsvrGetTimeCB.cpp,v 1.1 2006-06-27 08:58:06 lsauge Exp $"; 
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
#include "mfsvrGETTIME_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

#include <time.h>

/*
 * Public methods
 */
/*!
    display current date and time 
 */
evhCB_COMPL_STAT mfsvrSERVER::GetTimeCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrGETTIME_CMD getTimeCmd(msg.GetCommand(), msg.GetBody());
    if (getTimeCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
	
	time_t clock = time(NULL); 
    PrintBody(msg,mcsTRUE,"Current Date and Time: %s",asctime(localtime(&clock))); 
	
    return evhCB_NO_DELETE;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
