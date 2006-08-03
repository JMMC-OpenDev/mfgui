/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrSetParameterCB.cpp,v 1.4 2006-08-02 08:24:10 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/06/28 20:01:51  lsauge
 * Safe copy of parameter structures from model to engine and vice versa
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SETparamETER command callback.
 */

static char *rcsId="@(#) $"; 
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
#include "mfsvrSETPARAMETER_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*!
  Set parameter values, bounds and other properties Strategy is the following :
  if the fit engine is intialised, we work with the fitengine parmeter.
  otherwise, we consider the model parameters 
 */
evhCB_COMPL_STAT mfsvrSERVER::SetParameterCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrSETPARAMETER_CMD SetParameterCmd(msg.GetCommand(), msg.GetBody());
    if (SetParameterCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    if (_params ==  NULL)
    {
        
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

        if (_model->GetParameters(&paramList,mcsTRUE) == mcsFAILURE)
        {
            PrintBody(msg,mcsTRUE,"Cannot get any parameter list!");
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
        

        // Get the resquested parameter index
        mcsINT32 paramIdx;
        if (SetParameterCmd.GetIndex(&paramIdx) == mcsFAILURE)
        {
            return  evhCB_NO_DELETE | evhCB_FAILURE;
        }
        

        // Check if it lying into the bounds
        mcsUINT16 nbOfParam = paramList.Size();

        if (paramIdx >= nbOfParam)
        {
            PrintBody(msg,mcsFALSE,"Index out of bounds (<%d)",nbOfParam);
            return  evhCB_NO_DELETE | evhCB_FAILURE;
        }
        

        mfmdlPARAMETER *param = NULL;
        mcsUINT16 idx=0;
        do
        {
            param = paramList.GetNextParameter((idx == 0)?mcsTRUE:mcsFALSE);
        }
        
        while(paramIdx>idx++);

        // Be sure that the param ptr is ok
        if (param == NULL)
        {
            return  evhCB_NO_DELETE | evhCB_FAILURE;
        }
        

        // Set value if required
        if (SetParameterCmd.IsDefinedValue() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetValue(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetValue(value);
        }

        // Set Min value if required 
        if (SetParameterCmd.IsDefinedMin() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetMin(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetMinValue(value);
        }

        // Set Max value if required 
        if (SetParameterCmd.IsDefinedMax() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetMax(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetMaxValue(value);
        }

        // Set the fixed flag if required
        if (SetParameterCmd.IsDefinedFixed() == mcsTRUE)
        {
            mcsLOGICAL value;
            if (SetParameterCmd.GetFixed(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetFixedValue(value);
        }

        // Set the scale if required
        if (SetParameterCmd.IsDefinedScale() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetScale(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetScale(value);
        }
        
        (void) CopyParameters_ModelToEngine();

    }
    else
    {
        
        // Get the resquested parameter index
        mcsINT32 paramIdx;
        if (SetParameterCmd.GetIndex(&paramIdx) == mcsFAILURE)
        {
            return  evhCB_NO_DELETE | evhCB_FAILURE;
        }
        
        // Check if it lying into the bounds
        mcsUINT16 nbOfParam = _params->Size();

        if (paramIdx >= nbOfParam)
        {
            PrintBody(msg,mcsFALSE,"Index out of bounds (<%d)",nbOfParam);
            return  evhCB_NO_DELETE | evhCB_FAILURE;
        }

        fitengPARAMETER *param = NULL;
        mcsUINT16 idx=0;
        do
        {
            param = _params->GetNextParameter((idx == 0)?mcsTRUE:mcsFALSE);
        }
        while(paramIdx>idx++);

        // Be sure that the param ptr is ok
        if (param == NULL)
        {
            return  evhCB_NO_DELETE | evhCB_FAILURE;
        }

        // Set value if required
        if (SetParameterCmd.IsDefinedValue() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetValue(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetValue(value);
        }

        // Set Min value if required 
        if (SetParameterCmd.IsDefinedMin() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetMin(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetMinValue(value);
        }

        // Set Max value if required 
        if (SetParameterCmd.IsDefinedMax() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetMax(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetMaxValue(value);
        }

        // Set the fixed flag if required
        if (SetParameterCmd.IsDefinedFixed() == mcsTRUE)
        {
            mcsLOGICAL value;
            if (SetParameterCmd.GetFixed(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetFixedValue(value);
        }

        // Set the scale if required
        if (SetParameterCmd.IsDefinedScale() == mcsTRUE)
        {
            mcsDOUBLE value;
            if (SetParameterCmd.GetScale(&value) == mcsFAILURE)
            {
                return  evhCB_NO_DELETE | evhCB_FAILURE;
            }
            param->SetScale(value);
        }

        (void) CopyParameters_EnginetoModel();
        
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
