/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrDoNextStepCB.cpp,v 1.7 2006-08-03 14:20:52 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2006/08/02 08:15:55  lsauge
 * deport Engine intilizations from InitLMEngine to the current part of code.
 *
 * Revision 1.5  2006/07/10 12:24:58  lsauge
 * Refactor to eliminate code duplication of residuals evaluation
 *
 * Revision 1.4  2006/07/05 14:52:07  lsauge
 * Fix problem with replacement of data module name by mfdata
 *
 * Revision 1.3  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of DONEXTSTEP command callback.
 */

static char *rcsId="@(#) $Id: mfsvrDoNextStepCB.cpp,v 1.7 2006-08-03 14:20:52 lsauge Exp $"; 
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
#include "mfsvrDONEXTSTEP_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*!
  Perform a new fit step. By default, "one step" corresponds to one fit
  engine iteration. User could set the number of iteration using -iter
  argument of the present message 

TODO: implement chi2 'end-of-loop' criteria as an argument 
 */
evhCB_COMPL_STAT mfsvrSERVER::DoNextStepCB(msgMESSAGE &msg, void*)
{

    // Build the command object
    mfsvrDONEXTSTEP_CMD DoNextStepCmd(msg.GetCommand(), msg.GetBody());

    if (DoNextStepCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
           
    // Get the max number of allowed iteration
    mcsINT32 iterMax = 1;
    if (DoNextStepCmd.GetIter(&iterMax) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Get the 'resettign engine' flag 
    mcsLOGICAL resettingEngine = mcsFALSE ; 
    if (DoNextStepCmd.GetReset(&resettingEngine) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // if the previous flag is set to TRUE, resetting the engine
    if (resettingEngine == mcsTRUE)
    {
        PrintBody(msg,mcsFALSE,"Fitting Engine should be reset.");
        this->ResetEngine();
    }
   
    // Init the LM engine if necessary
    if ( (_iter == 0)||
         (_fitEngine == NULL ))
    {
        PrintBody(msg,mcsFALSE,"Initialize LM engine");
        if (this->InitLMEngine(msg) == mcsFAILURE)
        {
            PrintBody(msg,mcsFALSE,"An error occured during fit engine initialization. Abort.");
            return evhCB_NO_DELETE | evhCB_SUCCESS;
        }
    }
    
    // Check the previous stage status, maybe no further progress is possible
    if ( (_fitEngine->GetStage() == oplmSTAGE_ERROR)||
         (_fitEngine->GetStage() == oplmSTAGE_END) )
    {
        PrintBody(msg,mcsTRUE,"No further progess is possible. Done.");
        return evhCB_NO_DELETE | evhCB_SUCCESS;
    }
    
    // main iteration loop
    mcsUINT16 iter =  0;
    for(iter=0 ; iter<iterMax ; iter++)
    {

        // MAIN JOB : next iteration 
        mcsCOMPL_STAT status = _fitEngine->DoNextStep();

        // Set model parameters using the fit engine parameters
        (void) CopyParameters_EnginetoModel();
        
    
        // Compute total flux
        mcsDOUBLE sumFlux = 0.0; 
        for(mcsUINT16 i=0 ; i<_params->Size() ; i++)
        {
            fitengPARAMETER *parfit = _params ->GetNextParameter((i==0)?mcsTRUE:mcsFALSE);
            if((parfit->GetName()).c_str() == "intensityCoefficient")
            {
                sumFlux += parfit->GetValue(); 
            }
        }

        // New Evaluation of residuals
        if (this->EvaluateResiduals() == mcsFAILURE)
        {
            PrintBody(msg,mcsTRUE,"Cannot evalute residuals. Abort.");
        }

        // Get Lagrange's multiplier
        fitengPARAMETER *paramLM;
        if ( (paramLM = _params->GetParameter("lagrangeMultiplier")) == NULL)
        {
            PrintBody(msg,mcsTRUE,"Cannot get Lagrange's multiplier. Done.");
            return evhCB_NO_DELETE | evhCB_SUCCESS;
        }
        mcsDOUBLE lambda = paramLM->GetValue();
        
        // And set last residual 
        _residues->SetValue( _residues->Size()-1, lambda*(1.0-sumFlux));

        // Display some info
        mcsDOUBLE chi2 = _residues->GetChi2() ;
        mcsUINT32 currentStage = _fitEngine->GetStage() ;
        PrintBody(msg,mcsFALSE,"%04d Stage: %d Info:%d chi2=%e (chi2/dof=%e)",
                   ++_iter,
                  currentStage,
                  _fitEngine->GetInfo(),
                  chi2,
                  chi2/_dof
                 );
       
        if (currentStage == oplmSTAGE_ERROR)
        {
            PrintBody(msg,mcsTRUE,"An error occured. See stage/info parameter values.");
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        
        if (currentStage == oplmSTAGE_END)
        {
            mcsDOUBLE chi2 = _residues->GetChi2();
            PrintBody(msg,mcsFALSE,"Optimization has converged");
            PrintBody(msg,mcsTRUE,"=== Iter: %d chi2=%e (chi2/dof=%e)",
                  _iter,
                  chi2,
                  chi2/_dof
                 );
            return evhCB_NO_DELETE | evhCB_SUCCESS;
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
