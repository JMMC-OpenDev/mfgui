/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfmdlCOMPOSED_MODEL.cpp,v 1.8 2006-06-20 09:41:47 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2006/06/20 09:22:33  lsauge
 * Modified methods
 *
 * Revision 1.6  2006/05/11 13:04:55  mella
 * Changed rcsId declaration to perform good gcc4 and gcc3 compilation
 *
 * Revision 1.5  2006/03/31 08:17:02  gzins
 * Updated API
 *
 * Revision 1.4  2006/03/01 13:30:51  lsauge
 * Supprees argument mjd, time and wave in the Eval() function description
 *
 * Revision 1.3  2006/02/06 09:53:15  lsauge
 * Added model's type description (protected member and corresponding enum array type)
 *
 * Revision 1.2  2006/02/03 16:44:07  lsauge
 * Add Eval method
 *
 * Revision 1.1  2005/09/08 14:05:47  mella
 * First Revision
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of mfmdlCOMPOSED_MODEL class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: mfmdlCOMPOSED_MODEL.cpp,v 1.8 2006-06-20 09:41:47 lsauge Exp $";
/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
using namespace std;
#include <string>

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"

/*
 * Local Headers 
 */
#include "mfmdlErrors.h"
#include "mfmdlCOMPOSED_MODEL.h"
#include "mfmdlPrivate.h"

/**
 * Class constructor
 *
 * @param modelName name of model
 */
mfmdlCOMPOSED_MODEL::mfmdlCOMPOSED_MODEL(string modelName):
mfmdlMODEL(modelName)
{
    logTrace("mfmdlCOMPOSED_MODEL::mfmdlCOMPOSED_MODEL()");

    _modelType = mfmdlCOMPOSED_MODEL_TYPE;
}

/**
 * Class destructor
 *
 * The class destructor deletes all models which compose it
 */
mfmdlCOMPOSED_MODEL::~mfmdlCOMPOSED_MODEL()
{
    logTrace("mfmdlCOMPOSED_MODEL::~mfmdlCOMPOSED_MODEL()");

    // Delete all models of the list
    for( unsigned int i = 0; i < _composingModels.size(); i++ )
    {
        delete(_composingModels[i]);
    }
}

/*
 * Public methods
 */
/** 
 * Get the list of the parameters of the composed model
 *
 * This method adds the parameters of all the models composing this one into the
 * given list.
 *
 * @param paramList list int which model's parameters are added
 * @param clear indicate if list has to be cleared first.
 *
 * @returns always mcsSUCCESS  
 */
mcsCOMPL_STAT mfmdlCOMPOSED_MODEL::GetParameters
    (mfmdlPARAMETER_LIST *paramList, mcsLOGICAL clear)
{
    logTrace("mfmdlCOMPOSED_MODEL::GetParameters()");

    // Clear list is requested
    if (clear == mcsTRUE)
    {
        paramList->Clear();
    }

    // Now the parameters of each composing model
    for(unsigned int i = 0; i < _composingModels.size(); i++)
    {
        _composingModels[i]->GetParameters(paramList, mcsFALSE);
    }
    
    return mcsSUCCESS; 
}

/** 
 * Add the given model at the end of the composing models.
 *
 * @param model the model to add. 
 *
 * @returns always mcsSUCCESS  
 */
mcsCOMPL_STAT mfmdlCOMPOSED_MODEL::AddAtTail(mfmdlMODEL * model)
{
    logTrace("mfmdlCOMPOSED_MODEL::Add()");
    _composingModels.push_back(model); 

    return mcsSUCCESS;
}

/** 
 * Return the total number of block in the composed model.
 *
 * @returns the number of composing models.
 */
mcsINT32 mfmdlCOMPOSED_MODEL::GetNbOfComposingModels()
{
    logTrace("mfmdlCOMPOSED_MODEL::GetNbOfBlockModels()");
    return _composingModels.size();
}

/** 
 * Get one of the composing model
 *
 * @param index  index of the requested composing model.
 * @param model  reference onto the requested composing model.
 *
 * @return mcsSUCCESS on successful completion or mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT mfmdlCOMPOSED_MODEL::GetComposingModel(mcsUINT32 index,
                                                     mfmdlMODEL **model)
{
    logTrace("mfmdlCOMPOSED_MODEL::GetComposingModel()");
    
    if(index >= _composingModels.size())
    {
        errAdd(mfmdlERR_INDEX_OUT_OF_BOUNDS,
               "composing model index", index, 0,
               _composingModels.size()); 
        return mcsFAILURE;
    }
    *model=_composingModels[index];
    
    return mcsSUCCESS;
}


/**
 * Evaluation method for a composite model
 *
 * @param u array containing the U coordinates
 * @param v array containing the V coordinates
 * @param sizeOfArrays number of coordinates
 * @param result array where complex visibilities corresponding to the given
 * coordinates will be stored.
 * 
 * @return mcsSUCCESS on successful completion or mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT mfmdlCOMPOSED_MODEL::Eval(mfmdlUV_COORDS &uvCoords, mfmdlVIS &vis)
{
    /* Local declaration */
    mfmdlMODEL *elementaryModel;
    mcsCOMPL_STAT status;
        
    /* Get the number of elementary models */
    mcsINT16 nbOfModels = GetNbOfComposingModels();
 
    // reset the vis array
    vis.Reset();
    // Temporary vis structure     
    mfmdlVIS *visTemp = new mfmdlVIS(vis.Size());

    /* Loop other models */
    mcsUINT8 modelIndex;
    for ( modelIndex = 0; modelIndex<nbOfModels ; modelIndex++)
    {
        /* Get next model in the list */ 
        status = GetComposingModel(modelIndex,&elementaryModel);
        
        /* TODO:
         * Model Elementary model could be also a composite one 
         * */

        /* Compute the corresponding complex visibility */
        if (elementaryModel->Eval(uvCoords,*visTemp) == mcsFAILURE)
        {
            // release visTemp
            delete visTemp;
            return mcsFAILURE;
        }
        vis.Add(*visTemp);
    } 
    // release visTemp
    delete visTemp;
    // and quit ... 
    return mcsSUCCESS;
}



/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
