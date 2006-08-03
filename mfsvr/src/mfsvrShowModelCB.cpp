/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrShowModelCB.cpp,v 1.4 2006-08-02 08:25:28 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/07/04 08:34:41  lsauge
 * minor changes
 *
 * Revision 1.2  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SHOWMODEL command callback.
 */

static char *rcsId="@(#) $Id: mfsvrShowModelCB.cpp,v 1.4 2006-08-02 08:25:28 lsauge Exp $"; 
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
#include "mfsvrSHOWMODEL_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*!
  Show model list
 */

evhCB_COMPL_STAT mfsvrSERVER::ShowModelCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrSHOWMODEL_CMD ShowModelCmd(msg.GetCommand(), msg.GetBody());
    if (ShowModelCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
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
  
    // cast the model as a composed one
    mfmdlCOMPOSED_MODEL *model = (mfmdlCOMPOSED_MODEL *) _model;
    
    // Get the nb of models
    mcsUINT16 nbOfModels = model->GetNbOfComposingModels();

    // Display some information oabout individual model
    mcsUINT16 modelIdx;
    for (modelIdx = 0 ; modelIdx < nbOfModels; modelIdx++)
    {
        mfmdlMODEL *indivModel = NULL;
        if (model->GetComposingModel(modelIdx,&indivModel) == mcsSUCCESS)
        {
            PrintBody(msg,mcsFALSE,"%02d [%-20s] -> [%-30s] %-10s [type=%02d]",
                   modelIdx,
                   indivModel->GetName(),
                   indivModel->GetDescription(),
                   (indivModel->IsPolarEnabled()==mcsTRUE)?"POLAR":"CARTESIAN",
                   indivModel->GetModelType()
                   );
        }
        else
        {
            PrintBody(msg,mcsTRUE,"Cannot get information about individual model.");
            logError("Cannot get information about individual model.");
            return evhCB_NO_DELETE | evhCB_FAILURE;
        }
    }


    this->ExpandModelTree(_model);
   
    // All jobs done.
    if (PrintBody(msg,mcsTRUE,"Done.") == evhCB_FAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}

/*___oOo___*/
