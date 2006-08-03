/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrSetScaleCB.cpp,v 1.5 2006-08-02 08:24:27 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2006/07/04 08:34:09  lsauge
 * Add "method" for autoscaling
 *
 * Revision 1.3  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SETSCALE command callback.
 */

static char *rcsId="@(#) $Id: mfsvrSetScaleCB.cpp,v 1.5 2006-08-02 08:24:27 lsauge Exp $"; 
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
#include "mfsvrSETSCALE_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*!
    Regularize or Reset parameter scales
 */
evhCB_COMPL_STAT mfsvrSERVER::SetScaleCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrSETSCALE_CMD SetScaleCmd(msg.GetCommand(), msg.GetBody());
    if (SetScaleCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
        
    if ((SetScaleCmd.IsDefinedAuto() == mcsTRUE) &&
        (SetScaleCmd.IsDefinedReset() == mcsTRUE))
    {
        PrintBody(msg,mcsTRUE,"Auto and Reset methods could no be called simultaneously");
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
 

    if (_params == NULL)
    {
        PrintBody(msg,mcsTRUE,"Cannot get engine parameters.");
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Reset Method
    if (SetScaleCmd.IsDefinedReset() == mcsTRUE)
    {
        for(mcsUINT16 i=0 ; i<_params->Size() ; i++)
        {
            fitengPARAMETER *parfit = _params->GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
            parfit->SetScale(1.0);
        }
        PrintBody(msg,mcsTRUE,"Resetting scale factors done.");
    }
    
    // Autoscaling method
    if (SetScaleCmd.IsDefinedAuto() == mcsTRUE)
    {

        // Get the autoscaling method (default=0)
        mcsINT32 method;
        if (SetScaleCmd.GetMethod(&method) == mcsFAILURE)
        {
            PrintBody(msg,mcsTRUE,"Cannot get autoscaling method...");
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        
        _params->ComputeParameterScale(mcsTRUE,method);
        PrintBody(msg,mcsTRUE,"Autoscaling done.");
    }
	
	// Reset fit engine
	this->ResetEngine();
    
    return evhCB_NO_DELETE;

}

/*___oOo___*/
