/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrInitLMEngineCB.cpp,v 1.8 2006-08-02 08:22:06 lsauge Exp $"
 *
 * History
 * -------
 * $Log:
 *
******************************************************************************/

/**
 * @file
 *  Definition of INITLMENGINE command callback.
 */

static char *rcsId="@(#) $Id: mfsvrInitLMEngineCB.cpp,v 1.8 2006-08-02 08:22:06 lsauge Exp $";
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
#include "mfsvrINITLMENGINE_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

#include "fiteng.h"
#include "fitengFIT_ENGINE.h"
#include "fitengLM_ENGINE.h"
#include "fitengPARAMETER.h"
#include "fitengPARAMETER_LIST.h"
#include "fitengRESIDUE_LIST.h"

/*!
	Useless method. Exist for compatibility purposes, should be removed soon.
	All engine inilisation are performed during the DONEXTSTEP procedure.
*/
evhCB_COMPL_STAT mfsvrSERVER::InitLMEngineCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrINITLMENGINE_CMD InitLMEngineCmd(msg.GetCommand(), msg.GetBody());
    if (InitLMEngineCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
   
	// 
	PrintBody(msg,mcsFALSE,"This method is useless and not longer available if this version.");
	PrintBody(msg,mcsTRUE ,"Use DONEXTSTEP method directly.");

    return evhCB_NO_DELETE;
}

/*___oOo___*/
