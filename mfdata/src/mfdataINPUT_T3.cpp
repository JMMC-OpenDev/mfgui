/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_T3.cpp,v 1.2 2006-07-10 12:14:44 lsauge Exp $
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/06/27 12:05:36  lsauge
 * First import
 *
 *
 ******************************************************************************/

/**
 * @file
 *  Definition of mfdataINPUT_T3 class.
 */

static char *rcsId="@(#) $Id: mfdataINPUT_T3.cpp,v 1.2 2006-07-10 12:14:44 lsauge Exp $"; 
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
#include "mfdataINPUT_T3.h"
#include "mfdataPrivate.h"

/**
 * Class constructor
 */
mfdataINPUT_T3::mfdataINPUT_T3()
{
    logTrace("mfdataINPUT_T3::mfdataINPUT_T3()");
    _inputDataType=mfdataINPUT_T3_TYPE;
    _nbOfData=0;
    
}

/**
 * Class destructor
 */
mfdataINPUT_T3::~mfdataINPUT_T3()
{
    logTrace("mfdataINPUT_T3::~mfdataINPUT_T3()");
}

/*
 * Public methods
 */
mcsCOMPL_STAT mfdataINPUT_T3::SetT3Refs(mcsDOUBLE * u1Coord,
                                       mcsDOUBLE * v1Coord,
                                       mcsDOUBLE * u2Coord,
                                       mcsDOUBLE * v2Coord,
                                       mcsDOUBLE * t3Amp,
                                       mcsDOUBLE * t3AmpErr,
                                       mcsDOUBLE * t3Phi,
                                       mcsDOUBLE * t3PhiErr)
{
    logTrace("mfdataINPUT_T3::SetT3Refs()");
    _u1Coord=u1Coord;
    _v1Coord=v1Coord;
    _u2Coord=u2Coord;
    _v2Coord=v2Coord;
    _t3Amp=t3Amp;
    _t3AmpErr=t3AmpErr;
    _t3Phi=t3Phi;
    _t3PhiErr=t3PhiErr;
    return mcsSUCCESS;
}


/**
 * Get U coordinate of baseline AB of the triangle (meters).
 *
 * @param u1Coord U coordinate of baseline AB of the triangle (meters).
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetU1Coord(mcsDOUBLE ** u1Coord)
{
    logTrace("mfdataINPUT_T3::GetU1Coord()");
    *u1Coord=_u1Coord;
    return mcsSUCCESS;
}


/**
 * Get V coordinate of baseline AB of the triangle (meters).
 *
 * @param v1Coord V coordinate of baseline AB of the triangle (meters).
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetV1Coord(mcsDOUBLE ** v1Coord)
{
    logTrace("mfdataINPUT_T3::GetV1Coord()");
    *v1Coord=_v1Coord;
    return mcsSUCCESS;
}


/**
 * Get U coordinate of baseline BC of the triangle (meters).
 *
 * @param u2Coord U coordinate of baseline BC of the triangle (meters).
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetU2Coord(mcsDOUBLE ** u2Coord)
{
    logTrace("mfdataINPUT_T3::GetU2Coord()");
    *u2Coord=_u2Coord;
    return mcsSUCCESS;
}


/**
 * Get V coordinate of baseline BC of the triangle (meters).
 *
 * @param v2Coord V coordinate of baseline BC of the triangle (meters).
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetV2Coord(mcsDOUBLE ** v2Coord)
{
    logTrace("mfdataINPUT_T3::GetV2Coord()");
    *v2Coord=_v2Coord;
    return mcsSUCCESS;
}


/**
 *  Get triple product amplitude.
 *
 * @param t3Amp Triple product amplitude
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetT3Amp(mcsDOUBLE ** t3Amp)
{
    logTrace("mfdataINPUT_T3::getT3Amp()");
    *t3Amp=_t3Amp;
    return mcsSUCCESS;
}


/**
 * Get Error in Triple product amplitude.
 *
 * @param t3AmpErr Error in Triple product amplitude
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetT3AmpErr(mcsDOUBLE ** t3AmpErr)
{
    logTrace("mfdataINPUT_T3::GetT3AmpErr()");
    *t3AmpErr=_t3AmpErr;
    return mcsSUCCESS;
}


/**
 * Get Triple product phase (degrees).
 *
 * @param t3Phi Triple product phase (degrees).
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetT3Phi(mcsDOUBLE ** t3Phi)
{
    logTrace("mfdataINPUT_T3::GetT3Phi()");
    *t3Phi=_t3Phi;
    return mcsSUCCESS;
}


/**
 * Get Error in Triple product phase (degrees).
 *
 * @param t3PhiErr Error in Triple product phase (degrees).
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetT3PhiErr(mcsDOUBLE ** t3PhiErr)
{
    logTrace("mfdataINPUT_T3::GetT3PhiErr()");
    *t3PhiErr=_t3PhiErr;
    return mcsSUCCESS;
}

/**
 * Get the uvCoords of the two base AB and BC as two mdmdlUV_COORDS objects.
 *
 * @param uvCoordsAB  uvCoords object corresponding two the baseline AB 
 * @param uvCoordsBC  uvCoords object corresponding two the baseline BC 
 *
 * @return mcsSUCCESS on successful completion. Otherwise mcsFAILURE is
 * returned
 */
mcsCOMPL_STAT mfdataINPUT_T3::GetUVCoords(
        mfmdlUV_COORDS &uvCoordsAB,
        mfmdlUV_COORDS &uvCoordsBC
        )
{
    logTrace("mfdataINPUT_T3::GetUVCoords()");
    if ( ((uvCoordsAB.Resize(this->GetNbOfData()) == mcsSUCCESS))&&
         ((uvCoordsBC.Resize(this->GetNbOfData()) == mcsSUCCESS))
         )
    {
        for (mcsUINT16 idx=0 ; idx<this->GetNbOfData() ; idx++)
        {
            uvCoordsAB.Set(idx,*(_u1Coord+idx),*(_v1Coord+idx));
            uvCoordsBC.Set(idx,*(_u2Coord+idx),*(_v2Coord+idx));
        }
        return mcsSUCCESS;
    }
        
    return mcsFAILURE;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
