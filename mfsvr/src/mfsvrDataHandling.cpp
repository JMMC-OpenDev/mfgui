/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfsvrDataHandling.cpp,v 1.1 2006-06-27 08:58:06 lsauge Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/

/**
 * @file
 *  Definition of mfsvrDataHandling class.
 */

static char *rcsId __attribute__ ((unused)) = "@(#) $Id: mfsvrDataHandling.cpp,v 1.1 2006-06-27 08:58:06 lsauge Exp $"; 

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

/*
 * Public methods
 */

mcsCOMPL_STAT mfsvrSERVER::AddInputData(mfseqINPUT_DATA *inputData)
{
    logTrace("mfsvrSERVER::AddInputData()");
    
    _inputDataList.push_back(inputData);
    
    return mcsSUCCESS;
}

mcsINT16 mfsvrSERVER::GetNbOfDataSet()
{
    logTrace("mfsvrSERVER::GetNbOfDataSet()");
    return((mcsINT16)_inputDataList.size());
}

mcsINT16 mfsvrSERVER::GetNbOfData()
{
    logTrace("mfsvrSERVER::GetNbOfData()");
    // Get the size of the data list
    mcsINT16 listSize;
    listSize=this->GetNbOfDataSet();

    // Pointer to mfseqINPUT_DATA for further references 
    mfseqINPUT_DATA *inputData;
    
    // Get the number of data in the list 
    mcsINT16 sum=0;
    mcsINT16 listIndex;
    for(listIndex = 0; listIndex<listSize ; listIndex++)
    {
        inputData = _inputDataList[listIndex];
        sum += inputData->GetNbOfData()*
               inputData->GetNbOfWaves();
    }
    // Return the total number of Data
    return(sum);
}

mfseqINPUT_DATA *mfsvrSERVER::GetDataSet(mcsINT16 index)
{
    logTrace("mfsvrSERVER::GetDataSet");
    
    mfseqINPUT_DATA *inputDataRef;
    if((index>=0)&&(index<(mcsINT16)_inputDataList.size()))
    {
       inputDataRef =  _inputDataList[index] ;
       return(inputDataRef);
    }
    else
    {
        logError("mfsvrSERVER::GetDataSet : Index out of range (index=%d).",index);
        return NULL;
    }
}


/*___oOo___*/
