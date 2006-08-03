/*******************************************************************************
 * JMMC project
 *
 * "@(#) : $Id: mfsvrExploreMCCB.cpp,v 1.3 2006-07-05 14:52:07 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 *
******************************************************************************/

/**
 * @file
 *  Definition of EXPLOREMC command callback.
 */

static char *rcsId="@(#) : $Id: mfsvrExploreMCCB.cpp,v 1.3 2006-07-05 14:52:07 lsauge Exp $"; 
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
#include "mfsvrEXPLOREMC_CMD.h"
#include "mfsvrGETTIME_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

#include <time.h>
#include <stdlib.h>

/**
 *  DOXYGEN HEADER
 */
evhCB_COMPL_STAT mfsvrSERVER::ExploreMCCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrEXPLOREMC_CMD ExploreMCCmd(msg.GetCommand(), msg.GetBody());
    if (ExploreMCCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Get the number of d iteration
    mcsINT32 iterMax = 1;
    if (ExploreMCCmd.GetIter(&iterMax) == mcsFAILURE)
    {
        return  evhCB_NO_DELETE | evhCB_FAILURE;
    }


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

    // Set the seed of uniform variate generator using UNIX clock 
    unsigned int seed = (unsigned int)time(NULL) ;
    (void) srand(seed);


    mcsDOUBLE bestChi2 = 1.0e+300 ;

    for (mcsUINT16 iter=0 ; iter<iterMax ; iter++)
    {
        PrintBody(msg,mcsFALSE,"==================================================",iter);
        PrintBody(msg,mcsFALSE,"ITER #%d",iter);

        // if _fitEngine is already exist delete it 
        if ( _fitEngine != NULL)
        {
            delete(_fitEngine);
        }

        // instantiate a new session
        _fitEngine = new fitengLM_ENGINE();

        mcsUINT16 idx;
        for (idx=0 ; idx<_params->Size() ; idx++)
        {
            fitengPARAMETER *param = NULL;
            param = _params->GetNextParameter((idx == 0)?mcsTRUE:mcsFALSE);

            if ( (param != NULL) && (param->HasFixedValue() == mcsFALSE) )
            {
                mcsDOUBLE rUnif = (mcsDOUBLE)rand()/((mcsDOUBLE)RAND_MAX);
                mcsDOUBLE val_m = param->GetMinValue();
                mcsDOUBLE val_M = param->GetMaxValue();
                mcsDOUBLE val_c = val_m + (val_M-val_m)*rUnif ;

                param->SetValue(val_c);
            }
        }

        // Regularize parameter
        _params->ComputeParameterScale(mcsTRUE,1);

        // Then, instantiate new residual list
        // "+1" in order to take into account the normalized flux constraint
        _residues = new fitengRESIDUE_LIST(_data->GetNbOfData()+1);

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
            PrintBody(msg,mcsTRUE,"An error occured during the engine initialization Abort.");
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }

        // First computation of residuals
        idx = 0;
        for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
        {
            mfdataINPUT_VIS2 *dataVis2 = _data->GetDataSet(dsIdx) ; 

            mcsINT16  nbOfData = dataVis2->GetNbOfData();

            mfmdlUV_COORDS *uvCoords = new mfmdlUV_COORDS(nbOfData);
            mfmdlVIS       *visCpt   = new mfmdlVIS(nbOfData);

            (void) dataVis2->GetUVCoords(*uvCoords);

            if (_model->Eval(*uvCoords,*visCpt) != mcsSUCCESS)
            {
                PrintBody(msg,mcsTRUE,"An error occured during the model evaluation.");
                return evhCB_NO_DELETE | evhCB_FAILURE;
            }

            for (mcsUINT16 dataIdx=0 ; dataIdx < nbOfData ; dataIdx++)
            {

                mcsDOUBLE O = dataVis2->GetVis2Data(dataIdx);
                mcsDOUBLE E = dataVis2->GetVis2Err (dataIdx);
                mcsDOUBLE C = visCpt->GetVis2(dataIdx);
                mcsDOUBLE residue = (O-C)/E; 
                _residues->SetValue(idx,residue);
                idx++;
            }

            delete(uvCoords);
            delete(visCpt);   
        }

        // Set the last residual to zero
        _residues->SetValue(idx,0.0);

        // print starting chi2 value and exit ...
        PrintBody(msg,mcsFALSE,"Chi2 Starting value : %g",_residues->GetChi2());

        for(mcsINT32 iter=0 ; iter<10000 ; iter++)
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
            mcsUINT16 idx = 0;
            for (mcsUINT16 dsIdx=0 ; dsIdx < _data->GetNbOfDataSet() ; dsIdx++)
            {
                mfdataINPUT_VIS2 *dataVis2 = _data->GetDataSet(dsIdx) ; 

                mcsINT16  nbOfData = dataVis2->GetNbOfData();

                mfmdlUV_COORDS *uvCoords = new mfmdlUV_COORDS(nbOfData);
                mfmdlVIS       *visCpt   = new mfmdlVIS(nbOfData);

                (void) dataVis2->GetUVCoords(*uvCoords);

                // Compute model
                if (_model->Eval(*uvCoords,*visCpt) != mcsSUCCESS)
                {
                    PrintBody(msg,mcsTRUE,"An error occured during the model evaluation.");
                    return evhCB_NO_DELETE | evhCB_FAILURE;
                }

                // Compute corresponding residuals
                for (mcsUINT16 dataIdx=0 ; dataIdx < nbOfData ; dataIdx++)
                {
                    mcsDOUBLE O = dataVis2->GetVis2Data(dataIdx) ;
                    mcsDOUBLE E = dataVis2->GetVis2Err (dataIdx) ;
                    mcsDOUBLE C = visCpt->GetVis2(dataIdx) ;
                    mcsDOUBLE residue = (O-C)/E; 

                    _residues->SetValue(idx,residue);

                    idx++;
                }

                delete(uvCoords);
                delete(visCpt);   
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
            _residues->SetValue(idx,sqrt(fabs(lambda*(sumFlux-1.0))));

            // Display some info
            mcsUINT32 currentStage; 
            currentStage = _fitEngine->GetStage();

#if 0
            PrintBody(msg,mcsFALSE,"Stage: %d Info:%d chi2=%e ",
                        currentStage = _fitEngine->GetStage(),
                        _fitEngine->GetInfo(),
                        _residues->GetChi2()
                        );
#endif

            if (currentStage == oplmSTAGE_ERROR)
            {
                PrintBody(msg,mcsTRUE,"An error occured. See stage/info parameter values.");
                return evhCB_NO_DELETE | evhCB_FAILURE;
            }

            if (currentStage == oplmSTAGE_END)
            {
                mcsDOUBLE chi2;
                PrintBody(msg,mcsFALSE,"Optimization has converged");
                PrintBody(msg,mcsFALSE,"Stage: %d Info:%d chi2=%e ",
                        _fitEngine->GetStage(),
                        _fitEngine->GetInfo(),
                        chi2 = _residues->GetChi2()
                        );

                bestChi2 = (chi2<bestChi2)?chi2:bestChi2 ;
                    
                for (idx=0 ; idx<_params->Size() ; idx++)
                {
                    fitengPARAMETER *param = NULL;
                    param = _params->GetNextParameter((idx == 0)?mcsTRUE:mcsFALSE);

                    if ( (param != NULL))
                    {
                        PrintBody(msg,mcsFALSE,"%-25s %+7.4e [%+7.4e,%+7.4e]",
                                (param->GetName()).c_str(),
                                param->GetValue(),
                                param->GetMinValue(),
                                param->GetMaxValue()
                                );
                    }
                }

                break;
            }
        }
    }

    PrintBody(msg,mcsFALSE,"Best chi2 value = %7.4e",bestChi2);

    // Send reply
    msg.SetBody("Done.");
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}

/*___oOo___*/
