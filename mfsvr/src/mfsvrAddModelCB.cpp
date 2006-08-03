/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrAddModelCB.cpp,v 1.7 2006-08-02 08:14:35 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2006/07/05 14:52:07  lsauge
 * Fix problem with replacement of data module name by mfdata
 *
 * Revision 1.5  2006/07/04 08:28:53  lsauge
 * Added polar coordinates facilities
 *
 * Revision 1.4  2006/06/29 10:23:48  lsauge
 * safe managing of parameter values
 *
 * Revision 1.3  2006/06/28 20:26:37  lsauge
 * Fix header problem (cvs/rcs keywords)
 *
 * Revision 1.2  2006/06/28 20:02:59  lsauge
 * Fix bad memory access ; set pointer to null after delete
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of ADDMODEL command callback.
 */

static char *rcsId="@(#) $Id: mfsvrAddModelCB.cpp,v 1.7 2006-08-02 08:14:35 lsauge Exp $"; 
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
#include "mfsvrADDMODEL_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/**
 *  Add a model to the current model container (root)
 */
evhCB_COMPL_STAT mfsvrSERVER::AddModelCB(msgMESSAGE &msg, void*)
{
    // Build the command object
    mfsvrADDMODEL_CMD AddModelCmd(msg.GetCommand(), msg.GetBody());
    if (AddModelCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Check if a model is defined 
    if (_model == NULL)
    {
        logInfo("No model defined.");
        evhCB_COMPL_STAT status = PrintBody(msg,mcsFALSE,"No model defined. Done.");
        mfmdlCOMPOSED_MODEL* _model = new mfmdlCOMPOSED_MODEL("Composed Model");
    }
    
    // Model must be type of composed at top level.
    // It should be the case if the user used the SetModel command
    if (_model->GetModelType() != mfmdlCOMPOSED_MODEL_TYPE)
    {
        PrintBody(msg,mcsTRUE,"An error occured. Model must be type of composed at top level.");
		logError("An error occured. Model must be type of composed at top level.");
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }
    
    // Cast the model as a composed one
    mfmdlCOMPOSED_MODEL *model = (mfmdlCOMPOSED_MODEL *) _model;
    
    // Get type of model to be fitted
    mcsINT32 modelType;
	if (AddModelCmd.GetType(&modelType) == mcsFAILURE)
	{
			errAdd(mfsvrERR_UNKNOWN_MODEL, modelType);
			return evhCB_NO_DELETE | evhCB_FAILURE;
    }

	mcsLOGICAL polarForm;
    if (AddModelCmd.GetPolar(&polarForm) == mcsFAILURE)
	{
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    PrintBody(msg,mcsFALSE,"requested: %d,%s",
            modelType,
            (polarForm==mcsTRUE)?"POLAR":"CARTESIAN"
            ) ;
   
    // Add new component 
    switch (modelType)
    {
        case 1:
            logTest("Add ponctual star model...");
            model->AddAtTail(new mfmdlPONCT("Point like source",polarForm));
            break;
        case 2:
            logTest("Add uniform disk model...");
            model->AddAtTail(new mfmdlUNIFORM_DISK("Uniform disk",polarForm));
            break;
        default:
            errAdd(mfsvrERR_UNKNOWN_MODEL, modelType);
            return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Rebuild parameters list
    if ( this->BuildParameterList( msg ) == mcsFAILURE )
    {
        PrintBody(msg,mcsTRUE,"Cannot set parameters. Abort.");
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

	// Reset fit engine
	this->ResetEngine();
	
    // reset some structures ...
    if (_residues != NULL)
    {
        delete (_residues);
        _residues = NULL;
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
