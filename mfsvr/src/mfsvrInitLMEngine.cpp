/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrInitLMEngine.cpp,v 1.2 2006-08-03 14:22:58 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/08/02 08:26:30  lsauge
 * First import
 *
 *
******************************************************************************/

/**
 * @file
 *   */

static char *rcsId="@(#) $Id: mfsvrInitLMEngine.cpp,v 1.2 2006-08-03 14:22:58 lsauge Exp $";
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

  Perform the the Levenberg-Marquardt fit engine initialization.

*/
mcsCOMPL_STAT mfsvrSERVER::InitLMEngine(msgMESSAGE msg)
{

    // Data must be read first. If it is not the case, abort.
    if (_data == NULL )
    {
        PrintBody(msg,mcsFALSE,"Read data first! Abort.");
        return mcsFAILURE;
    }

    // if _fitEngine is already exist delete it 
    if ( _fitEngine != NULL)
    {
        delete(_fitEngine);
    }

    // instantiate a new session
    _fitEngine = new fitengLM_ENGINE();
    
    // Display some info about the Fit Engine (name and version)
    mcsUINT16 minor,major;
    _fitEngine->GetVersion(major,minor);
    PrintBody(msg,mcsFALSE,"Fit engine: %s (ver %d.%d)",
            _fitEngine->GetDescription(),
            major,
            minor
            );

    // _model must be defined 
    if (_model == NULL )
    {
        PrintBody(msg,mcsFALSE,"No model defined. Abort.");
        return mcsFAILURE;
    }

    // Set model parameters using the fit engine parameters
    (void) CopyParameters_EnginetoModel();
   
    // Compute the size of the residual list
    mcsUINT16 residualListSize = 1 ;
    for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
    {
        mfdataINPUT_DATA *dataNoCast = _data->GetDataSet(dsIdx) ; 
        switch ( dataNoCast->GetType() )
        {
            case mfdataINPUT_VIS2_TYPE:
                residualListSize +=  dataNoCast->GetNbOfWaves()*
                                     dataNoCast->GetNbOfData();
                break;
            case mfdataINPUT_T3_TYPE:
                residualListSize +=  2*dataNoCast->GetNbOfWaves()*
                                       dataNoCast->GetNbOfData();
                break;
            default:
                break;
        }
    }
    
    // Compute and display the number of degrees of freedom
    _dof = residualListSize-_params->Size();
    
    if (_dof <= 0)
    {
        PrintBody(msg,mcsFALSE,"Number of degrees of freedom must be >0. Abort.");
        return mcsFAILURE; 
    }
    PrintBody(msg,mcsFALSE,"Number of degrees of freedom : %d",_dof);
    
    // Then, instantiate new residual list
    // "+1" in order to take into account the normalized flux constraint
    _residues = new fitengRESIDUE_LIST(residualListSize);
    
    // Init engine 
    if((_params != NULL) && (_residues != NULL))
    {
        _fitEngine->Init(_params,_residues);
        // Set tolerance and FMaxEval
        // TODO: this setting must be provided using a dedicated server
        // message
        _fitEngine->SetTolerance(1e-7,1e-7,1e-7);
        _fitEngine->SetMaxFEval(5000);
    }
    else
    {
        PrintBody(msg,mcsFALSE,"An error occured during the engine initialization. Abort.");
        return mcsFAILURE; 
    }

    // First computation of residuals
    if (this->EvaluateResiduals() == mcsFAILURE)
    {
        PrintBody(msg,mcsFALSE,"Cannot evalute residuals. Abort.");
        return mcsFAILURE; 
    }

    // And set last residual (relative to the flux normalization)  
    {
        // Get Lagrange's multiplier
        fitengPARAMETER *paramLM;
        if ( (paramLM = _params->GetParameter("lagrangeMultiplier")) == NULL)
        {
            PrintBody(msg,mcsTRUE,"Cannot get Lagrange's multiplier. Done.");
            return mcsFAILURE; 
        }
        mcsDOUBLE lambda = paramLM->GetValue();
        
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
        
        _residues->SetValue( _residues->Size()-1, lambda*(1.0-sumFlux));
    }

    // print starting chi2 value
    mcsDOUBLE chi2 = _residues->GetChi2(),
    PrintBody(msg,mcsFALSE,"Chi2 Starting value : %g (chi2/dof=%g)",
            chi2,
            chi2/_dof);

    // exit function
    return mcsSUCCESS;
}

/*___oOo___*/
