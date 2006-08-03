/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrSetModelCB.cpp,v 1.7 2006-08-02 08:23:52 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2006/07/04 08:33:19  lsauge
 * minor changes
 *
 * Revision 1.5  2006/06/28 20:02:59  lsauge
 * Fix bad memory access ; set pointer to null after delete
 *
 * Revision 1.4  2006/06/27 09:02:35  lsauge
 * Implementation modified
 *
 * Revision 1.1  2006/03/31 08:18:49  gzins
 * Added
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of SETMODEL command callback.
 */

static char *rcsId="@(#) $Id: mfsvrSetModelCB.cpp,v 1.7 2006-08-02 08:23:52 lsauge Exp $"; 
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
#include "mfsvrSETMODEL_CMD.h"
#include "mfsvrPrivate.h"
#include "mfsvrErrors.h"

/*
 * Public methods
 */
/**
 * Callback for SETMODE command.
 *
 * @param msg received message corresponding to SETMODE command
 *
 * @return evhCB_SUCCESS on succesful completeion, evhCB_FAILURE otherwise.
 */
evhCB_COMPL_STAT mfsvrSERVER::SetModeCB(msgMESSAGE &msg, void*)
{
    logTrace("mfsvrSERVER::GetCalCB()");

    // Build the command object
    mfsvrSETMODEL_CMD setModelCmd(msg.GetCommand(), msg.GetBody());
    if (setModelCmd.Parse() == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Get type of model to be fitted
    mcsINT32 modelType;
    if (setModelCmd.GetType(&modelType) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // If model is already defined, reset model, residue and fit engine
    // parameters ...
    if (_model != NULL)
    {
        delete (_model);
        _model = NULL;
    }
    if (_residues != NULL)
    {
        delete (_residues);
        _residues = NULL;
    }
    if (_params!= NULL)
    {
        delete (_params);
        _params = NULL;
    } 
    if (_fitEngine!= NULL)
    {
        delete (_fitEngine);
        _fitEngine = NULL;
    }
	
	// Reset fit engine
	this->ResetEngine();

    // Build model
    switch (modelType)
    {

        case 0:
            {
                logTest("Creating Composed model...");
                mfmdlCOMPOSED_MODEL *model = new mfmdlCOMPOSED_MODEL("Composed model");
                _model = model ;
            }
            break;
        case 1:
            {
                logTest("Creating ponctual star model...");
                mfmdlCOMPOSED_MODEL *model = new mfmdlCOMPOSED_MODEL("Point like source");
                mfmdlPONCT* star = new mfmdlPONCT("unresolved source");
                model->AddAtTail(star);
                _model = model;
            }
            break;
        case 2:
            {
                logTest("Creating binary star model...");
                mfmdlCOMPOSED_MODEL* binaryStar = 
                    new mfmdlCOMPOSED_MODEL("Binary star");
                mfmdlPONCT* star1 = new mfmdlPONCT("1rst component");
                mfmdlPONCT* star2 = new mfmdlPONCT("2nd component");
                binaryStar->AddAtTail(star1);
                binaryStar->AddAtTail(star2);
                _model = binaryStar;
            }
            break;
        case 3:
            {
                logTest("Creating triple star model...");
                mfmdlCOMPOSED_MODEL* tripleStar = 
                    new mfmdlCOMPOSED_MODEL("Binary star");
                mfmdlPONCT* star1 = new mfmdlPONCT("1rst component");
                mfmdlPONCT* star2 = new mfmdlPONCT("2nd component");
                mfmdlPONCT* star3 = new mfmdlPONCT("3st component");
                tripleStar->AddAtTail(star1);
                tripleStar->AddAtTail(star2);
                tripleStar->AddAtTail(star3);
                _model = tripleStar;
            }
            break;
        default:
            errAdd(mfsvrERR_UNKNOWN_MODEL, modelType);
            return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    // Build parameters list
    this->BuildParameterList( msg ) ;

    // Send reply
    msg.SetBody("Done.");
    if (SendReply(msg) == mcsFAILURE)
    {
        return evhCB_NO_DELETE | evhCB_FAILURE;
    }

    return evhCB_NO_DELETE;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
