/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrShowParametersCB.cpp,v 1.8 2006-08-03 14:20:52 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2006/08/02 08:25:52  lsauge
 * Minor changes
 *
 * Revision 1.6  2006/07/04 08:35:15  lsauge
 * minor changes
 *
 * Revision 1.5  2006/06/29 10:23:23  lsauge
 * add "model" and "engine" param flags
 * safe managing of parameter values
 *
 * Revision 1.4  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SHOWPARAMETERS command callback.
 */

static char *rcsId="@(#) $Id: mfsvrShowParametersCB.cpp,v 1.8 2006-08-03 14:20:52 lsauge Exp $"; 
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
#include "mfsvrSHOWPARAMETERS_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*!
  Show parameter list and properties
TODO : it is better to consider fit engine parameters than the model one ...
 */
evhCB_COMPL_STAT mfsvrSERVER::ShowParametersCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrSHOWPARAMETERS_CMD ShowParametersCmd(msg.GetCommand(), msg.GetBody());
    if (ShowParametersCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Get method parameters
    mcsLOGICAL  cModel = mcsFALSE ;
    mcsLOGICAL  cEngine= mcsTRUE  ;

    if (ShowParametersCmd.GetEngine(&cEngine) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }

    if (ShowParametersCmd.GetModel(&cModel) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }


    // === Display Engine parameters if required
    if ( cEngine )
    {
        // Check if _params is defined. Itis not the case if the fitting engine was
        // not previously intiliazed. In this case, we disàlay the model parameters.
        if (_params != NULL)
        {
            (void) PrintBody(msg,mcsFALSE,"=== FITTED PARAMETERS");
            
            mcsUINT16 idx;
            for (idx=0 ; idx<_params->Size() ; idx++)
            {
                fitengPARAMETER *param = NULL;
                param = _params->GetNextParameter((idx == 0)?mcsTRUE:mcsFALSE);
                if (param != NULL)
                {
                    mcsDOUBLE val_c = param->GetValue();
                    mcsDOUBLE val_m = param->GetMinValue();
                    mcsDOUBLE val_M = param->GetMaxValue();

                    PrintBody(msg,mcsFALSE,"%03d %-20s %+7.5e [%+7.5e , %+7.5e] %s %s %7.5e",
                              idx,
                              (param->GetName()).c_str(),
                              val_c,
                              val_m,
                              val_M,
                              (param->HasFixedValue() == mcsTRUE ) ? "FIXED" : "FREE ",
                              (val_c>=val_m)&&(val_c<=val_M) ? "" : "OUT OF BOUNDS",
                              param->GetScale()
                             );
                }
            }
        }
        else
        {
            // Display infos
            (void) PrintBody(msg,mcsFALSE,"Cannot access to the engine parameters.");
            (void) PrintBody(msg,mcsFALSE,"Fit engine must be initialised first.");
            // then, force the display of the model parameters instead     
            cModel = mcsTRUE ;
        }
    }


    // === Display Model parameters if required
    if( cModel )
    {
        // Fit engine is not initialised yet. Show model parameters. 
        // Check if a model is defined 
        if (_model == NULL)
        {
            logInfo("No model defined.");
            evhCB_COMPL_STAT status = PrintBody(msg,mcsTRUE,"No model defined. Done.");
            return status | evhCB_NO_DELETE;
        }

        // Model must be type of composed at top level.
        // It should me the case if the user used the SetModel command
        if (_model->GetModelType() != mfmdlCOMPOSED_MODEL_TYPE)
        {
            PrintBody(msg,mcsTRUE,"An error occured. Model must be type of composed at top level.");
            logError("An error occured. Model must be type of composed at top level.");
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

        mfmdlPARAMETER_LIST paramList;

        if (_model->GetParameters(&paramList,mcsTRUE) == mcsSUCCESS)
        {
            (void) PrintBody(msg,mcsFALSE,"=== MODEL PARAMETERS");
            
            mcsUINT16 idx;
            for (idx=0 ; idx<paramList.Size() ; idx++)
            {
                mfmdlPARAMETER *param = NULL;
                param = paramList.GetNextParameter((idx == 0)?mcsTRUE:mcsFALSE);
                if (param != NULL)
                {

                    PrintBody(msg,mcsFALSE,"%03d %-20s %+7.5e",
                            idx,
                            (param->GetName()).c_str(),
                            param->GetValue()
                            );
                }
            }
        }
    }

	// Display lat fit results, one from the parameter values are resulting 
	if (_residues != NULL)
	{
			mcsDOUBLE chi2=_residues->GetChi2() ;
			PrintBody(msg,mcsFALSE,"=== Iter: %d chi2=%e (chi2/dof=%e)",
							_iter,
							chi2,
							chi2/_dof
					 );
	}
    
    PrintBody(msg,mcsTRUE,"Done.");

    return evhCB_NO_DELETE;
}

/*___oOo___*/
