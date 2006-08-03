/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrSetLMParamCB.cpp,v 1.2 2006-06-28 20:26:37 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SETLMPARAM command callback.
 */

static char *rcsId="@(#) $Id: mfsvrSetLMParamCB.cpp,v 1.2 2006-06-28 20:26:37 lsauge Exp $"; 
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
#include "mfsvrSETLMPARAM_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*!
  Set LM param ...
TODO: implement the function !
 */
evhCB_COMPL_STAT mfsvrSERVER::SetLMParamCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrSETLMPARAM_CMD SetLMParamCmd(msg.GetCommand(), msg.GetBody());
    if (SetLMParamCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // ***********************************
    //       INSERT YOUR CODE HERE
    // ***********************************
	
    // Send reply
    msg.SetBody("Done.");
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}

/*___oOo___*/
