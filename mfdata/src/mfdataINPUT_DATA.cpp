/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: mfdataINPUT_DATA.cpp,v 1.2 2006-07-05 14:05:42 lsauge Exp $
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
 *  Definition of mfdataINPUT_DATA class.
 */

static char *rcsId="@(#) $Id: mfdataINPUT_DATA.cpp,v 1.2 2006-07-05 14:05:42 lsauge Exp $"; 
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
#include "mfdataINPUT_DATA.h"
#include "mfdataPrivate.h"

/**
 * Class constructor
 */
mfdataINPUT_DATA::mfdataINPUT_DATA()
{
    logTrace("mfdataINPUT_DATA::mfdataINPUT_DATA()");
}

/**
 * Class destructor
 */
mfdataINPUT_DATA::~mfdataINPUT_DATA()
{
    logTrace("mfdataINPUT_DATA::~mfdataINPUT_DATA()");
}

/*
 * Public methods
 */
mcsCOMPL_STAT mfdataINPUT_DATA::SetDataDimensions(mcsINT16 nbOfData,
                                                 mcsINT16 nbOfWaves)
{
   logTrace("mfdataINPUT_DATA::SetDataDimensions()");
   _nbOfData =nbOfData;
   _nbOfWaves=nbOfWaves;
   return mcsSUCCESS;
}

mcsCOMPL_STAT mfdataINPUT_DATA::SetDataRefs(mcsSTRING32 dateObs,
                                           mcsSTRING32 arrName,
                                           mcsSTRING32 insName,
                                           mcsINT16 targetId,
                                           mcsDOUBLE * lambda,
                                           mcsDOUBLE * time,
                                           mcsDOUBLE * intTime)
{
   logTrace("mfdataINPUT_DATA::SetDataRefs()");
    strncpy(_dateObs,dateObs,32);
    strncpy(_arrName,arrName,32);
    strncpy(_insName,insName,32);
    _targetId=targetId;
    _lambda=lambda;
    _time=time;
    _intTime=intTime;
   return mcsSUCCESS;
}

/** 
 *  Get type of this input data class.
 *
 *  @returns type of input data class
 */
mfdataInputDataType mfdataINPUT_DATA::GetType()
{
    logTrace("mfdataINPUT_DATA::GetType()");
    return _inputDataType;
}

char * mfdataINPUT_DATA::GetDateObs()
{
    logTrace("mfdataINPUT_DATA::GetDateObs()");
    return _dateObs;
}

char * mfdataINPUT_DATA::GetArrName()
{
    logTrace("mfdataINPUT_DATA::GetArrName()");
    return _arrName;
}

char * mfdataINPUT_DATA::GetInsName()
{
    logTrace("mfdataINPUT_DATA::GetInsName()");
    return _insName;
}

mcsINT16 mfdataINPUT_DATA::GetTargetId()
{
    logTrace("mfdataINPUT_DATA::GetTargetId()");
    return _targetId;
}

mcsINT16 mfdataINPUT_DATA::GetNbOfData()
{
    logTrace("mfdataINPUT_DATA::GetNbOfData()");
    return _nbOfData;
}

mcsINT16 mfdataINPUT_DATA::GetNbOfWaves()
{
    logTrace("mfdataINPUT_DATA::GetNbOfWaves()");
    return _nbOfWaves;
}

mcsCOMPL_STAT mfdataINPUT_DATA::GetLambda(mcsDOUBLE ** lambda)
{
    logTrace("mfdataINPUT_DATA::GetLambda()");
    *lambda=_lambda; 
    return mcsSUCCESS;
}

mcsCOMPL_STAT mfdataINPUT_DATA::GetTime(mcsDOUBLE ** time)
{
    logTrace("mfdataINPUT_DATA::GetTime()");
    *time=_time; 
    return mcsSUCCESS;
}

mcsCOMPL_STAT mfdataINPUT_DATA::GetIntTime(mcsDOUBLE ** intTime)
{
    logTrace("mfdataINPUT_DATA::GetIntTime()");
    *intTime=_intTime;
    return mcsSUCCESS;
}


/*
 * Protected methods
 */


/*
 * Private methods
 */


/*___oOo___*/
