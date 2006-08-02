/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_VIS2.cpp,v 1.1 2006-06-27 12:05:36 lsauge Exp $
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of mfdataINPUT_VIS2 class.
 */

static char *rcsId="@(#) $Id: mfdataINPUT_VIS2.cpp,v 1.1 2006-06-27 12:05:36 lsauge Exp $"; 
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
#include "mfdataINPUT_VIS2.h"
#include "mfdataPrivate.h"

/**
 * Class constructor
 */
mfdataINPUT_VIS2::mfdataINPUT_VIS2()
{
    logTrace("mfdataINPUT_VIS2::mfdataINPUT_VIS2()");
    _inputDataType=mfdataINPUT_VIS2_TYPE;
    _nbOfData=0;
}

/**
 * Class destructor
 */
mfdataINPUT_VIS2::~mfdataINPUT_VIS2()
{
    logTrace("mfdataINPUT_VIS2::~mfdataINPUT_VIS2()");
}

/*
 * Public methods
 */
mcsCOMPL_STAT mfdataINPUT_VIS2::SetVis2Refs( mcsDOUBLE * uCoord,
                                           mcsDOUBLE * vCoord,
                                           mcsDOUBLE * vis2Data,
                                           mcsDOUBLE * vis2Err)
{
    logTrace("mfdataINPUT_VIS2::SetVis2Refs()");
    _uCoord     = uCoord;
    _vCoord     = vCoord;
    _vis2Data   = vis2Data;
    _vis2Err    = vis2Err;
    return mcsSUCCESS;
}


/**
 * Get U coordinate of the data (meters).
 *
 * @param uCoord U coordinate of the data (meters)
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_VIS2::GetUCoord(mcsDOUBLE ** uCoord)
{
    logTrace("mfdataINPUT_VIS2::GetUCoord()");
    *uCoord=_uCoord;
    return mcsSUCCESS;
}


/**
 * Get V coordinate of the data (meters).
 *
 * @param vCoord V coordinate of the data (meters)
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_VIS2::GetVCoord(mcsDOUBLE ** vCoord)
{
    logTrace("mfdataINPUT_VIS2::GetVCoord()");
    *vCoord=_vCoord;
    return mcsSUCCESS;
}

/**
 * Get Squared Visibility.
 *
 * @param vis2Data Squared Visibility
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_VIS2::GetVis2Data(mcsDOUBLE ** vis2Data)
{
    logTrace("mfdataINPUT_VIS2::GetVis2Data()");
    *vis2Data=_vis2Data;
    return mcsSUCCESS;
}

/**
 * Get Error in Squared Visibility.
 *
 * @param vis2Err Error in Squared Visibility
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_VIS2::GetVis2Err(mcsDOUBLE ** vis2Err)
{
    logTrace("mfdataINPUT_VIS2::GetVis2Err()");
    *vis2Err=_vis2Err;
    return mcsSUCCESS;
}


/**
 * Get the uvCoords as a mdmdlUV_COORDS object.
 *
 * @param uvCoords  uvCoords object 
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_VIS2::GetUVCoords(mfmdlUV_COORDS &uvCoords)
{
    logTrace("mfdataINPUT_VIS2::GetVis2Err()");
    if (uvCoords.Resize(this->GetNbOfData()) == mcsSUCCESS)
    {
        for (mcsUINT16 idx=0 ; idx<this->GetNbOfData() ; idx++)
        {
            uvCoords.Set(idx,*(_uCoord+idx),*(_vCoord+idx));
        }
        return mcsSUCCESS;
    }
        
    return mcsFAILURE;
}

mcsDOUBLE mfdataINPUT_VIS2::GetUCoord  (mcsUINT16 index)
{
    if (index<_nbOfData)
    {
        return *(_uCoord+index);
    }
    return 0.0;
}

mcsDOUBLE mfdataINPUT_VIS2::GetVCoord  (mcsUINT16 index)
{
    if (index<_nbOfData)
    {
        return *(_vCoord+index);
    }
    return 0.0;
}

mcsDOUBLE mfdataINPUT_VIS2::GetVis2Data(mcsUINT16 index)
{
    if (index<_nbOfData)
    {
        return *(_vis2Data+index);
    }
    return 0.0;
}

mcsDOUBLE mfdataINPUT_VIS2::GetVis2Err (mcsUINT16 index)
{
    if (index<_nbOfData)
    {
        return *(_vis2Err+index);
    }
    return 0.0;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
