/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrExpandModelTree.cpp,v 1.1 2006-08-02 08:26:25 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 *  Definition of mfsvrExpandModelTree class.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfsvrExpandModelTree.cpp,v 1.1 2006-08-02 08:26:25 lsauge Exp $"; 

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
#include "mfsvrPrivate.h"

mcsCOMPL_STAT mfsvrSERVER::DumpParameters(
        mfmdlMODEL *model
        )
{

    /*
        ADD SOME CHECKS 
    */  
    mfmdlPARAMETER_LIST paramList;

    if (model->GetParameters(&paramList,mcsTRUE) == mcsSUCCESS)
    {
        for (mcsUINT16 idx=0 ; idx<paramList.Size() ; idx++)
        {
            mfmdlPARAMETER *param = NULL;
            param = paramList.GetNextParameter((idx == 0)?mcsTRUE:mcsFALSE);
            if (param != NULL)
            {

                printf("%03d %-20s %+7.5e\n",
                        idx,
                        (param->GetName()).c_str(),
                        param->GetValue()
                      );
            }
        }
    }
    return mcsSUCCESS;
}



mcsCOMPL_STAT mfsvrSERVER::ExpandModelTree(
        mfmdlCOMPOSED_MODEL *model 
        )
{
    // Get the number of composing model in this branch
    mcsUINT16 nbOfModels = model->GetNbOfComposingModels();

    printf("<model name=composed>\n"); 

    // Display some information about individual model
    mcsUINT16 modelIdx;
    for (modelIdx = 0 ; modelIdx < nbOfModels; modelIdx++)
    {
        mfmdlMODEL *indivModel = NULL;
        if (model->GetComposingModel(modelIdx,&indivModel) == mcsSUCCESS)
        {
            switch ( indivModel->GetModelType() )
            {
                case mfmdlCOMPOSED_MODEL_TYPE:
                    // Recursive call
                    this->ExpandModelTree((mfmdlCOMPOSED_MODEL *)(&indivModel)); 
                    break;
                case mfmdlPONCT_MODEL_TYPE:      
                    printf("<model name=punct type=punct>\n"); 

                    this->DumpParameters(indivModel); 


                    printf("</model>\n"); 
                    break;

                case mfmdlUNIFORM_DISK_MODEL_TYPE:
                    printf("<model name=uniform type=uniform>\n"); 
                    this->DumpParameters(indivModel); 
                    printf("</model>\n"); 
                    break;
                default:
                    break;
            }
        }
        else
        {
            logError("Cannot get information about individual model.");
            return mcsFAILURE; 
        }
    }
    
    printf("</model>\n") ;
    
    return mcsSUCCESS;
}

/*___oOo___*/
